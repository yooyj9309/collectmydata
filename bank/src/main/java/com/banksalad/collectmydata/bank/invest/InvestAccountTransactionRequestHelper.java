package com.banksalad.collectmydata.bank.invest;

import org.springframework.stereotype.Component;

import com.banksalad.collectmydata.bank.common.db.entity.AccountSummaryEntity;
import com.banksalad.collectmydata.bank.common.db.repository.AccountSummaryRepository;
import com.banksalad.collectmydata.bank.common.enums.BankAccountType;
import com.banksalad.collectmydata.bank.common.service.AccountSummaryService;
import com.banksalad.collectmydata.bank.invest.dto.ListInvestAccountTransactionsRequest;
import com.banksalad.collectmydata.bank.summary.dto.AccountSummary;
import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.util.DateUtil;
import com.banksalad.collectmydata.finance.api.transaction.TransactionRequestHelper;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class InvestAccountTransactionRequestHelper implements
    TransactionRequestHelper<AccountSummary, ListInvestAccountTransactionsRequest> {

  private static final int DEFAULT_LIMIT = 500;
  private static final int MINUS_YEAR = 5;

  private final AccountSummaryService accountSummaryService;
  private final AccountSummaryRepository accountSummaryRepository;

  @Override
  public List<AccountSummary> listSummaries(ExecutionContext executionContext) {
    return accountSummaryService.listSummariesConsented(
        executionContext.getBanksaladUserId(),
        executionContext.getOrganizationId(),
        BankAccountType.INVEST
    );
  }

  @Override
  public LocalDateTime getTransactionSyncedAt(ExecutionContext executionContext, AccountSummary accountSummary) {
    return accountSummaryRepository
        .findByBanksaladUserIdAndOrganizationIdAndAccountNumAndSeqno(
            executionContext.getBanksaladUserId(),
            executionContext.getOrganizationId(),
            accountSummary.getAccountNum(), accountSummary.getSeqno())
        .map(AccountSummaryEntity::getTransactionSyncedAt)
        .orElse(executionContext.getSyncStartedAt().minusYears(MINUS_YEAR));
  }

  @Override
  public ListInvestAccountTransactionsRequest make(ExecutionContext executionContext, AccountSummary accountSummary,
      LocalDate fromDate, LocalDate toDate, String nextPage) {
    return ListInvestAccountTransactionsRequest.builder()
        .orgCode(executionContext.getOrganizationCode())
        .accountNum(accountSummary.getAccountNum())
        .seqno(accountSummary.getSeqno())
        .fromDate(DateUtil.toDateString(fromDate))
        .toDate(DateUtil.toDateString(toDate))
        .nextPage(nextPage)
        .limit(DEFAULT_LIMIT)
        .build();
  }
}
