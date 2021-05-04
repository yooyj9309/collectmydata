package com.banksalad.collectmydata.insu.insurance;

import org.springframework.stereotype.Component;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.enums.FinanceIndustry;
import com.banksalad.collectmydata.common.enums.FinanceSyncItem;
import com.banksalad.collectmydata.common.message.MessageTopic;
import com.banksalad.collectmydata.common.message.PublishmentRequestedInsuMessage;
import com.banksalad.collectmydata.common.message.PublishmentRequestedMessage;
import com.banksalad.collectmydata.finance.api.accountinfo.AccountInfoPublishmentHelper;

@Component
public class InsurancePaymentPublishmentHelper implements AccountInfoPublishmentHelper {

  @Override
  public String getMessageTopic() {
    return MessageTopic.insuPublishmentRequested;
  }

  @Override
  public PublishmentRequestedMessage makePublishmentRequestedMessage(ExecutionContext executionContext) {
    return PublishmentRequestedInsuMessage.builder()
        .financeIndustry(FinanceIndustry.INSU)
        .financeSyncItem(FinanceSyncItem.COLLECTMYDATAINSU_SYNC_ITEM_INSURANCE_PAYMENT)
        .banksaladUserId(executionContext.getBanksaladUserId())
        .organizationId(executionContext.getOrganizationId())
        .syncRequestId(executionContext.getSyncRequestId())
        .build();
  }
}
