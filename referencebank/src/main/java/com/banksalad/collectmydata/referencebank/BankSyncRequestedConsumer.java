package com.banksalad.collectmydata.referencebank;

import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFutureCallback;

import com.banksalad.collectmydata.common.exception.CollectException;
import com.banksalad.collectmydata.common.logging.LoggingMdcUtil;
import com.banksalad.collectmydata.common.message.ConsumerGroupId;
import com.banksalad.collectmydata.common.message.MessageTopic;
import com.banksalad.collectmydata.common.message.PublishmentRequestedMessage;
import com.banksalad.collectmydata.common.message.SyncRequestedMessage;
import com.banksalad.collectmydata.finance.common.exception.ResponseNotOkException;
import com.banksalad.collectmydata.referencebank.common.dto.BankApiResponse;
import com.banksalad.collectmydata.referencebank.common.service.BankMessageService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@Profile("!test")
@RequiredArgsConstructor
public class BankSyncRequestedConsumer {

  private final ObjectMapper objectMapper;
  private final BankApiService bankApiService;
  private final BankMessageService bankMessageService;

  @KafkaListener(
      topics = MessageTopic.bankSyncRequested,
      containerFactory = "bankSyncRequestedKafkaListenerContainerFactory")
  public void consumeSyncRequested(String source) {
    try {
      /* deserialize message */
      SyncRequestedMessage message = objectMapper.readValue(source, SyncRequestedMessage.class);

      LoggingMdcUtil.set(message.getBanksaladUserId(), message.getOrganizationId(), message.getSyncRequestId());
      log.info("[collectmydata-bank] consume SyncRequested syncRequestId: {} ", message.getSyncRequestId());

      /* request api */
      BankApiResponse bankApiResponse = bankApiService.requestApi(message.getBanksaladUserId(),
          message.getOrganizationId(), message.getSyncRequestId(), message.getSyncRequestType());

      /* produce publish */
      producePublishmentRequested(message.getBanksaladUserId(), message.getOrganizationId(),
          message.getSyncRequestId(), bankApiResponse);

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
      BankApiResponse bankApiResponse) throws CollectException {

    try {
      PublishmentRequestedMessage publishmentRequestedMessage = PublishmentRequestedMessage.builder()
          .banksaladUserId(banksaladUserId)
          .organizationId(organizationId)
          .syncRequestId(syncRequestId)
          .apiResponseBody(objectMapper.writeValueAsString(bankApiResponse))
          .build();

      bankMessageService.producePublishmentRequested(publishmentRequestedMessage)
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
