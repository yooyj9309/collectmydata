package com.banksalad.collectmydata.insu.loan;

import org.springframework.stereotype.Component;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.finance.api.accountinfo.AccountInfoRequestHelper;
import com.banksalad.collectmydata.insu.common.service.LoanSummaryService;
import com.banksalad.collectmydata.insu.loan.dto.GetLoanDetailRequest;
import com.banksalad.collectmydata.insu.summary.dto.LoanSummary;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Component
@RequiredArgsConstructor
public class LoanDetailRequestHelper implements AccountInfoRequestHelper<GetLoanDetailRequest, LoanSummary> {

  private final LoanSummaryService loanSummaryService;

  @Override
  public List<LoanSummary> listSummaries(ExecutionContext executionContext) {
    return loanSummaryService
        .listLoanSummaries(executionContext.getBanksaladUserId(), executionContext.getOrganizationId());
  }

  @Override
  public GetLoanDetailRequest make(ExecutionContext executionContext, LoanSummary loanSummary) {
    return GetLoanDetailRequest.builder()
        .orgCode(executionContext.getOrganizationCode())
        .accountNum(loanSummary.getAccountNum())
        .searchTimestamp(loanSummary.getDetailSearchTimestamp())
        .build();
  }
}
