package com.banksalad.collectmydata.efin.account;

import org.springframework.stereotype.Component;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.util.ObjectComparator;
import com.banksalad.collectmydata.efin.account.dto.AccountTransaction;
import com.banksalad.collectmydata.efin.account.dto.ListAccountTransactionsResponse;
import com.banksalad.collectmydata.efin.common.db.entity.TransactionEntity;
import com.banksalad.collectmydata.efin.common.db.repository.TransactionRepository;
import com.banksalad.collectmydata.efin.common.mapper.TransactionMapper;
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
  private final TransactionRepository transactionRepository;

  private final TransactionMapper transactionMapper = Mappers.getMapper(TransactionMapper.class);

  @Override
  public List<AccountTransaction> getTransactionsFromResponse(TransactionResponse transactionResponse) {
    return ((ListAccountTransactionsResponse) transactionResponse).getAccountTransactions();
  }

  @Override
  public void saveTransactions(ExecutionContext executionContext, AccountSummary accountSummary,
      List<AccountTransaction> accountTransactions) {
    accountTransactions.forEach(accountTransaction -> {
      TransactionEntity transactionEntity = transactionMapper.dtoToEntity(accountTransaction);
      transactionEntity.setSyncedAt(executionContext.getSyncStartedAt());
      transactionEntity.setBanksaladUserId(executionContext.getBanksaladUserId());
      transactionEntity.setOrganizationId(executionContext.getOrganizationId());
      transactionEntity.setSubKey(accountSummary.getSubKey());

      TransactionEntity existingTransactionEntity = transactionRepository
          .findByBanksaladUserIdAndOrganizationIdAndSubKeyAndFobNameAndTransNumAndTransDtime(
              transactionEntity.getBanksaladUserId(), transactionEntity.getOrganizationId(),
              transactionEntity.getSubKey(), transactionEntity.getFobName(),
              transactionEntity.getTransNum(), transactionEntity.getTransDtime()
          ).map(targetTransaction -> {
            transactionEntity.setId(targetTransaction.getId());
            return targetTransaction;
          }).orElseGet(() -> TransactionEntity.builder().build());

      if (!ObjectComparator.isSame(transactionEntity, existingTransactionEntity, ENTITY_EXCLUDE_FIELD)) {
        transactionRepository.save(transactionEntity);
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
