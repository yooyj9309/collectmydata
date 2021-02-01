package com.banksalad.collectmydata.connect.support.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class FinanceOrganizationTokenResponse {

  private String tokenType; // 접근토큰 유형
  private String accessToken; // 발급된 접근토큰
  private Long expiresIn; // 접근토큰 유효기간(단위: 초)
  private String scope; // 고정값 리턴 manage

  private String error; // 에러코드 TODO.. 공통 으로 뺴는게 맞을지 검토
  private String errorDescription; // 에러메시지
}
