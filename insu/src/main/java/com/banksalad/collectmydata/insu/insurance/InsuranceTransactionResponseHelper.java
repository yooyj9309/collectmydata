package com.banksalad.collectmydata.insu.insurance;

import org.springframework.stereotype.Component;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.finance.api.transaction.TransactionResponseHelper;
import com.banksalad.collectmydata.finance.api.transaction.dto.TransactionResponse;
import com.banksalad.collectmydata.insu.common.db.entity.InsuranceTransactionEntity;
import com.banksalad.collectmydata.insu.common.db.repository.InsuranceTransactionRepository;
import com.banksalad.collectmydata.insu.common.mapper.InsuranceTransactionMapper;
import com.banksalad.collectmydata.insu.common.service.InsuranceSummaryService;
import com.banksalad.collectmydata.insu.insurance.dto.InsuranceTransaction;
import com.banksalad.collectmydata.insu.insurance.dto.ListInsuranceTransactionsResponse;
import com.banksalad.collectmydata.insu.summary.dto.InsuranceSummary;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class InsuranceTransactionResponseHelper implements
    TransactionResponseHelper<InsuranceSummary, InsuranceTransaction> {

  private final InsuranceSummaryService insuranceSummaryService;
  private final InsuranceTransactionRepository insuranceTransactionRepository;

  private final InsuranceTransactionMapper insuranceTransactionMapper = Mappers
      .getMapper(InsuranceTransactionMapper.class);

  @Override

  public List<InsuranceTransaction> getTransactionsFromResponse(TransactionResponse transactionResponse) {
    return ((ListInsuranceTransactionsResponse) transactionResponse).getTransList();
  }

  @Override
  public void saveTransactions(ExecutionContext executionContext, InsuranceSummary insuranceSummary,
      List<InsuranceTransaction> insuranceTransactions) {

    for (InsuranceTransaction insuranceTransaction : insuranceTransactions) {
      InsuranceTransactionEntity insuranceTransactionEntity = insuranceTransactionMapper
          .dtoToEntity(insuranceTransaction);
      insuranceTransactionEntity.setInsuNum(insuranceSummary.getInsuNum());
      insuranceTransactionEntity.setTransactionYearMonth(
          Integer.valueOf(insuranceTransaction.getTransDate().substring(0, 6)));
      insuranceTransactionEntity.setSyncedAt(executionContext.getSyncStartedAt());
      insuranceTransactionEntity.setBanksaladUserId(executionContext.getBanksaladUserId());
      insuranceTransactionEntity.setOrganizationId(executionContext.getOrganizationId());
      insuranceTransactionEntity.setConsentId(executionContext.getConsentId());
      insuranceTransactionEntity.setSyncRequestId(executionContext.getSyncRequestId());
      insuranceTransactionEntity.setCreatedBy(executionContext.getRequestedBy());
      insuranceTransactionEntity.setUpdatedBy(executionContext.getRequestedBy());

      InsuranceTransactionEntity existingTransactionEntity = insuranceTransactionRepository
          .findByBanksaladUserIdAndOrganizationIdAndInsuNumAndTransNoAndTransactionYearMonth(
              insuranceTransactionEntity.getBanksaladUserId(),
              insuranceTransactionEntity.getOrganizationId(),
              insuranceTransactionEntity.getInsuNum(),
              insuranceTransactionEntity.getTransNo(),
              insuranceTransactionEntity.getTransactionYearMonth()
          ).orElse(null);

      if (existingTransactionEntity == null) {
        insuranceTransactionRepository.save(insuranceTransactionEntity);
      }
    }
  }

  @Override
  public void saveTransactionSyncedAt(ExecutionContext executionContext, InsuranceSummary insuranceSummary,
      LocalDateTime syncStartedAt) {
    insuranceSummaryService
        .updateTransactionSyncedAt(executionContext.getBanksaladUserId(), executionContext.getOrganizationId(),
            insuranceSummary, executionContext.getSyncStartedAt());
  }

  @Override
  public void saveResponseCode(ExecutionContext executionContext, InsuranceSummary insuranceSummary,
      String responseCode) {
    insuranceSummaryService
        .updateTransactionResponseCode(executionContext.getBanksaladUserId(), executionContext.getOrganizationId(),
            insuranceSummary, responseCode);
  }
}
