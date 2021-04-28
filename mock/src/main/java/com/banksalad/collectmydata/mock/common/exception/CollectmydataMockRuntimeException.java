package com.banksalad.collectmydata.mock.common.exception;

import com.banksalad.collectmydata.mock.common.exception.code.CollectmydataMockExceptionCode;
import lombok.Getter;

@Getter
public class CollectmydataMockRuntimeException extends RuntimeException {

  private CollectmydataMockExceptionCode collectmydataMockExceptionCode;
  private String exceptionMessage;

  public CollectmydataMockRuntimeException(CollectmydataMockExceptionCode collectmydataMockRuntimeExceptionCode) {
    this(collectmydataMockRuntimeExceptionCode, collectmydataMockRuntimeExceptionCode.getDefaultMessage());
  }

  public CollectmydataMockRuntimeException(CollectmydataMockExceptionCode collectmydataMockExceptionCode,
      String exceptionMessage) {
    super(collectmydataMockExceptionCode.name() + " - " + exceptionMessage);
    this.collectmydataMockExceptionCode = collectmydataMockExceptionCode;
    this.exceptionMessage = exceptionMessage;
  }

}
