package com.banksalad.collectmydata.bank;

import org.springframework.stereotype.Service;

import com.banksalad.collectmydata.bank.common.collect.Executions;
import com.banksalad.collectmydata.bank.common.dto.BankApiResponse;
import com.banksalad.collectmydata.bank.deposit.dto.DepositAccountBasic;
import com.banksalad.collectmydata.bank.deposit.dto.DepositAccountDetail;
import com.banksalad.collectmydata.bank.deposit.dto.DepositAccountTransaction;
import com.banksalad.collectmydata.bank.deposit.dto.GetDepositAccountBasicRequest;
import com.banksalad.collectmydata.bank.deposit.dto.GetDepositAccountDetailRequest;
import com.banksalad.collectmydata.bank.deposit.dto.ListDepositAccountTransactionsRequest;
import com.banksalad.collectmydata.bank.invest.dto.GetInvestAccountBasicRequest;
import com.banksalad.collectmydata.bank.invest.dto.GetInvestAccountDetailRequest;
import com.banksalad.collectmydata.bank.invest.dto.InvestAccountBasic;
import com.banksalad.collectmydata.bank.invest.dto.InvestAccountDetail;
import com.banksalad.collectmydata.bank.invest.dto.InvestAccountTransaction;
import com.banksalad.collectmydata.bank.invest.dto.ListInvestAccountTransactionsRequest;
import com.banksalad.collectmydata.bank.loan.dto.GetLoanAccountBasicRequest;
import com.banksalad.collectmydata.bank.loan.dto.GetLoanAccountDetailRequest;
import com.banksalad.collectmydata.bank.loan.dto.ListLoanAccountTransactionsRequest;
import com.banksalad.collectmydata.bank.loan.dto.LoanAccountBasic;
import com.banksalad.collectmydata.bank.loan.dto.LoanAccountDetail;
import com.banksalad.collectmydata.bank.loan.dto.LoanAccountTransaction;
import com.banksalad.collectmydata.bank.summary.dto.AccountSummary;
import com.banksalad.collectmydata.bank.summary.dto.ListAccountSummariesRequest;
import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.enums.SyncRequestType;
import com.banksalad.collectmydata.common.util.DateUtil;
import com.banksalad.collectmydata.finance.api.accountinfo.AccountInfoRequestHelper;
import com.banksalad.collectmydata.finance.api.accountinfo.AccountInfoResponseHelper;
import com.banksalad.collectmydata.finance.api.accountinfo.AccountInfoService;
import com.banksalad.collectmydata.finance.api.summary.SummaryRequestHelper;
import com.banksalad.collectmydata.finance.api.summary.SummaryResponseHelper;
import com.banksalad.collectmydata.finance.api.summary.SummaryService;
import com.banksalad.collectmydata.finance.api.transaction.TransactionApiService;
import com.banksalad.collectmydata.finance.api.transaction.TransactionRequestHelper;
import com.banksalad.collectmydata.finance.api.transaction.TransactionResponseHelper;
import com.banksalad.collectmydata.finance.common.exception.ResponseNotOkException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Service
@RequiredArgsConstructor
public class BankApiServiceImpl implements BankApiService {

  private final SummaryService<ListAccountSummariesRequest, AccountSummary> accountSummaryService;
  private final SummaryRequestHelper<ListAccountSummariesRequest> summaryRequestHelper;
  private final SummaryResponseHelper<AccountSummary> summaryResponseHelper;

  // DEPOSIT
  private final AccountInfoService<AccountSummary, GetDepositAccountBasicRequest, DepositAccountBasic> depositAccountBasicApiService;
  private final AccountInfoService<AccountSummary, GetDepositAccountDetailRequest, List<DepositAccountDetail>> depositAccountDetailApiService;
  private final TransactionApiService<AccountSummary, ListDepositAccountTransactionsRequest, DepositAccountTransaction> depositTransactionApiService;

  private final AccountInfoRequestHelper<GetDepositAccountBasicRequest, AccountSummary> depositAccountBasicInfoRequestHelper;
  private final AccountInfoResponseHelper<AccountSummary, DepositAccountBasic> depositAccountBasicInfoResponseHelper;

  private final AccountInfoRequestHelper<GetDepositAccountDetailRequest, AccountSummary> depositAccountDetailInfoRequestHelper;
  private final AccountInfoResponseHelper<AccountSummary, List<DepositAccountDetail>> depositAccountDetailInfoResponseHelper;

  private final TransactionRequestHelper<AccountSummary, ListDepositAccountTransactionsRequest> depositAccountTransactionRequestHelper;
  private final TransactionResponseHelper<AccountSummary, DepositAccountTransaction> depositAccountTransactionResponseHelper;

  // INVEST
  private final AccountInfoService<AccountSummary, GetInvestAccountBasicRequest, InvestAccountBasic> investAccountBasicApiService;
  private final AccountInfoService<AccountSummary, GetInvestAccountDetailRequest, InvestAccountDetail> investAccountDetailApiService;
  private final TransactionApiService<AccountSummary, ListInvestAccountTransactionsRequest, InvestAccountTransaction> investTransactionApiService;

  private final AccountInfoRequestHelper<GetInvestAccountBasicRequest, AccountSummary> investAccountBasicInfoRequestHelper;
  private final AccountInfoResponseHelper<AccountSummary, InvestAccountBasic> investAccountInfoBasicResponseHelper;

  private final AccountInfoRequestHelper<GetInvestAccountDetailRequest, AccountSummary> investAccountDetailInfoRequestHelper;
  private final AccountInfoResponseHelper<AccountSummary, InvestAccountDetail> investAccountDetailInfoResponseHelper;

  private final TransactionRequestHelper<AccountSummary, ListInvestAccountTransactionsRequest> investAccountTransactionRequestHelper;
  private final TransactionResponseHelper<AccountSummary, InvestAccountTransaction> investAccountTransactionResponseHelper;

  // LOAN
  private final AccountInfoService<AccountSummary, GetLoanAccountBasicRequest, LoanAccountBasic> loanAccountBasicApiService;
  private final AccountInfoService<AccountSummary, GetLoanAccountDetailRequest, LoanAccountDetail> loanAccountDetailApiService;
  private final TransactionApiService<AccountSummary, ListLoanAccountTransactionsRequest, LoanAccountTransaction> loanTransactionApiService;

  private final AccountInfoRequestHelper<GetLoanAccountBasicRequest, AccountSummary> loanAccountBasicInfoRequestHelper;
  private final AccountInfoResponseHelper<AccountSummary, LoanAccountBasic> loanAccountInfoBasicResponseHelper;

  private final AccountInfoRequestHelper<GetLoanAccountDetailRequest, AccountSummary> loanAccountDetailInfoRequestHelper;
  private final AccountInfoResponseHelper<AccountSummary, LoanAccountDetail> loanAccountDetailInfoResponseHelper;

  private final TransactionRequestHelper<AccountSummary, ListLoanAccountTransactionsRequest> loanAccountTransactionRequestHelper;
  private final TransactionResponseHelper<AccountSummary, LoanAccountTransaction> loanAccountTransactionResponseHelper;

  @Override
  public BankApiResponse requestApi(long banksaladUserId, String organizationId, String syncRequestId,
      SyncRequestType syncRequestType) throws ResponseNotOkException {

    // TODO jayden-lee organization service 호출 해서 Organization 정보 가져 오기

    ExecutionContext executionContext = ExecutionContext.builder()
        .banksaladUserId(banksaladUserId)
        .executionRequestId(UUID.randomUUID().toString())
        .organizationId(organizationId)
        .accessToken("fixme")
        .organizationHost("http://whatever")
        .syncStartedAt(LocalDateTime.now(DateUtil.UTC_ZONE_ID))
        .build();

    accountSummaryService.listAccountSummaries(executionContext, Executions.finance_bank_summaries,
        summaryRequestHelper, summaryResponseHelper);

    AtomicReference<BankApiResponse> bankApiResponseAtomicReference = new AtomicReference<>();
    bankApiResponseAtomicReference.set(BankApiResponse.builder().build());

    CompletableFuture.allOf(
        // Deposit
        CompletableFuture.supplyAsync(() -> depositAccountBasicApiService.listAccountInfos(
            executionContext, Executions.finance_bank_deposit_account_basic, depositAccountBasicInfoRequestHelper,
            depositAccountBasicInfoResponseHelper)),

        CompletableFuture.supplyAsync(() -> depositAccountDetailApiService.listAccountInfos(
            executionContext, Executions.finance_bank_deposit_account_detail, depositAccountDetailInfoRequestHelper,
            depositAccountDetailInfoResponseHelper)),

        CompletableFuture.supplyAsync(() -> depositTransactionApiService.listTransactions(executionContext,
            Executions.finance_bank_deposit_account_transaction, depositAccountTransactionRequestHelper,
            depositAccountTransactionResponseHelper)),

        // Invest
        CompletableFuture.supplyAsync(() -> investAccountBasicApiService.listAccountInfos(
            executionContext, Executions.finance_bank_invest_account_basic, investAccountBasicInfoRequestHelper,
            investAccountInfoBasicResponseHelper)),

        CompletableFuture.supplyAsync(() -> investAccountDetailApiService.listAccountInfos(
            executionContext, Executions.finance_bank_invest_account_detail, investAccountDetailInfoRequestHelper,
            investAccountDetailInfoResponseHelper)),

        CompletableFuture.supplyAsync(
            () -> investTransactionApiService.listTransactions(
                executionContext, Executions.finance_bank_invest_account_transaction,
                investAccountTransactionRequestHelper, investAccountTransactionResponseHelper))
            .thenAccept(investAccountTransactions -> bankApiResponseAtomicReference.get()
                .setInvestAccountTransactions(investAccountTransactions)),

        // Loan
        CompletableFuture.supplyAsync(() -> loanAccountBasicApiService.listAccountInfos(
            executionContext, Executions.finance_bank_loan_account_basic, loanAccountBasicInfoRequestHelper,
            loanAccountInfoBasicResponseHelper)),

        CompletableFuture.supplyAsync(() -> loanAccountDetailApiService.listAccountInfos(
            executionContext, Executions.finance_bank_loan_account_detail, loanAccountDetailInfoRequestHelper,
            loanAccountDetailInfoResponseHelper)),

        CompletableFuture.supplyAsync(
            () -> loanTransactionApiService.listTransactions(
                executionContext, Executions.finance_bank_loan_account_transaction,
                loanAccountTransactionRequestHelper, loanAccountTransactionResponseHelper))
            .thenAccept(loanAccountTransactions -> bankApiResponseAtomicReference.get()
                .setLoanAccountTransactions(loanAccountTransactions))
    ).join();

    return bankApiResponseAtomicReference.get();
  }
}
