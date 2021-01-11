package com.banksalad.collectmydata.common.exception;

public class CollectRuntimeException extends RuntimeException {

  public CollectRuntimeException(String message) {
    super(message);
  }

  public CollectRuntimeException(String message, Throwable cause) {
    super(message, cause);
  }
}
