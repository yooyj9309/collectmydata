package com.banksalad.collectmydata.collect.common.service;

import com.banksalad.collectmydata.common.message.SyncRequestedMessage;

import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;


public interface CollectMessageService {

  ListenableFuture<SendResult<String, String>> produceBankSyncRequested(SyncRequestedMessage syncRequestedMessage);

}
