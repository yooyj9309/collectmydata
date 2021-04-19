package com.banksalad.collectmydata.finance.api.userbase;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.message.PublishmentRequestedMessage;

public interface UserbasePublishmentHelper {

  String getMessageTopic();

  PublishmentRequestedMessage makePublishmentRequestedMessage(ExecutionContext executionContext);
}
