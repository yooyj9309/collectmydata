package com.banksalad.collectmydata.bank.common.config;

import com.banksalad.collectmydata.bank.grpc.handler.CollectmydataBankGrpcService;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.linecorp.armeria.common.grpc.GrpcSerializationFormats;
import com.linecorp.armeria.server.docs.DocService;
import com.linecorp.armeria.server.grpc.GrpcService;
import com.linecorp.armeria.server.logging.AccessLogWriter;
import com.linecorp.armeria.server.logging.LoggingService;
import com.linecorp.armeria.spring.ArmeriaServerConfigurator;

@Configuration
public class ArmeriaConfiguration {

  public ArmeriaConfiguration(CollectmydataBankGrpcService collectmydataBankGrpcService) {
    this.collectmydataBankGrpcService = collectmydataBankGrpcService;
  }

  private final CollectmydataBankGrpcService collectmydataBankGrpcService;

  @Bean
  public ArmeriaServerConfigurator armeriaServerConfigurator() {
    return serverBuilder -> {

      serverBuilder.decorator(LoggingService.newDecorator());
      serverBuilder.accessLogWriter(AccessLogWriter.combined(), false);

      serverBuilder.service(
          GrpcService.builder()
              .addService(collectmydataBankGrpcService)
              .supportedSerializationFormats(GrpcSerializationFormats.values())
              .enableUnframedRequests(true)
              .build());

      serverBuilder.serviceUnder("/docs", new DocService());
    };
  }
}
