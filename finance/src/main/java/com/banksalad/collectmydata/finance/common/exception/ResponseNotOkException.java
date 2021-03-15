package com.banksalad.collectmydata.finance.common.exception;

import lombok.Getter;

@Getter
public class ResponseNotOkException extends Exception {

  private int statusCode;
  private String responseCode;
  private String responseMessage;

  public ResponseNotOkException(int statusCode, String responseCode, String responseMessage) {
    this.statusCode = statusCode;
    this.responseCode = responseCode;
    this.responseMessage = responseMessage;
  }
}
