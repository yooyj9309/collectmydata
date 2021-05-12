package com.banksalad.collectmydata.card.card.bill;

import org.springframework.stereotype.Component;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.enums.FinanceIndustry;
import com.banksalad.collectmydata.common.enums.FinanceSyncItem;
import com.banksalad.collectmydata.common.message.MessageTopic;
import com.banksalad.collectmydata.common.message.PublishmentRequestedCardMessage;
import com.banksalad.collectmydata.common.message.PublishmentRequestedMessage;
import com.banksalad.collectmydata.finance.api.bill.BillPublishmentHelper;

@Component
public class BillBasicPublishmentHelper implements BillPublishmentHelper {

  private static final FinanceIndustry financeIndustry = FinanceIndustry.CARD;
  private static final FinanceSyncItem financeSyncItem = FinanceSyncItem.COLLECTMYDATACARD_SYNC_ITEM_CARD_BILL_BASIC;

  @Override
  public String getMessageTopic() {
    return MessageTopic.cardPublishmentRequested;
  }

  @Override
  public PublishmentRequestedMessage makePublishmentRequestedMessage(ExecutionContext executionContext, boolean hasNextPage) {
    return PublishmentRequestedCardMessage.builder()
        .financeIndustry(financeIndustry)
        .financeSyncItem(financeSyncItem)
        .banksaladUserId(executionContext.getBanksaladUserId())
        .organizationId(executionContext.getOrganizationId())
        .syncRequestId(executionContext.getSyncRequestId())
        .cardId("")
        .hasNextPage(hasNextPage).build();
  }
}
