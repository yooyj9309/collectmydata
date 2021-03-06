package com.banksalad.collectmydata.insu.insurance;

import org.springframework.stereotype.Component;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.util.ObjectComparator;
import com.banksalad.collectmydata.finance.api.accountinfo.AccountInfoResponseHelper;
import com.banksalad.collectmydata.finance.api.accountinfo.dto.AccountResponse;
import com.banksalad.collectmydata.insu.common.db.entity.InsurancePaymentEntity;
import com.banksalad.collectmydata.insu.common.db.entity.InsurancePaymentHistoryEntity;
import com.banksalad.collectmydata.insu.common.db.repository.InsurancePaymentHistoryRepository;
import com.banksalad.collectmydata.insu.common.db.repository.InsurancePaymentRepository;
import com.banksalad.collectmydata.insu.common.mapper.InsurancePaymentHistoryMapper;
import com.banksalad.collectmydata.insu.common.service.InsuranceSummaryService;
import com.banksalad.collectmydata.insu.insurance.dto.GetInsurancePaymentResponse;
import com.banksalad.collectmydata.insu.insurance.dto.InsurancePayment;
import com.banksalad.collectmydata.insu.summary.dto.InsuranceSummary;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;

import static com.banksalad.collectmydata.common.util.ObjectComparator.*;
import static com.banksalad.collectmydata.finance.common.constant.FinanceConstant.ENTITY_EXCLUDE_FIELD;

@Component
@RequiredArgsConstructor
public class InsurancePaymentResponseHelper implements AccountInfoResponseHelper<InsuranceSummary, InsurancePayment> {

  private final InsuranceSummaryService insuranceSummaryService;
  private final InsurancePaymentRepository insurancePaymentRepository;
  private final InsurancePaymentHistoryRepository insurancePaymentHistoryRepository;

  private final InsurancePaymentHistoryMapper insurancePaymentHistoryMapper = Mappers
      .getMapper(InsurancePaymentHistoryMapper.class);

  @Override
  public InsurancePayment getAccountFromResponse(AccountResponse accountResponse) {
    return ((GetInsurancePaymentResponse) accountResponse).getInsurancePayment();
  }

  @Override
  public void saveAccountAndHistory(ExecutionContext executionContext, InsuranceSummary insuranceSummary,
      InsurancePayment insurancePayment) {
    InsurancePaymentEntity entity = InsurancePaymentEntity.builder()
        .syncedAt(executionContext.getSyncStartedAt())
        .banksaladUserId(executionContext.getBanksaladUserId())
        .organizationId(executionContext.getOrganizationId())
        .insuNum(insuranceSummary.getInsuNum())
        .payDue(insurancePayment.getPayDue())
        .payCycle(insurancePayment.getPayCycle())
        .payCnt(insurancePayment.getPayCnt())
        .payOrgCode(insurancePayment.getPayOrgCode())
        .payDate(insurancePayment.getPayDate())
        .payEndDate(insurancePayment.getPayEndDate())
        .payAmt(insurancePayment.getPayAmt())
        .currencyCode(insurancePayment.getCurrencyCode())
        .autoPay(insurancePayment.isAutoPay())
        .consentId(executionContext.getConsentId())
        .syncRequestId(executionContext.getSyncRequestId())
        .build();
    entity.setCreatedBy(executionContext.getRequestedBy());
    entity.setUpdatedBy(executionContext.getRequestedBy());

    InsurancePaymentEntity existingEntity = insurancePaymentRepository
        .findByBanksaladUserIdAndOrganizationIdAndInsuNum(executionContext.getBanksaladUserId(),
            executionContext.getOrganizationId(), insuranceSummary.getInsuNum())
        .orElse(null);

    if (existingEntity != null) {
      entity.setId(existingEntity.getId());
      entity.setCreatedBy(existingEntity.getCreatedBy());
    }

    if (!isSame(entity, existingEntity, ENTITY_EXCLUDE_FIELD)) {
      insurancePaymentRepository.save(entity);

      InsurancePaymentHistoryEntity insurancePaymentHistoryEntity = insurancePaymentHistoryMapper
          .entityToHistoryEntity(entity, InsurancePaymentHistoryEntity.builder().build());
      insurancePaymentHistoryRepository.save(insurancePaymentHistoryEntity);
    }
  }

  @Override
  public void saveSearchTimestamp(ExecutionContext executionContext, InsuranceSummary insuranceSummary,
      long searchTimestamp) {
    insuranceSummaryService.updatePaymentSearchTimestamp(
        executionContext.getBanksaladUserId(),
        executionContext.getOrganizationId(),
        insuranceSummary.getInsuNum(),
        searchTimestamp
    );
  }

  @Override
  public void saveResponseCode(ExecutionContext executionContext, InsuranceSummary insuranceSummary,
      String responseCode) {
    insuranceSummaryService.updatePaymentResponseCode(
        executionContext.getBanksaladUserId(),
        executionContext.getOrganizationId(),
        insuranceSummary.getInsuNum(),
        responseCode
    );
  }
}
