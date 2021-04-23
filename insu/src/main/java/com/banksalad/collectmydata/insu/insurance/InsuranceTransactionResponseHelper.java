package com.banksalad.collectmydata.insu.insurance;

import org.springframework.stereotype.Component;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.util.ObjectComparator;
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

import static com.banksalad.collectmydata.finance.common.constant.FinanceConstant.ENTITY_EXCLUDE_FIELD;

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
    long banksaladUserId = executionContext.getBanksaladUserId();
    String organizationId = executionContext.getOrganizationId();
    String insuNum = insuranceSummary.getInsuNum();

    for (InsuranceTransaction insuranceTransaction : insuranceTransactions) {
      insuranceTransaction.setInsuNum(insuNum);
      Integer transactionYearMonth = Integer.parseInt(insuranceTransaction.getTransAppliedMonth());

      InsuranceTransactionEntity transactionEntity = InsuranceTransactionEntity.builder()
          .transactionYearMonth(transactionYearMonth)
          .syncedAt(executionContext.getSyncStartedAt())
          .banksaladUserId(banksaladUserId)
          .organizationId(organizationId)
          .insuNum(insuNum)
          .transNo(insuranceTransaction.getTransNo())
          .transDate(insuranceTransaction.getTransDate())
          .transAppliedMonth(Integer.valueOf(insuranceTransaction.getTransAppliedMonth()))
          .paidAmt(insuranceTransaction.getPaidAmt())
          .currencyCode(insuranceTransaction.getCurrencyCode())
          .payMethod(insuranceTransaction.getPayMethod())
          .build();

      // load existing insurance transaction
      InsuranceTransactionEntity existingTransactionEntity = insuranceTransactionRepository
          .findByBanksaladUserIdAndOrganizationIdAndInsuNumAndTransNoAndTransactionYearMonth(
              banksaladUserId,
              organizationId,
              insuNum,
              insuranceTransaction.getTransNo(),
              transactionYearMonth
          ).orElse(InsuranceTransactionEntity.builder().build());

      // merge
      insuranceTransactionMapper.merge(transactionEntity, transactionEntity);

      // copy PK for update
      if (existingTransactionEntity != null) {
        transactionEntity.setId(existingTransactionEntity.getId());
      }

      // upsert
      if (!ObjectComparator.isSame(transactionEntity, existingTransactionEntity, ENTITY_EXCLUDE_FIELD)) {
        insuranceTransactionRepository.save(transactionEntity);
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
