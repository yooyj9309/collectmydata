package com.banksalad.collectmydata.connect.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.micrometer.core.aop.TimedAspect;
import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.statsd.StatsdConfig;
import io.micrometer.statsd.StatsdMeterRegistry;

@Configuration
public class MicrometerConfig {

  private final CustomNamingConvention customNamingConvention;

  public MicrometerConfig(CustomNamingConvention customNamingConvention) {
    this.customNamingConvention = customNamingConvention;
  }

  @Bean
  public StatsdMeterRegistry statsdMeterRegistry(StatsdConfig statsdConfig, Clock clock) {
    StatsdMeterRegistry statsdMeterRegistry = new StatsdMeterRegistry(statsdConfig, clock);
    statsdMeterRegistry.config().namingConvention(customNamingConvention);
    return statsdMeterRegistry;
  }

  @Bean
  public TimedAspect timedAspect(MeterRegistry registry) {
    return new TimedAspect(registry);
  }
}
