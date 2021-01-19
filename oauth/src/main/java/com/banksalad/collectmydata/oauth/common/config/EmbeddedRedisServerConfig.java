package com.banksalad.collectmydata.oauth.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import redis.embedded.RedisServer;

@Configuration
@Profile(value = {"local", "test"})
public class EmbeddedRedisServerConfig {

  private RedisServer redisServer;

  @Value("${spring.redis.port}")
  private int redisPort;

  @PostConstruct
  public void redisServer() {
    redisServer = new RedisServer(redisPort);
    redisServer.start();
  }

  @PreDestroy
  public void stopRedis() {
    if (redisServer != null) {
      redisServer.stop();
    }
  }
}
