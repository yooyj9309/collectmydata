package com.banksalad.collectmydata.mock.common.exception.code;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum CollectmydataMockExceptionCode {

  NOT_FOUND_ASSETS("요청 자산이 없습니다."),
  INVALID_PARAMETER_TYPE("잘못된 파라미터 유형입니다.");

  private final String defaultMessage;
}
