package com.banksalad.collectmydata.common.exception.dto;

import com.github.banksalad.idl.apis.external.v1.result.ErrorProto.ClientDisplayError;
import com.github.banksalad.idl.apis.external.v1.result.ErrorProto.ErrorResult;
import com.github.banksalad.idl.apis.external.v1.result.ErrorProto.ErrorResult.ErrorCode;
import lombok.Builder;

@Builder
public class ErrorResultResponse {

  private ErrorCode errorCode;
  private String message;
  private String description;
  private ClientDisplayError clientDisplayError;

  public ErrorResult toErrorResult() {
    return ErrorResult.newBuilder()
        .setCode(errorCode)
        .setMessage(message)
        .setDescription(description)
        .setClientDisplayError(clientDisplayError)
        .build();
  }
}
