package com.banksalad.collectmydata.invest.mock;

import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.banksalad.collectmydata.common.enums.Industry;
import com.banksalad.collectmydata.common.enums.Sector;
import com.banksalad.collectmydata.common.logging.LoggingMdcUtil;
import com.banksalad.collectmydata.common.message.MessageTopic;
import com.banksalad.collectmydata.common.message.SyncRequestedMessage;
import com.banksalad.collectmydata.finance.common.exception.ResponseNotOkException;
import com.banksalad.collectmydata.invest.InvestApiService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CountDownLatch;

@Slf4j
@Component
@Profile("test")
@RequiredArgsConstructor
public class MockInvestSyncRequestedConsumer {

  private final ObjectMapper objectMapper;
  private final InvestApiService investApiService;

  private CountDownLatch latch = new CountDownLatch(1);

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

      investApiService
          .onDemandRequestApi(message.getBanksaladUserId(), message.getOrganizationId(), message.getSyncRequestId(),
              message.getSyncRequestType());

    } catch (ResponseNotOkException e) {

      log.error("Fail to sync: {}", e.getMessage());
      e.printStackTrace();
    } catch (Throwable t) {

      log.error("Fail to sync: {}", t.getMessage());
      t.printStackTrace();
    } finally {

      LoggingMdcUtil.clear();
      latch.countDown();
    }
  }

  public CountDownLatch getLatch() {
    return latch;
  }
}
