package com.banksalad.collectmydata.oauth.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.linecorp.armeria.server.docs.DocService;
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
      serverBuilder.serviceUnder("/docs", new DocService());
    };
  }
}
