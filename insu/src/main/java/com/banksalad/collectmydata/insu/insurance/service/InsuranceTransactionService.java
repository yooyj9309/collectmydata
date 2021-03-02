package com.banksalad.collectmydata.insu.insurance.service;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.insu.common.dto.InsuranceSummary;
import com.banksalad.collectmydata.insu.insurance.dto.InsuranceTransaction;

import java.util.List;

public interface InsuranceTransactionService {

  List<InsuranceTransaction> listInsuranceTransactions(ExecutionContext executionContext, String organizationCode,
      List<InsuranceSummary> insuranceSummaries);
}
