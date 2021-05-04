package com.banksalad.collectmydata.insu.insurance;

import org.springframework.stereotype.Component;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.enums.FinanceIndustry;
import com.banksalad.collectmydata.common.enums.FinanceSyncItem;
import com.banksalad.collectmydata.common.message.MessageTopic;
import com.banksalad.collectmydata.common.message.PublishmentRequestedInsuMessage;
import com.banksalad.collectmydata.common.message.PublishmentRequestedMessage;
import com.banksalad.collectmydata.finance.api.transaction.TransactionPublishmentHelper;
import com.banksalad.collectmydata.insu.summary.dto.InsuranceSummary;

@Component
public class InsuranceTransactionPublishmentHelper implements TransactionPublishmentHelper<InsuranceSummary> {

  @Override
  public String getMessageTopic() {
    return MessageTopic.insuPublishmentRequested;
  }

  @Override
  public PublishmentRequestedMessage makePublishmentRequestedMessage(ExecutionContext executionContext,
      InsuranceSummary insuranceSummary, boolean hasNextPage) {

    return PublishmentRequestedInsuMessage.builder()
        .financeIndustry(FinanceIndustry.INSU)
        .financeSyncItem(FinanceSyncItem.COLLECTMYDATAINSU_SYNC_ITEM_INSURANCE_TRANSACTION)
        .banksaladUserId(executionContext.getBanksaladUserId())
        .organizationId(executionContext.getOrganizationId())
        .syncRequestId(executionContext.getSyncRequestId())
        .insuNum(insuranceSummary.getInsuNum())
        .hasNextPage(hasNextPage)
        .build();
  }
}
