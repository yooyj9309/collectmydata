package com.banksalad.collectmydata.telecom.summary;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.finance.api.summary.SummaryResponseHelper;
import com.banksalad.collectmydata.finance.api.summary.dto.SummaryResponse;
import com.banksalad.collectmydata.telecom.common.db.entity.TelecomSummaryEntity;
import com.banksalad.collectmydata.telecom.common.db.repository.TelecomSummaryRepository;
import com.banksalad.collectmydata.telecom.common.mapper.TelecomSummaryMapper;
import com.banksalad.collectmydata.telecom.summary.dto.ListTelecomSummariesResponse;
import com.banksalad.collectmydata.telecom.summary.dto.TelecomSummary;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.util.Iterator;

@Component
@RequiredArgsConstructor
public class TelecomSummaryResponseHelper implements SummaryResponseHelper<TelecomSummary> {

  private final TelecomSummaryRepository telecomSummaryRepository;

  private final TelecomSummaryMapper telecomSummaryMapper = Mappers.getMapper(TelecomSummaryMapper.class);

  @Override
  public Iterator<TelecomSummary> iterator(SummaryResponse response) {
    return ((ListTelecomSummariesResponse) response).getTelecomList().iterator();
  }

  @Override
  public void saveOrganizationUser(ExecutionContext executionContext, SummaryResponse response) {
    // No implementation
  }

  @Override
  public void saveSummary(ExecutionContext executionContext, TelecomSummary telecomSummary) {
    // Declare input variables
    final long banksaladUserId = executionContext.getBanksaladUserId();
    final String organizationId = executionContext.getOrganizationId();
    final String mgmtId = telecomSummary.getMgmtId();
    final LocalDateTime syncedAt = executionContext.getSyncStartedAt();

    // Read a record from the table
    TelecomSummaryEntity telecomSummaryEntity = telecomSummaryRepository
        .findByBanksaladUserIdAndOrganizationIdAndMgmtId(banksaladUserId, organizationId, mgmtId)
        .orElse(TelecomSummaryEntity.builder().build());

    // Merge a DTO and an entity into the entity
    telecomSummaryMapper.mergeDtoToEntity(telecomSummary, telecomSummaryEntity);

    // Save in the table
    telecomSummaryEntity.setBanksaladUserId(banksaladUserId);
    telecomSummaryEntity.setOrganizationId(organizationId);
    telecomSummaryEntity.setSyncedAt(syncedAt);
    telecomSummaryRepository.save(telecomSummaryEntity);
  }
}
