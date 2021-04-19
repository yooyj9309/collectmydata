package com.banksalad.collectmydata.finance.api.accountinfo;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.message.PublishmentRequestedMessage;

public interface AccountInfoPublishmentHelper {

  String getMessageTopic();

  PublishmentRequestedMessage makePublishmentRequestedMessage(ExecutionContext executionContext);
}
