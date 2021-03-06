package com.banksalad.collectmydata.telecom.common.service;

import com.banksalad.collectmydata.common.util.DateUtil;
import com.banksalad.collectmydata.telecom.common.db.entity.TelecomSummaryEntity;
import com.banksalad.collectmydata.telecom.common.db.repository.TelecomSummaryRepository;
import com.banksalad.collectmydata.telecom.common.mapper.TelecomSummaryMapper;
import com.banksalad.collectmydata.telecom.summary.dto.TelecomSummary;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static com.banksalad.collectmydata.finance.common.constant.FinanceConstant.DEFAULT_SEARCH_YEAR;

@Service
@RequiredArgsConstructor
public class TelecomSummaryServiceImpl implements TelecomSummaryService {

  private final TelecomSummaryRepository telecomSummaryRepository;
  private final TelecomSummaryMapper telecomSummaryMapper = Mappers.getMapper(TelecomSummaryMapper.class);

  @Override
  public List<TelecomSummary> listSummariesConsented(long banksaladUserId, String organizationId) {
    return telecomSummaryRepository
        .findByBanksaladUserIdAndOrganizationIdAndConsentIsTrue(banksaladUserId, organizationId)
        .stream()
        .map(telecomSummaryMapper::entityToDto)
        .collect(Collectors.toList());
  }

  @Override
  public LocalDateTime getTransactionSyncedAt(long banksaladUserId, String organizationId,
      TelecomSummary telecomSummary) {
    return telecomSummaryRepository
        .findByBanksaladUserIdAndOrganizationIdAndMgmtId(
            banksaladUserId,
            organizationId,
            telecomSummary.getMgmtId())
        .map(TelecomSummaryEntity::getTransactionSyncedAt)
        .orElse(LocalDateTime.now(DateUtil.UTC_ZONE_ID).minusYears(DEFAULT_SEARCH_YEAR));
  }

  @Override
  public LocalDateTime getPaidTransactionSyncedAt(long banksaladUserId, String organizationId,
      TelecomSummary telecomSummary) {
    return telecomSummaryRepository
        .findByBanksaladUserIdAndOrganizationIdAndMgmtId(
            banksaladUserId,
            organizationId,
            telecomSummary.getMgmtId())
        .map(TelecomSummaryEntity::getPaidTransactionSyncedAt)
        .orElse(LocalDateTime.now(DateUtil.UTC_ZONE_ID).minusYears(DEFAULT_SEARCH_YEAR));
  }

  @Override
  public void updateTransactionSyncedAt(long banksaladUserId, String organizationId, TelecomSummary telecomSummary,
      LocalDateTime transactionSyncedAt) {
    telecomSummaryRepository
        .findByBanksaladUserIdAndOrganizationIdAndMgmtId(banksaladUserId, organizationId, telecomSummary.getMgmtId())
        .ifPresent(telecomSummaryEntity -> {
          telecomSummaryEntity.setTransactionSyncedAt(transactionSyncedAt);
          telecomSummaryRepository.save(telecomSummaryEntity);
        });
  }

  @Override
  public void updatePaidTransactionSyncedAt(long banksaladUserId, String organizationId, TelecomSummary telecomSummary,
      LocalDateTime paidTransactionSyncedAt) {
    telecomSummaryRepository
        .findByBanksaladUserIdAndOrganizationIdAndMgmtId(banksaladUserId, organizationId, telecomSummary.getMgmtId())
        .ifPresent(telecomSummaryEntity -> {
          telecomSummaryEntity.setPaidTransactionSyncedAt(paidTransactionSyncedAt);
          telecomSummaryRepository.save(telecomSummaryEntity);
        });
  }

  @Override
  public void updateTransactionResponseCode(long banksaladUserId, String organizationId, TelecomSummary telecomSummary,
      String responseCode) {
    telecomSummaryRepository
        .findByBanksaladUserIdAndOrganizationIdAndMgmtId(banksaladUserId, organizationId, telecomSummary.getMgmtId())
        .ifPresent(telecomSummaryEntity -> {
          telecomSummaryEntity.setTransactionResponseCode(responseCode);
          telecomSummaryRepository.save(telecomSummaryEntity);
        });
  }

  @Override
  public void updatePaidTransactionResponseCode(long banksaladUserId, String organizationId,
      TelecomSummary telecomSummary, String responseCode) {
    telecomSummaryRepository
        .findByBanksaladUserIdAndOrganizationIdAndMgmtId(banksaladUserId, organizationId, telecomSummary.getMgmtId())
        .ifPresent(telecomSummaryEntity -> {
          telecomSummaryEntity.setPaidTransactionResponseCode(responseCode);
          telecomSummaryRepository.save(telecomSummaryEntity);
        });
  }
}
