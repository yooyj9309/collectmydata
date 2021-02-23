package com.banksalad.collectmydata.referencebank;

import com.banksalad.collectmydata.common.dto.SyncFinanceBankResponse;
import com.banksalad.collectmydata.common.logging.LoggingMdcUtil;
import com.banksalad.collectmydata.common.message.ConsumerGroupId;
import com.banksalad.collectmydata.common.message.MessageTopic;
import com.banksalad.collectmydata.common.message.PublishmentRequestedMessage;
import com.banksalad.collectmydata.common.message.SyncCompletedMessage;
import com.banksalad.collectmydata.referencebank.common.dto.BankApiResponse;
import com.banksalad.collectmydata.referencebank.common.service.BankMessageService;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFutureCallback;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class BankPublishmentRequestedConsumer {

  private final ObjectMapper objectMapper;
  private final BankMessageService bankMessageService;
  private final BankPublishmentService bankPublishmentService;

  public BankPublishmentRequestedConsumer(
      ObjectMapper objectMapper,
      BankMessageService bankMessageService,
      BankPublishmentService bankPublishmentService
  ) {
    this.objectMapper = objectMapper;
    this.bankMessageService = bankMessageService;
    this.bankPublishmentService = bankPublishmentService;
  }

  @KafkaListener(
      topics = MessageTopic.bankPublishmentRequested,
      containerFactory = "bankPublishmentRequestedKafkaListenerContainerFactory",
      groupId = ConsumerGroupId.collectmydataFinanceBank)
  public void consumePublishmentRequested(String source) {
    try {
      /* deserialize message */
      PublishmentRequestedMessage message = objectMapper.readValue(source, PublishmentRequestedMessage.class);

      LoggingMdcUtil.set(message.getBanksaladUserId(), message.getOrganizationId(), message.getSyncRequestId());
      log.info("[collectmydata-bank] consume publishmentRequested syncRequestId: {} ", message.getSyncRequestId());

      /* request publish */
      SyncFinanceBankResponse syncFinanceBankResponse = bankPublishmentService.requestPublishment(message.getBanksaladUserId(),
          message.getOrganizationId(), objectMapper.readValue(message.getApiResponseBody(), BankApiResponse.class));

      /* produce sync completed */
      produceSyncCompleted(message.getBanksaladUserId(), message.getOrganizationId(), message.getSyncRequestId(),
          objectMapper.writeValueAsString(syncFinanceBankResponse));

    } catch (JsonProcessingException e) {
      log.error("Fail to deserialize message: {}", e.getMessage());

    } finally {
      LoggingMdcUtil.clear();
    }
  }

  private void produceSyncCompleted(long banksaladUserId, String organizationId, String syncRequestId, String syncResponseBody) {

    SyncCompletedMessage syncCompletedMessage = SyncCompletedMessage.builder()
        .banksaladUserId(banksaladUserId)
        .organizationId(organizationId)
        .syncRequestId(syncRequestId)
        .syncResponseBody(syncResponseBody)
        .build();

    bankMessageService.produceSyncCompleted(syncCompletedMessage)
        .addCallback(new ListenableFutureCallback<>() {
          @Override
          public void onSuccess(SendResult<String, String> result) {
            log.info("[collectmydata-bank] produce SyncCompleted syncRequestId: {} ", syncRequestId);
          }

          @Override
          public void onFailure(Throwable t) {
            log.error("[collectmydata-bank] fail to produce SyncCompleted syncRequestId: {}, exception: {}", syncRequestId,
                t.getMessage());
          }
        });
  }
}
