package com.banksalad.collectmydata.finance.api.transaction;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.message.PublishmentRequestedMessage;

public interface TransactionPublishmentHelper<Summary> {

  String getMessageTopic();

  PublishmentRequestedMessage makePublishmentRequestedMessage(ExecutionContext executionContext, Summary summary,
      boolean hasNextPage);
}
