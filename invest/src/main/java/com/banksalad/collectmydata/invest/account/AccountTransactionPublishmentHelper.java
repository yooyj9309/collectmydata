package com.banksalad.collectmydata.invest.account;

import org.springframework.stereotype.Component;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.enums.FinanceIndustry;
import com.banksalad.collectmydata.common.enums.FinanceSyncItem;
import com.banksalad.collectmydata.common.message.MessageTopic;
import com.banksalad.collectmydata.common.message.PublishmentRequestedInvestMessage;
import com.banksalad.collectmydata.common.message.PublishmentRequestedMessage;
import com.banksalad.collectmydata.finance.api.transaction.TransactionPublishmentHelper;
import com.banksalad.collectmydata.invest.summary.dto.AccountSummary;

@Component
public class AccountTransactionPublishmentHelper implements TransactionPublishmentHelper<AccountSummary> {

  @Override
  public String getMessageTopic() {
    return MessageTopic.investPublishmentRequested;
  }

  @Override
  public PublishmentRequestedMessage makePublishmentRequestedMessage(ExecutionContext executionContext,
      AccountSummary accountSummary, boolean hasNextPage) {
    return PublishmentRequestedInvestMessage.builder()
        .financeIndustry(FinanceIndustry.BANK)
        .financeSyncItem(FinanceSyncItem.COLLECTMYDATAINVEST_SYNC_ITEM_ACCOUNT_TRANSACTION)
        .banksaladUserId(executionContext.getBanksaladUserId())
        .organizationId(executionContext.getOrganizationId())
        .syncRequestId(executionContext.getSyncRequestId())
        .accountNum(accountSummary.getAccountNum())
        .hasNextPage(hasNextPage)
        .build();
  }
}
