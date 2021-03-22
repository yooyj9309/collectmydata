package com.banksalad.collectmydata.insu.car;

import org.springframework.stereotype.Component;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.finance.api.accountinfo.AccountInfoRequestHelper;
import com.banksalad.collectmydata.insu.car.dto.GetCarInsuranceRequest;
import com.banksalad.collectmydata.insu.common.service.InsuranceSummaryService;
import com.banksalad.collectmydata.insu.summary.dto.InsuranceSummary;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CarInsuranceRequestHelper implements AccountInfoRequestHelper<GetCarInsuranceRequest, InsuranceSummary> {

  private final InsuranceSummaryService insuranceSummaryService;

  @Override
  public List<InsuranceSummary> listSummaries(ExecutionContext executionContext) {
    return insuranceSummaryService
        .listSummariesConsented(executionContext.getBanksaladUserId(), executionContext.getOrganizationId());
  }

  @Override
  public GetCarInsuranceRequest make(ExecutionContext executionContext, InsuranceSummary insuranceSummary) {
    return GetCarInsuranceRequest.builder()
        .orgCode(executionContext.getOrganizationCode())
        .insuNum(insuranceSummary.getInsuNum())
        .searchTimestamp(insuranceSummary.getCarSearchTimestamp())
        .build();
  }
}
