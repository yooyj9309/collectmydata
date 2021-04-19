package com.banksalad.collectmydata.ginsu.insurance;

import org.springframework.stereotype.Component;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.util.ObjectComparator;
import com.banksalad.collectmydata.finance.api.accountinfo.AccountInfoResponseHelper;
import com.banksalad.collectmydata.finance.api.accountinfo.dto.AccountResponse;
import com.banksalad.collectmydata.ginsu.common.db.entity.InsuranceBasicEntity;
import com.banksalad.collectmydata.ginsu.common.db.entity.InsuredEntity;
import com.banksalad.collectmydata.ginsu.common.db.repository.InsuranceBasicHistoryRepository;
import com.banksalad.collectmydata.ginsu.common.db.repository.InsuranceBasicRepository;
import com.banksalad.collectmydata.ginsu.common.db.repository.InsuredHistoryRepository;
import com.banksalad.collectmydata.ginsu.common.db.repository.InsuredRepository;
import com.banksalad.collectmydata.ginsu.common.mapper.InsuranceBasicHistoryMapper;
import com.banksalad.collectmydata.ginsu.common.mapper.InsuranceBasicMapper;
import com.banksalad.collectmydata.ginsu.common.mapper.InsuredHistoryMapper;
import com.banksalad.collectmydata.ginsu.common.mapper.InsuredMapper;
import com.banksalad.collectmydata.ginsu.common.service.InsuranceSummaryService;
import com.banksalad.collectmydata.ginsu.insurance.dto.GetInsuranceBasicResponse;
import com.banksalad.collectmydata.ginsu.insurance.dto.InsuranceBasic;
import com.banksalad.collectmydata.ginsu.insurance.dto.Insured;
import com.banksalad.collectmydata.ginsu.summary.dto.InsuranceSummary;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static com.banksalad.collectmydata.finance.common.constant.FinanceConstant.ENTITY_EXCLUDE_FIELD;

@Component
@RequiredArgsConstructor
public class InsuranceBasicInfoResponseHelper implements AccountInfoResponseHelper<InsuranceSummary, InsuranceBasic> {

  private final InsuranceSummaryService insuranceSummaryService;

  private final InsuranceBasicRepository insuranceBasicRepository;
  private final InsuranceBasicHistoryRepository insuranceBasicHistoryRepository;

  private final InsuredRepository insuredRepository;
  private final InsuredHistoryRepository insuredHistoryRepository;

  private final InsuranceBasicMapper insuranceBasicMapper = Mappers.getMapper(InsuranceBasicMapper.class);
  private final InsuranceBasicHistoryMapper insuranceBasicHistoryMapper = Mappers.getMapper(
      InsuranceBasicHistoryMapper.class);

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
    LocalDateTime syncedAt = executionContext.getSyncStartedAt();
    String insuNum = insuranceSummary.getInsuNum();

    // upsert insurance basic
    InsuranceBasicEntity insuranceBasicEntity = insuranceBasicMapper.dtoToEntity(insuranceBasic);
    insuranceBasicEntity.setBanksaladUserId(banksaladUserId);
    insuranceBasicEntity.setOrganizationId(organizationId);
    insuranceBasicEntity.setSyncedAt(syncedAt);
    insuranceBasicEntity.setInsuNum(insuNum);

    InsuranceBasicEntity existingInsuranceBasicEntity = insuranceBasicRepository
        .findByBanksaladUserIdAndOrganizationIdAndInsuNum(banksaladUserId, organizationId, insuNum)
        .orElseGet(() -> null);

    if (existingInsuranceBasicEntity != null) {
      insuranceBasicEntity.setId(existingInsuranceBasicEntity.getId());
    }

    if (!ObjectComparator.isSame(insuranceBasicEntity, existingInsuranceBasicEntity, ENTITY_EXCLUDE_FIELD)) {
      insuranceBasicRepository.save(insuranceBasicEntity);
      insuranceBasicHistoryRepository.save(insuranceBasicHistoryMapper.toHistoryEntity(insuranceBasicEntity));
    }

    // delete insert insured
    List<Insured> existingInsureds = insuredRepository
        .findByBanksaladUserIdAndOrganizationIdAndInsuNum(banksaladUserId, organizationId, insuNum)
        .stream()
        .map(insuredMapper::entityToDto)
        .collect(Collectors.toList());

    if (!ObjectComparator.isSameListIgnoreOrder(insuranceBasic.getInsuredList(), existingInsureds)) {
      insuredRepository
          .deleteInsuredByBanksaladUserIdAndOrganizationIdAndInsuNum(banksaladUserId, organizationId, insuNum);

      short insuredNo = 1;
      for (Insured insured : insuranceBasic.getInsuredList()) {
        InsuredEntity insuredEntity = insuredMapper.dtoToEntity(insured);
        insuredEntity.setBanksaladUserId(banksaladUserId);
        insuredEntity.setOrganizationId(organizationId);
        insuredEntity.setSyncedAt(syncedAt);
        insuredEntity.setInsuNum(insuNum);
        insuredEntity.setInsuredNo(insuredNo++);
        insuredEntity.setInsuredName(insured.getInsuredName());

        insuredRepository.save(insuredEntity);
        insuredHistoryRepository.save(insuredHistoryMapper.toHistoryEntity(insuredEntity));
      }
    }
  }

  @Override
  public void saveSearchTimestamp(ExecutionContext executionContext, InsuranceSummary insuranceSummary,
      long searchTimestamp) {

    insuranceSummaryService.updateBasicSearchTimestamp(executionContext.getBanksaladUserId(),
        executionContext.getOrganizationId(), insuranceSummary, searchTimestamp);
  }

  @Override
  public void saveResponseCode(ExecutionContext executionContext, InsuranceSummary insuranceSummary,
      String responseCode) {

    insuranceSummaryService.updateBasicResponseCode(executionContext.getBanksaladUserId(),
        executionContext.getOrganizationId(), insuranceSummary, responseCode);
  }
}
