package com.banksalad.collectmydata.common.collect.execution;

import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Builder
@Getter
public class ExecutionResponse<T> {

  private final int httpStatusCode;

  private final Map<String, String> headers;
  private final T response;

  private final String nextPage;
}
