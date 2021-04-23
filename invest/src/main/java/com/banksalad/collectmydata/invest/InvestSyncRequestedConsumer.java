package com.banksalad.collectmydata.invest;

import com.banksalad.collectmydata.common.enums.Industry;
import com.banksalad.collectmydata.common.enums.Sector;
import com.banksalad.collectmydata.common.logging.LoggingMdcUtil;
import com.banksalad.collectmydata.common.message.MessageTopic;
import com.banksalad.collectmydata.common.message.SyncRequestedMessage;
import com.banksalad.collectmydata.finance.common.exception.ResponseNotOkException;

import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@Profile("!test")
@RequiredArgsConstructor
public class InvestSyncRequestedConsumer {

  private final ObjectMapper objectMapper;
  private final InvestApiService investApiService;

  @KafkaListener(
      topics = MessageTopic.investSyncRequested,
      containerFactory = "investSyncRequestedKafkaListenerContainerFactory"
  )
  public void consumeSyncRequested(String source) {
    try {
      SyncRequestedMessage message = objectMapper.readValue(source, SyncRequestedMessage.class);

      LoggingMdcUtil.set(Sector.FINANCE.name(), Industry.INVEST.name(), message.getBanksaladUserId(),
          message.getOrganizationId(), message.getSyncRequestId());

      log.info("[collectmydata-invest] consume SyncRequested syncRequestId: {} ", message.getSyncRequestId());

      investApiService.requestApi(message.getBanksaladUserId(), message.getOrganizationId(), message.getSyncRequestId(),
          message.getSyncRequestType());

    } catch (ResponseNotOkException e) {
      log.error("Fail to sync: {}", e.getMessage());
    } catch (Throwable t) {
      log.error("Fail to sync: {}", t.getMessage());
    } finally {
      LoggingMdcUtil.clear();
    }
  }
}
