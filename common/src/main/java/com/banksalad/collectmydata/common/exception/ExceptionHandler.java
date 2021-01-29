package com.banksalad.collectmydata.common.exception;

import com.banksalad.collectmydata.common.exception.collectMydataException.CollectMydataException;
import com.banksalad.collectmydata.common.exception.collectStatusException.CollectStatusException;
import com.banksalad.collectmydata.common.exception.collectStatusException.ValidatorException;
import com.github.banksalad.idl.apis.external.v1.result.ErrorProto.ClientDisplayError;
import com.github.banksalad.idl.apis.external.v1.result.ErrorProto.ErrorResult;
import com.github.banksalad.idl.apis.external.v1.result.ErrorProto.ErrorResult.ErrorCode;
import com.google.protobuf.Any;
import com.google.rpc.Code;
import com.google.rpc.Status;
import io.grpc.StatusException;
import io.grpc.protobuf.StatusProto;
import javax.validation.ValidationException;

public class ExceptionHandler {

  private static final String UNKNOWN_ERROR = "UNKNOWN ERROR";

  public static StatusException handle(Exception e) {
    if (e instanceof CollectStatusException) {
      Status status = new CollectStatusException((CollectStatusException) e).toStatus();
      return StatusProto.toStatusException(status);
    }
    if (e instanceof CollectMydataException) {
      Status status = new CollectMydataException((CollectMydataException) e).toStatus();
      return StatusProto.toStatusException(status);
    }
    if (e instanceof ValidationException) {
      ValidatorException validatorException = new ValidatorException(e.toString());
      Status status = new CollectStatusException(validatorException).toStatus();
      return StatusProto.toStatusException(status);
    }
    Status status = getDefaultStatus(e);
    return StatusProto.toStatusException(status);
  }

  private static Status getDefaultStatus(Exception e) {
    ErrorResult errorResult = getDefaultErrorResult();
    return Status.newBuilder()
        .setCode(Code.UNKNOWN_VALUE)
        .setMessage(e.toString())
        .addDetails(Any.pack(errorResult))
        .build();
  }

  private static ErrorResult getDefaultErrorResult() {
    return ErrorResult.newBuilder()
        .setCode(ErrorCode.UNKNOWN)
        .setMessage(UNKNOWN_ERROR)
        .setDescription(UNKNOWN_ERROR)
        .setClientDisplayError(ClientDisplayError.getDefaultInstance())
        .build();
  }
}
