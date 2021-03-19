package com.banksalad.collectmydata.insu.common.service;

import org.springframework.stereotype.Service;

import com.banksalad.collectmydata.common.collect.executor.CollectExecutor;
import com.banksalad.collectmydata.common.exception.CollectRuntimeException;
import com.banksalad.collectmydata.finance.common.service.UserSyncStatusService;
import com.banksalad.collectmydata.insu.common.db.entity.InsuranceSummaryEntity;
import com.banksalad.collectmydata.insu.common.db.repository.InsuranceSummaryRepository;
import com.banksalad.collectmydata.insu.common.mapper.InsuranceSummaryMapper;
import com.banksalad.collectmydata.insu.summary.dto.InsuranceSummary;
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

  private final UserSyncStatusService userSyncStatusService;
  private final InsuranceSummaryRepository insuranceSummaryRepository;
  private final CollectExecutor collectExecutor;

  private static final String AUTHORIZATION = "Authorization";
  private static final InsuranceSummaryMapper insuranceSummaryMapper = Mappers.getMapper(InsuranceSummaryMapper.class);

  @Override
  public List<InsuranceSummary> listSummariesConsented(long banksaladUserId, String organizationId) {
    // TODO : insurance 업권도 조회 시 insu_type 포함하여 조회해야 하는가? (parameter 추가 및 jpa query method 변경)
    return insuranceSummaryRepository
        .findByBanksaladUserIdAndOrganizationIdAndConsentIsTrue(banksaladUserId, organizationId)
        .stream()
        .map(insuranceSummaryMapper::entityToDto)
        .collect(Collectors.toList());
  }

  @Override
  public void updateBasicSearchTimestampAndResponseCode(long banksaladUserId, String organizationId, String insuNum,
      long basicSearchTimestamp, String rspCode) {
    InsuranceSummaryEntity entity = getInsuranceSummaryEntity(banksaladUserId, organizationId, insuNum);
    entity.setBasicSearchTimestamp(basicSearchTimestamp);
    entity.setBasicResponseCode(rspCode);
    insuranceSummaryRepository.save(entity);
  }

  @Override
  public void updatePaymentSearchTimestampAndResponseCode(long banksaladUserId, String organizationId, String insuNum,
      long paymentSearchTimestamp, String rspCode) {
    InsuranceSummaryEntity entity = getInsuranceSummaryEntity(banksaladUserId, organizationId, insuNum);
    entity.setPaymentSearchTimestamp(paymentSearchTimestamp);
    entity.setPaymentResponseCode(rspCode);
    insuranceSummaryRepository.save(entity);
  }

  @Override
  public void updateTransactionSyncedAt(long banksaladUserId, String organizationId, String insuNum,
      LocalDateTime syncedAt) {
    InsuranceSummaryEntity entity = getInsuranceSummaryEntity(banksaladUserId, organizationId, insuNum);
    entity.setTransactionSyncedAt(syncedAt);
    insuranceSummaryRepository.save(entity);
  }

  @Override
  public void updateCarSearchTimestamp(long banksaladUserId, String organizationId, InsuranceSummary insuranceSummary,
      long carSearchTimestamp) {

    insuranceSummaryRepository
        .findByBanksaladUserIdAndOrganizationIdAndInsuNum(
            banksaladUserId, organizationId, insuranceSummary.getInsuNum())
        .ifPresent(insuranceSummaryEntity -> {
          insuranceSummaryEntity.setCarSearchTimestamp(carSearchTimestamp);
          insuranceSummaryRepository.save(insuranceSummaryEntity);
        });
  }

  @Override
  public void updateCarResponseCode(long banksaladUserId, String organizationId, InsuranceSummary insuranceSummary,
      String responseCode) {

    insuranceSummaryRepository
        .findByBanksaladUserIdAndOrganizationIdAndInsuNum(
            banksaladUserId, organizationId, insuranceSummary.getInsuNum())
        .ifPresent(insuranceSummaryEntity -> {
          insuranceSummaryEntity.setCarResponseCode(responseCode);
          insuranceSummaryRepository.save(insuranceSummaryEntity);
        });
  }

  public InsuranceSummaryEntity getInsuranceSummaryEntity(long banksaladUserId, String organizationId, String insuNum) {
    return insuranceSummaryRepository
        .findByBanksaladUserIdAndOrganizationIdAndInsuNum(
            banksaladUserId,
            organizationId,
            insuNum
        ).orElseThrow(() -> new CollectRuntimeException("No data AccountSummaryEntity"));
  }
}
