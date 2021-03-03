package com.banksalad.collectmydata.connect.common.exception;

import com.banksalad.collectmydata.common.exception.GrpcException;
import com.banksalad.collectmydata.connect.common.enums.ConnectErrorType;
import com.github.banksalad.idl.apis.v1.result.ErrorProto.ClientDisplayError;
import com.github.banksalad.idl.apis.v1.result.ErrorProto.ErrorResult;
import io.grpc.Status.Code;

import java.util.Optional;

public class ConnectException extends GrpcException {

  private static final String UNKNOWN_ERROR = "UNKNOWN ERROR";
  private static final Code UNKNOWN_CODE = Code.UNKNOWN;

  public ConnectException(ConnectErrorType errorType) {
    super(UNKNOWN_CODE, errorType.getMessage(), errorResultAssembler(errorType, null));
  }

  public ConnectException(ConnectErrorType errorType, String description) {
    super(UNKNOWN_CODE, errorType.getMessage(), errorResultAssembler(errorType, description));
  }

  private static ErrorResult errorResultAssembler(ConnectErrorType errorType, String description) {
    return ErrorResult.newBuilder()
        .setCode(errorType.getErrorCode())
        .setMessage(errorType.getMessage())
        .setDescription(Optional.ofNullable(description).orElse(errorType.getMessage()))
        .setClientDisplayError(ClientDisplayError.getDefaultInstance())
        .build();
  }
}
