package com.banksalad.collectmydata.collect.common.config;

import com.banksalad.collectmydata.collect.grpc.handler.CollectmydataCollectGrpcService;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.linecorp.armeria.common.grpc.GrpcSerializationFormats;
import com.linecorp.armeria.server.docs.DocService;
import com.linecorp.armeria.server.grpc.GrpcService;
import com.linecorp.armeria.server.healthcheck.HealthCheckService;
import com.linecorp.armeria.server.logging.AccessLogWriter;
import com.linecorp.armeria.server.logging.LoggingService;
import com.linecorp.armeria.spring.ArmeriaServerConfigurator;

@Configuration
public class ArmeriaConfig {

  private final CollectmydataCollectGrpcService collectmydataCollectGrpcService;

  public ArmeriaConfig(CollectmydataCollectGrpcService collectmydataCollectGrpcService) {
    this.collectmydataCollectGrpcService = collectmydataCollectGrpcService;
  }

  @Bean
  public ArmeriaServerConfigurator armeriaServerConfigurator() {
    return serverBuilder -> {

      serverBuilder.decorator(LoggingService.newDecorator());
      serverBuilder.accessLogWriter(AccessLogWriter.combined(), false);

      serverBuilder.service(
          GrpcService.builder()
              .addService(collectmydataCollectGrpcService)
              .supportedSerializationFormats(GrpcSerializationFormats.values())
              .enableUnframedRequests(true)
              .build());

      serverBuilder.service("/health", HealthCheckService.builder().build());
      serverBuilder.serviceUnder("/docs", new DocService());
    };
  }
}
