package com.banksalad.collectmydata.bank.deposit;

import org.springframework.stereotype.Service;

import com.banksalad.collectmydata.bank.common.db.entity.DepositAccountTransactionEntity;
import com.banksalad.collectmydata.bank.common.db.entity.mapper.DepositAccountTransactionMapper;
import com.banksalad.collectmydata.bank.common.db.repository.DepositAccountTransactionRepository;
import com.banksalad.collectmydata.bank.common.dto.AccountSummary;
import com.banksalad.collectmydata.bank.common.service.AccountSummaryService;
import com.banksalad.collectmydata.bank.common.service.ExternalApiService;
import com.banksalad.collectmydata.bank.deposit.dto.DepositAccountTransaction;
import com.banksalad.collectmydata.bank.deposit.dto.ListDepositAccountTransactionsResponse;
import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.crypto.HashUtil;
import com.banksalad.collectmydata.common.organization.Organization;
import com.banksalad.collectmydata.common.util.DateRange;
import com.banksalad.collectmydata.common.util.DateUtil;
import com.banksalad.collectmydata.common.util.ObjectComparator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.factory.Mappers;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DepositAccountTransactionServiceImpl implements DepositAccountTransactionService {

  private static final int MINUS_YEAR = 1; // TODO jayden-lee 최초 조회 시 5년으로 설정 할지 논의 필요 (동적으로 변경하도록 작성?)
  private static final int INTERVAL_MONTH = 3; // TODO jayden-lee 거래내역 요청 시 월별 간격 논의 필요 (동적으로 변경하도록 작성?)

  private final ExternalApiService externalApiService;
  private final AccountSummaryService accountSummaryService;

  private final DepositAccountTransactionRepository depositAccountTransactionRepository;

  private final Executor executor;

  private final DepositAccountTransactionMapper depositAccountTransactionMapper = Mappers
      .getMapper(DepositAccountTransactionMapper.class);

  @Override
  public List<DepositAccountTransaction> listDepositAccountTransactions(ExecutionContext executionContext,
      List<AccountSummary> accountSummaries) {

    Organization organization = getOrganization(executionContext);

    // TODO jayden-lee Rename variable
    AtomicReference<Boolean> exceptionOccurred = new AtomicReference<>(false);

    List<DepositAccountTransaction> allDepositAccountTransactions = accountSummaries.stream()
        .map(accountSummary -> CompletableFuture.supplyAsync(() -> {

          /* request data */
          LocalDateTime transactionSyncedAt = accountSummary.getTransactionSyncedAt();
          if (transactionSyncedAt == null) {
            transactionSyncedAt = executionContext.getSyncStartedAt()
                .minusYears(MINUS_YEAR)
                .plusDays(1L);
          }

          LocalDate startDate = DateUtil.utcLocalDateTimeToKstLocalDateTime(transactionSyncedAt).toLocalDate();
          LocalDate endDate = DateUtil.utcLocalDateTimeToKstLocalDateTime(executionContext.getSyncStartedAt())
              .toLocalDate();

          // TODO jayden-lee Rename variable
          AtomicReference<Boolean> innerExceptionOccurred = new AtomicReference<>(false);

          List<DateRange> dateRanges = DateUtil.splitDate(startDate, endDate, INTERVAL_MONTH);

          // 계좌 1개의 기간 별 거래 내역 조회
          List<DepositAccountTransaction> depositAccountTransactions = dateRanges.stream()
              .map(dateRange -> CompletableFuture.supplyAsync(() -> {

                ListDepositAccountTransactionsResponse response = externalApiService
                    .listDepositAccountTransactions(executionContext,
                        organization.getOrganizationCode(), accountSummary.getAccountNum(), accountSummary.getSeqno(),
                        dateRange.getStartDate(), dateRange.getEndDate());

                return response.getDepositAccountTransactions();

              }, executor)
                  .exceptionally(e -> {
                    log.error("Failed to send deposit account transaction", e);
                    innerExceptionOccurred.set(true);
                    return null;
                  }))
              .map(CompletableFuture::join)
              .filter(Objects::nonNull)
              .flatMap(List::stream)
              .collect(Collectors.toList());

          /* save deposit account transactions */
          for (DepositAccountTransaction depositAccountTransaction : depositAccountTransactions) {
            try {
              saveDepositAccountTransactions(executionContext, accountSummary, depositAccountTransaction);
            } catch (Exception e) {
              log.error("Failed to save deposit account transaction", e);
              innerExceptionOccurred.set(true);
            }
          }

          /* update transactionSyncedAt */
          if (innerExceptionOccurred.get()) {
            exceptionOccurred.set(true);
          } else {
            accountSummaryService
                .updateTransactionSyncedAt(executionContext.getBanksaladUserId(), executionContext.getOrganizationId(),
                    accountSummary, executionContext.getSyncStartedAt());
          }

          return depositAccountTransactions;

        }, executor)
            .exceptionally(e -> {
              log.error("Failed to account deposit account transaction", e);
              exceptionOccurred.set(true);
              return null;
            }))
        .map(CompletableFuture::join)
        .filter(Objects::nonNull)
        .flatMap(List::stream)
        .collect(Collectors.toList());

    // TODO jayden-lee Api 200 Ok, userSyncStatusService.upsert(executionContext);
    if (exceptionOccurred.get()) {
      log.info("Don't update BA04 API UserSyncStatus");
    } else {
      log.info("Update BA04 API UserSyncStatus");
    }

    return allDepositAccountTransactions;
  }

  private void saveDepositAccountTransactions(ExecutionContext executionContext, AccountSummary accountSummary,
      DepositAccountTransaction depositAccountTransaction) {

    // convert to entity
    DepositAccountTransactionEntity depositAccountTransactionEntity = depositAccountTransactionMapper
        .dtoToEntity(depositAccountTransaction);
    depositAccountTransactionEntity.setTransactionYearMonth(generateTransactionYearMonth(depositAccountTransaction));
    depositAccountTransactionEntity.setSyncedAt(executionContext.getSyncStartedAt());
    depositAccountTransactionEntity.setBanksaladUserId(executionContext.getBanksaladUserId());
    depositAccountTransactionEntity.setOrganizationId(executionContext.getOrganizationId());
    depositAccountTransactionEntity.setAccountNum(accountSummary.getAccountNum());
    depositAccountTransactionEntity.setSeqno(accountSummary.getSeqno());
    depositAccountTransactionEntity.setUniqueTransNo(generateUniqueTransNo(depositAccountTransaction));

    // load existing deposit account transaction
    DepositAccountTransactionEntity existingDepositAccountTransactionEntity = depositAccountTransactionRepository
        .findByTransactionYearMonthAndBanksaladUserIdAndOrganizationIdAndAccountNumAndSeqnoAndCurrencyCodeAndUniqueTransNo(
            depositAccountTransactionEntity.getTransactionYearMonth(),
            depositAccountTransactionEntity.getBanksaladUserId(),
            depositAccountTransactionEntity.getOrganizationId(),
            depositAccountTransactionEntity.getAccountNum(),
            depositAccountTransactionEntity.getSeqno(),
            depositAccountTransactionEntity.getCurrencyCode(),
            depositAccountTransactionEntity.getUniqueTransNo());

    // copy PK for update
    if (existingDepositAccountTransactionEntity != null) {
      depositAccountTransactionEntity
          .setId(existingDepositAccountTransactionEntity.getId());
    }

    // upsert deposit account transaction
    if (!ObjectComparator
        .isSame(depositAccountTransactionEntity, existingDepositAccountTransactionEntity, "syncedAt")) {
      depositAccountTransactionRepository.save(depositAccountTransactionEntity);
    }
  }

  private int generateTransactionYearMonth(DepositAccountTransaction depositAccountTransaction) {
    String transDtime = depositAccountTransaction.getTransDtime();
    String yearMonthString = transDtime.substring(0, 6);

    return Integer.valueOf(yearMonthString);
  }

  private String generateUniqueTransNo(DepositAccountTransaction depositAccountTransaction) {
    String transDtime = depositAccountTransaction.getTransDtime();
    String transAmtString = depositAccountTransaction.getTransAmt().toString();
    String balanceAmtString = depositAccountTransaction.getBalanceAmt().toString();

    return HashUtil.hashCat(transDtime, transAmtString, balanceAmtString);
  }

  private Organization getOrganization(ExecutionContext executionContext) {
    return Organization.builder()
        .organizationCode("020") // TODO jayden-lee implement organizationCode
        .build();
  }
}
