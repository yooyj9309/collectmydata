package com.banksalad.collectmydata.insu.common.service;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.insu.common.dto.InsuranceSummary;

import java.util.List;

public interface InsuranceSummaryService {

  List<InsuranceSummary> listInsuranceSummaries(ExecutionContext executionContext, String organizationCode);
}
