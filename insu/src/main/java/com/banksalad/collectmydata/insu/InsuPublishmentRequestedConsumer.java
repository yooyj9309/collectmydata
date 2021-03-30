package com.banksalad.collectmydata.insu;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFutureCallback;

import com.banksalad.collectmydata.common.dto.SyncFinanceInsuResponse;
import com.banksalad.collectmydata.common.logging.LoggingMdcUtil;
import com.banksalad.collectmydata.common.message.MessageTopic;
import com.banksalad.collectmydata.common.message.PublishmentRequestedMessage;
import com.banksalad.collectmydata.common.message.SyncCompletedMessage;
import com.banksalad.collectmydata.insu.common.dto.InsuApiResponse;
import com.banksalad.collectmydata.insu.common.service.InsuMessageService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class InsuPublishmentRequestedConsumer {

  private final ObjectMapper objectMapper;
  private final InsuMessageService insuMessageService;
  private final InsuPublishmentService insuPublishmentService;

  @KafkaListener(
      topics = MessageTopic.insuPublishmentRequested,
      containerFactory = "insuPublishmentRequestedKafkaListenerContainerFactory"
  )
  public void consumePublishmentRequested(String source) {
    try {
      /* deserialize message */
      PublishmentRequestedMessage message = objectMapper.readValue(source, PublishmentRequestedMessage.class);

      LoggingMdcUtil.set(message.getBanksaladUserId(), message.getOrganizationId(), message.getSyncRequestId());
      log.info("[collectmydata-insu] consume publishmentRequested syncRequestId: {} ", message.getSyncRequestId());

      /* request publish */
      SyncFinanceInsuResponse syncFinanceInsuResponse = insuPublishmentService
          .requestPublishment(message.getBanksaladUserId(), message.getOrganizationId(),
              objectMapper.readValue(message.getApiResponseBody(), InsuApiResponse.class));

      /* produce sync completed */
      produceSyncCompleted(message.getBanksaladUserId(), message.getOrganizationId(), message.getSyncRequestId(),
          objectMapper.writeValueAsString(syncFinanceInsuResponse));
    } catch (JsonProcessingException e) {
      log.error("Fail to deserialize message: {}", e.getMessage());
    } finally {
      LoggingMdcUtil.clear();
    }
  }

  private void produceSyncCompleted(long banksaladUserId, String organizationId, String syncRequestId,
      String syncResponseBody) {
    SyncCompletedMessage syncCompletedMessage = SyncCompletedMessage.builder()
        .banksaladUserId(banksaladUserId)
        .organizationId(organizationId)
        .syncRequestId(syncRequestId)
        .syncResponseBody(syncResponseBody)
        .build();

    insuMessageService.produceSyncCompleted(syncCompletedMessage)
        .addCallback(new ListenableFutureCallback<>() {
          @Override
          public void onSuccess(SendResult<String, String> result) {
            log.info("[collectmydata-insu] produce SyncCompleted syncRequestId: {} ", syncRequestId);
          }

          @Override
          public void onFailure(Throwable t) {
            log.error("[collectmydata-insu] fail to produce SyncCompleted syncRequestId: {}, exception: {}",
                syncRequestId, t.getMessage());
          }
        });
  }
}