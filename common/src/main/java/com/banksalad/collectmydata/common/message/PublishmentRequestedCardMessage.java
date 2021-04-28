package com.banksalad.collectmydata.common.message;

import com.banksalad.collectmydata.common.enums.FinanceIndustry;
import com.banksalad.collectmydata.common.enums.FinanceSyncItem;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Builder
public class PublishmentRequestedCardMessage implements PublishmentRequestedMessage {

  private final FinanceIndustry financeIndustry;
  private final FinanceSyncItem financeSyncItem;

  private final long banksaladUserId;
  private final String organizationId;
  private final String syncRequestId;
}
