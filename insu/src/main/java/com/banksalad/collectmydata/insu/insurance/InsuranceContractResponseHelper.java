package com.banksalad.collectmydata.insu.insurance;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.util.ObjectComparator;
import com.banksalad.collectmydata.finance.api.accountinfo.AccountInfoResponseHelper;
import com.banksalad.collectmydata.finance.api.accountinfo.dto.AccountResponse;
import com.banksalad.collectmydata.insu.common.db.entity.InsuranceContractEntity;
import com.banksalad.collectmydata.insu.common.db.entity.InsuranceContractHistoryEntity;
import com.banksalad.collectmydata.insu.common.db.repository.InsuranceContractHistoryRepository;
import com.banksalad.collectmydata.insu.common.db.repository.InsuranceContractRepository;
import com.banksalad.collectmydata.insu.common.db.repository.InsuredRepository;
import com.banksalad.collectmydata.insu.common.mapper.InsuranceContractHistoryMapper;
import com.banksalad.collectmydata.insu.common.mapper.InsuranceContractMapper;
import com.banksalad.collectmydata.insu.insurance.dto.InsuranceContract;
import com.banksalad.collectmydata.insu.insurance.dto.Insured;
import com.banksalad.collectmydata.insu.insurance.dto.ListInsuranceContractsResponse;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class InsuranceContractResponseHelper implements
    AccountInfoResponseHelper<Insured, List<InsuranceContract>> {

  private final InsuredRepository insuredRepository;
  private final InsuranceContractRepository insuranceContractRepository;
  private final InsuranceContractMapper insuranceContractMapper = Mappers.getMapper(InsuranceContractMapper.class);
  private final InsuranceContractHistoryRepository insuranceContractHistoryRepository;
  private final InsuranceContractHistoryMapper insuranceContractHistoryMapper = Mappers
      .getMapper(InsuranceContractHistoryMapper.class);

  @Override
  public List<InsuranceContract> getAccountFromResponse(AccountResponse accountResponse) {

    return ((ListInsuranceContractsResponse) accountResponse).getContractList();
  }

  @Override
  @Transactional
  public void saveAccountAndHistory(ExecutionContext executionContext, Insured insured,
      List<InsuranceContract> insuranceContracts) {

    final LocalDateTime syncedAt = executionContext.getSyncStartedAt();
    final long banksaladUserId = executionContext.getBanksaladUserId();
    final String organizationId = executionContext.getOrganizationId();
    final String insuNum = insured.getInsuNum();
    final String insuredNo = insured.getInsuredNo();

    List<InsuranceContract> existingInsuranceContracts = insuranceContractRepository
        .findAllByBanksaladUserIdAndOrganizationIdAndInsuNum(banksaladUserId, organizationId, insuNum)
        .stream()
        .map(insuranceContractMapper::entityToDto)
        .collect(Collectors.toList());
    /* Do nothing if identical. */
    if (ObjectComparator.isSameListIgnoreOrder(insuranceContracts, existingInsuranceContracts)) {
      return;
    }
    // Delete All
    insuranceContractRepository.deleteAllByBanksaladUserIdAndOrganizationIdAndInsuNumAndInsuredNo(
        banksaladUserId, organizationId, insuNum, insuredNo
    );
    insuranceContractRepository.flush();
    /* Save main entity and history entity */
    for (int idx = 0; idx < insuranceContracts.size(); idx++) {
      InsuranceContract insuranceContract = insuranceContracts.get(idx);
      insuranceContract.setInsuNum(insuNum);
      insuranceContract.setInsuredNo(insuredNo);

      InsuranceContractEntity insuranceContractEntity = insuranceContractMapper.dtoToEntity(insuranceContract);
      insuranceContractEntity.setSyncedAt(syncedAt);
      insuranceContractEntity.setBanksaladUserId(banksaladUserId);
      insuranceContractEntity.setOrganizationId(organizationId);
      insuranceContractEntity.setContractNo(idx);
      insuranceContractEntity.setConsentId(executionContext.getConsentId());
      insuranceContractEntity.setSyncRequestId(executionContext.getSyncRequestId());
      insuranceContractEntity.setCreatedBy(executionContext.getRequestedBy());
      insuranceContractEntity.setUpdatedBy(executionContext.getRequestedBy());
      insuranceContractRepository.save(insuranceContractEntity);

      InsuranceContractHistoryEntity insuranceContractHistoryEntity = insuranceContractHistoryMapper
          .entityToHistoryEntity(insuranceContractEntity, InsuranceContractHistoryEntity.builder().build());
      insuranceContractHistoryRepository.save(insuranceContractHistoryEntity);
    }
  }

  @Override
  public void saveSearchTimestamp(ExecutionContext executionContext, Insured insured, long searchTimestamp) {

    insuredRepository
        .findByBanksaladUserIdAndOrganizationIdAndInsuNumAndInsuredNo(executionContext.getBanksaladUserId(),
            executionContext.getOrganizationId(), insured.getInsuNum(), insured.getInsuredNo())
        .ifPresent(insuredEntity -> {
          insuredEntity.setContractSearchTimestamp(searchTimestamp);
          insuredRepository.save(insuredEntity);
        });
  }

  @Override
  public void saveResponseCode(ExecutionContext executionContext, Insured insured, String responseCode) {

    insuredRepository
        .findByBanksaladUserIdAndOrganizationIdAndInsuNumAndInsuredNo(executionContext.getBanksaladUserId(),
            executionContext.getOrganizationId(), insured.getInsuNum(), insured.getInsuredNo())
        .ifPresent(insuredEntity -> {
          insuredEntity.setContractResponseCode(responseCode);
          insuredRepository.save(insuredEntity);
        });
  }
}
