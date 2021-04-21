package com.banksalad.collectmydata.collect.common.config;

import org.springframework.boot.autoconfigure.cache.JCacheManagerCustomizer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.cache.configuration.MutableConfiguration;
import javax.cache.expiry.CreatedExpiryPolicy;
import javax.cache.expiry.Duration;

import java.util.concurrent.TimeUnit;

@EnableCaching
@Configuration
public class EhcacheConfig {

  private final MutableConfiguration<Object, Object> configuration;

  public EhcacheConfig() {
    configuration = new MutableConfiguration<Object, Object>()
        .setTypes(Object.class, Object.class)
        .setStoreByValue(false)
        .setExpiryPolicyFactory(CreatedExpiryPolicy.factoryOf(new Duration(TimeUnit.MINUTES, 1)));
  }

  @Bean
  public JCacheManagerCustomizer cacheManagerCustomizer() {
    return cm -> {
      if (cm.getCache("getOrganizationByOrganizationObjectidCache") == null) {
        cm.createCache("getOrganizationByOrganizationObjectidCache", configuration);
      }
    };
  }
}
