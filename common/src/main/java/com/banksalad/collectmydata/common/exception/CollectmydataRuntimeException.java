package com.banksalad.collectmydata.common.exception;

public class CollectmydataRuntimeException extends RuntimeException {

  public CollectmydataRuntimeException(String message) {
    super(message);
  }

  public CollectmydataRuntimeException(String message, Throwable cause) {
    super(message, cause);
  }
}
