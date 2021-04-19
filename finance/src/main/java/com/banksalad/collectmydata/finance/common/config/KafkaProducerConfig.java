package com.banksalad.collectmydata.finance.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Map;

@Configuration
public class KafkaProducerConfig {

  @Value(value = "${kafka.bootstrap-servers}")
  private String kafkaBootstrapServers;

  private ProducerFactory<String, String> producerFactory() {
    return new DefaultKafkaProducerFactory<>(
        Map.of(
            ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBootstrapServers,
            ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class,
            ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class));
  }

  @Bean
  public KafkaTemplate<String, String> publishKafkaTemplate() {
    return new KafkaTemplate<>(producerFactory());
  }
}
