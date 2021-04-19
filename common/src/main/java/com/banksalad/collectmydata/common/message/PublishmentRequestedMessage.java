package com.banksalad.collectmydata.common.message;

import com.banksalad.collectmydata.common.enums.FinanceIndustry;

public interface PublishmentRequestedMessage {

  FinanceIndustry getFinanceIndustry();

  long getBanksaladUserId();

  String getOrganizationId();

  String getSyncRequestId();
}
