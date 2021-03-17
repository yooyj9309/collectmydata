package com.banksalad.collectmydata.capital.account;

import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import com.banksalad.collectmydata.capital.account.dto.AccountDetail;
import com.banksalad.collectmydata.capital.account.dto.AccountDetailResponse;
import com.banksalad.collectmydata.capital.account.dto.AccountTransaction;
import com.banksalad.collectmydata.capital.account.dto.AccountTransactionResponse;
import com.banksalad.collectmydata.capital.common.db.entity.AccountDetailEntity;
import com.banksalad.collectmydata.capital.common.db.entity.AccountSummaryEntity;
import com.banksalad.collectmydata.capital.common.db.entity.AccountTransactionEntity;
import com.banksalad.collectmydata.capital.common.db.entity.AccountTransactionInterestEntity;
import com.banksalad.collectmydata.capital.common.db.mapper.AccountDetailHistoryMapper;
import com.banksalad.collectmydata.capital.common.db.mapper.AccountDetailMapper;
import com.banksalad.collectmydata.capital.common.db.mapper.AccountTransactionInterestMapper;
import com.banksalad.collectmydata.capital.common.db.mapper.AccountTransactionMapper;
import com.banksalad.collectmydata.capital.common.db.repository.AccountDetailHistoryRepository;
import com.banksalad.collectmydata.capital.common.db.repository.AccountDetailRepository;
import com.banksalad.collectmydata.capital.common.db.repository.AccountSummaryRepository;
import com.banksalad.collectmydata.capital.common.db.repository.AccountTransactionInterestRepository;
import com.banksalad.collectmydata.capital.common.db.repository.AccountTransactionRepository;
import com.banksalad.collectmydata.capital.common.dto.Organization;
import com.banksalad.collectmydata.capital.common.service.ExternalApiService;
import com.banksalad.collectmydata.capital.summary.dto.AccountSummary;
import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.crypto.HashUtil;
import com.banksalad.collectmydata.common.util.DateUtil;
import com.banksalad.collectmydata.common.util.ObjectComparator;
import javax.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

@Deprecated
@Slf4j
@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

  private final ExternalApiService externalApiService;
  private final AccountSummaryRepository accountSummaryRepository;
  private final AccountDetailRepository accountDetailRepository;
  private final AccountDetailHistoryRepository accountDetailHistoryRepository;
  private final AccountTransactionRepository accountTransactionRepository;
  private final AccountTransactionInterestRepository accountTransactionInterestRepository;
  private final ThreadPoolTaskExecutor threadPoolTaskExecutor;

  private final AccountDetailMapper accountDetailMapper = Mappers.getMapper(AccountDetailMapper.class);
  private final AccountDetailHistoryMapper accountDetailHistoryMapper = Mappers
      .getMapper(AccountDetailHistoryMapper.class);
  private final AccountTransactionMapper accountTransactionMapper = Mappers.getMapper(AccountTransactionMapper.class);
  private final AccountTransactionInterestMapper accountTransactionInterestMapper = Mappers
      .getMapper(AccountTransactionInterestMapper.class);

  @Override
  public List<AccountDetail> listAccountDetails(ExecutionContext executionContext, Organization organization,
      List<AccountSummary> accountSummaries) {
    List<AccountDetail> accountDetails = new ArrayList<>();

    boolean isExceptionOccurred = FALSE;
    for (AccountSummary accountSummary : accountSummaries) {
      try {
        AccountDetailResponse response = externalApiService
            .getAccountDetail(executionContext, organization, accountSummary);
        AccountDetailEntity accountDetailEntity = saveAccountDetailWithHistory(executionContext, accountSummary,
            response);
        accountDetails.add(accountDetailMapper.toAccountDetailFrom(accountDetailEntity));
        updateSearchTimestamp(executionContext, accountSummary, response);
      } catch (Exception e) {
        isExceptionOccurred = TRUE;
        log.error("Failed to save account detail", e);
      }
    }
    return accountDetails;
  }

  private AccountDetailEntity saveAccountDetailWithHistory(ExecutionContext executionContext,
      AccountSummary accountSummary, AccountDetailResponse accountDetailResponse) {
    AccountDetailEntity accountDetailEntity = accountDetailMapper.toAccountDetailEntityFrom(accountDetailResponse);
    accountDetailEntity.setSyncedAt(executionContext.getSyncStartedAt());
    accountDetailEntity.setBanksaladUserId(executionContext.getBanksaladUserId());
    accountDetailEntity.setOrganizationId(executionContext.getOrganizationId());
    accountDetailEntity.setAccountNum(accountSummary.getAccountNum());
    accountDetailEntity.setSeqno(accountSummary.getSeqno());

    AccountDetailEntity existingAccountDetailEntity = accountDetailRepository
        .findByBanksaladUserIdAndOrganizationIdAndAccountNumAndSeqno(executionContext.getBanksaladUserId(),
            executionContext.getOrganizationId(), accountSummary.getAccountNum(), accountSummary.getSeqno())
        .orElse((AccountDetailEntity.builder().build()));

    if (existingAccountDetailEntity.getId() != null) {
      accountDetailEntity.setId(existingAccountDetailEntity.getId());
    }

    if (!ObjectComparator.isSame(accountDetailEntity, existingAccountDetailEntity,
        "syncedAt", "createdAt", "createdBy", "updatedAt", "updatedBy")) {
      accountDetailRepository.save(accountDetailEntity);
      accountDetailHistoryRepository
          .save(accountDetailHistoryMapper.toAccountDetailHistoryEntityFrom(accountDetailEntity));
    }

    return accountDetailEntity;
  }

  private void updateSearchTimestamp(ExecutionContext executionContext, AccountSummary accountSummary,
      AccountDetailResponse accountDetailResponse) {
    AccountSummaryEntity accountSummaryEntity = accountSummaryRepository
        .findByBanksaladUserIdAndOrganizationIdAndAccountNumAndSeqno(
            executionContext.getBanksaladUserId(),
            executionContext.getOrganizationId(),
            accountSummary.getAccountNum(),
            accountSummary.getSeqno())
        .orElseThrow(EntityNotFoundException::new);

    accountSummaryEntity.setDetailSearchTimestamp(accountDetailResponse.getSearchTimestamp());
    accountSummaryRepository.save(accountSummaryEntity);
  }

  /**
   * 6.7.4 대출상품계좌 거래내역 조회
   * <p>[Changes in v202102-1]
   * - seqno: type changed from Integer to String
   * </p>
   *
   * @param executionContext text
   * @param organization     text
   * @param accountSummaries text
   * @return text
   */
  @Override
  public List<AccountTransaction> listAccountTransactions(ExecutionContext executionContext,
      Organization organization, List<AccountSummary> accountSummaries) {
    AtomicBoolean exceptionOccurred = new AtomicBoolean(false);

    // FIXME: Comment in the below creating a new context. Commented out for mock test.
//    final ExecutionContext context = executionContext.withExecutionRequestId(UUID.randomUUID().toString());
    final long banksaladUserId = executionContext.getBanksaladUserId();
    final String organizationId = organization.getOrganizationId();
    // Make a list of AccountTransaction.
    List<AccountTransaction> accountTransactions = accountSummaries.stream()
        .map(accountSummary -> CompletableFuture
            .supplyAsync(() -> processAccountTransaction(executionContext, organization, accountSummary),
                threadPoolTaskExecutor)
            .exceptionally(throwable -> {
              exceptionOccurred.set(true);
              log.error("Skip collection transactions of this account", throwable);
              return null;
            }))
        .map(CompletableFuture::join)
        .filter(Objects::nonNull)
        .flatMap(Stream::ofNullable)
        .flatMap(Collection::stream)
        .collect(Collectors.toList());
    // Save the start time on `user_sync_status` table in case of no error.

    return accountTransactions;
  }

  private List<AccountTransaction> processAccountTransaction(ExecutionContext executionContext,
      Organization organization, AccountSummary accountSummary) {

    // Frequently-used constants
    final LocalDateTime syncedAt = executionContext.getSyncStartedAt();
    final long banksaladUserId = executionContext.getBanksaladUserId();
    final String organizationId = executionContext.getOrganizationId();
    final String orgCode = organization.getOrganizationCode();
    final String accountNum = accountSummary.getAccountNum();
    final String seqno = accountSummary.getSeqno();

    // Call a Mydata API. Pay attention to use a KST date string made by a UTC datetime.
    AccountTransactionResponse accountTransactionResponse = externalApiService
        .getAccountTransactions(executionContext, orgCode, accountNum, seqno,
            // fromDate: Take the previous transaction_synced_at of account_summary and convert.
            DateUtil.utcLocalDateTimeToKstDateString(accountSummary.getTransactionSyncedAt()),
            // toDate: Take the syncStartAt of executionContext and convert.
            DateUtil.utcLocalDateTimeToKstDateString(executionContext.getSyncStartedAt()));
    List<AccountTransaction> accountTransactions = accountTransactionResponse.getTransList();
    if (accountTransactionResponse.getTransCnt() == 0 || accountTransactionResponse.getTransList() == null) {
      log.error("No transaction was returned");
      return accountTransactions;
    }
    // Iterate on the transaction list
    accountTransactions.forEach(accountTransaction -> {
          final Integer transactionYearMonth = Integer.valueOf(accountTransaction.getTransDtime().substring(0, 6));
          final String uniqueTransNo = HashUtil.hashCat(accountTransaction.getTransDtime(),
              accountTransaction.getTransNo(), accountTransaction.getBalanceAmt().toString());
          // Make an entity from account_transaction table.
          AccountTransactionEntity accountTransactionEntity = accountTransactionRepository
              .findByBanksaladUserIdAndOrganizationIdAndAccountNumAndSeqnoAndTransactionYearMonthAndUniqueTransNo(
                  banksaladUserId, organizationId, accountNum, seqno, transactionYearMonth, uniqueTransNo)
              .orElse(AccountTransactionEntity.builder()
                  .transactionYearMonth(transactionYearMonth)
                  .syncedAt(syncedAt)
                  .banksaladUserId(banksaladUserId)
                  .organizationId(organizationId)
                  .accountNum(accountNum)
                  .seqno(seqno)
                  .uniqueTransNo(uniqueTransNo)
                  .build()
              );
          AccountTransaction accountTransactionFromEntity = AccountTransaction.builder().build();
          accountTransactionMapper.merge(accountTransactionEntity, accountTransactionFromEntity);
          // Compare the API result with the DB record. We don't compare their interest lists though.
          // If both are equal or the API result is new
          if (!ObjectComparator
              .isSame(accountTransaction, accountTransactionFromEntity, "intCnt", "intList")) {
            // Update and save the DTO on account_transaction table.
            accountTransactionMapper.merge(accountTransaction, accountTransactionEntity);
            accountTransactionRepository.save(accountTransactionEntity);
            // Delete all of interest records.
            accountTransactionInterestRepository
                .deleteByBanksaladUserIdAndOrganizationIdAndAccountNumAndSeqnoAndTransactionYearMonthAndUniqueTransNo(
                    banksaladUserId, organizationId, accountNum, seqno, transactionYearMonth, uniqueTransNo
                );
            // Make a new interest entity list and save it.
            AtomicInteger counter = new AtomicInteger();
            List<AccountTransactionInterestEntity> accountTransactionInterestEntities =
                accountTransaction.getIntList().stream()
                    .map(accountTransactionInterest -> {
                      AccountTransactionInterestEntity accountTransactionInterestEntity =
                          accountTransactionInterestMapper
                              .toEntity(accountTransactionEntity, accountTransactionInterest);
                      accountTransactionInterestEntity.setIntNo(counter.incrementAndGet());
                      return accountTransactionInterestEntity;
                    })
                    .collect(Collectors.toList());
            accountTransactionInterestRepository.saveAll(accountTransactionInterestEntities);
            // Set transaction_synced_at with executionContext.syncStartedAt. To be the next from_date.
          }
        }
    );
    return accountTransactions;
  }
}
