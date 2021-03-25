package com.banksalad.collectmydata.telecom.common.service;

import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;

import com.banksalad.collectmydata.common.message.PublishmentRequestedMessage;
import com.banksalad.collectmydata.common.message.SyncCompletedMessage;

public interface TelecomMessageService {

  ListenableFuture<SendResult<String, String>> producePublishmentRequested(
      PublishmentRequestedMessage publishmentRequestedMessage);

  ListenableFuture<SendResult<String, String>> produceSyncCompleted(
      SyncCompletedMessage syncCompletedMessage);
}
