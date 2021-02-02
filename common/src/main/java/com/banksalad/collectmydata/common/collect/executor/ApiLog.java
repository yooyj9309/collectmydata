package com.banksalad.collectmydata.common.collect.executor;


import com.banksalad.collectmydata.common.collect.api.Api;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ApiLog {

  private final String id;

  private final Api api;

  private final Request request;
  private final Response response;

  @Getter
  @Builder
  public static class Request {

    private final String header;
    private final String body;

    private final String transformedHeader;
    private final String transformedBody;
  }

  @Getter
  @Builder
  public static class Response {

    private final String responseCode;

    private final String header;
    private final String body;

    private final String transformedHeader;
    private final String transformedBody;
  }
}


