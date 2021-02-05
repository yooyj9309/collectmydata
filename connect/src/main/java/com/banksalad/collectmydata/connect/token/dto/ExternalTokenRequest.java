package com.banksalad.collectmydata.connect.token.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ExternalTokenRequest {

  /**
   * TODO
   * 공통 라이브러리 사용하여 개발 시 주석제거 및 필드명 일치시키기
   */
  // org_code
  private String organizationCode;

  // authorization_code 고정값
  private String grantType;

  // code
  private String authorizationCode;

  // token
  private String accessToken;

  // refresh_token
  private String refreshToken;

  // client_id
  private String clientId;

  // client_secret
  private String clientSecret;

  // redirect_uri (callback url, 인가코드 발급 요청 시 요청했던 Callback URL과 동일해야 함)
  private String redirectUrl;
}
