package com.banksalad.collectmydata.common.exception.collectStatusException;

import com.banksalad.collectmydata.common.exception.dto.ErrorResultResponse;
import com.github.banksalad.idl.apis.external.v1.result.ErrorProto.ClientDisplayError;
import com.github.banksalad.idl.apis.external.v1.result.ErrorProto.ErrorResult.ErrorCode;
import io.grpc.Status.Code;
import lombok.Getter;

@Getter
public class ValidatorException extends CollectStatusException {

  private Code code;
  private String message;
  private ErrorResultResponse errorResultResponse;

  public ValidatorException(String message) {
    this.code = Code.INVALID_ARGUMENT;
    this.message = message;
    this.errorResultResponse = buildErrorResultResponse();
  }

  private ErrorResultResponse buildErrorResultResponse() {
    final String ERROR_MESSAGE = "Invalid Data Input";
    return ErrorResultResponse.builder()
        .errorCode(ErrorCode.BAD_REQUEST)
        .message(ERROR_MESSAGE)
        .description(ERROR_MESSAGE)
        .clientDisplayError(ClientDisplayError.getDefaultInstance())
        .build();
  }
}
