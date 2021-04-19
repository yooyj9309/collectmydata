package com.banksalad.collectmydata.bank.loan;

import com.banksalad.collectmydata.bank.summary.dto.AccountSummary;
import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.enums.FinanceIndustry;
import com.banksalad.collectmydata.common.enums.FinanceSyncItem;
import com.banksalad.collectmydata.common.message.MessageTopic;
import com.banksalad.collectmydata.common.message.PublishmentRequestedBankMessage;
import com.banksalad.collectmydata.common.message.PublishmentRequestedMessage;
import com.banksalad.collectmydata.finance.api.transaction.TransactionPublishmentHelper;

public class LoanAccountTransactionPublishmentHelper implements TransactionPublishmentHelper<AccountSummary> {

  private static final FinanceIndustry financeIndustry = FinanceIndustry.BANK;
  private static final FinanceSyncItem financeSyncItem = FinanceSyncItem.COLLECTMYDATA_BANK_SYNC_ITEM_LOAN_ACCOUNT_TRANSACTION;

  @Override
  public String getMessageTopic() {
    return MessageTopic.bankPublishmentRequested;
  }

  @Override
  public PublishmentRequestedMessage makePublishmentRequestedMessage(ExecutionContext executionContext,
      AccountSummary accountSummary, boolean hasNextPage) {

    return PublishmentRequestedBankMessage.builder()
        .financeIndustry(financeIndustry)
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
