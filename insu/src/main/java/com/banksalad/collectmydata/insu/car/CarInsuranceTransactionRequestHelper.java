package com.banksalad.collectmydata.insu.car;

import org.springframework.stereotype.Component;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.finance.api.transaction.TransactionRequestHelper;
import com.banksalad.collectmydata.insu.car.dto.ListCarInsuranceTransactionsRequest;
import com.banksalad.collectmydata.insu.summary.dto.InsuranceSummary;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class CarInsuranceTransactionRequestHelper implements
    TransactionRequestHelper<InsuranceSummary, ListCarInsuranceTransactionsRequest> {

  @Override
  public List<InsuranceSummary> listSummaries(ExecutionContext executionContext) {
    return null;
  }

  @Override
  public LocalDateTime getTransactionSyncedAt(ExecutionContext executionContext, InsuranceSummary insuranceSummary) {
    return null;
  }

  @Override
  public ListCarInsuranceTransactionsRequest make(ExecutionContext executionContext, InsuranceSummary insuranceSummary,
      LocalDate fromDate, LocalDate toDate, String nextPage) {
    return null;
  }
}
