package com.banksalad.collectmydata.collect.common.service;

import com.banksalad.collectmydata.common.exception.CollectRuntimeException;
import com.banksalad.collectmydata.common.message.MessageTopic;
import com.banksalad.collectmydata.common.message.SyncRequestedMessage;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CollectMessageServiceImpl implements CollectMessageService {

  private final KafkaTemplate<String, String> kafkaTemplate;
  private final ObjectMapper objectMapper;

  public CollectMessageServiceImpl(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
    this.kafkaTemplate = kafkaTemplate;
    this.objectMapper = objectMapper;
  }

  @Override
  public ListenableFuture<SendResult<String, String>> produceBankSyncRequested(SyncRequestedMessage syncRequestedMessage) {
    final String message;

    try {
      message = objectMapper.writeValueAsString(syncRequestedMessage);
      return kafkaTemplate.send(MessageTopic.bankSyncRequested, message);

    } catch (JsonProcessingException e) {
      throw new CollectRuntimeException("Fail to serialize message", e);
    }
  }
}
