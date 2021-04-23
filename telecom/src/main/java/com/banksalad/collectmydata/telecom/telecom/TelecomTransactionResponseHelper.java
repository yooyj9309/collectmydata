package com.banksalad.collectmydata.telecom.telecom;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.finance.api.transaction.TransactionResponseHelper;
import com.banksalad.collectmydata.finance.api.transaction.dto.TransactionResponse;
import com.banksalad.collectmydata.telecom.common.db.entity.TelecomTransactionEntity;
import com.banksalad.collectmydata.telecom.common.db.repository.TelecomTransactionRepository;
import com.banksalad.collectmydata.telecom.common.mapper.TelecomTransactionMapper;
import com.banksalad.collectmydata.telecom.common.service.TelecomSummaryService;
import com.banksalad.collectmydata.telecom.summary.dto.TelecomSummary;
import com.banksalad.collectmydata.telecom.telecom.dto.ListTelecomTransactionsResponse;
import com.banksalad.collectmydata.telecom.telecom.dto.TelecomTransaction;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class TelecomTransactionResponseHelper implements TransactionResponseHelper<TelecomSummary, TelecomTransaction> {

  private final TelecomSummaryService telecomSummaryService;
  private final TelecomTransactionRepository telecomTransactionRepository;

  private final TelecomTransactionMapper telecomTransactionMapper = Mappers.getMapper(TelecomTransactionMapper.class);

  @Override
  public List<TelecomTransaction> getTransactionsFromResponse(TransactionResponse transactionResponse) {
    return ((ListTelecomTransactionsResponse) transactionResponse).getTelecomTransactions();
  }

  @Override
  public void saveTransactions(ExecutionContext executionContext, TelecomSummary telecomSummary,
      List<TelecomTransaction> telecomTransactions) {
    for (TelecomTransaction telecomTransaction : telecomTransactions) {

      /* mapping dto to entity */
      TelecomTransactionEntity transactionEntity = telecomTransactionMapper.dtoToEntity(telecomTransaction);
      transactionEntity.setBanksaladUserId(executionContext.getBanksaladUserId());
      transactionEntity.setOrganizationId(executionContext.getOrganizationId());
      transactionEntity.setSyncedAt(executionContext.getSyncStartedAt());
      transactionEntity.setMgmtId(telecomSummary.getMgmtId());

      /* Load existing entity. */
      TelecomTransactionEntity existingTelecomTransactionEntity = telecomTransactionRepository
          .findByBanksaladUserIdAndOrganizationIdAndMgmtIdAndTransMonth(
              executionContext.getBanksaladUserId(), executionContext.getOrganizationId(), telecomSummary.getMgmtId(),
              Integer.valueOf(telecomTransaction.getTransMonth()))
          .orElse(null);

      /* Skip the existing transaction. */
      if (existingTelecomTransactionEntity != null) {
        continue;
      }

      /* Insert the new transaction. */
      telecomTransactionRepository.save(transactionEntity);
    }
  }

  @Override
  public void saveTransactionSyncedAt(ExecutionContext executionContext, TelecomSummary telecomSummary,
      LocalDateTime syncStartedAt) {
    telecomSummaryService
        .updateTransactionSyncedAt(executionContext.getBanksaladUserId(), executionContext.getOrganizationId(),
            telecomSummary, syncStartedAt);
  }

  @Override
  public void saveResponseCode(ExecutionContext executionContext, TelecomSummary telecomSummary, String responseCode) {
    telecomSummaryService
        .updateTransactionResponseCode(executionContext.getBanksaladUserId(), executionContext.getOrganizationId(),
            telecomSummary, responseCode);
  }
}
