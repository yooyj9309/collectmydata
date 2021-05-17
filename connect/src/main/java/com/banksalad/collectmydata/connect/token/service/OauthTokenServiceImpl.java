package com.banksalad.collectmydata.connect.token.service;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.collect.execution.ExecutionRequest;
import com.banksalad.collectmydata.common.collect.execution.ExecutionResponse;
import com.banksalad.collectmydata.common.collect.executor.CollectExecutor;
import com.banksalad.collectmydata.common.exception.CollectRuntimeException;
import com.banksalad.collectmydata.common.util.ExecutionUtil;
import com.banksalad.collectmydata.common.util.ObjectComparator;
import com.banksalad.collectmydata.connect.collect.Executions;
import com.banksalad.collectmydata.connect.common.db.entity.BanksaladClientSecretEntity;
import com.banksalad.collectmydata.connect.common.db.entity.ConnectOrganizationEntity;
import com.banksalad.collectmydata.connect.common.db.entity.ConsentEntity;
import com.banksalad.collectmydata.connect.common.db.entity.ConsentHistoryEntity;
import com.banksalad.collectmydata.connect.common.db.entity.OauthTokenEntity;
import com.banksalad.collectmydata.connect.common.db.entity.OauthTokenHistoryEntity;
import com.banksalad.collectmydata.connect.common.db.repository.BanksaladClientSecretRepository;
import com.banksalad.collectmydata.connect.common.db.repository.ConnectOrganizationRepository;
import com.banksalad.collectmydata.connect.common.db.repository.ConsentHistoryRepository;
import com.banksalad.collectmydata.connect.common.db.repository.ConsentRepository;
import com.banksalad.collectmydata.connect.common.db.repository.OauthTokenHistoryRepository;
import com.banksalad.collectmydata.connect.common.db.repository.OauthTokenRepository;
import com.banksalad.collectmydata.connect.common.dto.Consent;
import com.banksalad.collectmydata.connect.common.dto.ErrorResponse;
import com.banksalad.collectmydata.connect.common.dto.GetConsentRequest;
import com.banksalad.collectmydata.connect.common.dto.GetConsentResponse;
import com.banksalad.collectmydata.connect.common.enums.ConnectErrorType;
import com.banksalad.collectmydata.connect.common.enums.TokenErrorType;
import com.banksalad.collectmydata.connect.common.exception.ConnectException;
import com.banksalad.collectmydata.connect.common.mapper.ConsentHistoryMapper;
import com.banksalad.collectmydata.connect.common.mapper.ConsentMapper;
import com.banksalad.collectmydata.connect.common.mapper.OauthTokenHistoryMapper;
import com.banksalad.collectmydata.connect.common.mapper.OauthTokenMapper;
import com.banksalad.collectmydata.connect.common.meters.ConnectMeterRegistry;
import com.banksalad.collectmydata.connect.grpc.client.CollectScheduleClientService;
import com.banksalad.collectmydata.connect.organization.dto.Organization;
import com.banksalad.collectmydata.connect.token.dto.GetIssueTokenRequest;
import com.banksalad.collectmydata.connect.token.dto.GetOauthTokenResponse;
import com.banksalad.collectmydata.connect.token.dto.GetRefreshTokenRequest;
import com.banksalad.collectmydata.connect.token.dto.GetRevokeTokenRequest;
import com.banksalad.collectmydata.connect.token.dto.OauthToken;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.banksalad.collectmydata.common.util.DateUtil.KST_ZONE_ID;

@Slf4j
@Service
@RequiredArgsConstructor
public class OauthTokenServiceImpl implements OauthTokenService {

  private final CollectScheduleClientService collectScheduleClientService;
  private final OauthTokenRepository oauthTokenRepository;
  private final OauthTokenHistoryRepository oauthTokenHistoryRepository;
  private final ConnectOrganizationRepository connectOrganizationRepository;
  private final BanksaladClientSecretRepository banksaladClientSecretRepository;
  private final ConsentRepository consentRepository;
  private final ConsentHistoryRepository consentHistoryRepository;

  private final CollectExecutor collectExecutor;
  private final ConnectMeterRegistry connectMeterRegistry;
  private final ConsentMapper consentMapper = Mappers.getMapper(ConsentMapper.class);
  private final ConsentHistoryMapper consentHistoryMapper = Mappers.getMapper(ConsentHistoryMapper.class);
  private final OauthTokenMapper oauthTokenMapper = Mappers.getMapper(OauthTokenMapper.class);
  private final OauthTokenHistoryMapper oauthTokenHistoryMapper = Mappers.getMapper(OauthTokenHistoryMapper.class);

  private final Map<String, String> header = Map.of(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE);

  @Value("${banksalad.oauth-callback-url}")
  private String redirectUrl;

  @Override
  public OauthToken issueToken(long banksaladUserId, String organizationId, String authorizationCode) {
    /* load connectOrganizationEntity */
    String consentId = UUID.randomUUID().toString();
    ConnectOrganizationEntity connectOrganizationEntity = connectOrganizationRepository
        .findByOrganizationId(organizationId)
        .orElseThrow(() -> new ConnectException(ConnectErrorType.NOT_FOUND_ORGANIZATION));

    /* request api */
    Organization organization = getOrganization(connectOrganizationEntity);
    GetOauthTokenResponse getOauthTokenResponse = requestIssueToken(organization, authorizationCode);

    /* load oauthTokenEntity */
    OauthTokenEntity oauthTokenEntity = oauthTokenRepository
        .findByBanksaladUserIdAndOrganizationId(banksaladUserId, organizationId)
        .orElseGet(() -> {
          OauthTokenEntity createdOauthTokenEntity = OauthTokenEntity.builder()
              .banksaladUserId(banksaladUserId)
              .organizationId(organizationId)
              .build();
          createdOauthTokenEntity.setCreatedBy(String.valueOf(banksaladUserId));
          createdOauthTokenEntity.setUpdatedBy(String.valueOf(banksaladUserId));

          return createdOauthTokenEntity;
        });

    /* upsert oauthTokenEntity */
    // TODO : check timezone (kst or utc)
    oauthTokenEntity = oauthTokenMapper.dtoToEntity(getOauthTokenResponse, oauthTokenEntity);
    oauthTokenEntity.setSyncedAt(LocalDateTime.now());
    oauthTokenEntity.setConsentId(consentId);
    oauthTokenEntity.setAuthorizationCode(authorizationCode);
    oauthTokenEntity.setAccessTokenExpiresAt(
        LocalDateTime.now().plusSeconds(getOauthTokenResponse.getExpiresIn())); // fixme : epoch time
    oauthTokenEntity.setRefreshTokenExpiresAt(
        LocalDateTime.now().plusSeconds(getOauthTokenResponse.getRefreshTokenExpiresIn())); // fixme : epoch time
    oauthTokenEntity.setIssuedAt(LocalDateTime.now());
    oauthTokenEntity.setRefreshedAt(LocalDateTime.now());

    oauthTokenRepository.save(oauthTokenEntity);
    oauthTokenHistoryRepository
        .save(oauthTokenHistoryMapper.toHistoryEntity(oauthTokenEntity, OauthTokenHistoryEntity.builder().build()));

    /* request consent api logic */
    syncConsent(banksaladUserId, organization, oauthTokenEntity.getAccessToken(), consentId);

    return OauthToken.builder()
        .accessToken(oauthTokenEntity.getAccessToken())
        .refreshToken(oauthTokenEntity.getRefreshToken())
        .scopes(getParseScope(oauthTokenEntity.getScope()))
        .consentId(consentId)
        .build();
  }

  @Override
  @Transactional(readOnly = true)
  public OauthToken getAccessToken(long banksaladUserId, String organizationId) {
    /* load connectOrganizationEntity */
    OauthTokenEntity oauthTokenEntity = oauthTokenRepository
        .findByBanksaladUserIdAndOrganizationId(banksaladUserId, organizationId)
        .orElseThrow(() -> new ConnectException(ConnectErrorType.NOT_FOUND_TOKEN));

    /* check if access token expired */
    if (isTokenExpired(oauthTokenEntity.getAccessTokenExpiresAt())) {
      return refreshToken(banksaladUserId, organizationId);
    }

    return OauthToken.builder()
        .accessToken(oauthTokenEntity.getAccessToken())
        .scopes(getParseScope(oauthTokenEntity.getScope()))
        .consentId(oauthTokenEntity.getConsentId())
        .build();
  }

  @Override
  public OauthToken refreshToken(long banksaladUserId, String organizationId) {
    /* load oauthTokenEntity */
    OauthTokenEntity oauthTokenEntity = oauthTokenRepository
        .findByBanksaladUserIdAndOrganizationId(banksaladUserId, organizationId)
        .orElseThrow(() -> new ConnectException(ConnectErrorType.NOT_FOUND_TOKEN));

    /* check refresh token expiration */
    if (isTokenExpired(oauthTokenEntity.getRefreshTokenExpiresAt())) {
      throw new ConnectException(ConnectErrorType.EXPIRED_TOKEN);
    }

    /* load connectOrganizationEntity */
    ConnectOrganizationEntity connectOrganizationEntity = connectOrganizationRepository
        .findByOrganizationId(organizationId)
        .orElseThrow(() -> new ConnectException(ConnectErrorType.NOT_FOUND_ORGANIZATION));

    /* request api */
    Organization organization = getOrganization(connectOrganizationEntity);
    GetOauthTokenResponse getOauthTokenResponse = requestRefreshToken(organization, oauthTokenEntity.getRefreshToken());

    /* upsert oauthTokenEntity */
    oauthTokenEntity = oauthTokenMapper.dtoToEntity(getOauthTokenResponse, oauthTokenEntity);
    oauthTokenEntity.setSyncedAt(LocalDateTime.now());
    oauthTokenEntity.setAccessTokenExpiresAt(
        LocalDateTime.now().plusSeconds(getOauthTokenResponse.getExpiresIn())); // fixme : epoch time
    oauthTokenEntity.setRefreshTokenExpiresAt(
        LocalDateTime.now().plusSeconds(getOauthTokenResponse.getRefreshTokenExpiresIn())); // fixme : epoch time
    oauthTokenEntity.setRefreshedAt(LocalDateTime.now());

    oauthTokenRepository.save(oauthTokenEntity);
    oauthTokenHistoryRepository
        .save(oauthTokenHistoryMapper.toHistoryEntity(oauthTokenEntity, OauthTokenHistoryEntity.builder().build()));

    return OauthToken.builder()
        .accessToken(oauthTokenEntity.getAccessToken())
        .scopes(getParseScope(oauthTokenEntity.getScope()))
        .consentId(oauthTokenEntity.getConsentId())
        .build();
  }

  @Override
  public void revokeToken(long banksaladUserId, String organizationId) {
    /* delete oauthTokenEntity */
    OauthTokenEntity oauthTokenEntity = oauthTokenRepository
        .findByBanksaladUserIdAndOrganizationId(banksaladUserId, organizationId)
        .orElseThrow(() -> new ConnectException(ConnectErrorType.NOT_FOUND_TOKEN));

    oauthTokenRepository.delete(oauthTokenEntity);
    oauthTokenHistoryRepository
        .save(oauthTokenHistoryMapper.toHistoryEntity(oauthTokenEntity, OauthTokenHistoryEntity.builder().build()));

    /* load connectOrganizationEntity */
    ConnectOrganizationEntity connectOrganizationEntity = connectOrganizationRepository
        .findByOrganizationId(organizationId)
        .orElseThrow(() -> new ConnectException(ConnectErrorType.NOT_FOUND_ORGANIZATION));

    /* request api */
    Organization organization = getOrganization(connectOrganizationEntity);
    requestRevokeToken(organization, oauthTokenEntity.getAccessToken());

    ConsentEntity consentEntity = consentRepository
        .findByBanksaladUserIdAndOrganizationId(banksaladUserId, organization.getOrganizationId())
        .orElseThrow(() -> new ConnectException(ConnectErrorType.NOT_FOUND_CONSENT));

    Consent consent = consentMapper.entityToDto(consentEntity);
    if (consent.isScheduled()) {
      collectScheduleClientService.unregisterScheduledSync(banksaladUserId, organization, consent);
    }
  }

  @Override
  public void revokeAllTokens(long banksaladUserId) {
    List<OauthTokenEntity> oauthTokenEntities = oauthTokenRepository
        .findAllByBanksaladUserId(banksaladUserId);

    for (OauthTokenEntity oauthTokenEntity : oauthTokenEntities) {
      revokeToken(banksaladUserId, oauthTokenEntity.getOrganizationId());
    }
  }

  private void syncConsent(long banksaladUserId, Organization organization, String accessToken, String consentId) {
    /* request api */
    GetConsentResponse consentResponse = requestConsent(organization, accessToken);
    Consent consent = consentResponse.getConsent();
    consent.setConsentId(consentId);

    /* load consent entity */
    ConsentEntity consentEntity = consentRepository
        .findByBanksaladUserIdAndOrganizationId(banksaladUserId, organization.getOrganizationId())
        .orElseGet(() -> {
          ConsentEntity createdConsentEntity = ConsentEntity.builder()
              .banksaladUserId(banksaladUserId)
              .organizationId(organization.getOrganizationId())
              .build();
          createdConsentEntity.setCreatedBy(String.valueOf(banksaladUserId));
          createdConsentEntity.setUpdatedBy(String.valueOf(banksaladUserId));

          return createdConsentEntity;
        });

    /* mapping dto to entity */
    consentEntity = consentMapper.dtoToEntity(consent, consentEntity);
    consentEntity.setSyncedAt(LocalDateTime.now());

    /* upsert entity */
    consentRepository.save(consentEntity);
    consentHistoryRepository
        .save(consentHistoryMapper.toHistoryEntity(consentEntity, ConsentHistoryEntity.builder().build()));

    /* call scheduler register */
    if (consent.isScheduled()) {
      collectScheduleClientService.registerScheduledSync(banksaladUserId, organization, consent);
    }
  }

  private GetConsentResponse requestConsent(Organization organization, String accessToken) {
    ExecutionContext executionContext = getExecutionContext(organization);
    Map<String, String> header = Map.of(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);

    GetConsentRequest request = GetConsentRequest.builder()
        .orgCode(organization.getOrganizationCode())
        .build();
    ExecutionRequest<GetConsentRequest> executionRequest = ExecutionUtil.assembleExecutionRequest(header, request);

    ExecutionResponse<GetConsentResponse> executionResponse = collectExecutor
        .execute(executionContext, Executions.common_consent, executionRequest);

    if (executionResponse.getHttpStatusCode() != HttpStatus.OK.value()) {
      throw new ConnectException(ConnectErrorType.INVALID_API_RESPONSE);
    }
    return executionResponse.getResponse();
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
    ExecutionRequest<GetIssueTokenRequest> executionRequest = ExecutionUtil.assembleExecutionRequest(header, request);

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
    ExecutionRequest<GetRefreshTokenRequest> executionRequest = ExecutionUtil.assembleExecutionRequest(header, request);

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
    ExecutionRequest<GetRevokeTokenRequest> executionRequest = ExecutionUtil.assembleExecutionRequest(header, request);

    ExecutionResponse<GetOauthTokenResponse> executionResponse = collectExecutor
        .execute(executionContext, Executions.oauth_revoke_token, executionRequest);

    validateResponse(executionResponse, executionContext);
  }

  private void validateResponse(ExecutionResponse executionResponse, ExecutionContext executionContext) {
    if (executionResponse.getHttpStatusCode() != HttpStatus.OK.value()) {
      ErrorResponse errorResponse = ((GetOauthTokenResponse) executionResponse.getResponse()).getErrorResponse();
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
    return expirationTime.isBefore(LocalDateTime.now(KST_ZONE_ID));
  }
}
