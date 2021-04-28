package com.banksalad.collectmydata.collect.common.service;

import com.banksalad.collectmydata.common.message.SyncRequestedMessage;


public interface CollectMessageService {

  void produceBankSyncRequested(SyncRequestedMessage syncRequestedMessage);

  void produceInvestSyncRequested(SyncRequestedMessage syncRequestedMessage);

  void produceCardSyncRequested(SyncRequestedMessage syncRequestedMessage);

}
