package com.banksalad.collectmydata.insu.insurance;

import org.springframework.stereotype.Component;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.util.DateUtil;
import com.banksalad.collectmydata.finance.api.transaction.TransactionRequestHelper;
import com.banksalad.collectmydata.insu.common.service.InsuranceSummaryService;
import com.banksalad.collectmydata.insu.insurance.dto.ListInsuranceTransactionsRequest;
import com.banksalad.collectmydata.insu.summary.dto.InsuranceSummary;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static com.banksalad.collectmydata.finance.common.constant.FinanceConstant.DEFAULT_PAGING_LIMIT;
import static com.banksalad.collectmydata.finance.common.constant.FinanceConstant.DEFAULT_SEARCH_YEAR;

@Component
@RequiredArgsConstructor
public class InsuranceTransactionRequestHelper implements
    TransactionRequestHelper<InsuranceSummary, ListInsuranceTransactionsRequest> {

  private final InsuranceSummaryService insuranceSummaryService;

  @Override
  public List<InsuranceSummary> listSummaries(ExecutionContext executionContext) {
    return insuranceSummaryService.listSummariesConsented(executionContext.getBanksaladUserId(),
        executionContext.getOrganizationId());
  }

  @Override
  public LocalDateTime getTransactionSyncedAt(ExecutionContext executionContext, InsuranceSummary insuranceSummary) {
    LocalDateTime fromDateTime = insuranceSummary.getTransactionSyncedAt();

    if (fromDateTime == null) {
      fromDateTime = executionContext.getSyncStartedAt().minusYears(DEFAULT_SEARCH_YEAR);
    }

    return fromDateTime;
  }

  @Override
  public ListInsuranceTransactionsRequest make(ExecutionContext executionContext, InsuranceSummary insuranceSummary,
      LocalDate fromDate, LocalDate toDate, String nextPage) {
    return ListInsuranceTransactionsRequest.builder()
        .orgCode(executionContext.getOrganizationCode())
        .insuNum(insuranceSummary.getInsuNum())
        .fromDate(DateUtil.toDateString(fromDate))
        .toDate(DateUtil.toDateString(toDate))
        .nextPage(nextPage)
        .limit(DEFAULT_PAGING_LIMIT)
        .build();
  }
}
