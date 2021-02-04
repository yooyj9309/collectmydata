package com.banksalad.collectmydata.oauth.common.config;

import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStaticMasterReplicaConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import io.lettuce.core.ReadFrom;

import java.util.List;

@Configuration
@EnableRedisRepositories
public class RedisConfig {

  @Bean
  public RedisConnectionFactory redisConnectionFactory(RedisProperties redisProperties) {
    List<String> nodeList = redisProperties.getCluster().getNodes();
    String[] masterNode = splitHostAndPort(nodeList.get(0));
    RedisStaticMasterReplicaConfiguration redisConfig = new RedisStaticMasterReplicaConfiguration(masterNode[0], Integer.parseInt(masterNode[1]));

    for (int idx = 1; idx < nodeList.size(); idx++) {
      String[] url = splitHostAndPort(nodeList.get(idx));
      redisConfig.addNode(url[0], Integer.parseInt(url[1]));
    }

    LettuceClientConfiguration lettuceClientConfiguration = LettuceClientConfiguration.builder()
        .readFrom(ReadFrom.REPLICA_PREFERRED)
        .build();

    return new LettuceConnectionFactory(redisConfig, lettuceClientConfiguration);
  }

  @Bean
  public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
    RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
    redisTemplate.setConnectionFactory(connectionFactory);
    redisTemplate.setKeySerializer(new StringRedisSerializer());
    redisTemplate.setHashValueSerializer(new StringRedisSerializer());
    redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
    redisTemplate.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
    return redisTemplate;
  }

  private String[] splitHostAndPort(String node) {
    return node.split(":");
  }
}
