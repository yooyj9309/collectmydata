package com.banksalad.collectmydata.insu.loan;

import org.springframework.stereotype.Component;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.enums.FinanceIndustry;
import com.banksalad.collectmydata.common.enums.FinanceSyncItem;
import com.banksalad.collectmydata.common.message.MessageTopic;
import com.banksalad.collectmydata.common.message.PublishmentRequestedInsuMessage;
import com.banksalad.collectmydata.common.message.PublishmentRequestedMessage;
import com.banksalad.collectmydata.finance.api.transaction.TransactionPublishmentHelper;
import com.banksalad.collectmydata.insu.summary.dto.LoanSummary;

@Component
public class LoanTransactionPublishmentHelper implements TransactionPublishmentHelper<LoanSummary> {

  @Override
  public String getMessageTopic() {
    return MessageTopic.insuPublishmentRequested;
  }

  @Override
  public PublishmentRequestedMessage makePublishmentRequestedMessage(ExecutionContext executionContext,
      LoanSummary loanSummary, boolean hasNextPage) {

    return PublishmentRequestedInsuMessage.builder()
        .financeIndustry(FinanceIndustry.INSU)
        .financeSyncItem(FinanceSyncItem.COLLECTMYDATAINSU_SYNC_ITEM_LOAN_TRANSACTION)
        .banksaladUserId(executionContext.getBanksaladUserId())
        .organizationId(executionContext.getOrganizationId())
        .syncRequestId(executionContext.getSyncRequestId())
        .accountNum(loanSummary.getAccountNum())
        .hasNextPage(hasNextPage)
        .build();
  }
}
