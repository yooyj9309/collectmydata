package com.banksalad.collectmydata.referencebank.common.service;

import com.banksalad.collectmydata.common.exception.CollectRuntimeException;
import com.banksalad.collectmydata.common.message.MessageTopic;
import com.banksalad.collectmydata.common.message.PublishmentRequestedMessage;
import com.banksalad.collectmydata.common.message.SyncCompletedMessage;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class BankMessageServiceImpl implements BankMessageService {

  private final KafkaTemplate<String, String> kafkaTemplate;
  private final ObjectMapper objectMapper;

  public BankMessageServiceImpl(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
    this.kafkaTemplate = kafkaTemplate;
    this.objectMapper = objectMapper;
  }

  public ListenableFuture<SendResult<String, String>> producePublishmentRequested(
      PublishmentRequestedMessage publishmentRequestedMessage
  ) {
    final String message;

    try {
      message = objectMapper.writeValueAsString(publishmentRequestedMessage);

    } catch (JsonProcessingException e) {
      throw new CollectRuntimeException("Fail to serialize message", e);
    }

    return kafkaTemplate.send(
        MessageTopic.bankPublishmentRequested,
        String.valueOf(publishmentRequestedMessage.getBanksaladUserId()),
        message
    );
  }

  @Override
  public ListenableFuture<SendResult<String, String>> produceSyncCompleted(
      SyncCompletedMessage syncCompletedMessage
  ) {
    final String message;

    try {
      message = objectMapper.writeValueAsString(syncCompletedMessage);

    } catch (JsonProcessingException e) {
      throw new CollectRuntimeException("Fail to serialize message", e);
    }

    return kafkaTemplate.send(
        MessageTopic.bankSyncCompleted,
        String.valueOf(syncCompletedMessage.getBanksaladUserId()),
        message
    );
  }
}
