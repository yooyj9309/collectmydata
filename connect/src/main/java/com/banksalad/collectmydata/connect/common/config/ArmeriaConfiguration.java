package com.banksalad.collectmydata.connect.common.config;

import com.banksalad.collectmydata.connect.grpc.handler.ConnectGrpcService;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.banksalad.idl.apis.v1.connectmydata.ConnectmydataGrpc;
import com.linecorp.armeria.common.grpc.GrpcSerializationFormats;
import com.linecorp.armeria.server.docs.DocService;
import com.linecorp.armeria.server.grpc.GrpcService;
import com.linecorp.armeria.server.healthcheck.HealthCheckService;
import com.linecorp.armeria.server.logging.AccessLogWriter;
import com.linecorp.armeria.server.logging.LoggingService;
import com.linecorp.armeria.spring.ArmeriaServerConfigurator;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class ArmeriaConfiguration {

  private final ConnectGrpcService connectGrpcService;

  @Bean
  public ArmeriaServerConfigurator armeriaServerConfigurator() {
    return serverBuilder -> {
      serverBuilder.decorator(LoggingService.newDecorator());
      serverBuilder.accessLogWriter(AccessLogWriter.combined(), false);

      serverBuilder.service(
          GrpcService.builder()
              .addService(connectGrpcService)
              .supportedSerializationFormats(GrpcSerializationFormats.values())
              .enableUnframedRequests(true)
              .build());

      serverBuilder.service("/health",HealthCheckService.builder().build());
      serverBuilder.serviceUnder("/docs", new DocService());
    };
  }
}
