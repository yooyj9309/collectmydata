package com.banksalad.collectmydata.card.card.transaction;

import org.springframework.stereotype.Component;

import com.banksalad.collectmydata.card.summary.dto.CardSummary;
import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.enums.FinanceIndustry;
import com.banksalad.collectmydata.common.enums.FinanceSyncItem;
import com.banksalad.collectmydata.common.message.MessageTopic;
import com.banksalad.collectmydata.common.message.PublishmentRequestedCardMessage;
import com.banksalad.collectmydata.common.message.PublishmentRequestedMessage;
import com.banksalad.collectmydata.finance.api.transaction.TransactionPublishmentHelper;

@Component
public class ApprovalOverseasPublishmentHelper implements TransactionPublishmentHelper<CardSummary> {

  private static final FinanceIndustry financeIndustry = FinanceIndustry.CARD;
  private static final FinanceSyncItem financeSyncItem = FinanceSyncItem.COLLECTMYDATACARD_SYNC_ITEM_CARD_APPROVAL_OVERSEAS_TRANSACTION;

  @Override
  public String getMessageTopic() {
    return MessageTopic.cardPublishmentRequested;
  }

  @Override
  public PublishmentRequestedMessage makePublishmentRequestedMessage(ExecutionContext executionContext,
      CardSummary cardSummary, boolean hasNextPage) {
    return PublishmentRequestedCardMessage.builder()
        .financeIndustry(financeIndustry)
        .financeSyncItem(financeSyncItem)
        .banksaladUserId(executionContext.getBanksaladUserId())
        .organizationId(executionContext.getOrganizationId())
        .syncRequestId(executionContext.getSyncRequestId())
        .cardId(cardSummary.getCardId())
        .hasNextPage(hasNextPage).build();
  }
}
