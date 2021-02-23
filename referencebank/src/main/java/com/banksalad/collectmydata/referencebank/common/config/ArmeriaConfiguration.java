package com.banksalad.collectmydata.referencebank.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.linecorp.armeria.server.logging.AccessLogWriter;
import com.linecorp.armeria.server.logging.LoggingService;
import com.linecorp.armeria.spring.ArmeriaServerConfigurator;

@Configuration
public class ArmeriaConfiguration {

  @Bean
  public ArmeriaServerConfigurator armeriaServerConfigurator() {
    return serverBuilder -> {

      serverBuilder.decorator(LoggingService.newDecorator());
      serverBuilder.accessLogWriter(AccessLogWriter.combined(), false);
    };
  }
}
