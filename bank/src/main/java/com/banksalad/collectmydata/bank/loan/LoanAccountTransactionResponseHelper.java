package com.banksalad.collectmydata.bank.loan;

import org.springframework.stereotype.Component;

import com.banksalad.collectmydata.bank.common.db.entity.LoanAccountTransactionEntity;
import com.banksalad.collectmydata.bank.common.db.entity.LoanAccountTransactionInterestEntity;
import com.banksalad.collectmydata.bank.common.db.repository.LoanAccountTransactionInterestRepository;
import com.banksalad.collectmydata.bank.common.db.repository.LoanAccountTransactionRepository;
import com.banksalad.collectmydata.bank.common.mapper.LoanAccountTransactionInterestMapper;
import com.banksalad.collectmydata.bank.common.mapper.LoanAccountTransactionMapper;
import com.banksalad.collectmydata.bank.common.service.AccountSummaryService;
import com.banksalad.collectmydata.bank.loan.dto.ListLoanAccountTransactionsResponse;
import com.banksalad.collectmydata.bank.loan.dto.LoanAccountTransaction;
import com.banksalad.collectmydata.bank.loan.dto.LoanAccountTransactionInterest;
import com.banksalad.collectmydata.bank.summary.dto.AccountSummary;
import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.crypto.HashUtil;
import com.banksalad.collectmydata.common.util.ObjectComparator;
import com.banksalad.collectmydata.finance.api.transaction.TransactionResponseHelper;
import com.banksalad.collectmydata.finance.api.transaction.dto.TransactionResponse;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.util.List;

import static com.banksalad.collectmydata.finance.common.constant.FinanceConstant.ENTITY_EXCLUDE_FIELD;

@Component
@RequiredArgsConstructor
public class LoanAccountTransactionResponseHelper implements
    TransactionResponseHelper<AccountSummary, LoanAccountTransaction> {

  private final AccountSummaryService accountSummaryService;
  private final LoanAccountTransactionRepository loanAccountTransactionRepository;
  private final LoanAccountTransactionInterestRepository loanAccountTransactionInterestRepository;

  private final LoanAccountTransactionMapper loanAccountTransactionMapper = Mappers
      .getMapper(LoanAccountTransactionMapper.class);
  private final LoanAccountTransactionInterestMapper loanAccountTransactionInterestMapper = Mappers
      .getMapper(LoanAccountTransactionInterestMapper.class);

  @Override
  public List<LoanAccountTransaction> getTransactionsFromResponse(TransactionResponse transactionResponse) {
    ListLoanAccountTransactionsResponse response = (ListLoanAccountTransactionsResponse) transactionResponse;
    return response.getLoanAccountTransactions();
  }

  @Override
  public void saveTransactions(ExecutionContext executionContext, AccountSummary accountSummary,
      List<LoanAccountTransaction> loanAccountTransactions) {

    for (LoanAccountTransaction loanAccountTransaction : loanAccountTransactions) {

      // start to save loan account transaction entity
      LoanAccountTransactionEntity loanAccountTransactionEntity = loanAccountTransactionMapper
          .dtoToEntity(loanAccountTransaction);

      Integer transactionYearMonth = generateTransactionYearMonth(loanAccountTransaction.getTransDtime());
      String uniqueTransNo = generateUniqueTransNo(loanAccountTransaction);

      loanAccountTransactionEntity.setTransactionYearMonth(transactionYearMonth);
      loanAccountTransactionEntity.setSyncedAt(executionContext.getSyncStartedAt());
      loanAccountTransactionEntity.setBanksaladUserId(executionContext.getBanksaladUserId());
      loanAccountTransactionEntity.setOrganizationId(executionContext.getOrganizationId());
      loanAccountTransactionEntity.setAccountNum(accountSummary.getAccountNum());
      loanAccountTransactionEntity.setSeqno(accountSummary.getSeqno());
      loanAccountTransactionEntity.setUniqueTransNo(uniqueTransNo);

      // TODO : on-demand, scheduler
      loanAccountTransactionEntity.setCreatedBy(String.valueOf(executionContext.getBanksaladUserId()));
      loanAccountTransactionEntity.setUpdatedBy(String.valueOf(executionContext.getBanksaladUserId()));
      loanAccountTransactionEntity.setConsentId(executionContext.getConsentId());

      LoanAccountTransactionEntity existingLoanAccountTransactionEntity = loanAccountTransactionRepository
          .findByBanksaladUserIdAndOrganizationIdAndAccountNumAndSeqnoAndUniqueTransNoAndTransactionYearMonth(
              loanAccountTransactionEntity.getBanksaladUserId(),
              loanAccountTransactionEntity.getOrganizationId(),
              loanAccountTransactionEntity.getAccountNum(),
              loanAccountTransactionEntity.getSeqno(),
              loanAccountTransactionEntity.getUniqueTransNo(),
              loanAccountTransactionEntity.getTransactionYearMonth()
          ).orElse(null);

      if (existingLoanAccountTransactionEntity != null) {
        loanAccountTransactionEntity.setId(existingLoanAccountTransactionEntity.getId());
      }

      if (ObjectComparator
          .isSame(loanAccountTransactionEntity, existingLoanAccountTransactionEntity, ENTITY_EXCLUDE_FIELD)) {
        continue;
      }
      loanAccountTransactionRepository.save(loanAccountTransactionEntity);

      // finish loan account transaction entity

      // delete previous loanAccountTransactionInterest entities
      loanAccountTransactionInterestRepository
          .deleteAllByBanksaladUserIdAndOrganizationIdAndAccountNumAndUniqueTransNoAndTransactionYearMonth(
              executionContext.getBanksaladUserId(),
              executionContext.getOrganizationId(),
              accountSummary.getAccountNum(),
              uniqueTransNo,
              transactionYearMonth
          );

      loanAccountTransactionInterestRepository.flush();

      int intNo = 0;
      final List<LoanAccountTransactionInterest> interests = loanAccountTransaction
          .getLoanAccountTransactionInterests();

      //start loan account transaction interest entity
      for (LoanAccountTransactionInterest interest : interests) {
        LoanAccountTransactionInterestEntity interestEntity = loanAccountTransactionInterestMapper
            .dtoToEntity(interest);
        interestEntity.setIntNo(intNo++);
        interestEntity.setTransactionYearMonth(transactionYearMonth);
        interestEntity.setSyncedAt(executionContext.getSyncStartedAt());
        interestEntity.setBanksaladUserId(executionContext.getBanksaladUserId());
        interestEntity.setOrganizationId(executionContext.getOrganizationId());
        interestEntity.setAccountNum(accountSummary.getAccountNum());
        interestEntity.setUniqueTransNo(uniqueTransNo);

        // TODO : on-demand, scheduler
        interestEntity.setCreatedBy(String.valueOf(executionContext.getBanksaladUserId()));
        interestEntity.setUpdatedBy(String.valueOf(executionContext.getBanksaladUserId()));
        interestEntity.setConsentId(executionContext.getConsentId());

        loanAccountTransactionInterestRepository.save(interestEntity);
      }
      // finish loan account transaction interest entity
    }
  }

  @Override
  public void saveTransactionSyncedAt(ExecutionContext executionContext, AccountSummary accountSummary,
      LocalDateTime syncStartedAt) {
    accountSummaryService.updateTransactionSyncedAt(
        executionContext.getBanksaladUserId(),
        executionContext.getOrganizationId(),
        accountSummary,
        syncStartedAt
    );
  }

  @Override
  public void saveResponseCode(ExecutionContext executionContext, AccountSummary accountSummary, String responseCode) {
    accountSummaryService.updateTransactionResponseCode(
        executionContext.getBanksaladUserId(),
        executionContext.getOrganizationId(),
        accountSummary,
        responseCode
    );
  }

  private int generateTransactionYearMonth(String transDtime) {
    String yearMonthString = transDtime.substring(0, 6);

    return Integer.parseInt(yearMonthString);
  }

  private String generateUniqueTransNo(LoanAccountTransaction loanAccountTransaction) {
    String transDtime = loanAccountTransaction.getTransDtime();
    String transType = loanAccountTransaction.getTransType();
    String transAmtString = loanAccountTransaction.getTransAmt().toString();
    String balanceAmtString = loanAccountTransaction.getBalanceAmt().toString();

    return HashUtil.hashCat(transDtime, transType, transAmtString, balanceAmtString);
  }
}
