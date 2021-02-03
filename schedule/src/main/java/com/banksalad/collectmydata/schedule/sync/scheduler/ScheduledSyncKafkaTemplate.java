package com.banksalad.collectmydata.schedule.sync.scheduler;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFutureCallback;

import com.banksalad.collectmydata.schedule.common.db.entity.ScheduledSync;
import com.banksalad.collectmydata.schedule.common.enums.SyncType;
import com.banksalad.collectmydata.schedule.sync.dto.ScheduledSyncMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class ScheduledSyncKafkaTemplate implements ScheduledSyncTemplate {

  private final ObjectMapper objectMapper;
  private final KafkaTemplate<String, String> template;
  private static final String TOPIC_PREFIX = "collect-mydata-";

  @Override
  public void sync(ScheduledSync scheduledSync, SyncType syncType) {
    String topic = getTopicNameFrom(scheduledSync);
    String message = getMessageFrom(scheduledSync, syncType);
    if (message == null) {
      return;
    }

    template
        .send(topic, message)
        .addCallback(new ListenableFutureCallback<>() {
          @Override
          public void onSuccess(SendResult<String, String> result) {
            log.info("Produce Success, Result : {}", result);
          }

          @Override
          public void onFailure(Throwable e) {
            log.error("Produce Fail, Exception : {}", e.getMessage());
          }
        });
  }

  private String getTopicNameFrom(ScheduledSync scheduledSync) {
    return TOPIC_PREFIX + scheduledSync.getIndustry();
  }

  private String getMessageFrom(ScheduledSync scheduledSync, SyncType syncType) {
    String message = null;

    try {
      ScheduledSyncMessage scheduledSyncMessage = ScheduledSyncMessage.of(scheduledSync, syncType);
      message = objectMapper.writeValueAsString(scheduledSyncMessage);
    } catch (JsonProcessingException e) {
      log.error("ScheduledSync Serialization Fail, Exception : {}", e.getMessage());
    }

    return message;
  }
}
