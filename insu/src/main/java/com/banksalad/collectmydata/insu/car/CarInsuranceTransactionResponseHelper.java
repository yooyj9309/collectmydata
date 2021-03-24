package com.banksalad.collectmydata.insu.car;

import org.springframework.stereotype.Component;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.finance.api.transaction.TransactionResponseHelper;
import com.banksalad.collectmydata.finance.api.transaction.dto.TransactionResponse;
import com.banksalad.collectmydata.insu.car.dto.CarInsuranceTransaction;
import com.banksalad.collectmydata.insu.summary.dto.InsuranceSummary;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class CarInsuranceTransactionResponseHelper implements
    TransactionResponseHelper<InsuranceSummary, CarInsuranceTransaction> {

  @Override
  public List<CarInsuranceTransaction> getTransactionsFromResponse(TransactionResponse transactionResponse) {
    return null;
  }

  @Override
  public void saveTransactions(ExecutionContext executionContext, InsuranceSummary insuranceSummary,
      List<CarInsuranceTransaction> carInsuranceTransactions) {

  }

  @Override
  public void saveTransactionSyncedAt(ExecutionContext executionContext, InsuranceSummary insuranceSummary,
      LocalDateTime syncStartedAt) {

  }

  @Override
  public void saveResponseCode(ExecutionContext executionContext, InsuranceSummary insuranceSummary,
      String responseCode) {

  }
}
