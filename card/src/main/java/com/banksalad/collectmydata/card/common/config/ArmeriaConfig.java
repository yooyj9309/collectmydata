package com.banksalad.collectmydata.card.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.linecorp.armeria.server.docs.DocService;
import com.linecorp.armeria.server.healthcheck.HealthCheckService;
import com.linecorp.armeria.server.logging.AccessLogWriter;
import com.linecorp.armeria.server.logging.LoggingService;
import com.linecorp.armeria.spring.ArmeriaServerConfigurator;
import io.micrometer.core.instrument.MeterRegistry;

@Configuration
public class ArmeriaConfig {

  private final MeterRegistry meterRegistry;

  public ArmeriaConfig(MeterRegistry meterRegistry) {
    this.meterRegistry = meterRegistry;
  }

  @Bean
  public ArmeriaServerConfigurator armeriaServerConfigurator() {
    return serverBuilder -> {

      serverBuilder.decorator(LoggingService.newDecorator());

      serverBuilder.accessLogWriter(AccessLogWriter.combined(), false);

      serverBuilder.serviceUnder("/docs", new DocService());
      serverBuilder.service("/health", HealthCheckService.of());

      serverBuilder.meterRegistry(meterRegistry);
    };
  }
}
