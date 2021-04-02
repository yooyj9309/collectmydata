package com.banksalad.collectmydata.efin.account;

import org.springframework.stereotype.Component;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.crypto.HashUtil;
import com.banksalad.collectmydata.common.util.ObjectComparator;
import com.banksalad.collectmydata.efin.account.dto.AccountPrepaidTransaction;
import com.banksalad.collectmydata.efin.account.dto.ListAccountPrepaidTransactionsResponse;
import com.banksalad.collectmydata.efin.common.db.entity.PrepaidTransactionEntity;
import com.banksalad.collectmydata.efin.common.db.repository.PrepaidTransactionRepository;
import com.banksalad.collectmydata.efin.common.mapper.PrepaidTransactionMapper;
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
  private final PrepaidTransactionRepository prepaidTransactionRepository;

  private final PrepaidTransactionMapper prepaidTransactionMapper = Mappers.getMapper(PrepaidTransactionMapper.class);

  @Override
  public List<AccountPrepaidTransaction> getTransactionsFromResponse(TransactionResponse transactionResponse) {
    return ((ListAccountPrepaidTransactionsResponse) transactionResponse).getAccountPrepaidTransactions();
  }

  @Override
  public void saveTransactions(ExecutionContext executionContext, AccountSummary accountSummary,
      List<AccountPrepaidTransaction> accountPrepaidTransactions) {

    accountPrepaidTransactions.forEach(accountPrepaidTransaction -> {
      PrepaidTransactionEntity prepaidTransactionEntity = prepaidTransactionMapper
          .dtoToEntity(accountPrepaidTransaction);
      prepaidTransactionEntity
          .setTransactionYearMonth(NumberUtils.toInt(StringUtils.left(accountPrepaidTransaction.getTransDtime(), 6)));
      prepaidTransactionEntity.setSyncedAt(executionContext.getSyncStartedAt());
      prepaidTransactionEntity.setBanksaladUserId(executionContext.getBanksaladUserId());
      prepaidTransactionEntity.setOrganizationId(executionContext.getOrganizationId());
      prepaidTransactionEntity.setSubKey(accountSummary.getSubKey());
      prepaidTransactionEntity.setUniqueTransNo(generateUniqueTransNo(accountPrepaidTransaction));

      PrepaidTransactionEntity existingPrepaidTransactionEntity = prepaidTransactionRepository
          .findByBanksaladUserIdAndOrganizationIdAndTransactionYearMonthAndSubKeyAndFobNameAndUniqueTransNo(
              prepaidTransactionEntity.getBanksaladUserId(), prepaidTransactionEntity.getOrganizationId(),
              prepaidTransactionEntity.getTransactionYearMonth(),
              prepaidTransactionEntity.getSubKey(), prepaidTransactionEntity.getFobName(),
              prepaidTransactionEntity.getUniqueTransNo()
          ).map(targetPrepaidTransaction -> {
            prepaidTransactionEntity.setId(targetPrepaidTransaction.getId());
            return targetPrepaidTransaction;
          }).orElseGet(() -> PrepaidTransactionEntity.builder().build());

      if (!ObjectComparator.isSame(prepaidTransactionEntity, existingPrepaidTransactionEntity, ENTITY_EXCLUDE_FIELD)) {
        prepaidTransactionRepository.save(prepaidTransactionEntity);
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

  private String generateUniqueTransNo(AccountPrepaidTransaction accountPrepaidTransaction) {
    String transDtime = accountPrepaidTransaction.getTransDtime();
    String transType = accountPrepaidTransaction.getTransType();
    String transAmtString = accountPrepaidTransaction.getTransAmt().toString();
    String balanceAmtString = Optional.ofNullable(accountPrepaidTransaction.getBalanceAmt()).orElse(BigDecimal.ZERO)
        .toString();

    return HashUtil.hashCat(transDtime, transType, transAmtString, balanceAmtString);
  }
}
