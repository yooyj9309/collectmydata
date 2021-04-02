package com.banksalad.collectmydata.efin.account;

import org.springframework.stereotype.Component;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.efin.account.dto.ListAccountPrepaidTransactionsRequest;
import com.banksalad.collectmydata.efin.common.service.AccountSummaryService;
import com.banksalad.collectmydata.efin.summary.dto.AccountSummary;
import com.banksalad.collectmydata.finance.api.transaction.TransactionRequestHelper;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import static com.banksalad.collectmydata.finance.common.constant.FinanceConstant.DEFAULT_PAGING_LIMIT;
import static com.banksalad.collectmydata.finance.common.constant.FinanceConstant.DEFAULT_SEARCH_YEAR;

@Component
@RequiredArgsConstructor
public class AccountPrepaidTransactionRequestHelper implements
    TransactionRequestHelper<AccountSummary, ListAccountPrepaidTransactionsRequest> {

  private final AccountSummaryService accountSummaryService;

  private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");

  @Override
  public List<AccountSummary> listSummaries(ExecutionContext executionContext) {
    return accountSummaryService.listSummariesConsented(executionContext.getBanksaladUserId(),
        executionContext.getOrganizationId());
  }

  @Override
  public LocalDateTime getTransactionSyncedAt(ExecutionContext executionContext, AccountSummary accountSummary) {
    return Optional.ofNullable(accountSummary.getTransactionSyncedAt())
        .orElseGet(() -> executionContext.getSyncStartedAt().minusYears(DEFAULT_SEARCH_YEAR));
  }

  @Override
  public ListAccountPrepaidTransactionsRequest make(ExecutionContext executionContext, AccountSummary accountSummary,
      LocalDate fromDate, LocalDate toDate, String nextPage) {
    return ListAccountPrepaidTransactionsRequest.builder()
        .orgCode(executionContext.getOrganizationCode())
        .subKey(accountSummary.getSubKey())
        .fromDate(dateFormatter.format(fromDate))
        .toDate(dateFormatter.format(toDate))
        .nextPage(nextPage)
        .limit(DEFAULT_PAGING_LIMIT)
        .build();
  }
}
