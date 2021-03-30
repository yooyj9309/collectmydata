package com.banksalad.collectmydata.connect.token.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.collect.execution.ExecutionRequest;
import com.banksalad.collectmydata.common.collect.execution.ExecutionResponse;
import com.banksalad.collectmydata.common.collect.executor.CollectExecutor;
import com.banksalad.collectmydata.common.exception.CollectRuntimeException;
import com.banksalad.collectmydata.common.util.ExecutionUtil;
import com.banksalad.collectmydata.connect.collect.Executions;
import com.banksalad.collectmydata.connect.common.db.entity.BanksaladClientSecretEntity;
import com.banksalad.collectmydata.connect.common.db.entity.ConnectOrganizationEntity;
import com.banksalad.collectmydata.connect.common.db.entity.OauthTokenEntity;
import com.banksalad.collectmydata.connect.common.db.repository.BanksaladClientSecretRepository;
import com.banksalad.collectmydata.connect.common.db.repository.ConnectOrganizationRepository;
import com.banksalad.collectmydata.connect.common.db.repository.OauthTokenHistoryRepository;
import com.banksalad.collectmydata.connect.common.db.repository.OauthTokenRepository;
import com.banksalad.collectmydata.connect.common.dto.ErrorResponse;
import com.banksalad.collectmydata.connect.common.enums.ConnectErrorType;
import com.banksalad.collectmydata.connect.common.enums.TokenErrorType;
import com.banksalad.collectmydata.connect.common.exception.ConnectException;
import com.banksalad.collectmydata.connect.common.mapper.OauthTokenHistoryMapper;
import com.banksalad.collectmydata.connect.common.mapper.OauthTokenMapper;
import com.banksalad.collectmydata.connect.common.meters.ConnectMeterRegistry;
import com.banksalad.collectmydata.connect.organization.dto.Organization;
import com.banksalad.collectmydata.connect.token.dto.GetIssueTokenRequest;
import com.banksalad.collectmydata.connect.token.dto.GetRefreshTokenRequest;
import com.banksalad.collectmydata.connect.token.dto.GetRevokeTokenRequest;
import com.banksalad.collectmydata.connect.token.dto.GetOauthTokenResponse;
import com.banksalad.collectmydata.connect.token.dto.OauthToken;
import com.github.banksalad.idl.apis.v1.connectmydata.ConnectmydataProto.GetAccessTokenRequest;
import com.github.banksalad.idl.apis.v1.connectmydata.ConnectmydataProto.IssueTokenRequest;
import com.github.banksalad.idl.apis.v1.connectmydata.ConnectmydataProto.RefreshTokenRequest;
import com.github.banksalad.idl.apis.v1.connectmydata.ConnectmydataProto.RevokeAllTokensRequest;
import com.github.banksalad.idl.apis.v1.connectmydata.ConnectmydataProto.RevokeTokenRequest;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OauthTokenServiceImpl implements OauthTokenService {

  private final OauthTokenRepository oauthTokenRepository;
  private final OauthTokenHistoryRepository oauthTokenHistoryRepository;
  private final ConnectOrganizationRepository connectOrganizationRepository;
  private final BanksaladClientSecretRepository banksaladClientSecretRepository;

  private final CollectExecutor collectExecutor;
  private final ConnectMeterRegistry connectMeterRegistry;
  private final OauthTokenMapper oauthTokenMapper = Mappers.getMapper(OauthTokenMapper.class);
  private final OauthTokenHistoryMapper oauthTokenHistoryMapper = Mappers.getMapper(OauthTokenHistoryMapper.class);

  @Value("${banksalad.oauth-callback-url}")
  private String redirectUrl;

  @Override
  public OauthToken issueToken(IssueTokenRequest request) {
    /* load connectOrganizationEntity */
    Long banksaladUserId = Long.parseLong(request.getBanksaladUserId());
    ConnectOrganizationEntity connectOrganizationEntity = connectOrganizationRepository
        .findByOrganizationId(request.getOrganizationId())
        .orElseThrow(() -> new ConnectException(ConnectErrorType.NOT_FOUND_ORGANIZATION));

    /* request api */
    Organization organization = getOrganization(connectOrganizationEntity);
    GetOauthTokenResponse getOauthTokenResponse = requestIssueToken(organization, request.getAuthorizationCode());

    /* load oauthTokenEntity */
    OauthTokenEntity oauthTokenEntity = oauthTokenRepository
        .findByBanksaladUserIdAndOrganizationId(banksaladUserId, request.getOrganizationId())
        .orElse(
            OauthTokenEntity.builder()
                .banksaladUserId(banksaladUserId)
                .organizationId(request.getOrganizationId())
                .authorizationCode(request.getAuthorizationCode())
                .build());

    /* upsert oauthTokenEntity */
    oauthTokenEntity = oauthTokenMapper.dtoToEntity(getOauthTokenResponse, oauthTokenEntity);
    // TODO : syncedAt, issuedAt, refreshedAt, consentId
//    oauthTokenEntity.setSyncedAt();
//    oauthTokenEntity.setConsentId();
//    oauthTokenEntity.setIssuedAt();
//    oauthTokenEntity.setRefreshedAt();
    oauthTokenEntity.setAuthorizationCode(request.getAuthorizationCode());
    oauthTokenEntity.setAccessTokenExpiresAt(LocalDateTime.now().plusSeconds(getOauthTokenResponse.getExpiresIn()));
    oauthTokenEntity.setRefreshTokenExpiresAt(
        LocalDateTime.now().plusSeconds(getOauthTokenResponse.getRefreshTokenExpiresIn()));

    oauthTokenRepository.save(oauthTokenEntity);
    oauthTokenHistoryRepository.save(oauthTokenHistoryMapper.toHistoryEntity(oauthTokenEntity));

    return OauthToken.builder()
        .accessToken(oauthTokenEntity.getAccessToken())
        .refreshToken(oauthTokenEntity.getRefreshToken())
        .scopes(getParseScope(oauthTokenEntity.getScope()))
        .build();
  }

  @Override
  @Transactional(readOnly = true)
  public OauthToken getAccessToken(GetAccessTokenRequest request) {
    /* load connectOrganizationEntity */
    Long banksaladUserId = Long.parseLong(request.getBanksaladUserId());
    OauthTokenEntity oauthTokenEntity = oauthTokenRepository
        .findByBanksaladUserIdAndOrganizationId(banksaladUserId, request.getOrganizationId())
        .orElseThrow(() -> new ConnectException(ConnectErrorType.NOT_FOUND_TOKEN));

    /* check if access token expired */
    if (isTokenExpired(oauthTokenEntity.getAccessTokenExpiresAt())) {
      RefreshTokenRequest refreshTokenRequest = RefreshTokenRequest.newBuilder()
          .setBanksaladUserId(request.getBanksaladUserId())
          .setOrganizationId(request.getOrganizationId())
          .build();
      return refreshToken(refreshTokenRequest);
    }

    return OauthToken.builder()
        .accessToken(oauthTokenEntity.getAccessToken())
        .scopes(getParseScope(oauthTokenEntity.getScope()))
        .build();
  }

  @Override
  public OauthToken refreshToken(RefreshTokenRequest request) {
    /* load oauthTokenEntity */
    Long banksaladUserId = Long.parseLong(request.getBanksaladUserId());
    OauthTokenEntity oauthTokenEntity = oauthTokenRepository
        .findByBanksaladUserIdAndOrganizationId(banksaladUserId, request.getOrganizationId())
        .orElseThrow(() -> new ConnectException(ConnectErrorType.NOT_FOUND_TOKEN));

    /* check refresh token expiration */
    if (isTokenExpired(oauthTokenEntity.getRefreshTokenExpiresAt())) {
      throw new ConnectException(ConnectErrorType.EXPIRED_TOKEN);
    }

    /* load connectOrganizationEntity */
    ConnectOrganizationEntity connectOrganizationEntity = connectOrganizationRepository
        .findByOrganizationId(request.getOrganizationId())
        .orElseThrow(() -> new ConnectException(ConnectErrorType.NOT_FOUND_ORGANIZATION));

    /* request api */
    Organization organization = getOrganization(connectOrganizationEntity);
    GetOauthTokenResponse getOauthTokenResponse = requestRefreshToken(organization, oauthTokenEntity.getRefreshToken());

    /* upsert oauthTokenEntity */
    oauthTokenEntity = oauthTokenMapper.dtoToEntity(getOauthTokenResponse, oauthTokenEntity);
    // TODO : syncedAt, issuedAt, refreshedAt, consentId
//    oauthTokenEntity.setSyncedAt();
//    oauthTokenEntity.setConsentId();
//    oauthTokenEntity.setIssuedAt();
//    oauthTokenEntity.setRefreshedAt();
    oauthTokenEntity.setAccessTokenExpiresAt(LocalDateTime.now().plusSeconds(getOauthTokenResponse.getExpiresIn()));
    oauthTokenEntity.setRefreshTokenExpiresAt(
        LocalDateTime.now().plusSeconds(getOauthTokenResponse.getRefreshTokenExpiresIn()));

    oauthTokenRepository.save(oauthTokenEntity);
    oauthTokenHistoryRepository.save(oauthTokenHistoryMapper.toHistoryEntity(oauthTokenEntity));

    return OauthToken.builder()
        .accessToken(oauthTokenEntity.getAccessToken())
        .scopes(getParseScope(oauthTokenEntity.getScope()))
        .build();
  }

  @Override
  public void revokeToken(RevokeTokenRequest request) {
    /* delete oauthTokenEntity */
    Long banksaladUserId = Long.parseLong(request.getBanksaladUserId());
    OauthTokenEntity oauthTokenEntity = oauthTokenRepository
        .findByBanksaladUserIdAndOrganizationId(banksaladUserId, request.getOrganizationId())
        .orElseThrow(() -> new ConnectException(ConnectErrorType.NOT_FOUND_TOKEN));
    oauthTokenRepository.delete(oauthTokenEntity);
    oauthTokenHistoryRepository.save(oauthTokenHistoryMapper.toHistoryEntity(oauthTokenEntity));

    /* load connectOrganizationEntity */
    ConnectOrganizationEntity connectOrganizationEntity = connectOrganizationRepository
        .findByOrganizationId(request.getOrganizationId())
        .orElseThrow(() -> new ConnectException(ConnectErrorType.NOT_FOUND_ORGANIZATION));

    /* request api */
    Organization organization = getOrganization(connectOrganizationEntity);
    requestRevokeToken(organization, oauthTokenEntity.getAccessToken());
  }

  @Override
  public void revokeAllTokens(RevokeAllTokensRequest request) {
    Long banksaladUserId = Long.parseLong(request.getBanksaladUserId());
    List<OauthTokenEntity> oauthTokenEntities = oauthTokenRepository
        .findAllByBanksaladUserId(banksaladUserId);

    for (OauthTokenEntity oauthTokenEntity : oauthTokenEntities) {
      oauthTokenRepository.delete(oauthTokenEntity);
      oauthTokenHistoryRepository.save(oauthTokenHistoryMapper.toHistoryEntity(oauthTokenEntity));
      ConnectOrganizationEntity connectOrganizationEntity = connectOrganizationRepository
          .findByOrganizationId(oauthTokenEntity.getOrganizationId())
          .orElseThrow(() -> new ConnectException(ConnectErrorType.NOT_FOUND_ORGANIZATION));

      Organization organization = getOrganization(connectOrganizationEntity);
      requestRevokeToken(organization, oauthTokenEntity.getAccessToken());
    }
  }

  private GetOauthTokenResponse requestIssueToken(Organization organization, String authorizationCode) {
    ExecutionContext executionContext = getExecutionContext(organization);
    BanksaladClientSecretEntity banksaladClientSecretEntity = getBanksaladClientSecretEntity(organization);

    GetIssueTokenRequest request = GetIssueTokenRequest.builder()
        .orgCode(organization.getOrganizationCode())
        .code(authorizationCode)
        .clientId(banksaladClientSecretEntity.getClientId())
        .clientSecret(banksaladClientSecretEntity.getClientSecret())
        .redirectUri(redirectUrl)
        .build();
    ExecutionRequest<GetIssueTokenRequest> executionRequest = ExecutionUtil.assembleExecutionRequest(request);

    ExecutionResponse<GetOauthTokenResponse> executionResponse = collectExecutor
        .execute(executionContext, Executions.oauth_issue_token, executionRequest);

    validateResponse(executionResponse, executionContext);
    return executionResponse.getResponse();
  }

  private GetOauthTokenResponse requestRefreshToken(Organization organization, String refreshToken) {
    ExecutionContext executionContext = getExecutionContext(organization);
    BanksaladClientSecretEntity banksaladClientSecretEntity = getBanksaladClientSecretEntity(organization);

    GetRefreshTokenRequest request = GetRefreshTokenRequest.builder()
        .orgCode(organization.getOrganizationCode())
        .refreshToken(refreshToken)
        .clientId(banksaladClientSecretEntity.getClientId())
        .clientSecret(banksaladClientSecretEntity.getClientSecret())
        .build();
    ExecutionRequest<GetRefreshTokenRequest> executionRequest = ExecutionUtil.assembleExecutionRequest(request);

    ExecutionResponse<GetOauthTokenResponse> executionResponse = collectExecutor
        .execute(executionContext, Executions.oauth_refresh_token, executionRequest);

    validateResponse(executionResponse, executionContext);
    return executionResponse.getResponse();
  }

  private void requestRevokeToken(Organization organization, String accessToken) {
    ExecutionContext executionContext = getExecutionContext(organization);
    BanksaladClientSecretEntity banksaladClientSecretEntity = getBanksaladClientSecretEntity(organization);

    GetRevokeTokenRequest request = GetRevokeTokenRequest.builder()
        .orgCode(organization.getOrganizationCode())
        .token(accessToken)
        .clientId(banksaladClientSecretEntity.getClientId())
        .clientSecret(banksaladClientSecretEntity.getClientSecret())
        .build();
    ExecutionRequest<GetRevokeTokenRequest> executionRequest = ExecutionUtil.assembleExecutionRequest(request);

    ExecutionResponse<ExecutionResponse> executionResponse = collectExecutor
        .execute(executionContext, Executions.oauth_revoke_token, executionRequest);

    validateResponse(executionResponse, executionContext);
  }

  private void validateResponse(ExecutionResponse executionResponse, ExecutionContext executionContext) {
    if (executionResponse.getHttpStatusCode() != HttpStatus.OK.value()) {
      ErrorResponse errorResponse = (ErrorResponse) executionResponse.getResponse();
      connectMeterRegistry.incrementTokenErrorCount(executionContext.getOrganizationId(),
          TokenErrorType.getValidatedError(errorResponse.getError()));

      throw new CollectRuntimeException(errorResponse.getError());
    }
  }

  private ExecutionContext getExecutionContext(Organization organization) {
    // TODO : 필수값만 지정?
    return ExecutionContext.builder()
        .organizationId(organization.getOrganizationId())
        .organizationHost(organization.getDomain())
        .build();
  }

  private BanksaladClientSecretEntity getBanksaladClientSecretEntity(Organization organization) {
    return banksaladClientSecretRepository
        .findBySecretType(organization.getSector())
        .orElseThrow(() -> new ConnectException(ConnectErrorType.NOT_FOUND_ORGANIZATION));
  }

  private Organization getOrganization(ConnectOrganizationEntity connectOrganizationEntity) {
    return Organization.builder()
        .sector(connectOrganizationEntity.getSector())
        .industry(connectOrganizationEntity.getIndustry())
        .organizationId(connectOrganizationEntity.getOrganizationId())
        .organizationCode(connectOrganizationEntity.getOrgCode())
        .domain(connectOrganizationEntity.getDomain())
        .build();
  }

  private List<String> getParseScope(String scope) {
    return Arrays.asList(scope.split(" "));
  }

  private boolean isTokenExpired(LocalDateTime expirationTime) {
    return expirationTime.isBefore(LocalDateTime.now());
  }
}
