package com.banksalad.collectmydata.common.exception;

import com.github.banksalad.idl.apis.v1.result.ErrorProto.ClientDisplayError;
import com.github.banksalad.idl.apis.v1.result.ErrorProto.ErrorResult;
import com.github.banksalad.idl.apis.v1.result.ErrorProto.ErrorResult.ErrorCode;
import com.google.protobuf.Any;
import com.google.rpc.Status;
import io.grpc.Status.Code;
import io.grpc.StatusException;
import io.grpc.protobuf.StatusProto;

public class GrpcException extends CollectRuntimeException {

  private static final String UNKNOWN_ERROR = "UNKNOWN ERROR";

  private Code code = Code.UNKNOWN;
  private String message = UNKNOWN_ERROR;
  private ErrorResult errorResult;

  public GrpcException() {
    super(UNKNOWN_ERROR);
    this.errorResult = defaultErrorResultAssembler();
  }

  public GrpcException(Code code, String message, ErrorResult errorResult) {
    super(message);
    this.code = code;
    this.message = message;
    this.errorResult = errorResult;
  }

  private ErrorResult defaultErrorResultAssembler() {
    return ErrorResult.newBuilder()
        .setCode(ErrorCode.UNKNOWN)
        .setMessage(UNKNOWN_ERROR)
        .setDescription(UNKNOWN_ERROR)
        .setClientDisplayError(ClientDisplayError.getDefaultInstance())
        .build();
  }

  public StatusException handle() {
    Status status = Status.newBuilder()
        .setCode(code.value())
        .setMessage(message)
        .addDetails(Any.pack(errorResult))
        .build();

    return StatusProto.toStatusException(status);
  }
}
