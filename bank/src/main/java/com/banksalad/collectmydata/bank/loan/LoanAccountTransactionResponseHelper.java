package com.banksalad.collectmydata.bank.loan;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

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
import com.banksalad.collectmydata.finance.api.transaction.TransactionResponseHelper;
import com.banksalad.collectmydata.finance.api.transaction.dto.TransactionResponse;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.util.List;

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

  @Transactional
  @Override
  public void saveTransactions(ExecutionContext executionContext, AccountSummary accountSummary,
      List<LoanAccountTransaction> loanAccountTransactions) {

    for (LoanAccountTransaction loanAccountTransaction : loanAccountTransactions) {

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
      loanAccountTransactionEntity.setConsentId(executionContext.getConsentId());
      loanAccountTransactionEntity.setSyncRequestId(executionContext.getSyncRequestId());
      loanAccountTransactionEntity.setCreatedBy(String.valueOf(executionContext.getBanksaladUserId()));
      loanAccountTransactionEntity.setUpdatedBy(String.valueOf(executionContext.getBanksaladUserId()));

      LoanAccountTransactionEntity existingLoanAccountTransactionEntity = loanAccountTransactionRepository
          .findByBanksaladUserIdAndOrganizationIdAndAccountNumAndSeqnoAndUniqueTransNoAndTransactionYearMonth(
              loanAccountTransactionEntity.getBanksaladUserId(),
              loanAccountTransactionEntity.getOrganizationId(),
              loanAccountTransactionEntity.getAccountNum(),
              loanAccountTransactionEntity.getSeqno(),
              loanAccountTransactionEntity.getUniqueTransNo(),
              loanAccountTransactionEntity.getTransactionYearMonth())
          .orElse(null);

      if (existingLoanAccountTransactionEntity != null) {
        continue;
      }

      loanAccountTransactionRepository.save(loanAccountTransactionEntity);

      final List<LoanAccountTransactionInterest> interests = loanAccountTransaction
          .getLoanAccountTransactionInterests();

      int intNo = 0;
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
        interestEntity.setConsentId(executionContext.getConsentId());
        interestEntity.setSyncRequestId(executionContext.getSyncRequestId());
        interestEntity.setCreatedBy(String.valueOf(executionContext.getBanksaladUserId()));
        interestEntity.setUpdatedBy(String.valueOf(executionContext.getBanksaladUserId()));

        loanAccountTransactionInterestRepository.save(interestEntity);
      }
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
