package com.banksalad.collectmydata.connect.common.enums;

import com.github.banksalad.idl.apis.v1.result.ErrorProto.ErrorResult.ErrorCode;
import lombok.Getter;

@Getter
public enum ConnectErrorType {
  UNKNOWN(ErrorCode.UNKNOWN, "UNKNOWN"),
  EXPIRED_TOKEN(ErrorCode.UNKNOWN, "EXPIRED_TOKEN"),
  NOT_FOUND_TOKEN(ErrorCode.UNKNOWN, "NOT_FOUND_TOKEN"),
  NOT_FOUND_ORGANIZATION(ErrorCode.UNKNOWN, "NOT_FOUND_ORGANIZATION"),
  INVALID_TOKEN(ErrorCode.UNKNOWN, "INVALID_TOKEN"),
  INVALID_PARAMETER(ErrorCode.UNKNOWN, "INVALID_PARAMETER");

  //TODO 현재 ErrorCode가 전체 UNKNOWN으로 되어있으나, 추후 idl에서 에러 정리 협의후 변경.

  private ErrorCode errorCode;
  private String message;

  ConnectErrorType(ErrorCode errorCode, String message) {
    this.errorCode = errorCode;
    this.message = message;
  }
}
