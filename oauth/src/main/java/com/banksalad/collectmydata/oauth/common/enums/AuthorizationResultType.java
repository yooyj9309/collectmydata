package com.banksalad.collectmydata.oauth.common.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum AuthorizationResultType {
  SUCCESS("success", "성공, 현재 성공의 경우 데이터가 없어서 가안."), // TODO 성공인경우에도 필수값이라 우선 가안으로 적용.
  INVALID_REQUESET("invalid_request", "client_id 및 redirect_uri가 유효하지만, 요청 파라미터에 문제가 있는 경우"),
  UNAUTHORIZED_CLIENT("unauthorized_client", "클라이언트가 권한이 없는 경우(허용되지 않은 원격지 IP 등)"),
  ACCESS_DENIED("access_denied", "정보주체가 요청을 거부한 경우(전송요구 취소 등)"),
  UNSUPPORTED_RESPONSE_TYPE("unsupported_response_type", "response_type이 'code'가 아닌 경우"),
  INVALID_SCOPE("invalid_scope", "지정한 scope 값이 잘못된 경우"),
  SERVER_ERROR("server_error", "Internal Server Error"),
  TEMPORARILY_UNAVAILABLE("temporarily_unavailable", "서버가 일시적인 부하 등으로 서비스가 불가한 경우"),
  UNKNOWN("unknown_error", "알 수 없는 오류입니다.");

  private String error;
  private String description;

  private static Map<String, AuthorizationResultType> authorizationErrorCode;

  static {
    Map<String, AuthorizationResultType> map = new HashMap<>();
    for (AuthorizationResultType authorizationResultType : AuthorizationResultType.values()) {
      String key = authorizationResultType.getError().replaceAll("([\\p{Z}_-]*)", "").toLowerCase();
      map.put(key, authorizationResultType);
    }
    authorizationErrorCode = Collections.unmodifiableMap(map);
  }

  public static AuthorizationResultType getAuthorizationResultCode(String error) {
    String key = error.replaceAll("([\\p{Z}_-]*)", "").toLowerCase();
    if (authorizationErrorCode.containsKey(key)) {
      return authorizationErrorCode.get(key);
    }
    return UNKNOWN;
  }
}
