package com.banksalad.collectmydata.capital.oplease;

import org.springframework.stereotype.Service;

import com.banksalad.collectmydata.capital.common.db.entity.OperatingLeaseTransactionEntity;
import com.banksalad.collectmydata.capital.common.db.mapper.OperatingLeaseTransactionMapper;
import com.banksalad.collectmydata.capital.common.db.repository.OperatingLeaseTransactionRepository;
import com.banksalad.collectmydata.capital.common.dto.Organization;
import com.banksalad.collectmydata.capital.common.service.ExternalApiService;
import com.banksalad.collectmydata.capital.oplease.dto.OperatingLeaseTransaction;
import com.banksalad.collectmydata.capital.summary.dto.AccountSummary;
import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.exception.CollectRuntimeException;
import com.banksalad.collectmydata.common.util.DateUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.factory.Mappers;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Deprecated
@Slf4j
@Service
@RequiredArgsConstructor
public class OperatingLeaseServiceImpl implements OperatingLeaseService {

  private final ExternalApiService externalApiService;
  private final OperatingLeaseTransactionRepository operatingLeaseTransactionRepository;
  private final OperatingLeaseTransactionMapper operatingLeaseTransactionMapper = Mappers
      .getMapper(OperatingLeaseTransactionMapper.class);

  private static final int INITIAL_YEARS_AGO = 1;

  @Override
  public List<OperatingLeaseTransaction> listOperatingLeaseTransactions(ExecutionContext executionContext,
      Organization organization, List<AccountSummary> accountSummaries) {

    AtomicReference<Boolean> isExceptionOccurred = new AtomicReference<>(false);
    List<OperatingLeaseTransaction> filteredOperatingLeaseTransactions = accountSummaries.stream()
        .map(accountSummary -> CompletableFuture.supplyAsync(() -> {

          /* set request date range */
          LocalDateTime transactionSyncedAt = accountSummary.getOperatingLeaseTransactionSyncedAt();
          if (transactionSyncedAt == null) {
            transactionSyncedAt = executionContext.getSyncStartedAt()
                .minusYears(INITIAL_YEARS_AGO)
                .plusDays(1L);
          }
          LocalDate fromDate = DateUtil.utcLocalDateTimeToKstLocalDateTime(transactionSyncedAt).toLocalDate();
          LocalDate toDate = DateUtil.utcLocalDateTimeToKstLocalDateTime(executionContext.getSyncStartedAt())
              .toLocalDate();

          /* request api */
          List<OperatingLeaseTransaction> operatingLeaseTransactions = new ArrayList<>();
          try {
            operatingLeaseTransactions = externalApiService
                .listOperatingLeaseTransactions(executionContext, organization, accountSummary, fromDate, toDate)
                .getOperatingLeaseTransactions();
          } catch (CollectRuntimeException e) {
            log.error("Failed to request API 6.7.6 : {}", e.getMessage());
            isExceptionOccurred.set(true);
          }

          /* save operatingLeaseTransaction */
          for (OperatingLeaseTransaction operatingLeaseTransaction : operatingLeaseTransactions) {
            try {
              saveOperatingLeaseTransaction(executionContext, accountSummary, operatingLeaseTransaction);
            } catch (Exception e) { // fixme : DB exception
              log.error("Failed to save OperatingLeaseTransactionEntity : {}", e.getMessage());
              isExceptionOccurred.set(true);
            }
          }

          /* update accountSummary OperatingLeaseTransactionSyncedAt */
          if (!isExceptionOccurred.get()) {
//            accountSummaryService.updateOperatingLeaseTransactionSyncedAt(executionContext, accountSummary);
          }

          return operatingLeaseTransactions;
        }).exceptionally(e -> {
          log.error("Unexpected service error : {}", e.getMessage());
          isExceptionOccurred.set(true);
          return null;
        }))
        .map(CompletableFuture::join)
        .filter(Objects::nonNull)
        .flatMap(List::stream)
        .collect(Collectors.toList());

    /* update userSyncStatus */
    if (!isExceptionOccurred.get()) {
/*      userSyncStatusService
          .updateUserSyncStatus(executionContext.getBanksaladUserId(), executionContext.getOrganizationId(),
              Apis.capital_get_operating_lease_transactions.getId(), executionContext.getSyncStartedAt(), null,
              executionResponseValidateService.isAllResponseResultSuccess(executionContext, isExceptionOccurred.get()));*/
    }
    return filteredOperatingLeaseTransactions;
  }

  private void saveOperatingLeaseTransaction(ExecutionContext executionContext, AccountSummary accountSummary,
      OperatingLeaseTransaction operatingLeaseTransaction) {

    /* find entity */
    final int transactionYearMonth = generateTransactionYearMonth(operatingLeaseTransaction);
    OperatingLeaseTransactionEntity entity = operatingLeaseTransactionRepository
        .findByBanksaladUserIdAndOrganizationIdAndAccountNumAndSeqnoAndTransDtimeAndTransNoAndTransactionYearMonth(
            executionContext.getBanksaladUserId(),
            executionContext.getOrganizationId(),
            accountSummary.getAccountNum(),
            accountSummary.getSeqno(),
            operatingLeaseTransaction.getTransDtime(),
            operatingLeaseTransaction.getTransNo(),
            transactionYearMonth)
        .orElse(OperatingLeaseTransactionEntity.builder().build());

    /* mapping response to entity */
    operatingLeaseTransactionMapper.merge(executionContext, accountSummary, operatingLeaseTransaction, entity);
    entity.setTransactionYearMonth(transactionYearMonth); // TODO : mapper 사용하여 처리
    operatingLeaseTransactionRepository.save(entity);
  }

  private int generateTransactionYearMonth(OperatingLeaseTransaction operatingLeaseTransaction) {
    String transDtime = operatingLeaseTransaction.getTransDtime();
    String yearMonthString = transDtime.substring(0, 6);

    return Integer.valueOf(yearMonthString);
  }
}
