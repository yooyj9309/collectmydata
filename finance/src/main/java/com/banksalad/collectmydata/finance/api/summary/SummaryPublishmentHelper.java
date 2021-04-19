package com.banksalad.collectmydata.finance.api.summary;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.message.PublishmentRequestedMessage;

public interface SummaryPublishmentHelper {

  String getMessageTopic();

  PublishmentRequestedMessage makePublishmentRequestedMessage(ExecutionContext executionContext);
}
