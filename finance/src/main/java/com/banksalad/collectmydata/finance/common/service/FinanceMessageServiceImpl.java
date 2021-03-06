package com.banksalad.collectmydata.finance.common.service;

import com.banksalad.collectmydata.common.exception.CollectRuntimeException;
import com.banksalad.collectmydata.common.message.PublishmentRequestedMessage;
import com.banksalad.collectmydata.common.message.SyncCompletedMessage;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFutureCallback;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class FinanceMessageServiceImpl implements FinanceMessageService {

  private final KafkaTemplate<String, String> kafkaTemplate;
  private final ObjectMapper objectMapper;

  @Override
  public void producePublishmentRequested(String messageTopic, PublishmentRequestedMessage publishmentRequestedMessage) {

    try {
      kafkaTemplate.send(
          messageTopic,
          String.valueOf(publishmentRequestedMessage.getBanksaladUserId()),
          objectMapper.writeValueAsString(publishmentRequestedMessage)
      ).addCallback(new ListenableFutureCallback<>() {
        @Override
        public void onSuccess(SendResult<String, String> result) {
          log.info("Produce PublishmentRequestedMessage. messageTopic: {}, syncRequestId: {} ",
              messageTopic, publishmentRequestedMessage.getSyncRequestId());
        }

        @Override
        public void onFailure(Throwable t) {
          log.error("Fail to produce PublishmentRequestedMessage. messageTopic: {},syncRequestId: {}, exception: {}",
              messageTopic, publishmentRequestedMessage.getSyncRequestId(), t.getMessage(), t);
        }
      });
    } catch (JsonProcessingException e) {
      throw new CollectRuntimeException("Fail to serialize message", e);
    }
  }

  @Override
  public void produceSyncCompleted(String messageTopic, SyncCompletedMessage syncCompletedMessage) {
    try {
      kafkaTemplate.send(messageTopic, String.valueOf(syncCompletedMessage.getBanksaladUserId()),
          objectMapper.writeValueAsString(syncCompletedMessage))
          .addCallback(new ListenableFutureCallback<>() {
            @Override
            public void onSuccess(SendResult<String, String> result) {
              log.info("Produce syncCompletedMessage. messageTopic: {}, syncRequestId: {} ",
                  messageTopic, syncCompletedMessage.getSyncRequestId());
            }

            @Override
            public void onFailure(Throwable t) {
              log.error("Fail to produce syncCompletedMessage. messageTopic: {}, syncRequestId: {}, exception: {}",
                  messageTopic, syncCompletedMessage.getSyncRequestId(), t.getMessage(), t);
            }
          });
    } catch (JsonProcessingException e) {
      throw new CollectRuntimeException("Fail to serialize message", e);
    }
  }
}
