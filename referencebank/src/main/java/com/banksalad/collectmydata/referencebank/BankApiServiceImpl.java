package com.banksalad.collectmydata.referencebank;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.enums.SyncRequestType;
import com.banksalad.collectmydata.common.exception.CollectException;
import com.banksalad.collectmydata.common.util.DateUtil;
import com.banksalad.collectmydata.referencebank.account.AccountService;
import com.banksalad.collectmydata.referencebank.account.dto.Account;
import com.banksalad.collectmydata.referencebank.common.dto.BankApiResponse;
import com.banksalad.collectmydata.referencebank.common.service.UserSyncStatusService;
import com.banksalad.collectmydata.referencebank.deposit.DepositAccountService;
import com.banksalad.collectmydata.referencebank.invest.InvestAccountService;
import com.banksalad.collectmydata.referencebank.loan.LoanAccountService;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Service
public class BankApiServiceImpl implements BankApiService {

  private final UserSyncStatusService userSyncStatusService;
  private final AccountService accountService;
  private final DepositAccountService depositAccountService;
  private final InvestAccountService investAccountService;
  private final LoanAccountService loanAccountService;

  public BankApiServiceImpl(
      UserSyncStatusService userSyncStatusService,
      AccountService accountService,
      DepositAccountService depositAccountService,
      InvestAccountService investAccountService,
      LoanAccountService loanAccountService
  ) {
    this.userSyncStatusService = userSyncStatusService;
    this.accountService = accountService;
    this.depositAccountService = depositAccountService;
    this.investAccountService = investAccountService;
    this.loanAccountService = loanAccountService;
  }

  @Override
  public BankApiResponse requestApi(long banksaladUserId, String organizationId, String syncRequestId,
      SyncRequestType syncRequestType)
      throws CollectException {

    ExecutionContext executionContext = ExecutionContext.builder()
        .banksaladUserId(banksaladUserId)
        .organizationId(organizationId)
        .accessToken("fixme")
        .organizationHost("http://localhost:9090")
        .executionRequestId(syncRequestId)
        .syncStartedAt(LocalDateTime.now(DateUtil.UTC_ZONE_ID))
        .build();

    List<Account> accounts = accountService.listAccounts(executionContext);

    AtomicReference<BankApiResponse> bankApiResponseAtomicReference = new AtomicReference<>();
    bankApiResponseAtomicReference.set(BankApiResponse.builder().build());

    CompletableFuture.allOf(
        CompletableFuture.supplyAsync(() -> depositAccountService.listDepositAccounts(executionContext, accounts))
            .thenAccept(depositAccounts -> bankApiResponseAtomicReference.get().setDepositAccounts(depositAccounts)),

        CompletableFuture.supplyAsync(() -> depositAccountService.listDepositAccountTransactions(executionContext, accounts))
            .thenAccept(depositAccountTransactions -> bankApiResponseAtomicReference.get()
                .setDepositAccountTransactions(depositAccountTransactions)),

        CompletableFuture.supplyAsync(() -> investAccountService.listInvestAccounts(executionContext, accounts))
            .thenAccept(investAccounts -> bankApiResponseAtomicReference.get().setInvestAccounts(investAccounts)),

        CompletableFuture.supplyAsync(() -> investAccountService.listInvestAccountTransactions(executionContext, accounts))
            .thenAccept(depositAccountTransactions -> bankApiResponseAtomicReference.get()
                .setInvestAccountTransactions(depositAccountTransactions)),

        CompletableFuture.supplyAsync(() -> loanAccountService.listLoanAccounts(executionContext, accounts))
            .thenAccept(loanAccounts -> bankApiResponseAtomicReference.get().setLoanAccounts(loanAccounts)),

        CompletableFuture.supplyAsync(() -> loanAccountService.listLoanAccountTransactions(executionContext, accounts))
            .thenAccept(loanAccountTransactions -> bankApiResponseAtomicReference.get()
                .setLoanAccountTransactions(loanAccountTransactions))
    ).join();

    return bankApiResponseAtomicReference.get();
  }
}
