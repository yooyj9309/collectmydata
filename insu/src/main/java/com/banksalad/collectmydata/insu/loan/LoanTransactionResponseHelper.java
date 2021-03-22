package com.banksalad.collectmydata.insu.loan;

import org.springframework.stereotype.Component;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.util.ObjectComparator;
import com.banksalad.collectmydata.finance.api.transaction.TransactionResponseHelper;
import com.banksalad.collectmydata.finance.api.transaction.dto.TransactionResponse;
import com.banksalad.collectmydata.insu.common.db.entity.LoanTransactionEntity;
import com.banksalad.collectmydata.insu.common.db.entity.LoanTransactionInterestEntity;
import com.banksalad.collectmydata.insu.common.db.repository.LoanTransactionInterestRepository;
import com.banksalad.collectmydata.insu.common.db.repository.LoanTransactionRepository;
import com.banksalad.collectmydata.insu.common.mapper.LoanTransactionMapper;
import com.banksalad.collectmydata.insu.common.service.LoanSummaryService;
import com.banksalad.collectmydata.insu.loan.dto.ListLoanTransactionResponse;
import com.banksalad.collectmydata.insu.loan.dto.LoanTransaction;
import com.banksalad.collectmydata.insu.loan.dto.LoanTransactionInterest;
import com.banksalad.collectmydata.insu.summary.dto.LoanSummary;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.util.List;

import static com.banksalad.collectmydata.finance.common.constant.FinanceConstant.ENTITY_EXCLUDE_FIELD;

@Component
@RequiredArgsConstructor
public class LoanTransactionResponseHelper implements TransactionResponseHelper<LoanSummary, LoanTransaction> {

  private final LoanSummaryService loanSummaryService;
  private final LoanTransactionRepository loanTransactionRepository;
  private final LoanTransactionInterestRepository loanTransactionInterestRepository;
  private final LoanTransactionMapper loanTransactionMapper = Mappers.getMapper(LoanTransactionMapper.class);

  @Override
  public List<LoanTransaction> getTransactionsFromResponse(TransactionResponse transactionResponse) {
    return ((ListLoanTransactionResponse) transactionResponse).getTransList();
  }

  @Override
  public void saveTransactions(ExecutionContext executionContext, LoanSummary loanSummary,
      List<LoanTransaction> loanTransactions) {
    long banksaladUserId = executionContext.getBanksaladUserId();
    String organizationId = executionContext.getOrganizationId();

    for (LoanTransaction loanTransaction : loanTransactions) {
      Integer transactionYearMonth = Integer.valueOf(loanTransaction.getTransDtime().substring(0, 6));

      LoanTransactionEntity loanTransactionEntity = LoanTransactionEntity.builder()
          .transactionYearMonth(transactionYearMonth)
          .syncedAt(executionContext.getSyncStartedAt())
          .banksaladUserId(banksaladUserId)
          .organizationId(organizationId)
          .accountNum(loanSummary.getAccountNum())
          .transDtime(loanTransaction.getTransDtime())
          .transNo(loanTransaction.getTransNo())
          .accountType(loanSummary.getAccountType()) // dusang, 여기에 type이..?
          .currencyCode(loanTransaction.getCurrencyCode())
          .loanPaidAmt(loanTransaction.getLoanPaidAmt())
          .intPaidAmt(loanTransaction.getIntPaidAmt())
          .build();

      LoanTransactionEntity loanTransactionEntityFromTable = loanTransactionRepository
          .findByBanksaladUserIdAndOrganizationIdAndAccountNumAndTransDtimeAndTransNoAndTransactionYearMonth(
              executionContext.getBanksaladUserId(), executionContext.getOrganizationId(), loanSummary.getAccountNum(),
              loanTransaction.getTransDtime(), loanTransaction.getTransNo(), transactionYearMonth)
          .orElse(LoanTransactionEntity.builder().build());

      if (loanTransactionEntityFromTable.getId() != null) {
        loanTransactionEntity.setId(loanTransactionEntityFromTable.getId());
      }

      if (!ObjectComparator.isSame(loanTransactionEntity, loanTransactionEntityFromTable, ENTITY_EXCLUDE_FIELD)) {
        loanTransactionRepository.save(loanTransactionEntity);

        loanTransactionInterestRepository
            .deleteAllByBanksaladUserIdAndOrganizationIdAndAccountNumAndTransDtimeAndTransNoAndTransactionYearMonth(
                executionContext.getBanksaladUserId(),
                executionContext.getOrganizationId(),
                loanSummary.getAccountNum(),
                loanTransaction.getTransDtime(),
                loanTransaction.getTransNo(),
                transactionYearMonth
            );
        loanTransactionInterestRepository.flush();

        int intNo = 1;
        for (LoanTransactionInterest loanTransactionInterest : loanTransaction.getIntList()) {
          LoanTransactionInterestEntity loanTransactionInterestEntity = LoanTransactionInterestEntity.builder()
              .transactionYearMonth(transactionYearMonth)
              .syncedAt(executionContext.getSyncStartedAt())
              .banksaladUserId(banksaladUserId)
              .organizationId(organizationId)
              .accountNum(loanSummary.getAccountNum())
              .transDtime(loanTransaction.getTransDtime())
              .transNo(loanTransaction.getTransNo())
              .intNo(intNo++)
              .intStartDate(loanTransactionInterest.getIntStartDate())
              .intEndDate(loanTransactionInterest.getIntEndDate())
              .intRate(loanTransactionInterest.getIntRate())
              .intType(loanTransactionInterest.getIntType())
              .build();
          loanTransactionInterestRepository.save(loanTransactionInterestEntity);
        }
      }
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
