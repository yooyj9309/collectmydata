package com.banksalad.collectmydata.common.message;

import com.banksalad.collectmydata.common.enums.FinanceIndustry;
import com.banksalad.collectmydata.common.enums.FinanceSyncItem;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PublishmentRequestedBankMessage implements PublishmentRequestedMessage {

  private final FinanceIndustry financeIndustry;
  private final FinanceSyncItem financeSyncItem;

  private final long banksaladUserId;
  private final String organizationId;
  private final String syncRequestId;

  private final String accountNum;
  private final String seqno;
  private final boolean hasNextPage;
}
