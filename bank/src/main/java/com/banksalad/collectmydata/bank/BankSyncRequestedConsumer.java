package com.banksalad.collectmydata.bank;

import org.springframework.stereotype.Component;

import com.banksalad.collectmydata.common.exception.CollectException;
import com.banksalad.collectmydata.common.logging.LoggingMdcUtil;
import com.banksalad.collectmydata.common.message.SyncRequestedMessage;
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

  public void consumeSyncRequested(String source) {
    try {
      /* deserialize message */
      SyncRequestedMessage message = objectMapper.readValue(source, SyncRequestedMessage.class);

      LoggingMdcUtil.set(message.getBanksaladUserId(), message.getOrganizationId(), message.getSyncRequestId());
      log.info("[collectmydata-bank] consume SyncRequested syncRequestId: {} ", message.getSyncRequestId());

      /* request api */
      bankApiService.requestApi(message.getBanksaladUserId(),
          message.getOrganizationId(), message.getSyncRequestId(), message.getSyncRequestType());

      // TODO jayden-lee product publishmentRequested 구현, kafka 메시지 버전 관리를 어떻게 할지?

    } catch (JsonProcessingException e) {
      log.error("Fail to deserialize syncRequestedMessage: {}", e.getMessage(), e);

    } catch (CollectException e) {
      log.error("Fail to sync: {}", e.getMessage(), e);

    } finally {
      LoggingMdcUtil.clear();
    }
  }
}
