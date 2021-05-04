package com.banksalad.collectmydata.insu.summary;

import org.springframework.stereotype.Component;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.enums.FinanceIndustry;
import com.banksalad.collectmydata.common.enums.FinanceSyncItem;
import com.banksalad.collectmydata.common.message.MessageTopic;
import com.banksalad.collectmydata.common.message.PublishmentRequestedInsuMessage;
import com.banksalad.collectmydata.common.message.PublishmentRequestedMessage;
import com.banksalad.collectmydata.finance.api.summary.SummaryPublishmentHelper;

@Component
public class InsuranceSummaryPublishmentHelper implements SummaryPublishmentHelper {

  @Override
  public String getMessageTopic() {
    return MessageTopic.insuPublishmentRequested;
  }

  @Override
  public PublishmentRequestedMessage makePublishmentRequestedMessage(ExecutionContext executionContext) {
    return PublishmentRequestedInsuMessage.builder()
        .financeIndustry(FinanceIndustry.INSU)
        .financeSyncItem(FinanceSyncItem.COLLECTMYDATAINSU_SYNC_ITEM_INSURANCE_SUMMARY)
        .banksaladUserId(executionContext.getBanksaladUserId())
        .organizationId(executionContext.getOrganizationId())
        .syncRequestId(executionContext.getSyncRequestId())
        .build();
  }
}
