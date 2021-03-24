package com.banksalad.collectmydata.insu.insurance;

import org.springframework.stereotype.Component;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.finance.api.accountinfo.AccountInfoRequestHelper;
import com.banksalad.collectmydata.insu.insurance.dto.GetInsuranceContractRequest;
import com.banksalad.collectmydata.insu.summary.dto.InsuranceSummary;

import java.util.List;

@Component
public class InsuranceContractRequestHelper implements
    AccountInfoRequestHelper<GetInsuranceContractRequest, InsuranceSummary> {

  @Override
  public List<InsuranceSummary> listSummaries(ExecutionContext executionContext) {
    return null;
  }

  @Override
  public GetInsuranceContractRequest make(ExecutionContext executionContext, InsuranceSummary insuranceSummary) {
    return null;
  }
}
