package com.banksalad.collectmydata.capital.account;

import com.banksalad.collectmydata.capital.account.dto.Account;
import com.banksalad.collectmydata.capital.account.dto.AccountBasic;
import com.banksalad.collectmydata.capital.account.dto.AccountBasicResponse;
import com.banksalad.collectmydata.capital.account.dto.AccountTransaction;
import com.banksalad.collectmydata.capital.account.dto.AccountTransactionResponse;
import com.banksalad.collectmydata.capital.common.collect.Apis;
import com.banksalad.collectmydata.capital.common.db.entity.AccountBasicEntity;
import com.banksalad.collectmydata.capital.common.db.entity.AccountSummaryEntity;
import com.banksalad.collectmydata.capital.common.db.entity.AccountTransactionEntity;
import com.banksalad.collectmydata.capital.common.db.entity.AccountTransactionInterestEntity;
import com.banksalad.collectmydata.capital.common.db.entity.mapper.AccountBasicHistoryMapper;
import com.banksalad.collectmydata.capital.common.db.entity.mapper.AccountBasicMapper;
import com.banksalad.collectmydata.capital.common.db.entity.mapper.AccountTransactionInterestMapper;
import com.banksalad.collectmydata.capital.common.db.entity.mapper.AccountTransactionMapper;
import com.banksalad.collectmydata.capital.common.db.repository.AccountBasicHistoryRepository;
import com.banksalad.collectmydata.capital.common.db.repository.AccountBasicRepository;
import com.banksalad.collectmydata.capital.common.db.repository.AccountSummaryRepository;
import com.banksalad.collectmydata.capital.common.db.repository.AccountTransactionInterestRepository;
import com.banksalad.collectmydata.capital.common.db.repository.AccountTransactionRepository;
import com.banksalad.collectmydata.capital.common.dto.AccountSummary;
import com.banksalad.collectmydata.capital.common.dto.Organization;
import com.banksalad.collectmydata.capital.common.service.ExecutionResponseValidateService;
import com.banksalad.collectmydata.capital.common.service.ExternalApiService;
import com.banksalad.collectmydata.capital.common.service.UserSyncStatusService;
import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.crypto.HashUtil;
import com.banksalad.collectmydata.common.util.ObjectComparator;

import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

  private final ExternalApiService externalApiService;
  private final UserSyncStatusService userSyncStatusService;
  private final ExecutionResponseValidateService executionResponseValidateService;
  private final AccountSummaryRepository accountSummaryRepository;
  private final AccountBasicRepository accountBasicRepository;
  private final AccountBasicHistoryRepository accountBasicHistoryRepository;
  private final AccountTransactionRepository accountTransactionRepository;
  private final AccountTransactionInterestRepository accountTransactionInterestRepository;
  private final ThreadPoolTaskExecutor threadPoolTaskExecutor;

  private final AccountBasicMapper accountBasicMapper = Mappers.getMapper(AccountBasicMapper.class);
  private final AccountBasicHistoryMapper accountBasicHistoryMapper = Mappers
      .getMapper(AccountBasicHistoryMapper.class);

  @Override
  public List<AccountBasic> listAccountBasics(ExecutionContext executionContext, Organization organization,
      List<AccountSummary> accountSummaries) {
    List<AccountBasic> accountBasics = new ArrayList<>();

    boolean isExceptionOccurred = FALSE;
    for (AccountSummary account : accountSummaries) {
      try {
        AccountBasicResponse response = externalApiService.getAccountBasic(executionContext, organization, account);
        AccountBasicEntity accountBasicEntity = saveAccountBasicWithHistory(executionContext, account, response);
        accountBasics.add(accountBasicMapper.toAccountBasicFrom(accountBasicEntity));
        updateSearchTimestamp(executionContext, account, response);
      } catch (Exception e) {
        isExceptionOccurred = TRUE;
        log.error("Failed to save account basic", e);
      }
    }

    userSyncStatusService.updateUserSyncStatus(
        executionContext.getBanksaladUserId(),
        executionContext.getOrganizationId(),
        Apis.capital_get_account_basic.getId(),
        executionContext.getSyncStartedAt(),
        null,
        executionResponseValidateService.isAllResponseResultSuccess(executionContext, isExceptionOccurred));

    return accountBasics;
  }

  private AccountBasicEntity saveAccountBasicWithHistory(ExecutionContext executionContext,
      AccountSummary accountSummary, AccountBasicResponse accountBasicResponse) {
    AccountBasicEntity accountBasicEntity = accountBasicMapper.toAccountBasicEntityFrom(accountBasicResponse);
    accountBasicEntity.setSyncedAt(executionContext.getSyncStartedAt());
    accountBasicEntity.setBanksaladUserId(executionContext.getBanksaladUserId());
    accountBasicEntity.setOrganizationId(executionContext.getOrganizationId());
    accountBasicEntity.setAccountNum(accountSummary.getAccountNum());
    accountBasicEntity.setSeqno(accountSummary.getSeqno());

    AccountBasicEntity existingAccountBasicEntity = accountBasicRepository
        .findByBanksaladUserIdAndOrganizationIdAndAccountNumAndSeqno(executionContext.getBanksaladUserId(),
            executionContext.getOrganizationId(), accountSummary.getAccountNum(), accountSummary.getSeqno());

    if (existingAccountBasicEntity != null) {
      accountBasicEntity.setId(existingAccountBasicEntity.getId());
    }

    if (!ObjectComparator
        .isSame(accountBasicEntity, existingAccountBasicEntity, "syncedAt", "createdAt", "createdBy", "updatedAt",
            "updatedBy")) {
      accountBasicRepository.save(accountBasicEntity);
      accountBasicHistoryRepository.save(accountBasicHistoryMapper.toAccountBasicHistoryEntityFrom(accountBasicEntity));
    }

    return accountBasicEntity;
  }

  private void updateSearchTimestamp(ExecutionContext executionContext, AccountSummary accountSummary,
      AccountBasicResponse accountBasicResponse) {
    AccountSummaryEntity accountSummaryEntity = accountSummaryRepository
        .findByBanksaladUserIdAndOrganizationIdAndAccountNumAndSeqno(
            executionContext.getBanksaladUserId(),
            executionContext.getOrganizationId(),
            accountSummary.getAccountNum(),
            accountSummary.getSeqno())
        .orElseThrow(EntityNotFoundException::new);

    accountSummaryEntity.setBasicSearchTimestamp(accountBasicResponse.getSearchTimestamp());
    accountSummaryRepository.save(accountSummaryEntity);
  }

  /**
   * 정기전송 시점에 6.7.3만 호출되는 경우. 업데이트가 있는경우 List<AccountInfo>에 매핑
   */
  @Override
  public List<Account> listAccountDetails(ExecutionContext executionContext, Organization organization,
      List<AccountSummary> accountSummaries) {
    return null;
  }

  /**
   * 6.7.4 대출상품계좌 거래내역 조회
   * <p>[Changes in v202102-1]
   * - seqno: type changed from Integer to String
   * </p>
   *
   * @param executionContext
   * @param organization
   * @param accountSummaries
   * @return
   */
  @Override
  public List<AccountTransaction> listAccountTransactions(ExecutionContext executionContext,
      Organization organization, List<AccountSummary> accountSummaries) {
    // Request POST to a data provider.
    // Check if the API has no any error.
    // Save the start time on `user_sync_status` table in case of no error.
    AtomicReference<Boolean> isExceptionOccurred = new AtomicReference<>(false);
    List<AccountTransaction> accountTransactions = accountSummaries.stream()
        .map(account -> CompletableFuture
            .supplyAsync(() -> externalApiService.getAccountTransactions(executionContext, organization, account),
                threadPoolTaskExecutor)
            .exceptionally(e -> {
              // TODO: log something
              isExceptionOccurred.set(true);
              return null;
            }))
        .map(CompletableFuture::join)
        .map(AccountTransactionResponse::getTransList)
        .flatMap(Stream::ofNullable)
        .flatMap(Collection::stream)
        .peek(accountTransaction -> saveAccountTransaction(executionContext, organization,
            accountTransaction))
        .collect(Collectors.toList());
    userSyncStatusService.updateUserSyncStatus(
        executionContext.getBanksaladUserId(),
        executionContext.getOrganizationId(),
        Apis.capital_get_account_transactions.getId(),
        executionContext.getSyncStartedAt(),
        null,
        executionResponseValidateService.isAllResponseResultSuccess(executionContext, isExceptionOccurred.get())
    );
    return accountTransactions;
  }

  private void saveAccountTransaction(ExecutionContext executionContext, Organization organization,
      AccountTransaction accountTransaction) {
    final AccountTransactionMapper accountTransactionMapper = Mappers.getMapper(AccountTransactionMapper.class);
    final AccountTransactionInterestMapper accountTransactionInterestMapper = Mappers
        .getMapper(AccountTransactionInterestMapper.class);
    final Integer transactionYearMonth = Integer.valueOf(accountTransaction.getTransDtime().substring(0, 6));
    final LocalDateTime syncedAt = executionContext.getSyncStartedAt();
    final Long bankSaladUserId = executionContext.getBanksaladUserId();
    final String organizationId = organization.getOrganizationId();
    final String accountNum = accountTransaction.getAccountNum();
    final String seqno = accountTransaction.getSeqno();
    final String uniqueTransNo = HashUtil.hashCat(Arrays.asList(accountTransaction.getTransDtime(),
        accountTransaction.getTransNo(), accountTransaction.getBalanceAmt().toString()));

    AccountTransactionEntity accountTransactionEntity = accountTransactionRepository
        .findByBanksaladUserIdAndOrganizationIdAndAccountNumAndSeqnoAndTransactionYearMonthAndUniqueTransNo(
            bankSaladUserId, organizationId, accountNum, seqno, transactionYearMonth, uniqueTransNo)
        .orElse(AccountTransactionEntity.builder()
            .transactionYearMonth(transactionYearMonth)
            .syncedAt(syncedAt)
            .banksaladUserId(bankSaladUserId)
            .organizationId(organizationId)
            .accountNum(accountNum)
            .seqno(seqno)
            .uniqueTransNo(uniqueTransNo)
            .build()
        );

    accountTransactionMapper.updateEntityFromDto(accountTransaction, accountTransactionEntity);
    accountTransactionRepository.save(accountTransactionEntity);
    accountTransactionInterestRepository
        .deleteByBanksaladUserIdAndOrganizationIdAndAccountNumAndSeqnoAndTransactionYearMonthAndUniqueTransNo(
            bankSaladUserId, organizationId, accountNum, seqno, transactionYearMonth, uniqueTransNo
        );
    AtomicInteger counter = new AtomicInteger();
    accountTransaction.getIntList()
        .forEach(accountTransactionInterest -> {
              AccountTransactionInterestEntity accountTransactionInterestEntity = AccountTransactionInterestEntity.builder()
                  .build();
              accountTransactionInterestMapper
                  .updateEntityFromDto(accountTransactionEntity, counter.incrementAndGet(), accountTransactionInterest,
                      accountTransactionInterestEntity);
              accountTransactionInterestRepository.save(accountTransactionInterestEntity);
            }
        );
  }
}
