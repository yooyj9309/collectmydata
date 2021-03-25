package com.banksalad.collectmydata.ginsu.common.service;

import org.springframework.stereotype.Service;

import com.banksalad.collectmydata.ginsu.common.db.repository.InsuranceSummaryRepository;
import com.banksalad.collectmydata.ginsu.common.mapper.InsuranceSummaryMapper;
import com.banksalad.collectmydata.ginsu.summary.dto.InsuranceSummary;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class InsuranceSummaryServiceImpl implements InsuranceSummaryService {

  private final InsuranceSummaryRepository insuranceSummaryRepository;
  private final InsuranceSummaryMapper insuranceSummaryMapper = Mappers.getMapper(InsuranceSummaryMapper.class);

  @Override
  public List<InsuranceSummary> listSummariesConsented(long banksaladUserId, String organizationId) {

    return insuranceSummaryRepository
        .findByBanksaladUserIdAndOrganizationIdAndConsentIsTrue(banksaladUserId, organizationId)
        .stream()
        .map(insuranceSummaryMapper::entityToDto)
        .collect(Collectors.toList());
  }

  @Override
  public void updateBasicSearchTimestamp(long banksaladUserId, String organizationId, InsuranceSummary insuranceSummary,
      long basicSearchTimestamp) {

    insuranceSummaryRepository
        .findByBanksaladUserIdAndOrganizationIdAndInsuNum(banksaladUserId, organizationId,
            insuranceSummary.getInsuNum())
        .ifPresent(insuranceSummaryEntity -> {
          insuranceSummaryEntity.setBasicSearchTimestamp(basicSearchTimestamp);
          insuranceSummaryRepository.save(insuranceSummaryEntity);
        });
  }

  @Override
  public void updateTransactionSyncedAt(long banksaladUserId, String organizationId, InsuranceSummary insuranceSummary,
      LocalDateTime transactionSyncedAt) {

    insuranceSummaryRepository
        .findByBanksaladUserIdAndOrganizationIdAndInsuNum(banksaladUserId, organizationId,
            insuranceSummary.getInsuNum())
        .ifPresent(insuranceSummaryEntity -> {
          insuranceSummaryEntity.setTransactionSyncedAt(transactionSyncedAt);
          insuranceSummaryRepository.save(insuranceSummaryEntity);
        });
  }

  @Override
  public void updateBasicResponseCode(long banksaladUserId, String organizationId, InsuranceSummary insuranceSummary,
      String responseCode) {

    insuranceSummaryRepository
        .findByBanksaladUserIdAndOrganizationIdAndInsuNum(banksaladUserId, organizationId,
            insuranceSummary.getInsuNum())
        .ifPresent(insuranceSummaryEntity -> {
          insuranceSummaryEntity.setBasicResponseCode(responseCode);
          insuranceSummaryRepository.save(insuranceSummaryEntity);
        });
  }

  @Override
  public void updateTransactionResponseCode(long banksaladUserId, String organizationId,
      InsuranceSummary insuranceSummary, String responseCode) {

    insuranceSummaryRepository
        .findByBanksaladUserIdAndOrganizationIdAndInsuNum(banksaladUserId, organizationId,
            insuranceSummary.getInsuNum())
        .ifPresent(insuranceSummaryEntity -> {
          insuranceSummaryEntity.setTransactionResponseCode(responseCode);
          insuranceSummaryRepository.save(insuranceSummaryEntity);
        });
  }

}
