package com.banksalad.collectmydata.insu.common.service;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;

import com.banksalad.collectmydata.common.exception.CollectRuntimeException;
import com.banksalad.collectmydata.common.message.MessageTopic;
import com.banksalad.collectmydata.common.message.PublishmentRequestedMessage;
import com.banksalad.collectmydata.common.message.SyncCompletedMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class InsuMessageServiceImpl implements InsuMessageService {

  private final KafkaTemplate<String, String> publishKafkaTemplate;
  private final ObjectMapper objectMapper;

  @Override
  public ListenableFuture<SendResult<String, String>> producePublishmentRequested(
      PublishmentRequestedMessage publishmentRequestedMessage) {
    final String message;

    try {
      message = objectMapper.writeValueAsString(publishmentRequestedMessage);
    } catch (JsonProcessingException e) {
      throw new CollectRuntimeException("Fail to serialize message", e);
    }

    return publishKafkaTemplate.send(
        MessageTopic.insuPublishmentRequested,
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

    return publishKafkaTemplate.send(
        MessageTopic.insuSyncCompleted,
        String.valueOf(syncCompletedMessage.getBanksaladUserId()),
        message
    );
  }
}
