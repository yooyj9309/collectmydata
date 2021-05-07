package com.banksalad.collectmydata.insu.insurance;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.util.ObjectComparator;
import com.banksalad.collectmydata.finance.api.accountinfo.AccountInfoResponseHelper;
import com.banksalad.collectmydata.finance.api.accountinfo.dto.AccountResponse;
import com.banksalad.collectmydata.insu.common.db.entity.InsuranceBasicEntity;
import com.banksalad.collectmydata.insu.common.db.entity.InsuranceBasicHistoryEntity;
import com.banksalad.collectmydata.insu.common.db.entity.InsuredEntity;
import com.banksalad.collectmydata.insu.common.db.entity.InsuredHistoryEntity;
import com.banksalad.collectmydata.insu.common.db.repository.InsuranceBasicHistoryRepository;
import com.banksalad.collectmydata.insu.common.db.repository.InsuranceBasicRepository;
import com.banksalad.collectmydata.insu.common.db.repository.InsuredHistoryRepository;
import com.banksalad.collectmydata.insu.common.db.repository.InsuredRepository;
import com.banksalad.collectmydata.insu.common.mapper.InsuranceBasicHistoryMapper;
import com.banksalad.collectmydata.insu.common.mapper.InsuranceBasicMapper;
import com.banksalad.collectmydata.insu.common.mapper.InsuredHistoryMapper;
import com.banksalad.collectmydata.insu.common.mapper.InsuredMapper;
import com.banksalad.collectmydata.insu.common.service.InsuranceSummaryService;
import com.banksalad.collectmydata.insu.insurance.dto.GetInsuranceBasicResponse;
import com.banksalad.collectmydata.insu.insurance.dto.InsuranceBasic;
import com.banksalad.collectmydata.insu.insurance.dto.Insured;
import com.banksalad.collectmydata.insu.summary.dto.InsuranceSummary;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.stream.Collectors;

import static com.banksalad.collectmydata.finance.common.constant.FinanceConstant.ENTITY_EXCLUDE_FIELD;

@Component
@RequiredArgsConstructor
public class InsuranceBasicResponseHelper implements AccountInfoResponseHelper<InsuranceSummary, InsuranceBasic> {

  private final InsuranceSummaryService insuranceSummaryService;
  private final InsuranceBasicRepository insuranceBasicRepository;
  private final InsuranceBasicHistoryRepository insuranceBasicHistoryRepository;
  private final InsuredRepository insuredRepository;
  private final InsuredHistoryRepository insuredHistoryRepository;

  private final InsuranceBasicMapper insuranceBasicMapper = Mappers.getMapper(InsuranceBasicMapper.class);
  private final InsuranceBasicHistoryMapper insuranceBasicHistoryMapper = Mappers
      .getMapper(InsuranceBasicHistoryMapper.class);
  private final InsuredMapper insuredMapper = Mappers.getMapper(InsuredMapper.class);
  private final InsuredHistoryMapper insuredHistoryMapper = Mappers.getMapper(InsuredHistoryMapper.class);

  @Override
  public InsuranceBasic getAccountFromResponse(AccountResponse accountResponse) {
    return ((GetInsuranceBasicResponse) accountResponse).getInsuranceBasic();
  }

  @Override
  @Transactional
  public void saveAccountAndHistory(ExecutionContext executionContext, InsuranceSummary insuranceSummary,
      InsuranceBasic insuranceBasic) {
    InsuranceBasicEntity insuranceBasicEntity = insuranceBasicMapper.dtoToEntity(insuranceBasic);
    insuranceBasicEntity.setSyncedAt(executionContext.getSyncStartedAt());
    insuranceBasicEntity.setBanksaladUserId(executionContext.getBanksaladUserId());
    insuranceBasicEntity.setOrganizationId(executionContext.getOrganizationId());
    insuranceBasicEntity.setInsuNum(insuranceSummary.getInsuNum());
    insuranceBasicEntity.setConsentId(executionContext.getConsentId());
    insuranceBasicEntity.setSyncRequestId(executionContext.getSyncRequestId());
    insuranceBasicEntity.setCreatedBy(executionContext.getRequestedBy());
    insuranceBasicEntity.setUpdatedBy(executionContext.getRequestedBy());

    InsuranceBasicEntity existingInsuranceBasicEntity = insuranceBasicRepository
        .findByBanksaladUserIdAndOrganizationIdAndInsuNum(
            insuranceBasicEntity.getBanksaladUserId(), insuranceBasicEntity.getOrganizationId(),
            insuranceSummary.getInsuNum())
        .orElse(null);

    if (existingInsuranceBasicEntity != null) {
      insuranceBasicEntity.setId(existingInsuranceBasicEntity.getId());
    }

    if (!ObjectComparator.isSame(insuranceBasicEntity, existingInsuranceBasicEntity, ENTITY_EXCLUDE_FIELD)) {
      insuranceBasicRepository.save(insuranceBasicEntity);

      InsuranceBasicHistoryEntity insuranceBasicHistoryEntity = insuranceBasicHistoryMapper
          .entityToHistoryEntity(insuranceBasicEntity, InsuranceBasicHistoryEntity.builder().build());
      insuranceBasicHistoryRepository.save(insuranceBasicHistoryEntity);
    }

    saveInsureds(insuranceBasic, insuranceBasicEntity);
  }

  private void saveInsureds(InsuranceBasic insuranceBasic, InsuranceBasicEntity insuranceBasicEntity) {
    List<Insured> existingInsureds = insuredRepository
        .findByBanksaladUserIdAndOrganizationIdAndInsuNum(
            insuranceBasicEntity.getBanksaladUserId(), insuranceBasicEntity.getOrganizationId(),
            insuranceBasicEntity.getInsuNum())
        .stream()
        .map(insuredMapper::entityToDto)
        .collect(Collectors.toList());

    if (!ObjectComparator.isSameListIgnoreOrder(insuranceBasic.getInsuredList(), existingInsureds)) {
      insuredRepository
          .deleteInsuredByBanksaladUserIdAndOrganizationIdAndInsuNum(insuranceBasicEntity.getBanksaladUserId(),
              insuranceBasicEntity.getOrganizationId(), insuranceBasicEntity.getInsuNum());
      insuredRepository.flush();

      short insuredNo = 1;
      for (Insured insured : insuranceBasic.getInsuredList()) {
        InsuredEntity insuredEntity = insuredMapper.dtoToEntity(insured);
        insuredEntity.setBanksaladUserId(insuranceBasicEntity.getBanksaladUserId());
        insuredEntity.setOrganizationId(insuranceBasicEntity.getOrganizationId());
        insuredEntity.setSyncedAt(insuranceBasicEntity.getSyncedAt());
        insuredEntity.setInsuNum(insuranceBasicEntity.getInsuNum());
        insuredEntity.setInsuredNo(String.valueOf(insuredNo++));
        insuredEntity.setInsuredName(insured.getInsuredName());
        insuredEntity.setConsentId(insuranceBasicEntity.getConsentId());
        insuredEntity.setSyncRequestId(insuranceBasicEntity.getSyncRequestId());
        insuredEntity.setCreatedBy(insuranceBasicEntity.getCreatedBy());
        insuredEntity.setUpdatedBy(insuranceBasicEntity.getUpdatedBy());
        insuredRepository.save(insuredEntity);

        InsuredHistoryEntity insuredHistoryEntity = insuredHistoryMapper
            .entityToHistoryEntity(insuredEntity, InsuredHistoryEntity.builder().build());
        insuredHistoryRepository.save(insuredHistoryEntity);
      }
    }
  }

  @Override
  public void saveSearchTimestamp(ExecutionContext executionContext, InsuranceSummary insuranceSummary,
      long searchTimestamp) {
    insuranceSummaryService
        .updateBasicSearchTimestamp(executionContext.getBanksaladUserId(), executionContext.getOrganizationId(),
            insuranceSummary.getInsuNum(), searchTimestamp);
  }

  @Override
  public void saveResponseCode(ExecutionContext executionContext, InsuranceSummary insuranceSummary,
      String responseCode) {
    insuranceSummaryService
        .updateBasicResponseCode(executionContext.getBanksaladUserId(), executionContext.getOrganizationId(),
            insuranceSummary.getInsuNum(), responseCode);
  }
}
