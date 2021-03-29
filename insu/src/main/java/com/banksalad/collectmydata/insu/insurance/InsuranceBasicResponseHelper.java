package com.banksalad.collectmydata.insu.insurance;

import org.springframework.stereotype.Component;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.util.ObjectComparator;
import com.banksalad.collectmydata.finance.api.accountinfo.AccountInfoResponseHelper;
import com.banksalad.collectmydata.finance.api.accountinfo.dto.AccountResponse;
import com.banksalad.collectmydata.insu.common.db.entity.InsuranceBasicEntity;
import com.banksalad.collectmydata.insu.common.db.entity.InsuredEntity;
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
  public void saveAccountAndHistory(ExecutionContext executionContext, InsuranceSummary insuranceSummary,
      InsuranceBasic insuranceBasic) {

    long banksaladUserId = executionContext.getBanksaladUserId();
    String organizationId = executionContext.getOrganizationId();
    String insuNum = insuranceSummary.getInsuNum();

    InsuranceBasicEntity insuranceBasicEntity = insuranceBasicMapper.dtoToEntity(insuranceBasic);
    insuranceBasicEntity.setSyncedAt(executionContext.getSyncStartedAt());
    insuranceBasicEntity.setBanksaladUserId(banksaladUserId);
    insuranceBasicEntity.setOrganizationId(organizationId);
    insuranceBasicEntity.setInsuNum(insuranceSummary.getInsuNum());

    InsuranceBasicEntity existingInsuranceBasicEntity = insuranceBasicRepository
        .findByBanksaladUserIdAndOrganizationIdAndInsuNum(
            banksaladUserId,
            organizationId,
            insuNum
        ).orElse(null);

    if (!ObjectComparator.isSame(insuranceBasicEntity, existingInsuranceBasicEntity, ENTITY_EXCLUDE_FIELD)) {
      // 보험기본 및 history save;
      insuranceBasicRepository.save(insuranceBasicEntity);
      insuranceBasicHistoryRepository.save(insuranceBasicHistoryMapper.toHistoryEntity(insuranceBasicEntity));

      // 피보험자 목록 비교 및 저장.
      if (insuranceBasic.getInsuredList() != null) {
        for (Insured insured : insuranceBasic.getInsuredList()) {
          InsuredEntity insuredEntity = insuredMapper.dtoToEntity(insured);
          insuredEntity.setSyncedAt(executionContext.getSyncStartedAt());
          insuredEntity.setBanksaladUserId(banksaladUserId);
          insuredEntity.setOrganizationId(organizationId);
          insuredEntity.setInsuNum(insuNum);

          InsuredEntity existingInsuredEntity = insuredRepository
              .findByBanksaladUserIdAndOrganizationIdAndInsuNumAndInsuredNo(
                  banksaladUserId, organizationId, insuNum, insured.getInsuredNo()
              ).orElse(null);

          if (existingInsuredEntity != null) {
            insuredEntity.setId(existingInsuredEntity.getId());
            insuredEntity.setContractSearchResponseCode(existingInsuredEntity.getContractSearchResponseCode());
            insuredEntity.setContractSearchTimestamp(existingInsuredEntity.getContractSearchTimestamp());
          }

          if (!ObjectComparator.isSame(insuredEntity, existingInsuredEntity, ENTITY_EXCLUDE_FIELD)) {
            insuredRepository.save(insuredEntity);
            insuredHistoryRepository.save(insuredHistoryMapper.toHistoryEntity(insuredEntity));
          }
        }
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
