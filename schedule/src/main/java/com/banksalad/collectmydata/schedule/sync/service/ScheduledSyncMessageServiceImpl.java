package com.banksalad.collectmydata.schedule.sync.service;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFutureCallback;

import com.banksalad.collectmydata.common.exception.CollectRuntimeException;
import com.banksalad.collectmydata.common.message.SyncRequestedMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScheduledSyncMessageServiceImpl implements ScheduledSyncMessageService {

  private final ObjectMapper objectMapper;
  private final KafkaTemplate<String, String> kafkaTemplate;

  @Override
  public void produceSyncRequest(SyncRequestedMessage syncRequestedMessage, String topic) {
    final String message;
    try {
      message = objectMapper.writeValueAsString(syncRequestedMessage);
    } catch (JsonProcessingException e) {
      throw new CollectRuntimeException("Fail to serialize message", e); // FIXME
    }
    if (message == null) {
      return;
    }

    kafkaTemplate
        .send(topic, String.valueOf(syncRequestedMessage.getBanksaladUserId()), message)
        .addCallback(new ListenableFutureCallback<>() {
          @Override
          public void onSuccess(SendResult<String, String> result) {
            log.info("[collectmydata-schedule] produce SyncRequestedMessage, syncRequestId: {}",
                syncRequestedMessage.getSyncRequestId());
          }

          @Override
          public void onFailure(Throwable t) {
            log.error("[collectmydata-schedule] fail to produce SyncRequestedMessage, syncRequestId: {}, exception: {}",
                syncRequestedMessage.getSyncRequestId(), t.getMessage());
          }
        });
  }
}
