package com.banksalad.collectmydata.insu.common.service;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.insu.common.dto.LoanSummary;

import java.util.List;

public interface LoanSummaryService {

  List<LoanSummary> listLoanSummaries(ExecutionContext executionContext, String organizationCode);
}
