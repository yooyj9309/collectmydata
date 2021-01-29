package com.banksalad.collectmydata.common.exception.collectMydataException;

import com.banksalad.collectmydata.common.exception.dto.ErrorResultResponse;
import com.github.banksalad.idl.apis.external.v1.result.ErrorProto.ClientDisplayError;
import com.github.banksalad.idl.apis.external.v1.result.ErrorProto.ErrorResult.ErrorCode;
import io.grpc.Status.Code;
import lombok.Getter;

@Getter
public class NotFoundTokenException extends CollectMydataException {

  private Code code;
  private String message;
  private ErrorResultResponse errorResultResponse;

  public NotFoundTokenException(String message) {
    this.code = Code.NOT_FOUND;
    this.message = message;
    this.errorResultResponse = buildErrorResultResponse();
  }

  private ErrorResultResponse buildErrorResultResponse() {
    final String ERROR_MESSAGE = "Not Found Token";
    return ErrorResultResponse.builder()
        .errorCode(ErrorCode.NOT_FOUND)
        .message(ERROR_MESSAGE)
        .description(ERROR_MESSAGE)
        .clientDisplayError(ClientDisplayError.getDefaultInstance())
        .build();
  }
}
