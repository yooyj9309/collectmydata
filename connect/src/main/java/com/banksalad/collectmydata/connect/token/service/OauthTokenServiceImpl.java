package com.banksalad.collectmydata.connect.token.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.banksalad.collectmydata.connect.common.db.entity.ConnectOrganizationEntity;
import com.banksalad.collectmydata.connect.common.db.entity.OauthTokenEntity;
import com.banksalad.collectmydata.connect.common.db.repository.ConnectOrganizationRepository;
import com.banksalad.collectmydata.connect.common.db.repository.OauthTokenRepository;
import com.banksalad.collectmydata.connect.common.enums.ConnectErrorType;
import com.banksalad.collectmydata.connect.common.exception.ConnectException;
import com.banksalad.collectmydata.connect.organization.dto.Organization;
import com.banksalad.collectmydata.connect.token.dto.GetTokenResponse;
import com.banksalad.collectmydata.connect.token.dto.OauthToken;
import com.github.banksalad.idl.apis.v1.connectmydata.ConnectmydataProto.GetAccessTokenRequest;
import com.github.banksalad.idl.apis.v1.connectmydata.ConnectmydataProto.IssueTokenRequest;
import com.github.banksalad.idl.apis.v1.connectmydata.ConnectmydataProto.RefreshTokenRequest;
import com.github.banksalad.idl.apis.v1.connectmydata.ConnectmydataProto.RevokeAllTokensRequest;
import com.github.banksalad.idl.apis.v1.connectmydata.ConnectmydataProto.RevokeTokenRequest;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OauthTokenServiceImpl implements OauthTokenService {

  private final OauthTokenRepository oauthTokenRepository;
  private final ConnectOrganizationRepository connectOrganizationRepository;
  private final ExternalTokenService externalTokenService;

  @Override
  @Transactional
  public OauthToken issueToken(IssueTokenRequest request) {
    Long banksaladUserId = Long.parseLong(request.getBanksaladUserId());
    ConnectOrganizationEntity connectOrganizationEntity = connectOrganizationRepository
        .findByOrganizationId(request.getOrganizationId())
        .orElseThrow(() -> new ConnectException(ConnectErrorType.NOT_FOUND_ORGANIZATION));

    Organization organization = createOrganization(connectOrganizationEntity);
    GetTokenResponse getTokenResponse = externalTokenService
        .issueToken(organization, request.getAuthorizationCode());

    // TODO query 수정부분
    OauthTokenEntity oauthTokenEntity = oauthTokenRepository
        .findByBanksaladUserIdAndOrganizationId(banksaladUserId, request.getOrganizationId())
        .orElse(createOauthTokenEntity(banksaladUserId, request.getOrganizationId()));

    updateFrom(oauthTokenEntity, getTokenResponse, request.getAuthorizationCode());
    oauthTokenRepository.save(oauthTokenEntity);

    return OauthToken.builder()
        .accessToken(oauthTokenEntity.getAccessToken())
        .refreshToken(oauthTokenEntity.getRefreshToken())
        .scopes(getParseScope(oauthTokenEntity.getScope()))
        .build();
  }

  @Override
  @Transactional(readOnly = true)
  public OauthToken getAccessToken(GetAccessTokenRequest request) {
    Long banksaladUserId = Long.parseLong(request.getBanksaladUserId());
    // TODO query 수정부분
    OauthTokenEntity oauthTokenEntity = oauthTokenRepository
        .findByBanksaladUserIdAndOrganizationId(banksaladUserId, request.getOrganizationId())
        .orElseThrow(() -> new ConnectException(ConnectErrorType.NOT_FOUND_TOKEN));

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
  @Transactional
  public OauthToken refreshToken(RefreshTokenRequest request) {
    Long banksaladUserId = Long.parseLong(request.getBanksaladUserId());
    OauthTokenEntity oauthTokenEntity = oauthTokenRepository
        .findByBanksaladUserIdAndOrganizationId(banksaladUserId, request.getOrganizationId())
        .orElseThrow(() -> new ConnectException(ConnectErrorType.NOT_FOUND_TOKEN));

    if (isTokenExpired(oauthTokenEntity.getRefreshTokenExpiresAt())) {
      throw new ConnectException(ConnectErrorType.EXPIRED_TOKEN);
    }

    ConnectOrganizationEntity connectOrganizationEntity = connectOrganizationRepository
        .findByOrganizationId(request.getOrganizationId())
        .orElseThrow(() -> new ConnectException(ConnectErrorType.NOT_FOUND_ORGANIZATION));

    Organization organization = createOrganization(connectOrganizationEntity);
    GetTokenResponse getTokenResponse = externalTokenService
        .refreshToken(organization, oauthTokenEntity.getRefreshToken());

    updateFrom(oauthTokenEntity, getTokenResponse, null);
    oauthTokenRepository.save(oauthTokenEntity);

    return OauthToken.builder()
        .accessToken(oauthTokenEntity.getAccessToken())
        .scopes(getParseScope(oauthTokenEntity.getScope()))
        .build();
  }

  @Override
  public void revokeToken(RevokeTokenRequest request) {
    Long banksaladUserId = Long.parseLong(request.getBanksaladUserId());
    OauthTokenEntity oauthTokenEntity = oauthTokenRepository
        .findByBanksaladUserIdAndOrganizationId(banksaladUserId, request.getOrganizationId())
        .orElseThrow(() -> new ConnectException(ConnectErrorType.NOT_FOUND_TOKEN));
    oauthTokenRepository.delete(oauthTokenEntity);

    ConnectOrganizationEntity connectOrganizationEntity = connectOrganizationRepository
        .findByOrganizationId(request.getOrganizationId())
        .orElseThrow(() -> new ConnectException(ConnectErrorType.NOT_FOUND_ORGANIZATION));

    Organization organization = createOrganization(connectOrganizationEntity);
    externalTokenService.revokeToken(organization, oauthTokenEntity.getAccessToken());
  }

  @Override
  public void revokeAllTokens(RevokeAllTokensRequest request) {
    Long banksaladUserId = Long.parseLong(request.getBanksaladUserId());
    List<OauthTokenEntity> oauthTokenEntities = oauthTokenRepository
        .findAllByBanksaladUserId(banksaladUserId);
    
    for (OauthTokenEntity oauthTokenEntity : oauthTokenEntities) {
      oauthTokenRepository.delete(oauthTokenEntity);
      ConnectOrganizationEntity connectOrganizationEntity = connectOrganizationRepository
          .findByOrganizationId(oauthTokenEntity.getOrganizationId())
          .orElseThrow(() -> new ConnectException(ConnectErrorType.NOT_FOUND_ORGANIZATION));

      Organization organization = createOrganization(connectOrganizationEntity);
      externalTokenService.revokeToken(organization, oauthTokenEntity.getAccessToken());
    }
  }

  private OauthTokenEntity createOauthTokenEntity(Long banksaladUserId, String organizationId) {
    return OauthTokenEntity.builder()
        .banksaladUserId(banksaladUserId)
        .organizationId(organizationId)
        .build();
  }

  private Organization createOrganization(ConnectOrganizationEntity connectOrganizationEntity) {
    return Organization.builder()
        .sector(connectOrganizationEntity.getSector())
        .industry(connectOrganizationEntity.getIndustry())
        .organizationId(connectOrganizationEntity.getOrganizationId())
        .organizationCode(connectOrganizationEntity.getOrgCode())
        .domain("fixme") // fixme
        .build();
  }

  private List<String> getParseScope(String scope) {
    return Arrays.asList(scope.split(" "));
  }

  private void updateFrom(OauthTokenEntity entity, GetTokenResponse response, String authorizationCode) {
    if (authorizationCode != null) {
      entity.setAuthorizationCode(authorizationCode);
    }
    entity.setAccessToken(response.getAccessToken());
    entity.setRefreshToken(response.getRefreshToken());
    entity.setAccessTokenExpiresAt(LocalDateTime.now().plusSeconds(response.getExpiresIn()));
    entity.setAccessTokenExpiresIn(response.getExpiresIn());
    entity.setRefreshTokenExpiresAt(LocalDateTime.now()
        .plusSeconds(response.getRefreshTokenExpiresIn()));
    entity.setRefreshTokenExpiresIn(response.getRefreshTokenExpiresIn());
    entity.setTokenType(response.getTokenType());
    entity.setScope(response.getScope());
  }

  private boolean isTokenExpired(LocalDateTime expirationTime) {
    return expirationTime.isBefore(LocalDateTime.now());
  }
}
