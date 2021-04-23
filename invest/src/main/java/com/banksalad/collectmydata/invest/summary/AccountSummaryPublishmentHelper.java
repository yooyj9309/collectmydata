package com.banksalad.collectmydata.invest.summary;

import org.springframework.stereotype.Component;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.enums.FinanceIndustry;
import com.banksalad.collectmydata.common.enums.FinanceSyncItem;
import com.banksalad.collectmydata.common.message.MessageTopic;
import com.banksalad.collectmydata.common.message.PublishmentRequestedInvestMessage;
import com.banksalad.collectmydata.common.message.PublishmentRequestedMessage;
import com.banksalad.collectmydata.finance.api.summary.SummaryPublishmentHelper;

@Component
public class AccountSummaryPublishmentHelper implements SummaryPublishmentHelper {

  @Override
  public String getMessageTopic() {
    return MessageTopic.investPublishmentRequested;
  }

  @Override
  public PublishmentRequestedMessage makePublishmentRequestedMessage(ExecutionContext executionContext) {
    return PublishmentRequestedInvestMessage.builder()
        .financeIndustry(FinanceIndustry.INVEST)
        .financeSyncItem(FinanceSyncItem.COLLECTMYDATA_INVEST_SYNC_ITEM_ACCOUNT_SUMMARY)
        .banksaladUserId(executionContext.getBanksaladUserId())
        .organizationId(executionContext.getOrganizationId())
        .syncRequestId(executionContext.getSyncRequestId())
        .build();
  }
}
