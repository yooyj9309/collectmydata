package com.banksalad.collectmydata.invest.account;

import org.springframework.stereotype.Component;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.crypto.HashUtil;
import com.banksalad.collectmydata.finance.api.transaction.TransactionResponseHelper;
import com.banksalad.collectmydata.finance.api.transaction.dto.TransactionResponse;
import com.banksalad.collectmydata.invest.account.dto.AccountTransaction;
import com.banksalad.collectmydata.invest.account.dto.ListAccountTransactionsResponse;
import com.banksalad.collectmydata.invest.common.db.entity.AccountTransactionEntity;
import com.banksalad.collectmydata.invest.common.db.entity.mapper.AccountTransactionMapper;
import com.banksalad.collectmydata.invest.common.db.repository.AccountTransactionRepository;
import com.banksalad.collectmydata.invest.common.service.AccountSummaryService;
import com.banksalad.collectmydata.invest.summary.dto.AccountSummary;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class AccountTransactionResponseHelper implements TransactionResponseHelper<AccountSummary, AccountTransaction> {

  private final AccountSummaryService accountSummaryService;
  private final AccountTransactionRepository accountTransactionRepository;

  private final AccountTransactionMapper accountTransactionMapper = Mappers.getMapper(AccountTransactionMapper.class);

  @Override
  public List<AccountTransaction> getTransactionsFromResponse(TransactionResponse transactionResponse) {
    ListAccountTransactionsResponse response = (ListAccountTransactionsResponse) transactionResponse;
    return response.getAccountTransactions();
  }

  @Override
  public void saveTransactions(ExecutionContext executionContext, AccountSummary accountSummary,
      List<AccountTransaction> accountTransactions) {

    for (AccountTransaction accountTransaction : accountTransactions) {
      AccountTransactionEntity accountTransactionEntity = accountTransactionMapper.dtoToEntity(accountTransaction);
      accountTransactionEntity.setTransactionYearMonth(generateTransactionYearMonth(accountTransaction));
      accountTransactionEntity.setSyncedAt(executionContext.getSyncStartedAt());
      accountTransactionEntity.setBanksaladUserId(executionContext.getBanksaladUserId());
      accountTransactionEntity.setOrganizationId(executionContext.getOrganizationId());
      accountTransactionEntity.setAccountNum(accountSummary.getAccountNum());
      accountTransactionEntity.setUniqueTransNo(generateUniqueTransNo(accountTransaction));

      accountTransactionRepository
          .findByTransactionYearMonthAndBanksaladUserIdAndOrganizationIdAndAccountNumAndUniqueTransNo(
              accountTransactionEntity.getTransactionYearMonth(), accountTransactionEntity.getBanksaladUserId(),
              accountTransactionEntity.getOrganizationId(), accountTransactionEntity.getAccountNum(),
              accountTransactionEntity.getUniqueTransNo())
          .orElseGet(() -> accountTransactionRepository.save(accountTransactionEntity));
    }
  }

  @Override
  public void saveTransactionSyncedAt(ExecutionContext executionContext, AccountSummary accountSummary,
      LocalDateTime syncStartedAt) {
    accountSummaryService
        .updateTransactionSyncedAt(executionContext.getBanksaladUserId(), executionContext.getOrganizationId(),
            accountSummary.getAccountNum(), syncStartedAt);
  }

  @Override
  public void saveResponseCode(ExecutionContext executionContext, AccountSummary accountSummary, String responseCode) {
    accountSummaryService
        .updateTransactionResponseCode(executionContext.getBanksaladUserId(), executionContext.getOrganizationId(),
            accountSummary.getAccountNum(), responseCode);
  }

  private String generateUniqueTransNo(AccountTransaction accountTransaction) {
    String transDtime = accountTransaction.getTransDtime();
    String transType = accountTransaction.getTransType();
    String transAmt = accountTransaction.getTransAmt().toString();
    String balanceAmt = accountTransaction.getBalanceAmt().toString();

    return HashUtil.hashCat(transDtime, transType, transAmt, balanceAmt);
  }

  private int generateTransactionYearMonth(AccountTransaction accountTransaction) {
    String transDtime = accountTransaction.getTransDtime();
    String yearMonthString = transDtime.substring(0, 6);

    return Integer.parseInt(yearMonthString);
  }
}
