package com.banksalad.collectmydata.connect.token.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.banksalad.collectmydata.common.exception.collectMydataException.NotFoundOrganizationException;
import com.banksalad.collectmydata.common.exception.collectMydataException.NotFoundTokenException;
import com.banksalad.collectmydata.connect.common.db.entity.OauthTokenEntity;
import com.banksalad.collectmydata.connect.common.db.entity.ConnectOrganizationEntity;
import com.banksalad.collectmydata.connect.common.db.repository.OauthTokenRepository;
import com.banksalad.collectmydata.connect.common.db.repository.ConnectOrganizationRepository;
import com.banksalad.collectmydata.connect.organization.dto.Organization;
import com.banksalad.collectmydata.connect.token.dto.ExternalTokenResponse;
import com.banksalad.collectmydata.connect.token.dto.OauthToken;
import com.github.banksalad.idl.apis.v1.connectmydata.ConnectmydataProto.GetAccessTokenRequest;
import com.github.banksalad.idl.apis.v1.connectmydata.ConnectmydataProto.IssueTokenRequest;
import com.github.banksalad.idl.apis.v1.connectmydata.ConnectmydataProto.RefreshTokenRequest;
import com.github.banksalad.idl.apis.v1.connectmydata.ConnectmydataProto.RevokeAllTokensRequest;
import com.github.banksalad.idl.apis.v1.connectmydata.ConnectmydataProto.RevokeTokenRequest;
import lombok.RequiredArgsConstructor;

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
        .findByOrganizationId(request.getOrganizationId()).orElseThrow(NotFoundOrganizationException::new);

    Organization organization = createOrganization(connectOrganizationEntity);
    ExternalTokenResponse externalTokenResponse = externalTokenService
        .issueToken(organization, request.getAuthorizationCode());

    OauthTokenEntity oauthTokenEntity = oauthTokenRepository
        .findByBanksaladUserIdAndOrganizationIdAndIsExpired(banksaladUserId, request.getOrganizationId(), false)
        .orElse(createOauthTokenEntity(banksaladUserId, request.getOrganizationId()));
    oauthTokenEntity.updateFrom(request.getAuthorizationCode(), externalTokenResponse);
    oauthTokenRepository.save(oauthTokenEntity);

    return OauthToken.builder()
        .accessToken(oauthTokenEntity.getAccessToken())
        .refreshToken(oauthTokenEntity.getRefreshToken())
        .scopes(oauthTokenEntity.getParseScope())
        .build();
  }

  @Override
  @Transactional(readOnly = true)
  public OauthToken getAccessToken(GetAccessTokenRequest request) {
    Long banksaladUserId = Long.parseLong(request.getBanksaladUserId());
    OauthTokenEntity oauthTokenEntity = oauthTokenRepository
        .findByBanksaladUserIdAndOrganizationIdAndIsExpired(banksaladUserId, request.getOrganizationId(), false)
        .orElseThrow(NotFoundTokenException::new);

    if (oauthTokenEntity.isAccessTokenExpired()) {
      RefreshTokenRequest refreshTokenRequest = RefreshTokenRequest.newBuilder()
          .setBanksaladUserId(request.getBanksaladUserId())
          .setOrganizationId(request.getOrganizationId())
          .build();
      return refreshToken(refreshTokenRequest);
    }

    return OauthToken.builder()
        .accessToken(oauthTokenEntity.getAccessToken())
        .scopes(oauthTokenEntity.getParseScope())
        .build();
  }

  @Override
  @Transactional
  public OauthToken refreshToken(RefreshTokenRequest request) {
    Long banksaladUserId = Long.parseLong(request.getBanksaladUserId());
    OauthTokenEntity oauthTokenEntity = oauthTokenRepository
        .findByBanksaladUserIdAndOrganizationIdAndIsExpired(banksaladUserId, request.getOrganizationId(), false)
        .orElseThrow(NotFoundTokenException::new);

    if (oauthTokenEntity.isRefreshTokenExpired()) {
      throw new NotFoundTokenException("Expired refresh Token");
    }

    ConnectOrganizationEntity connectOrganizationEntity = connectOrganizationRepository
        .findByOrganizationId(request.getOrganizationId()).orElseThrow(NotFoundOrganizationException::new);
    Organization organization = createOrganization(connectOrganizationEntity);
    ExternalTokenResponse externalTokenResponse = externalTokenService
        .refreshToken(organization, oauthTokenEntity.getRefreshToken());

    oauthTokenEntity.updateFrom(externalTokenResponse);
    oauthTokenRepository.save(oauthTokenEntity);

    return OauthToken.builder()
        .accessToken(oauthTokenEntity.getAccessToken())
        .scopes(oauthTokenEntity.getParseScope())
        .build();
  }

  @Override
  public void revokeToken(RevokeTokenRequest request) {
    Long banksaladUserId = Long.parseLong(request.getBanksaladUserId());
    OauthTokenEntity oauthTokenEntity = oauthTokenRepository
        .findByBanksaladUserIdAndOrganizationIdAndIsExpired(banksaladUserId, request.getOrganizationId(), false)
        .orElseThrow(NotFoundTokenException::new);
    oauthTokenRepository.delete(oauthTokenEntity);

    ConnectOrganizationEntity connectOrganizationEntity = connectOrganizationRepository
        .findByOrganizationId(request.getOrganizationId()).orElseThrow(NotFoundOrganizationException::new);
    Organization organization = createOrganization(connectOrganizationEntity);
    externalTokenService.revokeToken(organization, oauthTokenEntity.getAccessToken());
  }

  @Override
  public void revokeAllTokens(RevokeAllTokensRequest request) {
    Long banksaladUserId = Long.parseLong(request.getBanksaladUserId());
    List<OauthTokenEntity> oauthTokenEntities = oauthTokenRepository
        .findAllByBanksaladUserIdAndIsExpired(banksaladUserId, false)
        .orElseThrow(NotFoundTokenException::new);

    for (OauthTokenEntity oauthTokenEntity : oauthTokenEntities) {
      oauthTokenRepository.delete(oauthTokenEntity);
      ConnectOrganizationEntity connectOrganizationEntity = connectOrganizationRepository
          .findByOrganizationId(oauthTokenEntity.getOrganizationId()).orElseThrow(NotFoundOrganizationException::new);
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
        .organizationCode(connectOrganizationEntity.getRelayOrgCode())
        .domain(connectOrganizationEntity.getDomain())
        .build();
  }
}
