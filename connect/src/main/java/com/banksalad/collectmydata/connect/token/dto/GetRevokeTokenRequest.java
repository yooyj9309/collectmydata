package com.banksalad.collectmydata.connect.token.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class GetRevokeTokenRequest {

  private String orgCode;
  private String token; // 폐기할 토큰 : tokenTypeHint 미지정시 access token, refresh token 모두 폐기
  private String tokenTypeHint; // 폐기할 토큰 유형 : access token, refresh token
  private String clientId;
  private String clientSecret;
}
