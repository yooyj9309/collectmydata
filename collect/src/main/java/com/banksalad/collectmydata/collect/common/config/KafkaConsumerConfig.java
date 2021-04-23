package com.banksalad.collectmydata.collect.common.config;

import com.banksalad.collectmydata.common.message.MessageTopic;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.util.Map;

@Configuration
public class KafkaConsumerConfig {

  @Value(value = "${kafka.bootstrap-servers}")
  private String kafkaBootstrapServers;

  private ConsumerFactory<String, String> consumerFactory(String groupId) {
    return new DefaultKafkaConsumerFactory<>(
        Map.of(
            ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBootstrapServers,
            ConsumerConfig.GROUP_ID_CONFIG, groupId,
            ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class,
            ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class));
  }

  private ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory(String groupId) {
    ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(consumerFactory(groupId));

    return factory;
  }

  @Bean
  public ConcurrentKafkaListenerContainerFactory<String, String> bankPublishmentRequestedKafkaListenerContainerFactory() {
    return kafkaListenerContainerFactory(MessageTopic.bankPublishmentRequested);
  }

  @Bean
  public ConcurrentKafkaListenerContainerFactory<String, String> bankSyncCompletedKafkaListenerContainerFactory() {
    return kafkaListenerContainerFactory(MessageTopic.bankSyncCompleted);
  }
}
