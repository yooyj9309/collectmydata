package com.banksalad.collectmydata.finance.api.userbase;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.enums.FinanceIndustry;
import com.banksalad.collectmydata.common.enums.FinanceSyncItem;
import com.banksalad.collectmydata.common.message.PublishmentRequestedBankMessage;
import com.banksalad.collectmydata.common.message.PublishmentRequestedMessage;
import com.banksalad.collectmydata.finance.api.userbase.dto.UserBaseResponse;

public interface UserBaseResponseHelper<UserBaseInfo> {

  UserBaseInfo getUserBaseInfoFromResponse(UserBaseResponse userBaseResponse);

  void saveUserBaseInfo(ExecutionContext executionContext, UserBaseInfo userBaseInfo);

//  void saveResponseCode(ExecutionContext executionContext, String responseCode);

  // TODO change as abstract method after factoring
  // PublishmentRequestedMessage makePublishmentRequestedMessage(ExecutionContext executionContext);
  default PublishmentRequestedMessage makePublishmentRequestedMessage(ExecutionContext executionContext) {
    return PublishmentRequestedBankMessage.builder()
        .financeIndustry(FinanceIndustry.BANK)
        .financeSyncItem(FinanceSyncItem.COLLECTMYDATA_FINANCE_SYNC_ITEM_UNKNOWN)
        .banksaladUserId(executionContext.getBanksaladUserId())
        .organizationId(executionContext.getOrganizationId())
        .syncRequestId(executionContext.getSyncRequestId())
        .build();
  }
}
