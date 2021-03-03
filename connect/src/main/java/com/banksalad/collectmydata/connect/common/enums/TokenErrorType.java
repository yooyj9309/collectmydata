package com.banksalad.collectmydata.connect.common.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum TokenErrorType {
  INVALID_REQUEST("invalid_request", "grant_type을 제외한 요청 파라미터에 문제가 있는 경우"),
  INVALID_CLIENT("invalid client", "클라이언트 인증이 실패한 경우"),
  INVALID_GRANT("invalid_grant", "인가코드, refresh token이 틀리거나, 유효(만료)하지 않은 경우, redirect_uri가 일치하지 않는 경우"),
  UNAUTHORIZED_CLIENT("unauthorized_client", "클라이언트가 권한이 없는 경우"),
  UNSUPPORTED_GRANT_TYPE("unsupported_grant_type", "grant_type 값이 잘못된 경우"),
  INVALID_SCOPE("invalid_scope", "지정한 scope 값이 잘못된 경우"),
  UNSUPPORTED_TOKEN_TYPE("unsupported_token_type", "접근토큰 폐기 시 token_type_hint 값이 잘못된 경우"),
  UNKNOWN("unknown", "알 수 없는 에러");

  private String error;
  private String description;

  public static TokenErrorType getValidatedError(String error) {
    // TODO : input parameter transform using regex
    for(TokenErrorType tokenErrorType : TokenErrorType.values()) {
      if (tokenErrorType.getError().equals(error.toLowerCase())) {
        return tokenErrorType;
      }
    }
    return UNKNOWN;
  }
}
