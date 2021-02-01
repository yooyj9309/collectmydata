package com.banksalad.collectmydata.connect.token.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.banksalad.collectmydata.common.exception.collectMydataException.NotFoundTokenException;
import com.banksalad.collectmydata.connect.common.db.entity.OauthTokenEntity;
import com.banksalad.collectmydata.connect.common.db.repository.OauthTokenRepository;
import com.banksalad.collectmydata.connect.common.db.repository.OrganizationInfoRepository;
import com.banksalad.collectmydata.connect.token.dto.OauthToken;
import com.github.banksalad.idl.apis.v1.connectmydata.ConnectmydataProto.GetAccessTokenRequest;
import com.github.banksalad.idl.apis.v1.connectmydata.ConnectmydataProto.IssueTokenRequest;
import com.github.banksalad.idl.apis.v1.connectmydata.ConnectmydataProto.RefreshTokenRequest;
import com.github.banksalad.idl.apis.v1.connectmydata.ConnectmydataProto.RevokeAllTokensRequest;
import com.github.banksalad.idl.apis.v1.connectmydata.ConnectmydataProto.RevokeTokenRequest;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OauthTokenService {

  private final OauthTokenRepository oauthTokenRepository;
  private final OrganizationInfoRepository organizationInfoRepository;

  @Transactional
  public OauthToken issueToken(IssueTokenRequest request) {

    /**
     * TODO
     * 0. organization으로 기관 정보 조회
     * 1. 기관 인증페이지에 authorization code 및 기관 정보 바탕으로 토큰발행 요청 : webClient
     * 2. 발행받은 토큰정보 DB 저장
     * 3. DTO 생성 후 필요한 정보만 가공하여 응답
     */
    String accessToken = "access_token_ok";
    String refreshToken = "refresh_token_ok";
    List<String> scopes = new ArrayList<>();

    return OauthToken.builder()
        .accessToken(accessToken)
        .refreshToken(refreshToken)
        .scopes(scopes)
        .build();
  }

  @Transactional(readOnly = true)
  public OauthToken getAccessToken(GetAccessTokenRequest request) {
    Long banksaladUserId = Long.parseLong(request.getBanksaladUserId());
    OauthTokenEntity oauthTokenEntity = oauthTokenRepository
        .findByBanksaladUserIdAndOrganizationIdAndIsExpired(banksaladUserId, request.getOrganizationId(), false)
        .orElseThrow(() -> new NotFoundTokenException("Not Found Token"));

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

  @Transactional
  public OauthToken refreshToken(RefreshTokenRequest request) {

    /**
     * TODO
     * 1. DB를 조회하고 refresh token 만료시간 확인 후 유효하지 않다면 예외처리 진행
     * 2. 기관 인증페이지에 기관 정보 바탕으로 토큰갱신 요청 : webClient
     * 3. 갱신받은 토큰정보 DB 저장
     * 4. DTO 생성 후 필요한 정보만 가공하여 응답
     */
    String accessToken = "access_token_ok3";
    List<String> scopes = new ArrayList<>();

    return OauthToken.builder()
        .accessToken(accessToken)
        .scopes(scopes)
        .build();
  }

  public void revokeToken(RevokeTokenRequest request) {

    /**
     * TODO
     * 1. 토큰정보 DB에서 제거
     * 2. 기관 인증페이지에 기관 정보 바탕으로 토큰폐기 요청 : webClient
     */
  }

  public void revokeAllTokens(RevokeAllTokensRequest request) {
    /**
     * TODO
     * 1. 사용자의 연동되어 있는 모든 토큰정보 DB에서 제거
     * 2. 기관 인증페이지에 기관 정보 바탕으로 토큰폐기 요청 : webClient
     */
  }
}
