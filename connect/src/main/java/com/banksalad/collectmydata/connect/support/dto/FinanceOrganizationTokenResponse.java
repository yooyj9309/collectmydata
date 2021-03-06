package com.banksalad.collectmydata.connect.support.dto;

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
public class FinanceOrganizationTokenResponse {

  private String tokenType; // 접근토큰 유형
  private String accessToken; // 발급된 접근토큰
  private int expiresIn; // 접근토큰 유효기간(단위: 초)
  private String scope; // 고정값 리턴 manage

  @JsonUnwrapped
  private ErrorResponse errorResponse;
}
