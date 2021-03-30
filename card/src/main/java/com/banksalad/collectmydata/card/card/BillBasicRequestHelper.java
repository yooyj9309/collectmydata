package com.banksalad.collectmydata.card.card;

import org.springframework.stereotype.Component;

import com.banksalad.collectmydata.card.card.dto.ListBillBasicRequest;
import com.banksalad.collectmydata.card.collect.Apis;
import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.finance.api.bill.BillRequestHelper;
import com.banksalad.collectmydata.finance.common.db.entity.UserSyncStatusEntity;
import com.banksalad.collectmydata.finance.common.db.repository.UserSyncStatusRepository;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static com.banksalad.collectmydata.finance.common.constant.FinanceConstant.DEFAULT_PAGING_LIMIT;
import static com.banksalad.collectmydata.finance.common.constant.FinanceConstant.DEFAULT_SEARCH_YEAR;

@Component
@RequiredArgsConstructor
public class BillBasicRequestHelper implements BillRequestHelper<ListBillBasicRequest> {

  private final UserSyncStatusRepository userSyncStatusRepository;

  private static final DateTimeFormatter yearMonthFormatter = DateTimeFormatter.ofPattern("yyyyMM");

  @Override
  public LocalDateTime getTransactionSyncedAt(ExecutionContext executionContext) {

    return userSyncStatusRepository.findByBanksaladUserIdAndOrganizationIdAndApiId(
        executionContext.getBanksaladUserId(),
        executionContext.getOrganizationId(),
        Apis.finance_card_bills.getId()
    ).map(UserSyncStatusEntity::getSyncedAt)
        .orElse(executionContext.getSyncStartedAt().minusYears(DEFAULT_SEARCH_YEAR));
  }

  @Override
  public ListBillBasicRequest make(ExecutionContext executionContext, LocalDate fromDate, LocalDate toDate,
      String nextPage) {
    return ListBillBasicRequest.builder()
        .orgCode(executionContext.getOrganizationCode())
        .fromMonth(fromDate.format(yearMonthFormatter))
        .toMonth(toDate.format(yearMonthFormatter))
        .nextPage(nextPage)
        .limit(DEFAULT_PAGING_LIMIT)
        .build();
  }
}
