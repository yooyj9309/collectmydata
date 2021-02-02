package com.banksalad.collectmydata.common.collect.execution;

import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Builder
@Getter
public class ExecutionRequest<T> {

  private Map<String, String> headers;
  private T request;
}
