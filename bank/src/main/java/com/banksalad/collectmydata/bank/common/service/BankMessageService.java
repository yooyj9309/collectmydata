package com.banksalad.collectmydata.bank.common.service;

import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;

import com.banksalad.collectmydata.common.message.PublishmentRequestedMessage;

public interface BankMessageService {

  ListenableFuture<SendResult<String, String>> producePublishmentRequested(
      PublishmentRequestedMessage publishmentRequestedMessage);

}
