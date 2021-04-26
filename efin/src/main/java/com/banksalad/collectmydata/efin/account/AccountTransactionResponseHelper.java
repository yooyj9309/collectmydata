package com.banksalad.collectmydata.efin.account;

import org.springframework.stereotype.Component;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.util.ObjectComparator;
import com.banksalad.collectmydata.efin.account.dto.AccountTransaction;
import com.banksalad.collectmydata.efin.account.dto.ListAccountTransactionsResponse;
import com.banksalad.collectmydata.efin.common.db.entity.AccountTransactionEntity;
import com.banksalad.collectmydata.efin.common.db.repository.AccountTransactionRepository;
import com.banksalad.collectmydata.efin.common.mapper.AccountTransactionMapper;
import com.banksalad.collectmydata.efin.common.service.AccountSummaryService;
import com.banksalad.collectmydata.efin.summary.dto.AccountSummary;
import com.banksalad.collectmydata.finance.api.transaction.TransactionResponseHelper;
import com.banksalad.collectmydata.finance.api.transaction.dto.TransactionResponse;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.util.List;

import static com.banksalad.collectmydata.finance.common.constant.FinanceConstant.ENTITY_EXCLUDE_FIELD;

@Component
@RequiredArgsConstructor
public class AccountTransactionResponseHelper implements TransactionResponseHelper<AccountSummary, AccountTransaction> {

  private final AccountSummaryService accountSummaryService;
  private final AccountTransactionRepository accountTransactionRepository;

  private final AccountTransactionMapper accountTransactionMapper = Mappers.getMapper(AccountTransactionMapper.class);

  @Override
  public List<AccountTransaction> getTransactionsFromResponse(TransactionResponse transactionResponse) {
    return ((ListAccountTransactionsResponse) transactionResponse).getAccountTransactions();
  }

  @Override
  public void saveTransactions(ExecutionContext executionContext, AccountSummary accountSummary,
      List<AccountTransaction> accountTransactions) {
    accountTransactions.forEach(accountTransaction -> {
      AccountTransactionEntity accountTransactionEntity = accountTransactionMapper.dtoToEntity(accountTransaction);
      accountTransactionEntity.setSyncedAt(executionContext.getSyncStartedAt());
      accountTransactionEntity.setBanksaladUserId(executionContext.getBanksaladUserId());
      accountTransactionEntity.setOrganizationId(executionContext.getOrganizationId());
      accountTransactionEntity.setSubKey(accountSummary.getSubKey());

      AccountTransactionEntity existingAccountTransactionEntity = accountTransactionRepository
          .findByBanksaladUserIdAndOrganizationIdAndSubKeyAndFobNameAndTransNumAndTransDtime(
              accountTransactionEntity.getBanksaladUserId(), accountTransactionEntity.getOrganizationId(),
              accountTransactionEntity.getSubKey(), accountTransactionEntity.getFobName(),
              accountTransactionEntity.getTransNum(), accountTransactionEntity.getTransDtime()
          ).map(targetTransaction -> {
            accountTransactionEntity.setId(targetTransaction.getId());
            return targetTransaction;
          }).orElseGet(() -> AccountTransactionEntity.builder().build());

      if (!ObjectComparator.isSame(accountTransactionEntity, existingAccountTransactionEntity, ENTITY_EXCLUDE_FIELD)) {
        accountTransactionRepository.save(accountTransactionEntity);
      }
    });
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
        .updateTransactionResponseCode(executionContext.getBanksaladUserId(),
            executionContext.getOrganizationId(),
            accountSummary,
            responseCode);

  }
}
