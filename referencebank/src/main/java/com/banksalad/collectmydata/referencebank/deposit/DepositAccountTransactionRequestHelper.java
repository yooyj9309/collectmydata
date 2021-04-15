package com.banksalad.collectmydata.referencebank.deposit;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.util.DateUtil;
import com.banksalad.collectmydata.finance.api.transaction.TransactionRequestHelper;
import com.banksalad.collectmydata.referencebank.common.db.entity.AccountSummaryEntity;
import com.banksalad.collectmydata.referencebank.common.db.repository.AccountSummaryRepository;
import com.banksalad.collectmydata.referencebank.common.enums.BankAccountType;
import com.banksalad.collectmydata.referencebank.common.service.AccountSummaryService;
import com.banksalad.collectmydata.referencebank.deposit.dto.ListDepositAccountTransactionsRequest;
import com.banksalad.collectmydata.referencebank.summary.dto.AccountSummary;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DepositAccountTransactionRequestHelper implements
    TransactionRequestHelper<AccountSummary, ListDepositAccountTransactionsRequest> {

  private static final int DEFAULT_LIMIT = 500;
  private static final int MINUS_YEAR = 5; // TODO jayden-lee 최초 조회 시 5년으로 설정 할지 논의 필요 (동적으로 변경하도록 작성?)
  private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");

  private final AccountSummaryService accountSummaryService;
  private final AccountSummaryRepository accountSummaryRepository;

  @Override
  public List<AccountSummary> listSummaries(ExecutionContext executionContext) {

    return accountSummaryService
        .listSummariesConsented(executionContext.getBanksaladUserId(), executionContext.getOrganizationId(),
            BankAccountType.DEPOSIT);
  }

  @Override
  public LocalDateTime getTransactionSyncedAt(ExecutionContext executionContext, AccountSummary accountSummary) {
    return accountSummaryRepository
        .findByBanksaladUserIdAndOrganizationIdAndAccountNumAndSeqno(
            executionContext.getBanksaladUserId(),
            executionContext.getOrganizationId(),
            accountSummary.getAccountNum(), accountSummary.getSeqno())
        .map(AccountSummaryEntity::getTransactionSyncedAt)
        .orElseGet(() -> executionContext.getSyncStartedAt().minusYears(MINUS_YEAR));
  }

  @Override
  public ListDepositAccountTransactionsRequest make(ExecutionContext executionContext, AccountSummary accountSummary,
      LocalDate fromDate, LocalDate toDate, String nextPage) {

    return ListDepositAccountTransactionsRequest.builder()
        .orgCode(executionContext.getOrganizationCode())
        .accountNum(accountSummary.getAccountNum())
        .seqno(accountSummary.getSeqno())
        .fromDate(dateFormatter.format(fromDate))
        .toDate(dateFormatter.format(toDate))
        .nextPage(nextPage)
        .limit(DEFAULT_LIMIT)
        .build();
  }
}
