package com.banksalad.collectmydata.connect.token.service;

import org.springframework.stereotype.Service;

import com.banksalad.collectmydata.connect.token.dto.ExternalTokenRequest;
import com.banksalad.collectmydata.connect.token.dto.ExternalTokenResponse;

@Service
public class ExternalTokenServiceImpl implements ExternalTokenService {

  @Override
  public ExternalTokenResponse issueToken(String organizationCode, String authorizationCode) {
    /**
     * NOTE
     * 기관 인증페이지에 authorization_code 및 기관정보 바탕으로 토큰발행 요청 -> 공통 라이브러리 사용하여 조회하도록 로직 추가
     *
     * ExternalTokenRequest
     * - client_id, client_secret : 연동기관 클라이언트 DB 조회
     * - redirect_url : 뱅크샐러드 고정 url로 보여짐(properties 조회)
     *
     * ExternalTokenResponse
     * - 기관에 토큰발행 요청 후 응답값
     */
    ExternalTokenRequest externalTokenRequest = ExternalTokenRequest.builder()
        .organizationCode(organizationCode)
        .grantType("authorization_code")
        .authorizationCode(authorizationCode)
        .clientId("client_id_form_DB") // fixme
        .clientSecret("client_secret_from_DB") // fixme
        .redirectUrl("redirect_url_from_properties") // fixme
        .build();

    // TODO : issue token logic using collect library

    return ExternalTokenResponse.builder()
        .tokenType("Bearer")
        .accessToken("received_access_token")
        .accessTokenExpiresIn(90 * 3600)
        .refreshToken("received_refresh_token")
        .refreshTokenExpiresIn(365 * 3600)
        .scope("received_scope1 received_scope2")
        .build();
  }

  @Override
  public ExternalTokenResponse refreshToken(String organizationCode, String refreshToken) {
    ExternalTokenRequest externalTokenRequest = ExternalTokenRequest.builder()
        .organizationCode(organizationCode)
        .grantType("refresh_token")
        .refreshToken(refreshToken)
        .clientId("client_id_form_DB") // fixme
        .clientSecret("client_secret_from_DB") // fixme
        .build();

    // TODO : refresh token logic using collect library

    return ExternalTokenResponse.builder()
        .tokenType("Bearer")
        .accessToken("received_access_token")
        .accessTokenExpiresIn(90 * 3600)
        .refreshToken("received_refresh_token")
        .refreshTokenExpiresIn(365 * 3600)
        .scope("received_scope1 received_scope2")
        .build();
  }
}
