package com.banksalad.collectmydata.bank.deposit;

import org.springframework.stereotype.Component;

import com.banksalad.collectmydata.bank.common.db.entity.DepositAccountTransactionEntity;
import com.banksalad.collectmydata.bank.common.db.repository.DepositAccountTransactionRepository;
import com.banksalad.collectmydata.bank.common.mapper.DepositAccountTransactionMapper;
import com.banksalad.collectmydata.bank.common.service.AccountSummaryService;
import com.banksalad.collectmydata.bank.deposit.dto.DepositAccountTransaction;
import com.banksalad.collectmydata.bank.deposit.dto.ListDepositAccountTransactionsResponse;
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
public class DepositAccountTransactionResponseHelper implements
    TransactionResponseHelper<AccountSummary, DepositAccountTransaction> {

  private final AccountSummaryService accountSummaryService;
  private final DepositAccountTransactionRepository depositAccountTransactionRepository;

  private final DepositAccountTransactionMapper depositAccountTransactionMapper = Mappers
      .getMapper(DepositAccountTransactionMapper.class);

  @Override
  public List<DepositAccountTransaction> getTransactionsFromResponse(TransactionResponse transactionResponse) {
    ListDepositAccountTransactionsResponse response = (ListDepositAccountTransactionsResponse) transactionResponse;
    return response.getDepositAccountTransactions();
  }

  @Override
  public void saveTransactions(ExecutionContext executionContext, AccountSummary accountSummary,
      List<DepositAccountTransaction> depositAccountTransactions) {

    for (DepositAccountTransaction depositAccountTransaction : depositAccountTransactions) {

      DepositAccountTransactionEntity depositAccountTransactionEntity = depositAccountTransactionMapper
          .dtoToEntity(depositAccountTransaction);
      depositAccountTransactionEntity.setTransactionYearMonth(generateTransactionYearMonth(depositAccountTransaction));
      depositAccountTransactionEntity.setSyncedAt(executionContext.getSyncStartedAt());
      depositAccountTransactionEntity.setBanksaladUserId(executionContext.getBanksaladUserId());
      depositAccountTransactionEntity.setOrganizationId(executionContext.getOrganizationId());
      depositAccountTransactionEntity.setAccountNum(accountSummary.getAccountNum());
      depositAccountTransactionEntity.setSeqno(accountSummary.getSeqno());
      depositAccountTransactionEntity.setUniqueTransNo(generateUniqueTransNo(depositAccountTransaction));
      depositAccountTransactionEntity.setConsentId(executionContext.getConsentId());
      depositAccountTransactionEntity.setSyncRequestId(executionContext.getSyncRequestId());
      depositAccountTransactionEntity.setCreatedBy(String.valueOf(executionContext.getRequestedBy()));
      depositAccountTransactionEntity.setUpdatedBy(String.valueOf(executionContext.getRequestedBy()));

      depositAccountTransactionRepository
          .findByTransactionYearMonthAndBanksaladUserIdAndOrganizationIdAndAccountNumAndSeqnoAndCurrencyCodeAndUniqueTransNo(
              depositAccountTransactionEntity.getTransactionYearMonth(),
              depositAccountTransactionEntity.getBanksaladUserId(),
              depositAccountTransactionEntity.getOrganizationId(),
              depositAccountTransactionEntity.getAccountNum(),
              depositAccountTransactionEntity.getSeqno(),
              depositAccountTransactionEntity.getCurrencyCode(),
              depositAccountTransactionEntity.getUniqueTransNo())
          .orElseGet(() -> depositAccountTransactionRepository.save(depositAccountTransactionEntity));
    }
  }

  @Override
  public void saveTransactionSyncedAt(ExecutionContext executionContext, AccountSummary accountSummary,
      LocalDateTime syncStartedAt) {
    accountSummaryService
        .updateTransactionSyncedAt(executionContext.getBanksaladUserId(), executionContext.getOrganizationId(),
            accountSummary,
            syncStartedAt);
  }

  @Override
  public void saveResponseCode(ExecutionContext executionContext, AccountSummary accountSummary, String responseCode) {
    accountSummaryService
        .updateTransactionResponseCode(executionContext.getBanksaladUserId(), executionContext.getOrganizationId(),
            accountSummary, responseCode);
  }

  private int generateTransactionYearMonth(DepositAccountTransaction depositAccountTransaction) {
    String transDtime = depositAccountTransaction.getTransDtime();
    String yearMonthString = transDtime.substring(0, 6);

    return Integer.valueOf(yearMonthString);
  }

  private String generateUniqueTransNo(DepositAccountTransaction depositAccountTransaction) {
    String transDtime = depositAccountTransaction.getTransDtime();
    String transType = depositAccountTransaction.getTransType();
    String transAmtString = depositAccountTransaction.getTransAmt().toString();
    String balanceAmtString = depositAccountTransaction.getBalanceAmt().toString();

    return HashUtil.hashCat(transDtime, transType, transAmtString, balanceAmtString);
  }
}
