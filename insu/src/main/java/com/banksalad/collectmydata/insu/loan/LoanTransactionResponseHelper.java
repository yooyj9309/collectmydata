package com.banksalad.collectmydata.insu.loan;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.finance.api.transaction.TransactionResponseHelper;
import com.banksalad.collectmydata.finance.api.transaction.dto.TransactionResponse;
import com.banksalad.collectmydata.insu.common.db.entity.LoanTransactionEntity;
import com.banksalad.collectmydata.insu.common.db.entity.LoanTransactionInterestEntity;
import com.banksalad.collectmydata.insu.common.db.repository.LoanTransactionInterestRepository;
import com.banksalad.collectmydata.insu.common.db.repository.LoanTransactionRepository;
import com.banksalad.collectmydata.insu.common.service.LoanSummaryService;
import com.banksalad.collectmydata.insu.loan.dto.ListLoanTransactionResponse;
import com.banksalad.collectmydata.insu.loan.dto.LoanTransaction;
import com.banksalad.collectmydata.insu.loan.dto.LoanTransactionInterest;
import com.banksalad.collectmydata.insu.summary.dto.LoanSummary;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class LoanTransactionResponseHelper implements TransactionResponseHelper<LoanSummary, LoanTransaction> {

  private final LoanSummaryService loanSummaryService;
  private final LoanTransactionRepository loanTransactionRepository;
  private final LoanTransactionInterestRepository loanTransactionInterestRepository;

  @Override
  public List<LoanTransaction> getTransactionsFromResponse(TransactionResponse transactionResponse) {
    return ((ListLoanTransactionResponse) transactionResponse).getTransList();
  }

  @Override
  @Transactional
  public void saveTransactions(ExecutionContext executionContext, LoanSummary loanSummary,
      List<LoanTransaction> loanTransactions) {
    for (LoanTransaction loanTransaction : loanTransactions) {
      LoanTransactionEntity loanTransactionEntity = LoanTransactionEntity.builder()
          .transactionYearMonth(Integer.valueOf(loanTransaction.getTransDtime().substring(0, 6)))
          .syncedAt(executionContext.getSyncStartedAt())
          .banksaladUserId(executionContext.getBanksaladUserId())
          .organizationId(executionContext.getOrganizationId())
          .accountNum(loanSummary.getAccountNum())
          .transDtime(loanTransaction.getTransDtime())
          .transNo(loanTransaction.getTransNo())
          .currencyCode(loanTransaction.getCurrencyCode())
          .loanPaidAmt(loanTransaction.getLoanPaidAmt())
          .intPaidAmt(loanTransaction.getIntPaidAmt())
          .build();
      loanTransactionEntity.setCreatedBy(executionContext.getRequestedBy());
      loanTransactionEntity.setUpdatedBy(executionContext.getRequestedBy());

      LoanTransactionEntity existingLoanTransactionEntity = loanTransactionRepository
          .findByBanksaladUserIdAndOrganizationIdAndAccountNumAndTransDtimeAndTransNoAndTransactionYearMonth(
              executionContext.getBanksaladUserId(), executionContext.getOrganizationId(), loanSummary.getAccountNum(),
              loanTransaction.getTransDtime(), loanTransaction.getTransNo(),
              loanTransactionEntity.getTransactionYearMonth())
          .orElse(null);

      if (existingLoanTransactionEntity != null) {
        continue;
      }

      loanTransactionRepository.save(loanTransactionEntity);
      saveTransactionInterests(loanTransaction, loanTransactionEntity);
    }
  }

  private void saveTransactionInterests(LoanTransaction loanTransaction, LoanTransactionEntity loanTransactionEntity) {
    int intNo = 1;
    for (LoanTransactionInterest interest : loanTransaction.getIntList()) {
      LoanTransactionInterestEntity loanTransactionInterestEntity = LoanTransactionInterestEntity.builder()
          .transactionYearMonth(loanTransactionEntity.getTransactionYearMonth())
          .syncedAt(loanTransactionEntity.getSyncedAt())
          .banksaladUserId(loanTransactionEntity.getBanksaladUserId())
          .organizationId(loanTransactionEntity.getOrganizationId())
          .accountNum(loanTransactionEntity.getAccountNum())
          .transDtime(loanTransactionEntity.getTransDtime())
          .transNo(loanTransactionEntity.getTransNo())
          .intNo(intNo++)
          .intStartDate(interest.getIntStartDate())
          .intEndDate(interest.getIntEndDate())
          .intRate(interest.getIntRate())
          .intType(interest.getIntType())
          .build();
      loanTransactionInterestRepository.save(loanTransactionInterestEntity);
    }
  }

  @Override
  public void saveTransactionSyncedAt(ExecutionContext executionContext, LoanSummary loanSummary,
      LocalDateTime syncStartedAt) {
    loanSummaryService
        .updateTransactionSyncedAt(executionContext.getBanksaladUserId(), executionContext.getOrganizationId(),
            loanSummary.getAccountNum(), syncStartedAt);
  }

  @Override
  public void saveResponseCode(ExecutionContext executionContext, LoanSummary loanSummary, String responseCode) {
    loanSummaryService
        .updateTransactionResponseCode(executionContext.getBanksaladUserId(), executionContext.getOrganizationId(),
            loanSummary.getAccountNum(), responseCode);
  }
}
