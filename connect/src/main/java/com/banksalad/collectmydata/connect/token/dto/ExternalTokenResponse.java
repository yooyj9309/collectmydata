package com.banksalad.collectmydata.connect.token.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ExternalTokenResponse {

  /**
   * TODO
   * 공통 라이브러리 사용하여 개발 시 주석제거 및 필드명 일치시키기
   */
  // token_type (Bearer 고정값)
  private String tokenType;

  // access_token
  private String accessToken;

  // expires_in
  private Integer accessTokenExpiresIn;

  // refresh_token
  private String refreshToken;

  // refresh_token_expires_in
  private Integer refreshTokenExpiresIn;

  // scope
  private String scope;
}
