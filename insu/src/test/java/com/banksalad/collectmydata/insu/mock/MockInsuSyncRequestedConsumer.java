package com.banksalad.collectmydata.insu.mock;

import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.banksalad.collectmydata.common.enums.Industry;
import com.banksalad.collectmydata.common.enums.Sector;
import com.banksalad.collectmydata.common.exception.CollectException;
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
public class MockInsuSyncRequestedConsumer {

  private final ObjectMapper objectMapper;
  private final MockInsuApiServiceImpl mockInsuApiService;

  private CountDownLatch latch = new CountDownLatch(1);

  @KafkaListener(
      topics = MessageTopic.insuSyncRequested,
      containerFactory = "insuSyncRequestedKafkaListenerContainerFactory",
      groupId = ConsumerGroupId.collectmydataFinanceInsu)
  public void consumeSyncRequested(String source) {

    try {

      SyncRequestedMessage message = objectMapper.readValue(source, SyncRequestedMessage.class);

      LoggingMdcUtil.set(Sector.FINANCE.name(), Industry.INSU.name(), message.getBanksaladUserId(),
          message.getOrganizationId(), message.getSyncRequestId());

      log.info("[collectmydata-insu] consume SyncRequested syncRequestId: {} ", message.getSyncRequestId());

      /* 1. request api */
      switch (message.getSyncRequestType()) {
        case ONDEMAND:
          mockInsuApiService.onDemandRequestApi(message.getBanksaladUserId(), message.getOrganizationId(),
              message.getSyncRequestId());
          break;
        case SCHEDULED_BASIC:
          mockInsuApiService
              .scheduledBasicRequestApi(message.getBanksaladUserId(), message.getOrganizationId(),
                  message.getSyncRequestId());
          break;
        case SCHEDULED_ADDITIONAL:
          mockInsuApiService
              .scheduledAdditionalRequestApi(message.getBanksaladUserId(), message.getOrganizationId(),
                  message.getSyncRequestId());
          break;
        default:
          log.error("Fail to specify RequestType: {}", message.getSyncRequestType());
          throw new CollectException("undefined syncResultType"); // TODO Exception 처리는 모니터링 작업과 같이 진행
      }

    } catch (JsonProcessingException e) {

      log.error("Fail to deserialize syncRequestedMessage: {}", e.getMessage());
      e.printStackTrace();
    } catch (ResponseNotOkException e) {

      log.error("Fail to sync: {}", e.getMessage());
      e.printStackTrace();
      // TODO publish result with error code and message
    } catch (CollectException e) {

      log.error("Fail to sync: {}", e.getMessage());
      e.printStackTrace();
      // TODO publish result with error code and message
    } catch (Throwable t) {

      log.error("Fail to sync: {}", t.getMessage());
      t.printStackTrace();
      // TODO publish result with error code and message
    } finally {

      LoggingMdcUtil.clear();
      latch.countDown();
    }
  }

  public CountDownLatch getLatch() {
    return latch;
  }
}
