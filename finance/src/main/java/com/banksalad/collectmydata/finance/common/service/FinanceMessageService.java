package com.banksalad.collectmydata.finance.common.service;

import com.banksalad.collectmydata.common.message.PublishmentRequestedMessage;
import com.banksalad.collectmydata.common.message.SyncCompletedMessage;

public interface FinanceMessageService {

  void producePublishmentRequested(String messageTopic, PublishmentRequestedMessage publishmentRequestedMessage);

  void produceSyncCompleted(String messageTopic, SyncCompletedMessage syncCompletedMessage);
}
