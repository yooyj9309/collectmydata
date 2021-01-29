package com.banksalad.collectmydata.common.exception.collectMydataException;

import com.banksalad.collectmydata.common.exception.dto.ErrorResultResponse;
import com.github.banksalad.idl.apis.external.v1.result.ErrorProto.ClientDisplayError;
import com.github.banksalad.idl.apis.external.v1.result.ErrorProto.ErrorResult;
import com.github.banksalad.idl.apis.external.v1.result.ErrorProto.ErrorResult.ErrorCode;
import com.google.protobuf.Any;
import com.google.rpc.Status;
import io.grpc.Status.Code;
import lombok.Getter;

@Getter
public class CollectMydataException extends RuntimeException {

  private Code code;
  private String message;
  private ErrorResultResponse errorResultResponse;

  public CollectMydataException() {
    this.code = Code.UNKNOWN;
    this.message = "Unknown Error: CollectMydataException";
    this.errorResultResponse = buildErrorResultResponse();
  }

  public CollectMydataException(CollectMydataException e) {
    this.code = e.getCode();
    this.message = e.getMessage();
    this.errorResultResponse = e.getErrorResultResponse();
  }

  public Status toStatus() {
    ErrorResult errorResult = errorResultResponse.toErrorResult();
    return Status.newBuilder()
        .setCode(code.value())
        .setMessage(message)
        .addDetails(Any.pack(errorResult))
        .build();
  }

  private ErrorResultResponse buildErrorResultResponse() {
    final String ERROR_MESSAGE = "Unknown Error: CollectMydataException";
    return ErrorResultResponse.builder()
        .errorCode(ErrorCode.UNKNOWN)
        .message(ERROR_MESSAGE)
        .description(ERROR_MESSAGE)
        .clientDisplayError(ClientDisplayError.getDefaultInstance())
        .build();
  }
}
