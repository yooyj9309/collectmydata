package com.banksalad.collectmydata.card.card.userbase;

import org.springframework.stereotype.Component;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.enums.FinanceIndustry;
import com.banksalad.collectmydata.common.enums.FinanceSyncItem;
import com.banksalad.collectmydata.common.message.MessageTopic;
import com.banksalad.collectmydata.common.message.PublishmentRequestedCardMessage;
import com.banksalad.collectmydata.common.message.PublishmentRequestedMessage;
import com.banksalad.collectmydata.finance.api.userbase.UserbasePublishmentHelper;

@Component
public class LoanLongTermPublishmentHelper implements UserbasePublishmentHelper {

  private final FinanceIndustry financeIndustry = FinanceIndustry.CARD;
  private final FinanceSyncItem financeSyncItem = FinanceSyncItem.COLLECTMYDATACARD_SYNC_ITEM_LOAN_LONG_TERM;

  @Override
  public String getMessageTopic() {
    return MessageTopic.cardPublishmentRequested;
  }

  @Override
  public PublishmentRequestedMessage makePublishmentRequestedMessage(ExecutionContext executionContext) {
    return PublishmentRequestedCardMessage.builder()
        .financeIndustry(financeIndustry)
        .financeSyncItem(financeSyncItem)
        .banksaladUserId(executionContext.getBanksaladUserId())
        .organizationId(executionContext.getOrganizationId())
        .syncRequestId(executionContext.getSyncRequestId()).build();
  }
}