package com.banksalad.collectmydata.collect.consumer;

import com.banksalad.collectmydata.collect.common.service.RedisPubSubService;
import com.banksalad.collectmydata.common.message.ConsumerGroupId;
import com.banksalad.collectmydata.common.message.MessageTopic;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class BankSyncCompletedConsumer {

  private final ObjectMapper objectMapper;
  private final RedisPubSubService redisPubSubService;

  public BankSyncCompletedConsumer(ObjectMapper objectMapper, RedisPubSubService redisPubSubService) {
    this.objectMapper = objectMapper;
    this.redisPubSubService = redisPubSubService;
  }

  @KafkaListener(
      topics = MessageTopic.bankSyncCompleted,
      containerFactory = "kafkaListenerContainerFactory",
      groupId = ConsumerGroupId.collectConsumerGroupId)
  public void consume(String source) {

//    try {
//      /* deserialize message */
//      SyncCompletedMessage message = objectMapper.readValue(source, SyncCompletedMessage.class);
//
//      LoggingMdcUtil.set(message.getBanksaladUserId(), message.getOrganizationId(), message.getSyncRequestId());
//      log.info("[collect] consume SyncCompletedMessage syncRequestId: {} ", message.getSyncRequestId());
//
//      /* publish event */
//      redisPubSubService
//          .publishSyncCompleted(message.getBanksaladUserId(), message.getSyncRequestId(), message.getSyncResponseBody());
//
//
//    } catch (JsonProcessingException e) {
//      log.error("Fail to deserialize SyncCompletedMessage: {}", e.getMessage());
//
//    } finally {
//      LoggingMdcUtil.clear();
//    }
  }
}
