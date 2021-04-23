package com.banksalad.collectmydata.collect.consumer.bank;

import com.banksalad.collectmydata.common.enums.Industry;
import com.banksalad.collectmydata.common.enums.Sector;
import com.banksalad.collectmydata.common.logging.LoggingMdcUtil;
import com.banksalad.collectmydata.common.message.ConsumerGroupId;
import com.banksalad.collectmydata.common.message.MessageTopic;
import com.banksalad.collectmydata.common.message.PublishmentRequestedBankMessage;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.banksalad.idl.apis.v1.finance.FinanceGrpc.FinanceStub;
import com.github.banksalad.idl.apis.v1.finance.FinanceProto.NotifyCollectmydatabankSyncedResponse;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class BankPublishmentRequestedConsumer {

  private final ObjectMapper objectMapper;
  private final FinanceStub financeStub;

  @KafkaListener(
      topics = MessageTopic.bankPublishmentRequested,
      containerFactory = "kafkaListenerContainerFactory",
      groupId = ConsumerGroupId.collectConsumerGroupId)
  public void consume(String source) {

    try {
      /* deserialize message */
      PublishmentRequestedBankMessage message = objectMapper.readValue(source, PublishmentRequestedBankMessage.class);

      LoggingMdcUtil.set(Sector.FINANCE.name(), Industry.BANK.name(), message.getBanksaladUserId(), message.getOrganizationId(),
          message.getSyncRequestId());
      log.debug("[collect] consume PublishmentRequestedBankMessage syncRequestId: {} ", message.getSyncRequestId());

      /* notify */
      financeStub.notifyCollectmydatabankSynced(message.toNotifyRequest(),
          new StreamObserver<NotifyCollectmydatabankSyncedResponse>() {
            @Override
            public void onNext(NotifyCollectmydatabankSyncedResponse value) {

            }

            @Override
            public void onError(Throwable t) {
              log.error("[collect] error while notifying to finance syncRequestId: {}, t: {} ",
                  message.getSyncRequestId(), t.getMessage());
            }

            @Override
            public void onCompleted() {
              log.debug("[collect] notified to finance syncRequestId: {} ", message.getSyncRequestId());
            }
          });

    } catch (JsonProcessingException e) {
      log.error("Fail to deserialize PublishmentRequestedBankMessage: {}", e.getMessage());

    } finally {
      LoggingMdcUtil.clear();
    }
  }
}
