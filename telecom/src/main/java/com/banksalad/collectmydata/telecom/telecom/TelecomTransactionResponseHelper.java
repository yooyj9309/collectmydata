package com.banksalad.collectmydata.telecom.telecom;

import org.springframework.stereotype.Component;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.util.ObjectComparator;
import com.banksalad.collectmydata.finance.api.transaction.TransactionResponseHelper;
import com.banksalad.collectmydata.finance.api.transaction.dto.TransactionResponse;
import com.banksalad.collectmydata.telecom.common.db.entity.TransactionEntity;
import com.banksalad.collectmydata.telecom.common.db.repository.TransactionRepository;
import com.banksalad.collectmydata.telecom.common.mapper.TransactionMapper;
import com.banksalad.collectmydata.telecom.common.service.TelecomSummaryService;
import com.banksalad.collectmydata.telecom.summary.dto.TelecomSummary;
import com.banksalad.collectmydata.telecom.telecom.dto.ListTelecomTransactionsResponse;
import com.banksalad.collectmydata.telecom.telecom.dto.TelecomTransaction;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.util.List;

import static com.banksalad.collectmydata.finance.common.constant.FinanceConstant.*;

@Component
@RequiredArgsConstructor
public class TelecomTransactionResponseHelper implements TransactionResponseHelper<TelecomSummary, TelecomTransaction> {

  private final TelecomSummaryService telecomSummaryService;
  private final TransactionRepository transactionRepository;

  private final TransactionMapper transactionMapper = Mappers.getMapper(TransactionMapper.class);

  @Override
  public List<TelecomTransaction> getTransactionsFromResponse(TransactionResponse transactionResponse) {
    return ((ListTelecomTransactionsResponse) transactionResponse).getTelecomTransactions();
  }

  @Override
  public void saveTransactions(ExecutionContext executionContext, TelecomSummary telecomSummary,
      List<TelecomTransaction> telecomTransactions) {
    for (TelecomTransaction telecomTransaction : telecomTransactions) {

      /* mapping dto to entity */
      TransactionEntity transactionEntity = transactionMapper.dtoToEntity(telecomTransaction);
      transactionEntity.setBanksaladUserId(executionContext.getBanksaladUserId());
      transactionEntity.setOrganizationId(executionContext.getOrganizationId());
      transactionEntity.setSyncedAt(executionContext.getSyncStartedAt());
      transactionEntity.setMgmtId(telecomSummary.getMgmtId());

      /* load existing entity */
      TransactionEntity existingTransactionEntity = transactionRepository
          .findByBanksaladUserIdAndOrganizationIdAndMgmtIdAndTransMonth(
              executionContext.getBanksaladUserId(), executionContext.getOrganizationId(), telecomSummary.getMgmtId(),
              Integer.valueOf(telecomTransaction.getTransMonth()))
          .orElse(null);

      /* copy primary key for update */
      if (existingTransactionEntity != null) {
        transactionEntity.setId(existingTransactionEntity.getId());
      }

      /* upsert entity and history entity */
      if (!ObjectComparator.isSame(transactionEntity, existingTransactionEntity, ENTITY_EXCLUDE_FIELD)) {
        transactionRepository.save(transactionEntity);
      }
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
