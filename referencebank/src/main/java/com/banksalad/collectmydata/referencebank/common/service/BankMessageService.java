package com.banksalad.collectmydata.referencebank.common.service;

import com.banksalad.collectmydata.common.message.PublishmentRequestedMessage;
import com.banksalad.collectmydata.common.message.SyncCompletedMessage;

import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;

public interface BankMessageService {

  ListenableFuture<SendResult<String, String>> producePublishmentRequested(
      PublishmentRequestedMessage publishmentRequestedMessage);

  ListenableFuture<SendResult<String, String>> produceSyncCompleted(SyncCompletedMessage syncCompletedMessage);
}
