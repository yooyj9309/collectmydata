package com.banksalad.collectmydata.ginsu.insurance;

import org.springframework.stereotype.Component;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.finance.api.accountinfo.AccountInfoRequestHelper;
import com.banksalad.collectmydata.ginsu.common.db.entity.InsuranceSummaryEntity;
import com.banksalad.collectmydata.ginsu.common.db.repository.InsuranceSummaryRepository;
import com.banksalad.collectmydata.ginsu.common.service.InsuranceSummaryService;
import com.banksalad.collectmydata.ginsu.insurance.dto.GetInsuranceBasicRequest;
import com.banksalad.collectmydata.ginsu.summary.dto.InsuranceSummary;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Component
@RequiredArgsConstructor
public class InsuranceBasicInfoRequestHelper implements
    AccountInfoRequestHelper<GetInsuranceBasicRequest, InsuranceSummary> {
  private final InsuranceSummaryService insuranceSummaryService;

  @Override
  public List<InsuranceSummary> listSummaries(ExecutionContext executionContext) {
    return insuranceSummaryService.listSummariesConsented(executionContext.getBanksaladUserId(),
        executionContext.getOrganizationId());
  }

  @Override
  public GetInsuranceBasicRequest make(ExecutionContext executionContext, InsuranceSummary insuranceSummary) {

    return GetInsuranceBasicRequest.builder()
        .orgCode(executionContext.getOrganizationCode())
        .insuNum(insuranceSummary.getInsuNum())
        .searchTimestamp(insuranceSummary.getBasicSearchTimestamp())
        .build();
  }
}
