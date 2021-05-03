package com.banksalad.collectmydata.referencebank.deposit;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.enums.FinanceIndustry;
import com.banksalad.collectmydata.common.enums.FinanceSyncItem;
import com.banksalad.collectmydata.common.message.MessageTopic;
import com.banksalad.collectmydata.common.message.PublishmentRequestedBankMessage;
import com.banksalad.collectmydata.common.message.PublishmentRequestedMessage;
import com.banksalad.collectmydata.finance.api.transaction.TransactionPublishmentHelper;
import com.banksalad.collectmydata.referencebank.summary.dto.AccountSummary;

public class DepositAccountTransactionPublishmentHelper implements TransactionPublishmentHelper<AccountSummary> {

  private static final FinanceSyncItem financeSyncItem = FinanceSyncItem.COLLECTMYDATABANK_SYNC_ITEM_DEPOSIT_ACCOUNT_TRANSACTION;

  @Override
  public String getMessageTopic() {
    return MessageTopic.bankPublishmentRequested;
  }

  @Override
  public PublishmentRequestedMessage makePublishmentRequestedMessage(ExecutionContext executionContext,
      AccountSummary accountSummary, boolean hasNextPage) {

    return PublishmentRequestedBankMessage.builder()
        .financeIndustry(FinanceIndustry.BANK)
        .financeSyncItem(financeSyncItem)
        .banksaladUserId(executionContext.getBanksaladUserId())
        .organizationId(executionContext.getOrganizationId())
        .syncRequestId(executionContext.getSyncRequestId())
        .accountNum(accountSummary.getAccountNum())
        .seqno(accountSummary.getSeqno())
        .hasNextPage(hasNextPage)
        .build();
  }
}
