package com.banksalad.collectmydata.insu.loan;

import org.springframework.stereotype.Component;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.util.DateUtil;
import com.banksalad.collectmydata.finance.api.transaction.TransactionRequestHelper;
import com.banksalad.collectmydata.insu.common.service.LoanSummaryService;
import com.banksalad.collectmydata.insu.loan.dto.ListLoanTransactionRequest;
import com.banksalad.collectmydata.insu.summary.dto.LoanSummary;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static com.banksalad.collectmydata.finance.common.constant.FinanceConstant.DEFAULT_PAGING_LIMIT;
import static com.banksalad.collectmydata.finance.common.constant.FinanceConstant.DEFAULT_SEARCH_YEAR;

@Component
@RequiredArgsConstructor
public class LoanTransactionRequestHelper implements TransactionRequestHelper<LoanSummary, ListLoanTransactionRequest> {

  private final LoanSummaryService loanSummaryService;

  @Override
  public List<LoanSummary> listSummaries(ExecutionContext executionContext) {
    return loanSummaryService
        .listLoanSummaries(executionContext.getBanksaladUserId(), executionContext.getOrganizationId());
  }

  @Override
  public LocalDateTime getTransactionSyncedAt(ExecutionContext executionContext, LoanSummary loanSummary) {
    LocalDateTime fromDateTime = loanSummary.getTransactionSyncedAt();
    if (fromDateTime == null) {
      fromDateTime = executionContext.getSyncStartedAt().minusYears(DEFAULT_SEARCH_YEAR);
    }
    return fromDateTime;
  }

  @Override
  public ListLoanTransactionRequest make(ExecutionContext executionContext, LoanSummary loanSummary, LocalDate fromDate,
      LocalDate toDate, String nextPage) {
    return ListLoanTransactionRequest.builder()
        .orgCode(executionContext.getOrganizationCode())
        .accountNum(loanSummary.getAccountNum())
        .fromDate(DateUtil.toDateString(fromDate))
        .toDate(DateUtil.toDateString(toDate))
        .nextPage(nextPage)
        .limit(DEFAULT_PAGING_LIMIT)
        .build();
  }
}
