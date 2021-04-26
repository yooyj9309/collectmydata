package com.banksalad.collectmydata.efin.account;

import org.springframework.stereotype.Component;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.crypto.HashUtil;
import com.banksalad.collectmydata.common.util.ObjectComparator;
import com.banksalad.collectmydata.efin.account.dto.AccountPrepaidTransaction;
import com.banksalad.collectmydata.efin.account.dto.ListAccountPrepaidTransactionsResponse;
import com.banksalad.collectmydata.efin.common.db.entity.AccountPrepaidTransactionEntity;
import com.banksalad.collectmydata.efin.common.db.repository.AccountPrepaidTransactionRepository;
import com.banksalad.collectmydata.efin.common.mapper.AccountPrepaidTransactionMapper;
import com.banksalad.collectmydata.efin.common.service.AccountSummaryService;
import com.banksalad.collectmydata.efin.summary.dto.AccountSummary;
import com.banksalad.collectmydata.finance.api.transaction.TransactionResponseHelper;
import com.banksalad.collectmydata.finance.api.transaction.dto.TransactionResponse;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.banksalad.collectmydata.finance.common.constant.FinanceConstant.ENTITY_EXCLUDE_FIELD;

@Component
@RequiredArgsConstructor
public class AccountPrepaidTransactionResponseHelper implements
    TransactionResponseHelper<AccountSummary, AccountPrepaidTransaction> {

  private final AccountSummaryService accountSummaryService;
  private final AccountPrepaidTransactionRepository accountPrepaidTransactionRepository;

  private final AccountPrepaidTransactionMapper accountPrepaidTransactionMapper = Mappers.getMapper(
      AccountPrepaidTransactionMapper.class);

  @Override
  public List<AccountPrepaidTransaction> getTransactionsFromResponse(TransactionResponse transactionResponse) {
    return ((ListAccountPrepaidTransactionsResponse) transactionResponse).getAccountPrepaidTransactions();
  }

  @Override
  public void saveTransactions(ExecutionContext executionContext, AccountSummary accountSummary,
      List<AccountPrepaidTransaction> accountPrepaidTransactions) {

    accountPrepaidTransactions.forEach(accountPrepaidTransaction -> {
      AccountPrepaidTransactionEntity accountPrepaidTransactionEntity = accountPrepaidTransactionMapper
          .dtoToEntity(accountPrepaidTransaction);
      accountPrepaidTransactionEntity
          .setTransactionYearMonth(NumberUtils.toInt(StringUtils.left(accountPrepaidTransaction.getTransDtime(), 6)));
      accountPrepaidTransactionEntity.setSyncedAt(executionContext.getSyncStartedAt());
      accountPrepaidTransactionEntity.setBanksaladUserId(executionContext.getBanksaladUserId());
      accountPrepaidTransactionEntity.setOrganizationId(executionContext.getOrganizationId());
      accountPrepaidTransactionEntity.setSubKey(accountSummary.getSubKey());
      accountPrepaidTransactionEntity.setUniqueTransNo(generateUniqueTransNo(accountPrepaidTransactionEntity));

      AccountPrepaidTransactionEntity existingAccountPrepaidTransactionEntity = accountPrepaidTransactionRepository
          .findByBanksaladUserIdAndOrganizationIdAndTransactionYearMonthAndSubKeyAndFobNameAndUniqueTransNo(
              accountPrepaidTransactionEntity.getBanksaladUserId(), accountPrepaidTransactionEntity.getOrganizationId(),
              accountPrepaidTransactionEntity.getTransactionYearMonth(),
              accountPrepaidTransactionEntity.getSubKey(), accountPrepaidTransactionEntity.getFobName(),
              accountPrepaidTransactionEntity.getUniqueTransNo()
          ).map(targetPrepaidTransaction -> {
            accountPrepaidTransactionEntity.setId(targetPrepaidTransaction.getId());
            return targetPrepaidTransaction;
          }).orElseGet(() -> AccountPrepaidTransactionEntity.builder().build());

      if (!ObjectComparator.isSame(accountPrepaidTransactionEntity, existingAccountPrepaidTransactionEntity, ENTITY_EXCLUDE_FIELD)) {
        accountPrepaidTransactionRepository.save(accountPrepaidTransactionEntity);
      }
    });
  }

  @Override
  public void saveTransactionSyncedAt(ExecutionContext executionContext, AccountSummary accountSummary,
      LocalDateTime syncStartedAt) {
    accountSummaryService
        .updatePrepaidTransactionSyncedAt(executionContext.getBanksaladUserId(), executionContext.getOrganizationId(),
            accountSummary,
            syncStartedAt);
  }

  @Override
  public void saveResponseCode(ExecutionContext executionContext, AccountSummary accountSummary, String responseCode) {
    accountSummaryService
        .updatePrepaidTransactionResponseCode(executionContext.getBanksaladUserId(),
            executionContext.getOrganizationId(),
            accountSummary,
            responseCode);
  }

  private String generateUniqueTransNo(AccountPrepaidTransactionEntity accountPrepaidTransactionEntity) {
    String transDtime = accountPrepaidTransactionEntity.getTransDtime();
    String transType = accountPrepaidTransactionEntity.getTransType();
    String transAmtString = accountPrepaidTransactionEntity.getTransAmt().toString();
    String balanceAmtString = Optional.ofNullable(accountPrepaidTransactionEntity.getBalanceAmt()).orElse(BigDecimal.ZERO)
        .toString();

    return HashUtil.hashCat(transDtime, transType, transAmtString, balanceAmtString);
  }
}
