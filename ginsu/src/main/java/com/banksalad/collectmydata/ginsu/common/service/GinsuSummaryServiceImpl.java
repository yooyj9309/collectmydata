package com.banksalad.collectmydata.ginsu.common.service;

import org.springframework.stereotype.Service;

import com.banksalad.collectmydata.ginsu.common.db.repository.GinsuSummaryRepository;
import com.banksalad.collectmydata.ginsu.common.mapper.GinsuSummaryMapper;
import com.banksalad.collectmydata.ginsu.summary.dto.GinsuSummary;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class GinsuSummaryServiceImpl implements GinsuSummaryService {

  private final GinsuSummaryRepository ginsuSummaryRepository;
  private final GinsuSummaryMapper ginsuSummaryMapper = Mappers.getMapper(GinsuSummaryMapper.class);

  @Override
  public List<GinsuSummary> listSummariesConsented(long banksaladUserId, String organizationId) {

    return ginsuSummaryRepository
        .findByBanksaladUserIdAndOrganizationIdAndConsentIsTrue(banksaladUserId, organizationId)
        .stream()
        .map(ginsuSummaryMapper::entityToDto)
        .collect(Collectors.toList());
  }

  @Override
  public void updateBasicSearchTimestamp(long banksaladUserId, String organizationId, GinsuSummary ginsuSummary,
      long basicSearchTimestamp) {

    ginsuSummaryRepository
        .findByBanksaladUserIdAndOrganizationIdAndInsuNum(banksaladUserId, organizationId,
            ginsuSummary.getInsuNum())
        .ifPresent(insuranceSummaryEntity -> {
          insuranceSummaryEntity.setBasicSearchTimestamp(basicSearchTimestamp);
          ginsuSummaryRepository.save(insuranceSummaryEntity);
        });
  }

  @Override
  public void updateTransactionSyncedAt(long banksaladUserId, String organizationId, GinsuSummary ginsuSummary,
      LocalDateTime transactionSyncedAt) {

    ginsuSummaryRepository
        .findByBanksaladUserIdAndOrganizationIdAndInsuNum(banksaladUserId, organizationId,
            ginsuSummary.getInsuNum())
        .ifPresent(insuranceSummaryEntity -> {
          insuranceSummaryEntity.setTransactionSyncedAt(transactionSyncedAt);
          ginsuSummaryRepository.save(insuranceSummaryEntity);
        });
  }

  @Override
  public void updateBasicResponseCode(long banksaladUserId, String organizationId, GinsuSummary ginsuSummary,
      String responseCode) {

    ginsuSummaryRepository
        .findByBanksaladUserIdAndOrganizationIdAndInsuNum(banksaladUserId, organizationId,
            ginsuSummary.getInsuNum())
        .ifPresent(insuranceSummaryEntity -> {
          insuranceSummaryEntity.setBasicResponseCode(responseCode);
          ginsuSummaryRepository.save(insuranceSummaryEntity);
        });
  }

  @Override
  public void updateTransactionResponseCode(long banksaladUserId, String organizationId,
      GinsuSummary ginsuSummary, String responseCode) {

    ginsuSummaryRepository
        .findByBanksaladUserIdAndOrganizationIdAndInsuNum(banksaladUserId, organizationId,
            ginsuSummary.getInsuNum())
        .ifPresent(insuranceSummaryEntity -> {
          insuranceSummaryEntity.setTransactionResponseCode(responseCode);
          ginsuSummaryRepository.save(insuranceSummaryEntity);
        });
  }

}
