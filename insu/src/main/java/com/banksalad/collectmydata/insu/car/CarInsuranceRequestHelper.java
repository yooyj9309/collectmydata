package com.banksalad.collectmydata.insu.car;

import org.springframework.stereotype.Component;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.finance.api.accountinfo.AccountInfoRequestHelper;
import com.banksalad.collectmydata.insu.car.dto.GetCarInsuranceRequest;
import com.banksalad.collectmydata.insu.common.db.entity.InsuranceSummaryEntity;
import com.banksalad.collectmydata.insu.common.db.repository.InsuranceSummaryRepository;
import com.banksalad.collectmydata.insu.common.service.InsuranceSummaryService;
import com.banksalad.collectmydata.insu.summary.dto.InsuranceSummary;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CarInsuranceRequestHelper implements AccountInfoRequestHelper<GetCarInsuranceRequest, InsuranceSummary> {

  private final InsuranceSummaryService insuranceSummaryService;
  private final InsuranceSummaryRepository insuranceSummaryRepository;

  @Override
  public List<InsuranceSummary> listSummaries(ExecutionContext executionContext) {
    return insuranceSummaryService
        .listSummariesConsented(executionContext.getBanksaladUserId(), executionContext.getOrganizationId());
  }

  @Override
  public GetCarInsuranceRequest make(ExecutionContext executionContext, InsuranceSummary insuranceSummary) {
    // TODO : insuranceSummary DTO에 searchTimestamp, transactionSyncedAt 필드(API 송수신 외 필드)를 추가할 것 인가
    //  if yes -> long searchTimestamp = insuranceSummary.getCarSearchTimestamp()
    long searchTimestamp = insuranceSummaryRepository
        .findByBanksaladUserIdAndOrganizationIdAndInsuNum(
            executionContext.getBanksaladUserId(), executionContext.getOrganizationId(), insuranceSummary.getInsuNum())
        .map(InsuranceSummaryEntity::getCarSearchTimestamp)
        .orElse(0L);

    return GetCarInsuranceRequest.builder()
        .orgCode(executionContext.getOrganizationCode())
        .insuNum(insuranceSummary.getInsuNum())
        .searchTimestamp(searchTimestamp)
        .build();
  }
}
