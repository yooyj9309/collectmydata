package com.banksalad.collectmydata.schedule.sync.service;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFutureCallback;

import com.banksalad.collectmydata.schedule.common.db.entity.ScheduledSyncEntity;
import com.banksalad.collectmydata.schedule.common.enums.SyncType;
import com.banksalad.collectmydata.schedule.sync.dto.ScheduledSyncMessage;
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
  private static final String TOPIC_PREFIX = "collectmydata-";


  @Override
  public void produce(ScheduledSyncEntity scheduledSyncEntity, SyncType syncType) {
    String topic = getTopicNameFrom(scheduledSyncEntity);
    String message = getMessageFrom(scheduledSyncEntity, syncType);
    if (message == null) {
      return;
    }

    kafkaTemplate
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

  private String getTopicNameFrom(ScheduledSyncEntity scheduledSyncEntity) {
    return TOPIC_PREFIX + scheduledSyncEntity.getIndustry();
  }

  private String getMessageFrom(ScheduledSyncEntity scheduledSyncEntity, SyncType syncType) {
    String message = null;

    try {
      ScheduledSyncMessage scheduledSyncMessage = ScheduledSyncMessage.builder()
          .banksaladUserId(scheduledSyncEntity.getBanksaladUserId())
          .sector(scheduledSyncEntity.getSector())
          .industry(scheduledSyncEntity.getIndustry())
          .organizationId(scheduledSyncEntity.getOrganizationId())
          .syncType(syncType)
          .build();

      message = objectMapper.writeValueAsString(scheduledSyncMessage);
    } catch (JsonProcessingException e) {
      log.error("ScheduledSync Serialization Fail, Exception : {}", e.getMessage());
    }

    return message;
  }
}
