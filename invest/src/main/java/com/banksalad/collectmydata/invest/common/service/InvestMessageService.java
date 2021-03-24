package com.banksalad.collectmydata.invest.common.service;

import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;

import com.banksalad.collectmydata.common.message.PublishmentRequestedMessage;

public interface InvestMessageService {

  ListenableFuture<SendResult<String, String>> producePublishmentRequested(
      PublishmentRequestedMessage publishmentRequestedMessage);
}
