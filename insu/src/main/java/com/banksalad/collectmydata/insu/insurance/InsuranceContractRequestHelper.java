package com.banksalad.collectmydata.insu.insurance;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.finance.api.accountinfo.AccountInfoRequestHelper;
import com.banksalad.collectmydata.insu.common.db.repository.InsuredRepository;
import com.banksalad.collectmydata.insu.common.mapper.InsuredMapper;
import com.banksalad.collectmydata.insu.common.service.InsuranceSummaryService;
import com.banksalad.collectmydata.insu.insurance.dto.Insured;
import com.banksalad.collectmydata.insu.insurance.dto.ListInsuranceContractsRequest;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class InsuranceContractRequestHelper implements
    AccountInfoRequestHelper<ListInsuranceContractsRequest, Insured> {

  private final InsuranceSummaryService insuranceSummaryService;
  private final InsuredRepository insuredRepository;
  private final InsuredMapper insuredMapper = Mappers.getMapper(InsuredMapper.class);

  @Override
  public List<Insured> listSummaries(ExecutionContext executionContext) {
    return insuranceSummaryService
        .listSummariesConsented(executionContext.getBanksaladUserId(), executionContext.getOrganizationId()).stream()
        .map(insuranceSummary -> insuredRepository.findByBanksaladUserIdAndOrganizationIdAndInsuNum(
            executionContext.getBanksaladUserId(), executionContext.getOrganizationId(), insuranceSummary.getInsuNum()))
        .flatMap(Collection::stream)
        .map(insuredMapper::entityToDto)
        .collect(Collectors.toList());
  }

  @Override
  public ListInsuranceContractsRequest make(ExecutionContext executionContext, Insured insured) {
    return ListInsuranceContractsRequest.builder()
        .orgCode(executionContext.getOrganizationCode())
        .insuNum(insured.getInsuNum())
        .insuredNo(insured.getInsuredNo())
        .searchTimestamp(insured.getContractSearchTimestamp())
        .build();
  }
}
