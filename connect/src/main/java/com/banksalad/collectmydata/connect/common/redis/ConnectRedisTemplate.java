package com.banksalad.collectmydata.connect.common.redis;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class ConnectRedisTemplate {

  private final ValueOperations<String, Object> values;

  @Value("${spring.application.name}")
  private String applicationName;

  public ConnectRedisTemplate(RedisTemplate redisTemplate) {
    this.values = redisTemplate.opsForValue();
  }

  public Object get(String key) {
    return values.get(generateKey(key));
  }

  public Boolean setIfAbsent(String key, Object value, Long timeout, TimeUnit timeUnit) {
    return values.setIfAbsent(generateKey(key), value, timeout, timeUnit);
  }

  private String generateKey(String key) {
    return new StringBuffer().append(applicationName).append(":").append(key).toString();
  }

}
