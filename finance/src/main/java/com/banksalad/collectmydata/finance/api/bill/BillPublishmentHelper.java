package com.banksalad.collectmydata.finance.api.bill;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.message.PublishmentRequestedMessage;

public interface BillPublishmentHelper {

  String getMessageTopic();

  PublishmentRequestedMessage makePublishmentRequestedMessage(ExecutionContext executionContext);
}
