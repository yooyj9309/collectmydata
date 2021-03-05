package com.banksalad.collectmydata.common.util;

import com.banksalad.collectmydata.common.collect.execution.ExecutionRequest;

import java.util.Map;

public class ExecutionUtil {

  public static <T> ExecutionRequest<T> assembleExecutionRequest(Map<String, String> header, Object request) {
    return ExecutionRequest.<T>builder()
        .headers(header)
        .request((T) request)
        .build();
  }

  public static <T> ExecutionRequest<T> assembleExecutionRequest(Object request) {
    return ExecutionRequest.<T>builder()
        .request((T) request)
        .build();
  }
}
