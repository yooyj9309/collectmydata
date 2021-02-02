package com.banksalad.collectmydata.common.collect.api;

import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Builder
@Getter
public class ApiResponseEntity {

  private final int httpStatusCode;
  private final Map<String, String> headers;
  private final String body;
}
