package com.banksalad.collectmydata.capital.oplease;

import com.banksalad.collectmydata.capital.common.db.entity.OperatingLeaseTransactionEntity;
import com.banksalad.collectmydata.capital.common.db.repository.OperatingLeaseTransactionRepository;
import com.banksalad.collectmydata.capital.common.mapper.OperatingLeaseTransactionMapper;
import com.banksalad.collectmydata.capital.common.service.AccountSummaryService;
import com.banksalad.collectmydata.capital.oplease.dto.ListOperatingLeaseTransactionsResponse;
import com.banksalad.collectmydata.capital.oplease.dto.OperatingLeaseTransaction;
import com.banksalad.collectmydata.capital.summary.dto.AccountSummary;
import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.util.ObjectComparator;
import com.banksalad.collectmydata.finance.api.transaction.TransactionResponseHelper;
import com.banksalad.collectmydata.finance.api.transaction.dto.TransactionResponse;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.util.List;

import static com.banksalad.collectmydata.finance.common.constant.FinanceConstant.ENTITY_EXCLUDE_FIELD;

@Component
@RequiredArgsConstructor
public class OperatingLeaseTransactionResponseHelper implements
    TransactionResponseHelper<AccountSummary, OperatingLeaseTransaction> {

  private final AccountSummaryService accountSummaryService;
  private final OperatingLeaseTransactionRepository operatingLeaseTransactionRepository;

  private final OperatingLeaseTransactionMapper operatingLeaseTransactionMapper = Mappers
      .getMapper(OperatingLeaseTransactionMapper.class);

  @Override
  public List<OperatingLeaseTransaction> getTransactionsFromResponse(TransactionResponse transactionResponse) {
    return ((ListOperatingLeaseTransactionsResponse) transactionResponse).getOperatingLeaseTransactions();
  }

  @Override
  public void saveTransactions(ExecutionContext executionContext, AccountSummary accountSummary,
      List<OperatingLeaseTransaction> operatingLeaseTransactions) {
    for (OperatingLeaseTransaction operatingLeaseTransaction : operatingLeaseTransactions) {

      /* mapping dto to entity */
      OperatingLeaseTransactionEntity operatingLeaseTransactionEntity = operatingLeaseTransactionMapper
          .dtoToEntity(operatingLeaseTransaction);
      operatingLeaseTransactionEntity
          .setTransactionYearMonth(Integer.parseInt(operatingLeaseTransaction.getTransDtime().substring(0, 6)));
      operatingLeaseTransactionEntity.setSyncedAt(executionContext.getSyncStartedAt());
      operatingLeaseTransactionEntity.setBanksaladUserId(executionContext.getBanksaladUserId());
      operatingLeaseTransactionEntity.setOrganizationId(executionContext.getOrganizationId());
      operatingLeaseTransactionEntity.setAccountNum(accountSummary.getAccountNum());
      operatingLeaseTransactionEntity.setSeqno(accountSummary.getSeqno());

      /* load existing entity */
      OperatingLeaseTransactionEntity existingOperatingLeaseTransactionEntity = operatingLeaseTransactionRepository
          .findByBanksaladUserIdAndOrganizationIdAndAccountNumAndSeqnoAndTransDtimeAndTransNoAndTransactionYearMonth(
              executionContext.getBanksaladUserId(),
              executionContext.getOrganizationId(),
              accountSummary.getAccountNum(),
              accountSummary.getSeqno(),
              operatingLeaseTransaction.getTransDtime(),
              operatingLeaseTransaction.getTransNo(),
              Integer.parseInt(operatingLeaseTransaction.getTransDtime().substring(0, 6)))
          .orElseGet(() -> OperatingLeaseTransactionEntity.builder().build());

      if (existingOperatingLeaseTransactionEntity != null) {
        operatingLeaseTransactionEntity.setId(existingOperatingLeaseTransactionEntity.getId());
      }

      /* upsert entity */
      if (!ObjectComparator
          .isSame(operatingLeaseTransactionEntity, existingOperatingLeaseTransactionEntity, ENTITY_EXCLUDE_FIELD)) {
        operatingLeaseTransactionRepository.save(operatingLeaseTransactionEntity);
      }
    }
  }

  @Override
  public void saveTransactionSyncedAt(ExecutionContext executionContext, AccountSummary accountSummary,
      LocalDateTime syncStartedAt) {
    accountSummaryService.updateOperatingLeaseTransactionSyncedAt(executionContext.getBanksaladUserId(),
        executionContext.getOrganizationId(), accountSummary, syncStartedAt);
  }

  @Override
  public void saveResponseCode(ExecutionContext executionContext, AccountSummary accountSummary, String responseCode) {
    accountSummaryService.updateOperatingLeaseTransactionResponseCode(executionContext.getBanksaladUserId(),
        executionContext.getOrganizationId(), accountSummary, responseCode);
  }
}
