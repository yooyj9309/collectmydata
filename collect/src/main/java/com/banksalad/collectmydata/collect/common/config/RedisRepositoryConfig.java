package com.banksalad.collectmydata.collect.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;

@Configuration
public class RedisRepositoryConfig {

  @Value("${spring.redis.host}")
  private String redisHost;

  @Value("${spring.redis.port}")
  private int redisPort;

  @Bean
  public LettuceConnectionFactory redisConnectionFactory() {
    //TODO : configure redis connection pool

    RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(redisHost, redisPort);
    return new LettuceConnectionFactory(config);
  }

  @Bean
  public RedisTemplate<String, Object> redisTemplate() {
    RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
    redisTemplate.setConnectionFactory(redisConnectionFactory());
    redisTemplate.setKeySerializer(new StringRedisSerializer());
    return redisTemplate;
  }


  @Bean
  public RedisClient redisClient() {
    //TODO : configure redis connection pool

    return RedisClient.create(RedisURI.builder()
        .withHost(redisHost)
        .withPort(redisPort)
        .build());
  }
}
