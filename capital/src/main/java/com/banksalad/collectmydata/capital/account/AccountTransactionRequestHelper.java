package com.banksalad.collectmydata.capital.account;

import org.springframework.stereotype.Component;

import com.banksalad.collectmydata.capital.account.dto.ListAccountTransactionsRequest;
import com.banksalad.collectmydata.capital.common.service.AccountSummaryService;
import com.banksalad.collectmydata.capital.summary.dto.AccountSummary;
import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.finance.api.transaction.TransactionRequestHelper;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static com.banksalad.collectmydata.finance.common.constant.FinanceConstant.DEFAULT_PAGING_LIMIT;
import static com.banksalad.collectmydata.finance.common.constant.FinanceConstant.DEFAULT_SEARCH_YEAR;

@Component
@RequiredArgsConstructor
public class AccountTransactionRequestHelper implements
    TransactionRequestHelper<AccountSummary, ListAccountTransactionsRequest> {

  private final AccountSummaryService accountSummaryService;
  //TODO dusang, DateUtil에서 하는게 맞지않을까..?
  private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");


  @Override
  public List<AccountSummary> listSummaries(ExecutionContext executionContext) {
    return accountSummaryService
        .listSummariesConsented(executionContext.getBanksaladUserId(), executionContext.getOrganizationId(), false);
  }

  @Override
  public LocalDateTime getTransactionSyncedAt(ExecutionContext executionContext, AccountSummary accountSummary) {
    LocalDateTime fromDateTime = accountSummary.getTransactionSyncedAt();
    if (fromDateTime == null) {
      fromDateTime = executionContext.getSyncStartedAt().minusYears(DEFAULT_SEARCH_YEAR);
    }
    return fromDateTime;
  }

  @Override
  public ListAccountTransactionsRequest make(ExecutionContext executionContext, AccountSummary accountSummary,
      LocalDate fromDate, LocalDate toDate, String nextPage) {
    return ListAccountTransactionsRequest.builder()
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
