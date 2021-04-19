package com.banksalad.collectmydata.telecom;

import com.banksalad.collectmydata.common.enums.Industry;
import com.banksalad.collectmydata.common.enums.Sector;
import com.banksalad.collectmydata.common.exception.CollectException;
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
public class TelecomSyncRequestedConsumer {

  private final ObjectMapper objectMapper;
  private final TelecomApiService telecomApiService;

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

      /* 1. request api */
      switch (message.getSyncRequestType()) {
        case ONDEMAND:
          telecomApiService.onDemandRequestApi(message.getBanksaladUserId(), message.getOrganizationId(),
              message.getSyncRequestId());
          break;
        case SCHEDULED_BASIC:
          telecomApiService
              .scheduledBasicRequestApi(message.getBanksaladUserId(), message.getOrganizationId(),
                  message.getSyncRequestId());
          break;
        case SCHEDULED_ADDITIONAL:
          telecomApiService
              .scheduledAdditionalRequestApi(message.getBanksaladUserId(), message.getOrganizationId(),
                  message.getSyncRequestId());
          break;
        default:
          log.error("Fail to specify RequestType: {}", message.getSyncRequestType());
          throw new CollectException("undefined syncResultType"); // TODO Exception 처리는 모니터링 작업과 같이 진행
      }
    } catch (ResponseNotOkException e) {
      log.error("Fail to sync: {}", e.getMessage());
      // TODO publish result with error code and message

    } catch (Throwable t) {
      log.error("Fail to sync: {}", t.getMessage());
      // TODO publish result with error code and message
    } finally {
      LoggingMdcUtil.clear();
    }
  }


}
