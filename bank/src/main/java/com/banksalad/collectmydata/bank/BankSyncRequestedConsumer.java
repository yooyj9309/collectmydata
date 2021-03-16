package com.banksalad.collectmydata.bank;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFutureCallback;

import com.banksalad.collectmydata.bank.common.dto.BankApiResponse;
import com.banksalad.collectmydata.bank.common.service.BankMessageService;
import com.banksalad.collectmydata.common.enums.Industry;
import com.banksalad.collectmydata.common.enums.Sector;
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
public class BankSyncRequestedConsumer {

  private final ObjectMapper objectMapper;
  private final BankApiService bankApiService;
  private final BankMessageService bankMessageService;

  @KafkaListener(
      topics = MessageTopic.bankSyncRequested,
      containerFactory = "bankSyncRequestedKafkaListenerContainerFactory",
      groupId = ConsumerGroupId.collectmydataFinanceBank
  )
  public void consumeBankSyncRequested(String source) {
    try {
      SyncRequestedMessage message = objectMapper.readValue(source, SyncRequestedMessage.class);

      LoggingMdcUtil.set(Sector.FINANCE.name(), Industry.BANK.name(), message.getBanksaladUserId(),
          message.getOrganizationId(), message.getSyncRequestId());

      log.info("Consume SyncRequestedMessage. syncRequestId: {} ", message.getSyncRequestId());

      BankApiResponse bankApiResponse = bankApiService.requestApi(message.getBanksaladUserId(),
          message.getOrganizationId(), message.getSyncRequestId(), message.getSyncRequestType());

      producePublishmentRequested(message.getBanksaladUserId(), message.getOrganizationId(),
          message.getSyncRequestId(), bankApiResponse);

    } catch (JsonProcessingException e) {
      log.error("Fail to deserialize syncRequestedMessage: {}", e.getMessage(), e);

    } catch (ResponseNotOkException e) {
      log.error("Fail to sync: {}", e.getMessage(), e);
      // TODO publish result with error code and message

    } catch (CollectException e) {
      log.error("Fail to sync: {}", e.getMessage(), e);
      // TODO publish result with error code and message

    } catch (Throwable t) {
      log.error("Fail to sync: {}", t.getMessage(), t);
      // TODO publish result with error code and message

    } finally {
      LoggingMdcUtil.clear();
    }
  }

  private void producePublishmentRequested(long banksaladUserId, String organizationId, String syncRequestId,
      BankApiResponse bankApiResponse) throws CollectException {

    try {
      // TODO jayden-lee ApiResponseBody 내부 포맷은 변경될 수 있음
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
              log.info("Produce PublishmentRequestedMessage. syncRequestId: {} ", syncRequestId);
            }

            @Override
            public void onFailure(Throwable t) {
              log.error("Fail to produce PublishmentRequestedMessage. syncRequestId: {}, exception: {}",
                  syncRequestId, t.getMessage(), t);
            }
          });

    } catch (JsonProcessingException e) {
      log.error("Fail to publish. exception: {}", e.getMessage(), e);
      throw new CollectException("Fail write publish message", e);
    }
  }
}
