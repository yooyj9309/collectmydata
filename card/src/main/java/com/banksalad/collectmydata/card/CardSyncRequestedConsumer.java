package com.banksalad.collectmydata.card;

import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.banksalad.collectmydata.common.enums.Industry;
import com.banksalad.collectmydata.common.enums.Sector;
import com.banksalad.collectmydata.common.logging.LoggingMdcUtil;
import com.banksalad.collectmydata.common.message.ConsumerGroupId;
import com.banksalad.collectmydata.common.message.MessageTopic;
import com.banksalad.collectmydata.common.message.SyncRequestedMessage;
import com.banksalad.collectmydata.finance.common.exception.ResponseNotOkException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@Profile("!test")
@RequiredArgsConstructor
public class CardSyncRequestedConsumer {

  private final ObjectMapper objectMapper;
  private final CardApiService cardApiService;

  @KafkaListener(
      topics = MessageTopic.cardSyncRequested,
      containerFactory = "cardSyncRequestedKafkaListenerContainerFactory",
      groupId = ConsumerGroupId.collectmydataFinanceCard
  )
  public void consumeCardSyncRequested(String source) {
    try {
      SyncRequestedMessage message = objectMapper.readValue(source, SyncRequestedMessage.class);

      LoggingMdcUtil.set(Sector.FINANCE.name(), Industry.CARD.name(), message.getBanksaladUserId(),
          message.getOrganizationId(), message.getSyncRequestId());

      log.info("Consume SyncRequestedMessage. syncRequestId: {} ", message.getSyncRequestId());

      cardApiService.requestApi(message.getBanksaladUserId(), message.getOrganizationId(), message.getSyncRequestId(),
          message.getSyncRequestType());

    } catch (JsonProcessingException e) {
      log.error("Fail to deserialize syncRequestedMessage: {}", e.getMessage(), e);

    } catch (ResponseNotOkException e) {
      log.error("Fail to sync: {}", e.getMessage(), e);
      // TODO publish result with error code and message

    } catch (Throwable t) {
      log.error("Fail to sync: {}", t.getMessage(), t);
      // TODO publish result with error code and message

    } finally {
      LoggingMdcUtil.clear();
    }
  }

}
