package com.banksalad.collectmydata.invest;

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
import com.banksalad.collectmydata.invest.common.dto.InvestApiResponse;
import com.banksalad.collectmydata.invest.common.service.InvestMessageService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class InvestSyncRequestedConsumer {

  private final ObjectMapper objectMapper;
  private final InvestApiService investApiService;
  private final InvestMessageService investMessageService;

  @KafkaListener(
      topics = MessageTopic.investSyncRequested,
      containerFactory = "investSyncRequestedKafkaListenerContainerFactory"
  )
  public void consumeSyncRequested(String source) {
    try {
      SyncRequestedMessage message = objectMapper.readValue(source, SyncRequestedMessage.class);

      LoggingMdcUtil.set(Sector.FINANCE.name(), Industry.INVEST.name(), message.getBanksaladUserId(),
          message.getOrganizationId(), message.getSyncRequestId());

      InvestApiResponse investApiResponse = investApiService
          .requestApi(message.getBanksaladUserId(), message.getOrganizationId(), message.getSyncRequestId(),
              message.getSyncRequestType());

      producePublishmentRequested(message.getBanksaladUserId(), message.getOrganizationId(), message.getSyncRequestId(), investApiResponse);

    } catch (JsonProcessingException e) {
      log.error("Fail to deserialize syncRequestedMessage: {}", e.getMessage());
    } catch (ResponseNotOkException e) {
      log.error("Fail to sync: {}", e.getMessage());
    } catch (CollectException e) {
      log.error("Fail to sync: {}", e.getMessage());
    } catch (Throwable t) {
      log.error("Fail to sync: {}", t.getMessage());
    } finally {
      LoggingMdcUtil.clear();
    }
  }

  private void producePublishmentRequested(long banksaladUserId, String organizationId, String syncRequestId,
      InvestApiResponse investApiResponse) throws CollectException {

    try {
      PublishmentRequestedMessage publishmentRequestedMessage = PublishmentRequestedMessage.builder()
          .banksaladUserId(banksaladUserId)
          .organizationId(organizationId)
          .syncRequestId(syncRequestId)
          .apiResponseBody(objectMapper.writeValueAsString(investApiResponse))
          .build();

      investMessageService.producePublishmentRequested(publishmentRequestedMessage)
          .addCallback(new ListenableFutureCallback<>() {
            @Override
            public void onSuccess(SendResult<String, String> result) {
              log.info("[collectmydata-invest] produce PublishmentRequested syncRequestId: {} ", syncRequestId);
            }

            @Override
            public void onFailure(Throwable t) {
              log.error("[collectmydata-invest] fail to produce PublishmentRequested syncRequestId: {}, exception: {}",
                  syncRequestId, t.getMessage());
            }
          });

    } catch (JsonProcessingException e) {
      log.error("Fail to publish: {}", e.getMessage());
      throw new CollectException("Fail write publish message", e);
    }
  }
}
