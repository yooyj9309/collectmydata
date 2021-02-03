package com.banksalad.collectmydata.connect.token.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.banksalad.collectmydata.common.exception.collectMydataException.NotFoundOrganizationException;
import com.banksalad.collectmydata.common.exception.collectMydataException.NotFoundTokenException;
import com.banksalad.collectmydata.connect.common.db.entity.OauthTokenEntity;
import com.banksalad.collectmydata.connect.common.db.entity.OrganizationInfoEntity;
import com.banksalad.collectmydata.connect.common.db.repository.OauthTokenRepository;
import com.banksalad.collectmydata.connect.common.db.repository.OrganizationInfoRepository;
import com.banksalad.collectmydata.connect.token.dto.ExternalTokenResponse;
import com.banksalad.collectmydata.connect.token.dto.OauthToken;
import com.github.banksalad.idl.apis.v1.connectmydata.ConnectmydataProto.GetAccessTokenRequest;
import com.github.banksalad.idl.apis.v1.connectmydata.ConnectmydataProto.IssueTokenRequest;
import com.github.banksalad.idl.apis.v1.connectmydata.ConnectmydataProto.RefreshTokenRequest;
import com.github.banksalad.idl.apis.v1.connectmydata.ConnectmydataProto.RevokeAllTokensRequest;
import com.github.banksalad.idl.apis.v1.connectmydata.ConnectmydataProto.RevokeTokenRequest;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OauthTokenServiceImpl implements OauthTokenService {

  private final OauthTokenRepository oauthTokenRepository;
  private final OrganizationInfoRepository organizationInfoRepository;
  private final ExternalTokenService externalTokenService;

  @Override
  @Transactional
  public OauthToken issueToken(IssueTokenRequest request) {
    Long banksaladUserId = Long.parseLong(request.getBanksaladUserId());
    OrganizationInfoEntity organizationInfoEntity = organizationInfoRepository
        .findByOrganizationId(request.getOrganizationId()).orElseThrow(NotFoundOrganizationException::new);

    ExternalTokenResponse externalTokenResponse = externalTokenService
        .issueToken(organizationInfoEntity.getOrganizationCode(), request.getAuthorizationCode());

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

    OrganizationInfoEntity organizationInfoEntity = organizationInfoRepository
        .findByOrganizationId(request.getOrganizationId()).orElseThrow(NotFoundOrganizationException::new);
    ExternalTokenResponse externalTokenResponse = externalTokenService
        .refreshToken(organizationInfoEntity.getOrganizationCode(), oauthTokenEntity.getRefreshToken());

    oauthTokenEntity.updateFrom(externalTokenResponse);
    oauthTokenRepository.save(oauthTokenEntity);

    return OauthToken.builder()
        .accessToken(oauthTokenEntity.getAccessToken())
        .scopes(oauthTokenEntity.getParseScope())
        .build();
  }

  @Override
  public void revokeToken(RevokeTokenRequest request) {

    /**
     * TODO
     * 1. 토큰정보 DB에서 제거
     * 2. 기관 인증페이지에 기관 정보 바탕으로 토큰폐기 요청 : webClient
     */
  }

  @Override
  public void revokeAllTokens(RevokeAllTokensRequest request) {
    /**
     * TODO
     * 1. 사용자의 연동되어 있는 모든 토큰정보 DB에서 제거
     * 2. 기관 인증페이지에 기관 정보 바탕으로 토큰폐기 요청 : webClient
     */
  }

  private OauthTokenEntity createOauthTokenEntity(Long banksaladUserId, String organizationId) {
    return OauthTokenEntity.builder()
        .banksaladUserId(banksaladUserId)
        .organizationId(organizationId)
        .build();
  }
}
