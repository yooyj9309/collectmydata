package com.banksalad.collectmydata.collect.consumer.invest;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.banksalad.collectmydata.common.enums.Industry;
import com.banksalad.collectmydata.common.enums.Sector;
import com.banksalad.collectmydata.common.logging.LoggingMdcUtil;
import com.banksalad.collectmydata.common.message.ConsumerGroupId;
import com.banksalad.collectmydata.common.message.MessageTopic;
import com.banksalad.collectmydata.common.message.PublishmentRequestedInvestMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.banksalad.idl.apis.v1.finance.FinanceGrpc.FinanceStub;
import com.github.banksalad.idl.apis.v1.finance.FinanceProto.NotifyCollectmydatainvestSyncedResponse;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class InvestPublishmentRequestedConsumer {

  private final ObjectMapper objectMapper;
  private final FinanceStub financeStub;

  @KafkaListener(
      topics = MessageTopic.investPublishmentRequested,
      containerFactory = "investPublishmentRequestedKafkaListenerContainerFactory",
      groupId = ConsumerGroupId.collectConsumerGroupId)
  public void consume(String source) {

    try {
      /* deserialize message */
      PublishmentRequestedInvestMessage message = objectMapper.readValue(source, PublishmentRequestedInvestMessage.class);

      LoggingMdcUtil.set(Sector.FINANCE.name(), Industry.INVEST.name(), message.getBanksaladUserId(), message.getOrganizationId(),
          message.getSyncRequestId());
      log.info("[collect] consume PublishmentRequestedBankMessage syncRequestId: {} ", message.getSyncRequestId());

      /* notify */
      financeStub.notifyCollectmydatainvestSynced(message.toNotifyRequest(),
          new StreamObserver<NotifyCollectmydatainvestSyncedResponse>() {
            @Override
            public void onNext(NotifyCollectmydatainvestSyncedResponse value) {

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
