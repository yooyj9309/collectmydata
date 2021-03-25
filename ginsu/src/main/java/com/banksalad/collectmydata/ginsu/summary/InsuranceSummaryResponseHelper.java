package com.banksalad.collectmydata.ginsu.summary;

import org.springframework.stereotype.Component;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.finance.api.summary.SummaryResponseHelper;
import com.banksalad.collectmydata.finance.api.summary.dto.SummaryResponse;
import com.banksalad.collectmydata.ginsu.common.db.entity.InsuranceSummaryEntity;
import com.banksalad.collectmydata.ginsu.common.db.repository.InsuranceSummaryRepository;
import com.banksalad.collectmydata.ginsu.common.mapper.InsuranceSummaryMapper;
import com.banksalad.collectmydata.ginsu.summary.dto.InsuranceSummary;
import com.banksalad.collectmydata.ginsu.summary.dto.ListInsuranceSummariesResponse;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;

import java.util.Iterator;

@Component
@RequiredArgsConstructor
public class InsuranceSummaryResponseHelper implements SummaryResponseHelper<InsuranceSummary> {

  private final InsuranceSummaryRepository insuranceSummaryRepository;
  private final InsuranceSummaryMapper insuranceSummaryMapper = Mappers.getMapper(InsuranceSummaryMapper.class);

  @Override
  public Iterator<InsuranceSummary> iterator(SummaryResponse response) {
    return ((ListInsuranceSummariesResponse) response).getInsuranceSummaries().iterator();
  }

  @Override
  public void saveOrganizationUser(ExecutionContext executionContext, SummaryResponse response) {
    // yooyj9309 보증 보험의 경우 organization user 적재 없음
  }

  @Override
  public void saveSummary(ExecutionContext executionContext, InsuranceSummary insuranceSummary) {

    InsuranceSummaryEntity insuranceSummaryEntity = insuranceSummaryRepository
        .findByBanksaladUserIdAndOrganizationIdAndInsuNum(executionContext.getBanksaladUserId(),
            executionContext.getOrganizationId(), insuranceSummary.getInsuNum())
        .orElse(InsuranceSummaryEntity.builder().build());

    insuranceSummaryMapper.mergeDtoToEntity(insuranceSummary, insuranceSummaryEntity);

    insuranceSummaryEntity.setBanksaladUserId(executionContext.getBanksaladUserId());
    insuranceSummaryEntity.setOrganizationId(executionContext.getOrganizationId());
    insuranceSummaryEntity.setSyncedAt(executionContext.getSyncStartedAt());
    insuranceSummaryRepository.save(insuranceSummaryEntity);
  }
}
