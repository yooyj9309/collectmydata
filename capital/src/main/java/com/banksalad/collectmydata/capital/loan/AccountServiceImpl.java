package com.banksalad.collectmydata.capital.loan;

import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import com.banksalad.collectmydata.capital.common.collect.Apis;
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
import com.banksalad.collectmydata.capital.loan.dto.AccountBasic;
import com.banksalad.collectmydata.capital.loan.dto.LoanAccount;
import com.banksalad.collectmydata.capital.loan.dto.LoanAccountTransaction;
import com.banksalad.collectmydata.capital.loan.dto.LoanAccountTransactionResponse;
import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.crypto.HashUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

  /**
   * on-demand 6.7.2 (대출상품계좌 기본정보 조회) 및 6.7.3(대출상품계좌 추가정보 조회) 두개를 조회하여 조합, 적재
   *
   * @param executionContext
   * @param organization
   * @param accountSummaries
   * @return List<AccountInfo>
   */
  @Override
  public List<LoanAccount> listLoanAccounts(ExecutionContext executionContext, Organization organization,
      List<AccountSummary> accountSummaries) {

    // 2번 3번 api 조합

    return null;
  }

  /**
   * 정기전송 시점에 6.7.2만 호출되는 경우. 업데이트가 있는경우 List<AccountInfo>에 매핑
   */
  @Override
  public List<AccountBasic> listLoanAccountBasics(ExecutionContext executionContext, Organization organization,
      List<AccountSummary> accountSummaries) {

    return null;
  }

  /**
   * 정기전송 시점에 6.7.3만 호출되는 경우. 업데이트가 있는경우 List<AccountInfo>에 매핑
   */
  @Override
  public List<LoanAccount> listLoanAccountDetails(ExecutionContext executionContext, Organization organization,
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
  public List<LoanAccountTransaction> listAccountTransactions(ExecutionContext executionContext,
      Organization organization, List<AccountSummary> accountSummaries) {
    // Request POST to a data provider.
    // Check if the API has no any error.
    // Save the start time on `user_sync_status` table in case of no error.
    AtomicReference<Boolean> isExceptionOccurred = new AtomicReference<>(false);
    List<LoanAccountTransaction> loanAccountTransactions = accountSummaries.stream()
        .map(account -> CompletableFuture
            .supplyAsync(() -> externalApiService.getAccountTransactions(executionContext, organization, account),
                threadPoolTaskExecutor)
            .exceptionally(e -> {
              // TODO: log something
              isExceptionOccurred.set(true);
              return null;
            }))
        .map(CompletableFuture::join)
        .map(LoanAccountTransactionResponse::getTransList)
        .flatMap(Stream::ofNullable)
        .flatMap(Collection::stream)
        .peek(loanAccountTransaction -> saveAccountTransaction(executionContext, organization,
            loanAccountTransaction))
        .collect(Collectors.toList());
    userSyncStatusService.updateUserSyncStatus(
        executionContext.getBanksaladUserId(),
        executionContext.getOrganizationId(),
        Apis.capital_get_account_transactions.getId(),
        executionContext.getSyncStartedAt(),
        null,
        executionResponseValidateService.isAllResponseResultSuccess(executionContext, isExceptionOccurred.get())
    );
    return loanAccountTransactions;
  }

  private void saveAccountTransaction(ExecutionContext executionContext, Organization organization,
      LoanAccountTransaction loanAccountTransaction) {
    final AccountTransactionMapper accountTransactionMapper = Mappers.getMapper(AccountTransactionMapper.class);
    final AccountTransactionInterestMapper accountTransactionInterestMapper = Mappers
        .getMapper(AccountTransactionInterestMapper.class);
    final Integer transactionYearMonth = Integer.valueOf(loanAccountTransaction.getTransDtime().substring(0, 6));
    final LocalDateTime syncedAt = executionContext.getSyncStartedAt();
    final Long bankSaladUserId = executionContext.getBanksaladUserId();
    final String organizationId = organization.getOrganizationId();
    final String accountNum = loanAccountTransaction.getAccountNum();
    final String seqno = loanAccountTransaction.getSeqno();
    final String uniqueTransNo = HashUtil.hashCat(Arrays.asList(loanAccountTransaction.getTransDtime(),
        loanAccountTransaction.getTransNo(), loanAccountTransaction.getBalanceAmt().toString()));

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

    accountTransactionMapper.updateEntityFromDto(loanAccountTransaction, accountTransactionEntity);
    accountTransactionRepository.save(accountTransactionEntity);
    accountTransactionInterestRepository
        .deleteByBanksaladUserIdAndOrganizationIdAndAccountNumAndSeqnoAndTransactionYearMonthAndUniqueTransNo(
            bankSaladUserId, organizationId, accountNum, seqno, transactionYearMonth, uniqueTransNo
        );
    AtomicInteger counter = new AtomicInteger();
    loanAccountTransaction.getIntList()
        .forEach(loanAccountTransactionInterest -> {
              AccountTransactionInterestEntity accountTransactionInterestEntity = AccountTransactionInterestEntity.builder()
                  .build();
              accountTransactionInterestMapper
                  .updateEntityFromDto(accountTransactionEntity, counter.incrementAndGet(), loanAccountTransactionInterest,
                      accountTransactionInterestEntity);
              accountTransactionInterestRepository.save(accountTransactionInterestEntity);
            }
        );
  }
}
