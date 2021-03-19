package com.banksalad.collectmydata.capital;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFutureCallback;

import com.banksalad.collectmydata.capital.common.dto.CapitalApiResponse;
import com.banksalad.collectmydata.capital.common.service.CapitalMessageService;
import com.banksalad.collectmydata.common.exception.CollectException;
import com.banksalad.collectmydata.common.logging.LoggingMdcUtil;
import com.banksalad.collectmydata.common.message.ConsumerGroupId;
import com.banksalad.collectmydata.common.message.MessageTopic;
import com.banksalad.collectmydata.common.message.PublishmentRequestedMessage;
import com.banksalad.collectmydata.common.message.SyncRequestedMessage;
import com.banksalad.collectmydata.finance.common.exception.ResponseNotOkException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class CapitalSyncRequestedConsumer {

  private final ObjectMapper objectMapper;
  private final CapitalApiService capitalApiService;
  private final CapitalMessageService capitalMessageService;

  @KafkaListener(
      topics = MessageTopic.capitalSyncRequested,
      containerFactory = "capitalSyncRequestedKafkaListenerContainerFactory",
      groupId = ConsumerGroupId.collectmydataFinanceCapital)
  public void consumeSyncRequested(String source) throws ResponseNotOkException {
    try {
      SyncRequestedMessage message = objectMapper.readValue(source, SyncRequestedMessage.class);

      LoggingMdcUtil.set(message.getBanksaladUserId(), message.getOrganizationId(), message.getSyncRequestId());
      log.info("[collectmydata-capital] consume SyncRequested syncRequestId: {} ", message.getSyncRequestId());

      CapitalApiResponse response = null;

      /* 1. request api */
      switch (message.getSyncRequestType()) {
        case ONDEMAND:
          response = capitalApiService
              .requestApi(message.getBanksaladUserId(), message.getOrganizationId(), message.getSyncRequestId());
          break;
        case SCHEDULED_BASIC:
          // TODO
          break;
        case SCHEDULED_ADDITIONAL:
          // TODO
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
    } catch (CollectException e) {
      log.error("Fail to sync: {}", e.getMessage());
      // TODO publish result with error code and message
    } finally {
      LoggingMdcUtil.clear();
    }
  }

  private void producePublishmentRequested(long banksaladUserId, String organizationId, String syncRequestId,
      CapitalApiResponse capitalApiResponse) throws CollectException {

    try {
      PublishmentRequestedMessage publishmentRequestedMessage = PublishmentRequestedMessage.builder()
          .banksaladUserId(banksaladUserId)
          .organizationId(organizationId)
          .syncRequestId(syncRequestId)
          .apiResponseBody(objectMapper.writeValueAsString(capitalApiResponse))
          .build();

      capitalMessageService
          .producePublishmentRequested(publishmentRequestedMessage)
          .addCallback(new ListenableFutureCallback<>() {
            @Override
            public void onSuccess(SendResult<String, String> result) {
              log.info("[collectmydata-bank] produce PublishmentRequested syncRequestId: {} ", syncRequestId);
            }

            @Override
            public void onFailure(Throwable t) {
              log.error("[collectmydata-bank] fail to produce PublishmentRequested syncRequestId: {}, exception: {}",
                  syncRequestId, t.getMessage());
            }
          });

    } catch (JsonProcessingException e) {
      log.error("Fail to publish: {}", e.getMessage());
      throw new CollectException("Fail write publish message", e);
    }
  }
}
