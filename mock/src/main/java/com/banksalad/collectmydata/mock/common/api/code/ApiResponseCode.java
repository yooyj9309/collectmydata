package com.banksalad.collectmydata.mock.common.api.code;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ApiResponseCode {

  SUCCESS(HttpStatus.OK, "00000", "성공", "처리성공 "),

  INVALID_REQUEST_PARAMETER(HttpStatus.BAD_REQUEST, "40001", "잘못된 파라미터",
      "요청 파마미터에 문제가 있는 경우"),
  MISSING_HEADER_PARAMETER(HttpStatus.BAD_REQUEST, "40002", "헤더값 없음",
      "헤더 값 미존재"),

  INVALID_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "40101", "잘못된 토큰",
      "유효하지 않은 접근토큰"),
  NOT_ALLOWED_REMOTE_ADDRESS(HttpStatus.UNAUTHORIZED, "40102", "미허용 IP 요청",
      "허용되지 않은 접근토큰"),
  DISCARDED_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "40103", "폐기된 토큰",
      "접근토큰이 폐기된 상태"),
  UNAUTHORIZED_API(HttpStatus.UNAUTHORIZED, "40104", "해당 API 권한 없음",
      "API 사용 권한 없음(불충분한 scope 등)"),
  UNAUTHORIZED_ASSETS(HttpStatus.UNAUTHORIZED, "40105", "해당 자산 권한 없음",
      "자산(계좌, 카드 등)에 대한 정보조회 권한 없음"),

  INVALID_API_CALL(HttpStatus.FORBIDDEN, "40301", "잘못된 API 요청",
      "올바르지 않은 API 호출"),
  TEMPORARILY_LIMIT_CLIENT_REQUEST(HttpStatus.FORBIDDEN, "40302", "클라이언트 일시적 요청 제한",
      "일시적으로 해당 클라이언트의 요청이 제한됨"),
  UNRECOGNIZED_ORGANIZATION_CODE(HttpStatus.FORBIDDEN, "40303", "기관코드 확인 불가",
      "기관코드 확인 불가"),
  REQUEST_EXCEEDS_MAX_RETENTION_DATA(HttpStatus.FORBIDDEN, "40304", "최대 보존기간 초과 데이터",
      "최대 보존기간을 초과한 데이터 요청"),
  INVALID_ASSETS(HttpStatus.FORBIDDEN, "40305", "잘못된 자산 요청",
      "자산이 유효한 상태가 아님(카드분실, 해지 등 자산의 현 상태가 정상이아닌 경우)"),

  NOT_FOUND(HttpStatus.NOT_FOUND, "40401", "미존재 URL",
      "요청한 엔드포인트는 존재하지 않음(일반적인 HTTP 404 에러)"),
  NOT_FOUND_ASSETS(HttpStatus.NOT_FOUND, "40402", "미존재 자산",
      "요청한 정보(예:자산, 기관정보, 전송요 구내역 등)에 대한 정보는 존재하지 않음"),
  NOT_FOUND_USER(HttpStatus.NOT_FOUND, "40403", "미존재 고객",
      "정보주체(고객) 미존재"),
  TERMINATED_ASSETS(HttpStatus.NOT_FOUND, "40404", "해지 자산",
      "해지된 자산 (전송요구 당시에는 ‘해지’ 상태가 아니었으나, 정보 요청 시 ‘해지’된 상태인 경우)"),

  REQUEST_EXCEEDS_MAX_INFORMATION_REQUEST(HttpStatus.TOO_MANY_REQUESTS, "42901",
      "정보제공 요청한도 초과", "정보제공 요청한도 초과"),
  TOO_MANY_REQUESTS(HttpStatus.TOO_MANY_REQUESTS, "42902", "클라이언트 일시적 요청 제한",
      "일시적으로 해당 클라이언트의 요청이 제한됨"),

  INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "50001", "시스템장애",
      "시스템장애"),
  API_REQUEST_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "50002", "API 요청 처리 실패",
      "API 요청 처리 실패"),
  TIMEOUT_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "50003", "처리시간 초과",
      "처리시간 초과 에러"),
  UNKNOWN_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "50004", "알 수 없는 에러",
      "알 수 없는 에러(예비 배정)"),
  ;

  private final HttpStatus httpStatus;
  private final String responseCode;
  private final String defaultResponseMessage;
  private final String description;

}
