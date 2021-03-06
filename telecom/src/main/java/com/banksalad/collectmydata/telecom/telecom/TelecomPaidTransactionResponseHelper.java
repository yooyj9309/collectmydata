package com.banksalad.collectmydata.telecom.telecom;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.util.DateUtil;
import com.banksalad.collectmydata.finance.api.transaction.TransactionResponseHelper;
import com.banksalad.collectmydata.finance.api.transaction.dto.TransactionResponse;
import com.banksalad.collectmydata.telecom.common.db.entity.TelecomPaidTransactionEntity;
import com.banksalad.collectmydata.telecom.common.db.repository.TelecomPaidTransactionRepository;
import com.banksalad.collectmydata.telecom.common.mapper.TelecomPaidTransactionMapper;
import com.banksalad.collectmydata.telecom.common.service.TelecomSummaryService;
import com.banksalad.collectmydata.telecom.summary.dto.TelecomSummary;
import com.banksalad.collectmydata.telecom.telecom.dto.ListTelecomPaidTransactionsResponse;
import com.banksalad.collectmydata.telecom.telecom.dto.TelecomPaidTransaction;

import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class TelecomPaidTransactionResponseHelper implements
    TransactionResponseHelper<TelecomSummary, TelecomPaidTransaction> {

  private final TelecomSummaryService telecomSummaryService;
  private final TelecomPaidTransactionRepository telecomPaidTransactionRepository;
  private final TelecomPaidTransactionMapper telecomPaidTransactionMapper = Mappers.getMapper(
      TelecomPaidTransactionMapper.class);

  @Override
  public List<TelecomPaidTransaction> getTransactionsFromResponse(TransactionResponse transactionResponse) {

    ListTelecomPaidTransactionsResponse response = (ListTelecomPaidTransactionsResponse) transactionResponse;

    return response.getTransList();
  }

  @Transactional
  @Override
  public void saveTransactions(ExecutionContext executionContext, TelecomSummary telecomSummary,
      List<TelecomPaidTransaction> telecomPaidTransactions) {

    final LocalDateTime syncedAt = executionContext.getSyncStartedAt();
    final long banksaladUserId = executionContext.getBanksaladUserId();
    final String organizationId = executionContext.getOrganizationId();
    final String mgmtId = telecomSummary.getMgmtId();
    final String fromDateKst = DateUtil.utcLocalDateTimeToKstDateString(
        telecomSummaryService.getPaidTransactionSyncedAt(banksaladUserId, organizationId, telecomSummary));

    // Remove transactions in the previous toDate (current fromDate).
    // ?????? ??? ?????? ?????? ????????? ?????? ?????? ?????? ??????????????? ?????? ????????????.
    int transactionYearMonth = generateTransactionYearMonth(fromDateKst);
    telecomPaidTransactionRepository
        .deleteByBanksaladUserIdAndOrganizationIdAndMgmtIdAndTransactionYearMonthAndTransDate(
            banksaladUserId, organizationId, mgmtId, transactionYearMonth, fromDateKst);

    // Save new transactions.
    // ?????? ??????????????? ?????? ???????????? API????????? ?????? ?????? ????????? ????????????.
    List<TelecomPaidTransactionEntity> paidTransactionEntities = new ArrayList<>();
    for (TelecomPaidTransaction telecomPaidTransaction : telecomPaidTransactions) {
      transactionYearMonth = generateTransactionYearMonth(telecomPaidTransaction.getTransDate());
      TelecomPaidTransactionEntity telecomPaidTransactionEntity = telecomPaidTransactionMapper.dtoToEntity(telecomPaidTransaction);
      telecomPaidTransactionEntity.setTransactionYearMonth(transactionYearMonth);
      telecomPaidTransactionEntity.setSyncedAt(syncedAt);
      telecomPaidTransactionEntity.setBanksaladUserId(banksaladUserId);
      telecomPaidTransactionEntity.setOrganizationId(organizationId);
      telecomPaidTransactionEntity.setMgmtId(mgmtId);
      telecomPaidTransactionEntity.setConsentId(executionContext.getConsentId());
      telecomPaidTransactionEntity.setSyncRequestId(executionContext.getSyncRequestId());
      telecomPaidTransactionEntity.setCreatedBy(executionContext.getRequestedBy());
      telecomPaidTransactionEntity.setUpdatedBy(executionContext.getRequestedBy());

      paidTransactionEntities.add(telecomPaidTransactionEntity);
    }
    telecomPaidTransactionRepository.saveAll(paidTransactionEntities);
  }

  @Override
  public void saveTransactionSyncedAt(ExecutionContext executionContext, TelecomSummary telecomSummary,
      LocalDateTime syncStartedAt) {

    telecomSummaryService
        .updatePaidTransactionSyncedAt(executionContext.getBanksaladUserId(), executionContext.getOrganizationId(),
            telecomSummary,
            syncStartedAt);
  }

  @Override
  public void saveResponseCode(ExecutionContext executionContext, TelecomSummary telecomSummary, String responseCode) {

    telecomSummaryService
        .updatePaidTransactionResponseCode(executionContext.getBanksaladUserId(), executionContext.getOrganizationId(),
            telecomSummary, responseCode);
  }

  private int generateTransactionYearMonth(String transDate) {

    return Integer.parseInt(transDate.substring(0, 6));
  }
}
