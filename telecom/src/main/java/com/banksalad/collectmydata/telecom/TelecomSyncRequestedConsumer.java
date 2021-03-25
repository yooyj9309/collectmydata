package com.banksalad.collectmydata.telecom;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFutureCallback;

import com.banksalad.collectmydata.common.enums.Industry;
import com.banksalad.collectmydata.common.enums.Sector;
import com.banksalad.collectmydata.common.exception.CollectException;
import com.banksalad.collectmydata.common.logging.LoggingMdcUtil;
import com.banksalad.collectmydata.common.message.MessageTopic;
import com.banksalad.collectmydata.common.message.PublishmentRequestedMessage;
import com.banksalad.collectmydata.common.message.SyncRequestedMessage;
import com.banksalad.collectmydata.finance.common.exception.ResponseNotOkException;
import com.banksalad.collectmydata.telecom.common.dto.TelecomApiResponse;
import com.banksalad.collectmydata.telecom.common.service.TelecomMessageService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class TelecomSyncRequestedConsumer {

  private final ObjectMapper objectMapper;
  private final TelecomApiService telecomApiService;
  private final TelecomMessageService telecomMessageService;

  @KafkaListener(
      topics = MessageTopic.telecomSyncRequested,
      containerFactory = "telecomSyncRequestedKafkaListenerContainerFactory"
  )
  public void consumeSyncRequested(String source) throws ResponseNotOkException {
    try {
      SyncRequestedMessage message = objectMapper.readValue(source, SyncRequestedMessage.class);

      LoggingMdcUtil.set(Sector.FINANCE.name(), Industry.TELECOM.name(), message.getBanksaladUserId(),
          message.getOrganizationId(), message.getSyncRequestId());

      log.info("[collectmydata-telecom] consume SyncRequested syncRequestId: {} ", message.getSyncRequestId());

      TelecomApiResponse response;

      /* 1. request api */
      switch (message.getSyncRequestType()) {
        case ONDEMAND:
          response = telecomApiService.onDemandRequestApi(message.getBanksaladUserId(), message.getOrganizationId(),
              message.getSyncRequestId());
          break;
        case SCHEDULED_BASIC:
          response = telecomApiService
              .scheduledBasicRequestApi(message.getBanksaladUserId(), message.getOrganizationId(),
                  message.getSyncRequestId());
          break;
        case SCHEDULED_ADDITIONAL:
          response = telecomApiService
              .scheduledAdditionalRequestApi(message.getBanksaladUserId(), message.getOrganizationId(),
                  message.getSyncRequestId());
          break;
        default:
          log.error("Fail to specify RequestType: {}", message.getSyncRequestType());
          throw new CollectException("undefined syncResultType"); // TODO Exception 처리는 모니터링 작업과 같이 진행
      }

      /* 2. produce publish */
      producePublishmentRequested(message.getBanksaladUserId(), message.getOrganizationId(), message.getSyncRequestId(),
          response);

    } catch (JsonProcessingException e) {
      log.error("Fail to deserialize syncRequestedMessage: {}", e.getMessage());
    } catch (ResponseNotOkException e) {
      log.error("Fail to sync: {}", e.getMessage());
      // TODO publish result with error code and message
    } catch (CollectException e) {
      log.error("Fail to sync: {}", e.getMessage());
      // TODO publish result with error code and message
    } catch (Throwable t) {
      log.error("Fail to sync: {}", t.getMessage());
      // TODO publish result with error code and message
    } finally {
      LoggingMdcUtil.clear();
    }
  }

  private void producePublishmentRequested(long banksaladUserId, String organizationId, String syncRequestId,
      TelecomApiResponse telecomApiResponse) throws CollectException {

    try {
      PublishmentRequestedMessage publishmentRequestedMessage = PublishmentRequestedMessage.builder()
          .banksaladUserId(banksaladUserId)
          .organizationId(organizationId)
          .syncRequestId(syncRequestId)
          .apiResponseBody(objectMapper.writeValueAsString(telecomApiResponse))
          .build();

      telecomMessageService
          .producePublishmentRequested(publishmentRequestedMessage)
          .addCallback(new ListenableFutureCallback<>() {
            @Override
            public void onSuccess(SendResult<String, String> result) {
              log.info("[collectmydata-telecom] produce PublishmentRequested syncRequestId: {} ", syncRequestId);
            }

            @Override
            public void onFailure(Throwable t) {
              log.error("[collectmydata-telecom] fail to produce PublishmentRequested syncRequestId: {}, exception: {}",
                  syncRequestId, t.getMessage());
            }
          });
    } catch (JsonProcessingException e) {
      log.error("Fail to publish: {}", e.getMessage());
      throw new CollectException("Fail write publish message", e);
    }
  }
}
