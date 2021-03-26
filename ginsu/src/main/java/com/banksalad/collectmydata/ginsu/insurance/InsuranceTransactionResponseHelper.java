package com.banksalad.collectmydata.ginsu.insurance;

import org.springframework.stereotype.Component;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.finance.api.transaction.TransactionResponseHelper;
import com.banksalad.collectmydata.finance.api.transaction.dto.TransactionResponse;
import com.banksalad.collectmydata.ginsu.common.db.entity.InsuranceTransactionEntity;
import com.banksalad.collectmydata.ginsu.common.db.repository.InsuranceTransactionRepository;
import com.banksalad.collectmydata.ginsu.common.mapper.InsuranceTransactionMapper;
import com.banksalad.collectmydata.ginsu.common.service.InsuranceSummaryService;
import com.banksalad.collectmydata.ginsu.insurance.dto.InsuranceTransaction;
import com.banksalad.collectmydata.ginsu.insurance.dto.ListInsuranceTransactionsResponse;
import com.banksalad.collectmydata.ginsu.summary.dto.InsuranceSummary;
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

    ListInsuranceTransactionsResponse response = (ListInsuranceTransactionsResponse) transactionResponse;
    return response.getInsuranceTransactions();
  }

  @Override
  public void saveTransactions(ExecutionContext executionContext, InsuranceSummary insuranceSummary,
      List<InsuranceTransaction> insuranceTransactions) {

    final long banksaladUserId = executionContext.getBanksaladUserId();
    final String organizationId = executionContext.getOrganizationId();
    final String insuNum = insuranceSummary.getInsuNum();

    insuranceTransactionRepository
        .deleteInsuranceTransactionsByBanksaladUserIdAndOrganizationIdAndInsuNum(
            banksaladUserId,
            organizationId,
            insuNum
        );

    for (InsuranceTransaction insuranceTransaction : insuranceTransactions) {
      InsuranceTransactionEntity insuranceTransactionEntity = insuranceTransactionMapper
          .dtoToEntity(insuranceTransaction);

      insuranceTransactionEntity.setSyncedAt(executionContext.getSyncStartedAt());
      insuranceTransactionEntity.setBanksaladUserId(banksaladUserId);
      insuranceTransactionEntity.setOrganizationId(organizationId);
      insuranceTransactionEntity.setInsuNum(insuNum);

      insuranceTransactionRepository.save(insuranceTransactionEntity);
    }
  }

  @Override
  public void saveTransactionSyncedAt(ExecutionContext executionContext, InsuranceSummary insuranceSummary,
      LocalDateTime syncStartedAt) {

    insuranceSummaryService.updateTransactionSyncedAt(
        executionContext.getBanksaladUserId(),
        executionContext.getOrganizationId(),
        insuranceSummary,
        syncStartedAt
    );
  }

  @Override
  public void saveResponseCode(ExecutionContext executionContext, InsuranceSummary insuranceSummary,
      String responseCode) {

    insuranceSummaryService.updateTransactionResponseCode(
        executionContext.getBanksaladUserId(),
        executionContext.getOrganizationId(),
        insuranceSummary,
        responseCode
    );
  }
}
