package com.banksalad.collectmydata.oauth.common.config;

import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.util.SocketUtils;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import redis.embedded.RedisServer;

@TestConfiguration
public class TestRedisConfiguration {

  private RedisServer redisServer;
  private final int port;

  public TestRedisConfiguration(RedisProperties redisProperties) {
    port = SocketUtils.findAvailableTcpPort();
    redisProperties.setPort(port);
    this.redisServer = new RedisServer(port);
  }

  @PostConstruct
  public void postConstruct() {
    redisServer.start();
  }

  @PreDestroy
  public void preDestroy() {
    redisServer.stop();
  }
}
