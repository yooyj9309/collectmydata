package com.banksalad.collectmydata.bank.mock;

import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.banksalad.collectmydata.bank.BankApiService;
import com.banksalad.collectmydata.common.enums.Industry;
import com.banksalad.collectmydata.common.enums.Sector;
import com.banksalad.collectmydata.common.logging.LoggingMdcUtil;
import com.banksalad.collectmydata.common.message.ConsumerGroupId;
import com.banksalad.collectmydata.common.message.MessageTopic;
import com.banksalad.collectmydata.common.message.SyncRequestedMessage;
import com.banksalad.collectmydata.finance.common.exception.ResponseNotOkException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CountDownLatch;

@Slf4j
@Component
@Profile("test")
@RequiredArgsConstructor
public class MockBankSyncRequestedConsumer {

  private final ObjectMapper objectMapper;
  private final BankApiService bankApiService;

  private final CountDownLatch latch = new CountDownLatch(1);

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

      bankApiService.requestApi(message.getBanksaladUserId(), message.getOrganizationId(), message.getSyncRequestId(),
          message.getSyncRequestType());

    } catch (JsonProcessingException e) {
      log.error("Fail to deserialize syncRequestedMessage: {}", e.getMessage(), e);
      e.printStackTrace();
    } catch (ResponseNotOkException e) {
      log.error("Fail to sync: {}", e.getMessage(), e);
      // TODO publish result with error code and message
      e.printStackTrace();
    } catch (Throwable t) {
      log.error("Fail to sync: {}", t.getMessage(), t);
      // TODO publish result with error code and message
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
