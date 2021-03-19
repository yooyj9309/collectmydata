package com.banksalad.collectmydata.insu.summary;

import org.springframework.stereotype.Component;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.finance.api.summary.SummaryResponseHelper;
import com.banksalad.collectmydata.finance.api.summary.dto.SummaryResponse;
import com.banksalad.collectmydata.insu.common.db.entity.InsuranceSummaryEntity;
import com.banksalad.collectmydata.insu.common.db.repository.InsuranceSummaryRepository;
import com.banksalad.collectmydata.insu.common.mapper.InsuranceSummaryMapper;
import com.banksalad.collectmydata.insu.summary.dto.InsuranceSummary;
import com.banksalad.collectmydata.insu.summary.dto.ListInsuranceSummariesResponse;
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
    return ((ListInsuranceSummariesResponse) response).getInsuList().iterator();
  }

  @Override
  public void saveOrganizationUser(ExecutionContext executionContext, SummaryResponse response) {
    // dusang, 보험업권의 경우 OrganizationUser 적재없음
  }

  @Override
  public void saveSummary(ExecutionContext executionContext, InsuranceSummary insuranceSummary) {
    // 비교자체가 필요없는 로직
    InsuranceSummaryEntity insuranceSummaryEntity = insuranceSummaryRepository
        .findByBanksaladUserIdAndOrganizationIdAndInsuNum(
            executionContext.getBanksaladUserId(), executionContext.getOrganizationId(), insuranceSummary.getInsuNum()
        ).orElse(InsuranceSummaryEntity.builder().build());

    // merge
    insuranceSummaryMapper.mergeDtoToEntity(insuranceSummary, insuranceSummaryEntity);

    // save (insert, update)
    insuranceSummaryEntity.setBanksaladUserId(executionContext.getBanksaladUserId());
    insuranceSummaryEntity.setOrganizationId(executionContext.getOrganizationId());
    insuranceSummaryEntity.setSyncedAt(executionContext.getSyncStartedAt());
    insuranceSummaryRepository.save(insuranceSummaryEntity);
  }
}
