package com.banksalad.collectmydata.telecom.telecom;

import org.springframework.stereotype.Component;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.util.DateUtil;
import com.banksalad.collectmydata.finance.api.accountinfo.AccountInfoRequestHelper;
import com.banksalad.collectmydata.finance.common.db.entity.UserSyncStatusEntity;
import com.banksalad.collectmydata.finance.common.db.repository.UserSyncStatusRepository;
import com.banksalad.collectmydata.telecom.collect.Apis;
import com.banksalad.collectmydata.telecom.telecom.dto.ListTelecomBillsRequest;
import com.banksalad.collectmydata.telecom.telecom.dto.TelecomBillRequestSupporter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.banksalad.collectmydata.finance.common.constant.FinanceConstant.DEFAULT_SEARCH_YEAR;

@Component
@RequiredArgsConstructor
public class TelecomBillRequestHelper implements
    AccountInfoRequestHelper<ListTelecomBillsRequest, TelecomBillRequestSupporter> {

  private final UserSyncStatusRepository userSyncStatusRepository;

  @Override
  public List<TelecomBillRequestSupporter> listSummaries(ExecutionContext executionContext) {

    LocalDateTime fromDate = userSyncStatusRepository.findByBanksaladUserIdAndOrganizationIdAndApiId(
        executionContext.getBanksaladUserId(),
        executionContext.getOrganizationId(),
        Apis.finance_telecom_bills.getId()
    ).map(UserSyncStatusEntity::getSyncedAt)
        .orElse(LocalDateTime.now(DateUtil.UTC_ZONE_ID).minusYears(DEFAULT_SEARCH_YEAR));

    List<TelecomBillRequestSupporter> response = new ArrayList<>();

    // String range생성
    LocalDateTime endDate = executionContext.getSyncStartedAt();
    while (fromDate.isBefore(endDate)) {
      String changeDate = DateUtil.utcLocalDateTimeToKstYearMonthString(fromDate);

      response.add(
          TelecomBillRequestSupporter.builder()
              .changeMonth(changeDate)
              .build()
      );
      fromDate = fromDate.plusMonths(1);
    }

    return response;
  }

  @Override
  public ListTelecomBillsRequest make(ExecutionContext executionContext, TelecomBillRequestSupporter summaryDto) {
    return ListTelecomBillsRequest.builder()
        .orgCode(executionContext.getOrganizationCode())
        .chargeMonth(summaryDto.getChangeMonth())
        .build();
  }
}
