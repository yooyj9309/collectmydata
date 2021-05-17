package com.banksalad.collectmydata.connect.token.dto;

import com.banksalad.collectmydata.connect.common.dto.ErrorResponse;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
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
public class GetOauthTokenResponse {

  private String tokenType;
  private String accessToken;
  private Integer expiresIn;
  private String refreshToken;
  private Integer refreshTokenExpiresIn;
  private String scope;

  @JsonUnwrapped
  private ErrorResponse errorResponse; // TODO wooody92 : 처리방식 고민(response 가 성공, 실패에 따라 동적으로 응답이 변함)
}
