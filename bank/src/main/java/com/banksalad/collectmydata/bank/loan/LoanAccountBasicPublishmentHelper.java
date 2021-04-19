package com.banksalad.collectmydata.bank.loan;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.enums.FinanceIndustry;
import com.banksalad.collectmydata.common.enums.FinanceSyncItem;
import com.banksalad.collectmydata.common.message.MessageTopic;
import com.banksalad.collectmydata.common.message.PublishmentRequestedBankMessage;
import com.banksalad.collectmydata.common.message.PublishmentRequestedMessage;
import com.banksalad.collectmydata.finance.api.accountinfo.AccountInfoPublishmentHelper;

public class LoanAccountBasicPublishmentHelper implements AccountInfoPublishmentHelper {

  private static final FinanceIndustry financeIndustry = FinanceIndustry.BANK;
  private static final FinanceSyncItem financeSyncItem = FinanceSyncItem.COLLECTMYDATA_BANK_SYNC_ITEM_LOAN_ACCOUNT_BASIC;

  @Override
  public String getMessageTopic() {
    return MessageTopic.bankPublishmentRequested;
  }

  @Override
  public PublishmentRequestedMessage makePublishmentRequestedMessage(ExecutionContext executionContext) {
    return PublishmentRequestedBankMessage.builder()
        .financeIndustry(financeIndustry)
        .financeSyncItem(financeSyncItem)
        .banksaladUserId(executionContext.getBanksaladUserId())
        .organizationId(executionContext.getOrganizationId())
        .syncRequestId(executionContext.getSyncRequestId())
        .build();
  }
}
