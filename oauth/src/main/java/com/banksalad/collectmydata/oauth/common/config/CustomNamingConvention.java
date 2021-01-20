package com.banksalad.collectmydata.oauth.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import io.micrometer.core.instrument.Meter.Type;
import io.micrometer.core.instrument.config.NamingConvention;

import java.text.MessageFormat;

@Configuration
public class CustomNamingConvention implements NamingConvention {

  @Value("${spring.profiles.active}")
  private String activeName;

  @Value("${spring.application.name}")
  private String applicationName;

  @Override
  public String name(String name, Type type, String baseUnit) {
    return MessageFormat.format("{0}.{1}.{2}", activeName, applicationName, name);
  }
}
