package com.banksalad.collectmydata.common.logging;

import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import io.grpc.Context;

import java.util.Optional;

public class GrpcRequestIdConverter extends ClassicConverter {

  private static final String REQUEST_HEADER_NAME = "banksalad-request-id";
  private Context.Key<String> REQUEST_ID_CTX_KEY = Context.key(REQUEST_HEADER_NAME);

  @Override
  public String convert(ILoggingEvent event) {
    return Optional.ofNullable(REQUEST_ID_CTX_KEY.get()).orElse("NO_REQUEST");
  }
}
