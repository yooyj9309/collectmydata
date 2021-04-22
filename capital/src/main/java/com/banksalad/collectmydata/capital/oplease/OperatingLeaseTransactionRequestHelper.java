package com.banksalad.collectmydata.capital.oplease;

import com.banksalad.collectmydata.capital.common.service.AccountSummaryService;
import com.banksalad.collectmydata.capital.oplease.dto.ListOperatingLeaseTransactionsRequest;
import com.banksalad.collectmydata.capital.summary.dto.AccountSummary;
import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.finance.api.transaction.TransactionRequestHelper;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

import static com.banksalad.collectmydata.finance.common.constant.FinanceConstant.DEFAULT_SEARCH_YEAR;

@Component
@RequiredArgsConstructor
public class OperatingLeaseTransactionRequestHelper implements
    TransactionRequestHelper<AccountSummary, ListOperatingLeaseTransactionsRequest> {

  private final AccountSummaryService accountSummaryService;

  private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
  private static final int DEFAULT_PAGING_LIMIT = 500;

  @Override
  public List<AccountSummary> listSummaries(ExecutionContext executionContext) {
    return accountSummaryService
        .listSummariesConsented(executionContext.getBanksaladUserId(), executionContext.getOrganizationId(), true);
  }

  @Override
  public LocalDateTime getTransactionSyncedAt(ExecutionContext executionContext, AccountSummary accountSummary) {
    LocalDateTime operatingLeaseTransactionSyncedAt = accountSummary.getOperatingLeaseTransactionSyncedAt();
    return Objects.requireNonNullElseGet(operatingLeaseTransactionSyncedAt,
        () -> executionContext.getSyncStartedAt().minusYears(DEFAULT_SEARCH_YEAR));
  }

  @Override
  public ListOperatingLeaseTransactionsRequest make(ExecutionContext executionContext, AccountSummary accountSummary,
      LocalDate fromDate, LocalDate toDate, String nextPage) {
    return ListOperatingLeaseTransactionsRequest.builder()
        .orgCode(executionContext.getOrganizationCode())
        .accountNum(accountSummary.getAccountNum())
        .seqno(accountSummary.getSeqno())
        .fromDate(dateFormatter.format(fromDate))
        .toDate(dateFormatter.format(toDate))
        .nextPage(nextPage)
        .limit(DEFAULT_PAGING_LIMIT)
        .build();
  }
}
