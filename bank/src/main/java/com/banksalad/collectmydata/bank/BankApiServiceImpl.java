package com.banksalad.collectmydata.bank;

import com.banksalad.collectmydata.bank.common.collect.Executions;
import com.banksalad.collectmydata.bank.deposit.DepositAccountBasicPublishmentHelper;
import com.banksalad.collectmydata.bank.deposit.DepositAccountDetailPublishmentHelper;
import com.banksalad.collectmydata.bank.deposit.DepositAccountTransactionPublishmentHelper;
import com.banksalad.collectmydata.bank.deposit.dto.DepositAccountBasic;
import com.banksalad.collectmydata.bank.deposit.dto.DepositAccountDetail;
import com.banksalad.collectmydata.bank.deposit.dto.DepositAccountTransaction;
import com.banksalad.collectmydata.bank.deposit.dto.GetDepositAccountBasicRequest;
import com.banksalad.collectmydata.bank.deposit.dto.GetDepositAccountDetailRequest;
import com.banksalad.collectmydata.bank.deposit.dto.ListDepositAccountTransactionsRequest;
import com.banksalad.collectmydata.bank.grpc.client.ConnectClientService;
import com.banksalad.collectmydata.bank.invest.InvestAccountBasicPublishmentHelper;
import com.banksalad.collectmydata.bank.invest.InvestAccountDetailPublishmentHelper;
import com.banksalad.collectmydata.bank.invest.InvestAccountTransactionPublishmentHelper;
import com.banksalad.collectmydata.bank.invest.dto.GetInvestAccountBasicRequest;
import com.banksalad.collectmydata.bank.invest.dto.GetInvestAccountDetailRequest;
import com.banksalad.collectmydata.bank.invest.dto.InvestAccountBasic;
import com.banksalad.collectmydata.bank.invest.dto.InvestAccountDetail;
import com.banksalad.collectmydata.bank.invest.dto.InvestAccountTransaction;
import com.banksalad.collectmydata.bank.invest.dto.ListInvestAccountTransactionsRequest;
import com.banksalad.collectmydata.bank.loan.LoanAccountBasicPublishmentHelper;
import com.banksalad.collectmydata.bank.loan.LoanAccountDetailPublishmentHelper;
import com.banksalad.collectmydata.bank.loan.LoanAccountTransactionPublishmentHelper;
import com.banksalad.collectmydata.bank.loan.dto.GetLoanAccountBasicRequest;
import com.banksalad.collectmydata.bank.loan.dto.GetLoanAccountDetailRequest;
import com.banksalad.collectmydata.bank.loan.dto.ListLoanAccountTransactionsRequest;
import com.banksalad.collectmydata.bank.loan.dto.LoanAccountBasic;
import com.banksalad.collectmydata.bank.loan.dto.LoanAccountDetail;
import com.banksalad.collectmydata.bank.loan.dto.LoanAccountTransaction;
import com.banksalad.collectmydata.bank.summary.BankAccountSummaryPublishmentHelper;
import com.banksalad.collectmydata.bank.summary.dto.AccountSummary;
import com.banksalad.collectmydata.bank.summary.dto.ListAccountSummariesRequest;
import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.enums.SyncRequestType;
import com.banksalad.collectmydata.common.message.MessageTopic;
import com.banksalad.collectmydata.common.message.SyncCompletedMessage;
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
import com.banksalad.collectmydata.finance.common.service.FinanceMessageService;

import org.springframework.stereotype.Service;

import com.github.banksalad.idl.apis.v1.connectmydata.ConnectmydataProto.GetAccessTokenResponse;
import com.github.banksalad.idl.apis.v1.connectmydata.ConnectmydataProto.GetOrganizationResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class BankApiServiceImpl implements BankApiService {

  private final FinanceMessageService financeMessageService;
  private final ConnectClientService connectClientService;

  // SUMMARY
  private final SummaryService<ListAccountSummariesRequest, AccountSummary> accountSummaryService;
  private final SummaryRequestHelper<ListAccountSummariesRequest> summaryRequestHelper;
  private final SummaryResponseHelper<AccountSummary> summaryResponseHelper;
  private final BankAccountSummaryPublishmentHelper bankAccountSummaryPublishmentHelper;

  // DEPOSIT
  private final AccountInfoService<AccountSummary, GetDepositAccountBasicRequest, DepositAccountBasic> depositAccountBasicApiService;
  private final AccountInfoService<AccountSummary, GetDepositAccountDetailRequest, List<DepositAccountDetail>> depositAccountDetailApiService;
  private final TransactionApiService<AccountSummary, ListDepositAccountTransactionsRequest, DepositAccountTransaction> depositTransactionApiService;

  private final AccountInfoRequestHelper<GetDepositAccountBasicRequest, AccountSummary> depositAccountBasicInfoRequestHelper;
  private final AccountInfoResponseHelper<AccountSummary, DepositAccountBasic> depositAccountBasicInfoResponseHelper;
  private final DepositAccountBasicPublishmentHelper depositAccountBasicPublishmentHelper;

  private final AccountInfoRequestHelper<GetDepositAccountDetailRequest, AccountSummary> depositAccountDetailInfoRequestHelper;
  private final AccountInfoResponseHelper<AccountSummary, List<DepositAccountDetail>> depositAccountDetailInfoResponseHelper;
  private final DepositAccountDetailPublishmentHelper depositAccountDetailPublishmentHelper;

  private final TransactionRequestHelper<AccountSummary, ListDepositAccountTransactionsRequest> depositAccountTransactionRequestHelper;
  private final TransactionResponseHelper<AccountSummary, DepositAccountTransaction> depositAccountTransactionResponseHelper;
  private final DepositAccountTransactionPublishmentHelper depositAccountTransactionPublishmentHelper;


  // INVEST
  private final AccountInfoService<AccountSummary, GetInvestAccountBasicRequest, InvestAccountBasic> investAccountBasicApiService;
  private final AccountInfoService<AccountSummary, GetInvestAccountDetailRequest, InvestAccountDetail> investAccountDetailApiService;
  private final TransactionApiService<AccountSummary, ListInvestAccountTransactionsRequest, InvestAccountTransaction> investTransactionApiService;

  private final AccountInfoRequestHelper<GetInvestAccountBasicRequest, AccountSummary> investAccountBasicInfoRequestHelper;
  private final AccountInfoResponseHelper<AccountSummary, InvestAccountBasic> investAccountInfoBasicResponseHelper;
  private final InvestAccountBasicPublishmentHelper investAccountBasicPublishmentHelper;

  private final AccountInfoRequestHelper<GetInvestAccountDetailRequest, AccountSummary> investAccountDetailInfoRequestHelper;
  private final AccountInfoResponseHelper<AccountSummary, InvestAccountDetail> investAccountDetailInfoResponseHelper;
  private final InvestAccountDetailPublishmentHelper investAccountDetailPublishmentHelper;

  private final TransactionRequestHelper<AccountSummary, ListInvestAccountTransactionsRequest> investAccountTransactionRequestHelper;
  private final TransactionResponseHelper<AccountSummary, InvestAccountTransaction> investAccountTransactionResponseHelper;
  private final InvestAccountTransactionPublishmentHelper investAccountTransactionPublishmentHelper;

  // LOAN
  private final AccountInfoService<AccountSummary, GetLoanAccountBasicRequest, LoanAccountBasic> loanAccountBasicApiService;
  private final AccountInfoService<AccountSummary, GetLoanAccountDetailRequest, LoanAccountDetail> loanAccountDetailApiService;
  private final TransactionApiService<AccountSummary, ListLoanAccountTransactionsRequest, LoanAccountTransaction> loanTransactionApiService;

  private final AccountInfoRequestHelper<GetLoanAccountBasicRequest, AccountSummary> loanAccountBasicInfoRequestHelper;
  private final AccountInfoResponseHelper<AccountSummary, LoanAccountBasic> loanAccountInfoBasicResponseHelper;
  private final LoanAccountBasicPublishmentHelper loanAccountBasicPublishmentHelper;

  private final AccountInfoRequestHelper<GetLoanAccountDetailRequest, AccountSummary> loanAccountDetailInfoRequestHelper;
  private final AccountInfoResponseHelper<AccountSummary, LoanAccountDetail> loanAccountDetailInfoResponseHelper;
  private final LoanAccountDetailPublishmentHelper loanAccountDetailPublishmentHelper;

  private final TransactionRequestHelper<AccountSummary, ListLoanAccountTransactionsRequest> loanAccountTransactionRequestHelper;
  private final TransactionResponseHelper<AccountSummary, LoanAccountTransaction> loanAccountTransactionResponseHelper;
  private final LoanAccountTransactionPublishmentHelper loanAccountTransactionPublishmentHelper;

  @Override
  public void requestApi(long banksaladUserId, String organizationId, String syncRequestId,
      SyncRequestType syncRequestType) throws ResponseNotOkException {

    GetOrganizationResponse organization = connectClientService.getOrganizationByOrganizationId(organizationId);
    GetAccessTokenResponse accessToken = connectClientService
        .getAccessToken(String.valueOf(banksaladUserId), organizationId);

    ExecutionContext executionContext = ExecutionContext.builder()
        .consentId(accessToken.getConsentId())
        .syncRequestId(syncRequestId)
        .executionRequestId(UUID.randomUUID().toString())
        .banksaladUserId(banksaladUserId)
        .organizationId(organizationId)
        .organizationCode(organization.getOrganizationCode())
        .organizationHost(organization.getDomain())
        .accessToken(accessToken.getAccessToken())
        .syncStartedAt(LocalDateTime.now(DateUtil.UTC_ZONE_ID))
        .build();

    accountSummaryService.listAccountSummaries(executionContext, Executions.finance_bank_summaries,
        summaryRequestHelper, summaryResponseHelper, bankAccountSummaryPublishmentHelper);

    CompletableFuture.allOf(
        // Deposit
        CompletableFuture.runAsync(() ->
            depositAccountBasicApiService
                .listAccountInfos(executionContext, Executions.finance_bank_deposit_account_basic,
                    depositAccountBasicInfoRequestHelper, depositAccountBasicInfoResponseHelper,
                    depositAccountBasicPublishmentHelper)),

        CompletableFuture.runAsync(() ->
            depositAccountDetailApiService
                .listAccountInfos(executionContext, Executions.finance_bank_deposit_account_detail,
                    depositAccountDetailInfoRequestHelper, depositAccountDetailInfoResponseHelper,
                    depositAccountDetailPublishmentHelper)),

        CompletableFuture.runAsync(() ->
            depositTransactionApiService
                .listTransactions(executionContext, Executions.finance_bank_deposit_account_transaction,
                    depositAccountTransactionRequestHelper, depositAccountTransactionResponseHelper,
                    depositAccountTransactionPublishmentHelper)),

        // Invest
        CompletableFuture.runAsync(() ->
            investAccountBasicApiService
                .listAccountInfos(executionContext, Executions.finance_bank_invest_account_basic,
                    investAccountBasicInfoRequestHelper, investAccountInfoBasicResponseHelper,
                    investAccountBasicPublishmentHelper)),

        CompletableFuture.runAsync(() ->
            investAccountDetailApiService
                .listAccountInfos(executionContext, Executions.finance_bank_invest_account_detail,
                    investAccountDetailInfoRequestHelper, investAccountDetailInfoResponseHelper,
                    investAccountDetailPublishmentHelper)),

        CompletableFuture.runAsync(() ->
            investTransactionApiService
                .listTransactions(executionContext, Executions.finance_bank_invest_account_transaction,
                    investAccountTransactionRequestHelper, investAccountTransactionResponseHelper,
                    investAccountTransactionPublishmentHelper)),

        // Loan
        CompletableFuture.runAsync(() ->
            loanAccountBasicApiService
                .listAccountInfos(executionContext, Executions.finance_bank_loan_account_basic,
                    loanAccountBasicInfoRequestHelper, loanAccountInfoBasicResponseHelper,
                    loanAccountBasicPublishmentHelper)),

        CompletableFuture.runAsync(() ->
            loanAccountDetailApiService.listAccountInfos(executionContext, Executions.finance_bank_loan_account_detail,
                loanAccountDetailInfoRequestHelper, loanAccountDetailInfoResponseHelper,
                loanAccountDetailPublishmentHelper)),

        CompletableFuture.runAsync(() ->
            loanTransactionApiService
                .listTransactions(executionContext, Executions.finance_bank_loan_account_transaction,
                    loanAccountTransactionRequestHelper, loanAccountTransactionResponseHelper,
                    loanAccountTransactionPublishmentHelper))
    ).join();

    // TODO : irp module

    /* produce sync completed */
    financeMessageService.produceSyncCompleted(
        MessageTopic.bankSyncCompleted,
        SyncCompletedMessage.builder()
            .banksaladUserId(executionContext.getBanksaladUserId())
            .organizationId(executionContext.getOrganizationId())
            .syncRequestId(executionContext.getSyncRequestId())
            .syncRequestType(syncRequestType)
            .build());
  }
}
