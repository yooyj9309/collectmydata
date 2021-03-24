package com.banksalad.collectmydata.ginsu.summary;

import org.springframework.stereotype.Component;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.finance.api.summary.SummaryResponseHelper;
import com.banksalad.collectmydata.finance.api.summary.dto.SummaryResponse;
import com.banksalad.collectmydata.ginsu.common.db.entity.GinsuSummaryEntity;
import com.banksalad.collectmydata.ginsu.common.db.repository.GinsuSummaryRepository;
import com.banksalad.collectmydata.ginsu.common.mapper.GinsuSummaryMapper;
import com.banksalad.collectmydata.ginsu.summary.dto.GinsuSummary;
import com.banksalad.collectmydata.ginsu.summary.dto.ListGinsuSummariesResponse;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;

import java.util.Iterator;

@Component
@RequiredArgsConstructor
public class GinsuSummaryResponseHelper implements SummaryResponseHelper<GinsuSummary> {

  private final GinsuSummaryRepository ginsuSummaryRepository;
  private final GinsuSummaryMapper ginsuSummaryMapper = Mappers.getMapper(GinsuSummaryMapper.class);

  @Override
  public Iterator<GinsuSummary> iterator(SummaryResponse response) {
    return ((ListGinsuSummariesResponse) response).getGinsuSummaries().iterator();
  }

  @Override
  public void saveOrganizationUser(ExecutionContext executionContext, SummaryResponse response) {
    // yooyj9309 보증 보험의 경우 organization user 적재 없음
  }

  @Override
  public void saveSummary(ExecutionContext executionContext, GinsuSummary ginsuSummary) {

    GinsuSummaryEntity ginsuSummaryEntity = ginsuSummaryRepository
        .findByBanksaladUserIdAndOrganizationIdAndInsuNum(executionContext.getBanksaladUserId(),
            executionContext.getOrganizationId(), ginsuSummary.getInsuNum())
        .orElse(GinsuSummaryEntity.builder().build());

    ginsuSummaryMapper.mergeDtoToEntity(ginsuSummary, ginsuSummaryEntity);

    ginsuSummaryEntity.setBanksaladUserId(executionContext.getBanksaladUserId());
    ginsuSummaryEntity.setOrganizationId(executionContext.getOrganizationId());
    ginsuSummaryEntity.setSyncedAt(executionContext.getSyncStartedAt());
    ginsuSummaryRepository.save(ginsuSummaryEntity);
  }
}
