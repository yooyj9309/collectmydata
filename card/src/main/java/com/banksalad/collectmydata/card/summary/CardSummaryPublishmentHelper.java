package com.banksalad.collectmydata.card.summary;


import org.springframework.stereotype.Component;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.enums.FinanceIndustry;
import com.banksalad.collectmydata.common.enums.FinanceSyncItem;
import com.banksalad.collectmydata.common.message.MessageTopic;
import com.banksalad.collectmydata.common.message.PublishmentRequestedCardMessage;
import com.banksalad.collectmydata.common.message.PublishmentRequestedMessage;
import com.banksalad.collectmydata.finance.api.summary.SummaryPublishmentHelper;

@Component
public class CardSummaryPublishmentHelper implements SummaryPublishmentHelper {

  private static final FinanceIndustry financeIndustry = FinanceIndustry.CARD;
  private static final FinanceSyncItem financeSyncItem = FinanceSyncItem.COLLECTMYDATA_CARD_SYNC_ITEM_ACCOUNT_SUMMARY;

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
        .syncRequestId(executionContext.getSyncRequestId())
        .build();
  }
}
