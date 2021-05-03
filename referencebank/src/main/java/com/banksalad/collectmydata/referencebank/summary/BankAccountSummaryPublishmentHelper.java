package com.banksalad.collectmydata.referencebank.summary;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.enums.FinanceIndustry;
import com.banksalad.collectmydata.common.enums.FinanceSyncItem;
import com.banksalad.collectmydata.common.message.MessageTopic;
import com.banksalad.collectmydata.common.message.PublishmentRequestedBankMessage;
import com.banksalad.collectmydata.common.message.PublishmentRequestedMessage;
import com.banksalad.collectmydata.finance.api.summary.SummaryPublishmentHelper;

public class BankAccountSummaryPublishmentHelper implements SummaryPublishmentHelper {

  private static final FinanceSyncItem financeSyncItem = FinanceSyncItem.COLLECTMYDATABANK_SYNC_ITEM_ACCOUNT_SUMMARY;

  @Override
  public String getMessageTopic() {
    return MessageTopic.bankPublishmentRequested;
  }

  @Override
  public PublishmentRequestedMessage makePublishmentRequestedMessage(ExecutionContext executionContext) {
    return PublishmentRequestedBankMessage.builder()
        .financeIndustry(FinanceIndustry.BANK)
        .financeSyncItem(financeSyncItem)
        .banksaladUserId(executionContext.getBanksaladUserId())
        .organizationId(executionContext.getOrganizationId())
        .syncRequestId(executionContext.getSyncRequestId())
        .build();
  }
}
