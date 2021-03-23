package com.banksalad.collectmydata.insu.insurance;

import org.springframework.stereotype.Component;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.finance.api.accountinfo.AccountInfoRequestHelper;
import com.banksalad.collectmydata.insu.common.service.InsuranceSummaryService;
import com.banksalad.collectmydata.insu.insurance.dto.GetInsuranceBasicRequest;
import com.banksalad.collectmydata.insu.summary.dto.InsuranceSummary;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Component
@RequiredArgsConstructor
public class InsuranceBasicRequestHelper implements
    AccountInfoRequestHelper<GetInsuranceBasicRequest, InsuranceSummary> {

  private final InsuranceSummaryService insuranceSummaryService;

  @Override
  public List<InsuranceSummary> listSummaries(ExecutionContext executionContext) {
    return insuranceSummaryService
        .listSummariesConsented(executionContext.getBanksaladUserId(), executionContext.getOrganizationId());
  }

  @Override
  public GetInsuranceBasicRequest make(ExecutionContext executionContext, InsuranceSummary insuranceSummary) {
    return GetInsuranceBasicRequest.builder()
        .orgCode(executionContext.getOrganizationCode())
        .insuNum(insuranceSummary.getInsuNum())
        .searchTimestamp(insuranceSummary.getPaymentSearchTimestamp())
        .build();
  }
}
