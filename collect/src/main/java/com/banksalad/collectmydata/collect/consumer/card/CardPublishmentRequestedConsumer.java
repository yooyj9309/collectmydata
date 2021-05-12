package com.banksalad.collectmydata.collect.consumer.card;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.banksalad.collectmydata.common.enums.Industry;
import com.banksalad.collectmydata.common.enums.Sector;
import com.banksalad.collectmydata.common.logging.LoggingMdcUtil;
import com.banksalad.collectmydata.common.message.ConsumerGroupId;
import com.banksalad.collectmydata.common.message.MessageTopic;
import com.banksalad.collectmydata.common.message.PublishmentRequestedCardMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.banksalad.idl.apis.v1.finance.FinanceGrpc.FinanceStub;
import com.github.banksalad.idl.apis.v1.finance.FinanceProto.NotifyCollectmydatacardSyncedResponse;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class CardPublishmentRequestedConsumer {

  private final ObjectMapper objectMapper;
  private final FinanceStub financeStub;

  @KafkaListener(
      topics = MessageTopic.cardPublishmentRequested,
      containerFactory = "cardPublishmentRequestedKafkaListenerContainerFactory",
      groupId = ConsumerGroupId.collectConsumerGroupId)
  public void consume(String source) {

    try {
      /* deserialize message */
      PublishmentRequestedCardMessage message = objectMapper
          .readValue(source, PublishmentRequestedCardMessage.class);

      LoggingMdcUtil.set(Sector.FINANCE.name(), Industry.CARD.name(), message.getBanksaladUserId(),
          message.getOrganizationId(), message.getSyncRequestId());

      log.info("[collect][card] consume PublishmentRequestedCardMessage financeSyncItem: {}, syncRequestId: {} ",
          message.getFinanceSyncItem(), message.getSyncRequestId());

      /* notify */
      financeStub.notifyCollectmydatacardSynced(message.toNotifyRequest(),
          new StreamObserver<NotifyCollectmydatacardSyncedResponse>() {
            @Override
            public void onNext(NotifyCollectmydatacardSyncedResponse value) {

            }

            @Override
            public void onError(Throwable t) {
              log.error(
                  "[collect][card][publishment] error while notifying to finance. financeSyncItem: {}, syncRequestId: {}, t: {} ",
                  message.getFinanceSyncItem(), message.getSyncRequestId(), t.getMessage());
            }

            @Override
            public void onCompleted() {
              log.info("[collect][card][publishment] notified to finance. financeSyncItem: {},syncRequestId: {}",
                  message.getFinanceSyncItem(), message.getSyncRequestId());
            }
          });


    } catch (JsonProcessingException e) {
      log.error("[card] Fail to deserialize PublishmentRequestedCardMessage: {}", e.getMessage());

    } finally {
      LoggingMdcUtil.clear();
    }

  }

}
