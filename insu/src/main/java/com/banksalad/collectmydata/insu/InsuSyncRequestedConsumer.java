package com.banksalad.collectmydata.insu;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFutureCallback;

import com.banksalad.collectmydata.common.enums.Industry;
import com.banksalad.collectmydata.common.enums.Sector;
import com.banksalad.collectmydata.common.exception.CollectException;
import com.banksalad.collectmydata.common.logging.LoggingMdcUtil;
import com.banksalad.collectmydata.common.message.ConsumerGroupId;
import com.banksalad.collectmydata.common.message.MessageTopic;
import com.banksalad.collectmydata.common.message.PublishmentRequestedMessage;
import com.banksalad.collectmydata.common.message.SyncRequestedMessage;
import com.banksalad.collectmydata.finance.common.exception.ResponseNotOkException;
import com.banksalad.collectmydata.insu.common.dto.InsuApiResponse;
import com.banksalad.collectmydata.insu.common.service.InsuMessageService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class InsuSyncRequestedConsumer {

  private final ObjectMapper objectMapper;
  private final InsuApiService insuApiService;
  private final InsuMessageService insuMessageService;

  @KafkaListener(
      topics = MessageTopic.insuSyncRequested,
      containerFactory = "insuSyncRequestedKafkaListenerContainerFactory",
      groupId = ConsumerGroupId.collectmydataFinanceInsu)
  public void consumeSyncRequested(String source) throws ResponseNotOkException {
    try {
      SyncRequestedMessage message = objectMapper.readValue(source, SyncRequestedMessage.class);

      LoggingMdcUtil.set(Sector.FINANCE.name(), Industry.INSU.name(), message.getBanksaladUserId(),
          message.getOrganizationId(), message.getSyncRequestId());

      log.info("[collectmydata-insu] consume SyncRequested syncRequestId: {} ", message.getSyncRequestId());

      InsuApiResponse response;

      /* 1. request api */
      switch (message.getSyncRequestType()) {
        case ONDEMAND:
          response = insuApiService.onDemandRequestApi(message.getBanksaladUserId(), message.getOrganizationId(),
              message.getSyncRequestId());
          break;
        case SCHEDULED_BASIC:
          response = insuApiService
              .scheduledBasicRequestApi(message.getBanksaladUserId(), message.getOrganizationId(),
                  message.getSyncRequestId());
          break;
        case SCHEDULED_ADDITIONAL:
          response = insuApiService
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
      InsuApiResponse insuApiResponse) throws CollectException {

    try {
      PublishmentRequestedMessage publishmentRequestedMessage = PublishmentRequestedMessage.builder()
          .banksaladUserId(banksaladUserId)
          .organizationId(organizationId)
          .syncRequestId(syncRequestId)
          .apiResponseBody(objectMapper.writeValueAsString(insuApiResponse))
          .build();

      insuMessageService
          .producePublishmentRequested(publishmentRequestedMessage)
          .addCallback(new ListenableFutureCallback<>() {
            @Override
            public void onSuccess(SendResult<String, String> result) {
              log.info("[collectmydata-insu] produce PublishmentRequested syncRequestId: {} ", syncRequestId);
            }

            @Override
            public void onFailure(Throwable t) {
              log.error("[collectmydata-insu] fail to produce PublishmentRequested syncRequestId: {}, exception: {}",
                  syncRequestId, t.getMessage());
            }
          });

    } catch (JsonProcessingException e) {
      log.error("Fail to publish: {}", e.getMessage());
      throw new CollectException("Fail write publish message", e);
    }
  }
}
