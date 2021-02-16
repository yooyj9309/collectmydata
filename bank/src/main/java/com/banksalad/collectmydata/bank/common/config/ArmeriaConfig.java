package com.banksalad.collectmydata.bank.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.banksalad.collectmydata.bank.grpc.handler.CollectmydataBankGrpcService;
import com.linecorp.armeria.common.grpc.GrpcMeterIdPrefixFunction;
import com.linecorp.armeria.common.grpc.GrpcSerializationFormats;
import com.linecorp.armeria.server.docs.DocService;
import com.linecorp.armeria.server.grpc.GrpcService;
import com.linecorp.armeria.server.healthcheck.HealthCheckService;
import com.linecorp.armeria.server.logging.AccessLogWriter;
import com.linecorp.armeria.server.logging.LoggingService;
import com.linecorp.armeria.server.metric.MetricCollectingService;
import com.linecorp.armeria.spring.ArmeriaServerConfigurator;
import io.micrometer.core.instrument.MeterRegistry;

@Configuration
public class ArmeriaConfig {

  private final CollectmydataBankGrpcService collectmydataBankGrpcService;
  private final MeterRegistry meterRegistry;

  public ArmeriaConfig(CollectmydataBankGrpcService collectmydataBankGrpcService, MeterRegistry meterRegistry) {
    this.collectmydataBankGrpcService = collectmydataBankGrpcService;
    this.meterRegistry = meterRegistry;
  }

  @Bean
  public ArmeriaServerConfigurator armeriaServerConfigurator() {
    return serverBuilder -> {

      serverBuilder.decorator(LoggingService.newDecorator());
      serverBuilder.decorator(MetricCollectingService.newDecorator(GrpcMeterIdPrefixFunction.of("grpc.service")));

      serverBuilder.accessLogWriter(AccessLogWriter.combined(), false);

      serverBuilder.service(
          GrpcService.builder()
              .addService(collectmydataBankGrpcService)
              .supportedSerializationFormats(GrpcSerializationFormats.values())
              .enableUnframedRequests(true)
              .build());

      serverBuilder.serviceUnder("/docs", new DocService());
      serverBuilder.service("/health", HealthCheckService.of());

      serverBuilder.meterRegistry(meterRegistry);
    };
  }
}
