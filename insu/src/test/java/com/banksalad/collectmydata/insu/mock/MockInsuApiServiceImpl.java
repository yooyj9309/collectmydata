package com.banksalad.collectmydata.insu.mock;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
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
import com.banksalad.collectmydata.finance.common.dto.OauthToken;
import com.banksalad.collectmydata.finance.common.dto.Organization;
import com.banksalad.collectmydata.finance.common.exception.ResponseNotOkException;
import com.banksalad.collectmydata.finance.common.grpc.CollectmydataConnectClientService;
import com.banksalad.collectmydata.insu.collect.Executions;
import com.banksalad.collectmydata.insu.loan.dto.GetLoanBasicRequest;
import com.banksalad.collectmydata.insu.loan.dto.GetLoanDetailRequest;
import com.banksalad.collectmydata.insu.loan.dto.ListLoanTransactionRequest;
import com.banksalad.collectmydata.insu.loan.dto.LoanBasic;
import com.banksalad.collectmydata.insu.loan.dto.LoanDetail;
import com.banksalad.collectmydata.insu.loan.dto.LoanTransaction;
import com.banksalad.collectmydata.insu.summary.dto.ListLoanSummariesRequest;
import com.banksalad.collectmydata.insu.summary.dto.LoanSummary;
import com.banksalad.collectmydata.irp.account.IrpAccountService;
import com.banksalad.collectmydata.irp.account.IrpAccountTransactionService;
import com.banksalad.collectmydata.irp.summary.IrpAccountSummaryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

import static com.banksalad.collectmydata.finance.common.constant.FinanceConstant.REQUESTED_BY_SCHEDULE;

@Slf4j
@Service
@Profile("test")
@RequiredArgsConstructor
public class MockInsuApiServiceImpl /*implements InsuApiService*/ {

  private final SummaryService<ListLoanSummariesRequest, LoanSummary> loanSummaryService;
  private final SummaryRequestHelper<ListLoanSummariesRequest> loanSummaryRequestHelper;
  private final SummaryResponseHelper<LoanSummary> loanSummaryResponseHelper;

  // LOAN
  private final AccountInfoService<LoanSummary, GetLoanBasicRequest, LoanBasic> loanBasicService;
  private final AccountInfoRequestHelper<GetLoanBasicRequest, LoanSummary> loanBasicRequestHelper;
  private final AccountInfoResponseHelper<LoanSummary, LoanBasic> loanBasicResponseHelper;

  private final AccountInfoService<LoanSummary, GetLoanDetailRequest, LoanDetail> loanDetailService;
  private final AccountInfoRequestHelper<GetLoanDetailRequest, LoanSummary> loanDetailRequestHelper;
  private final AccountInfoResponseHelper<LoanSummary, LoanDetail> loanDetailResponseHelper;

  private final TransactionApiService<LoanSummary, ListLoanTransactionRequest, LoanTransaction> loanTransactionService;
  private final TransactionRequestHelper<LoanSummary, ListLoanTransactionRequest> loanTransactionRequestHelper;
  private final TransactionResponseHelper<LoanSummary, LoanTransaction> loanTransactionResponseHelper;

  // ETC
  private final CollectmydataConnectClientService collectmydataConnectClientService;

  // IRP
  private final IrpAccountSummaryService irpAccountSummaryService;
  private final IrpAccountService irpAccountService;
  private final IrpAccountTransactionService irpAccountTransactionService;

  //  @Override
  public void onDemandRequestApi(long banksaladUserId, String organizationId, String syncRequestId)
      throws ResponseNotOkException {

    Organization organization = collectmydataConnectClientService.getOrganization(organizationId);
    OauthToken accessToken = collectmydataConnectClientService.getAccessToken(banksaladUserId, organizationId);
//    String accessToken = "fixme"; //TODO 토큰 조회 로직 추가하여 적용

    ExecutionContext executionContext = generateExecutionContext(banksaladUserId, organizationId, syncRequestId,
        accessToken, organization, String.valueOf(banksaladUserId));

    loanSummaryService.listAccountSummaries(
        executionContext, Executions.insurance_get_loan_summaries, loanSummaryRequestHelper, loanSummaryResponseHelper);

    // IRP Account Summary
    irpAccountSummaryService.listAccountSummaries(executionContext);

    CompletableFuture.allOf(

        CompletableFuture
            .runAsync(
                () -> loanBasicService
                    .listAccountInfos(executionContext, Executions.insurance_get_loan_basic, loanBasicRequestHelper,
                        loanBasicResponseHelper)),

        CompletableFuture
            .runAsync(
                () -> loanDetailService
                    .listAccountInfos(executionContext, Executions.insurance_get_loan_detail, loanDetailRequestHelper,
                        loanDetailResponseHelper)),

        CompletableFuture
            .runAsync(
                () -> loanTransactionService
                    .listTransactions(executionContext, Executions.insurance_get_loan_transactions,
                        loanTransactionRequestHelper,
                        loanTransactionResponseHelper)),

        // IRP Account Basic, Detail, Transaction
        CompletableFuture.runAsync(() -> irpAccountService.listIrpAccountBasics(executionContext)),
        CompletableFuture.runAsync(() -> irpAccountService.listIrpAccountDetails(executionContext)),
        CompletableFuture.runAsync(() -> irpAccountTransactionService.listTransactions(executionContext))
    ).join();
  }

  //  @Override
  public void scheduledBasicRequestApi(long banksaladUserId, String organizationId, String syncRequestId)
      throws ResponseNotOkException {

    Organization organization = collectmydataConnectClientService.getOrganization(organizationId);
    OauthToken accessToken = collectmydataConnectClientService.getAccessToken(banksaladUserId, organizationId);
//    String accessToken = "fixme"; //TODO 토큰 조회 로직 추가하여 적용

    ExecutionContext executionContext = generateExecutionContext(banksaladUserId, organizationId, syncRequestId,
        accessToken, organization, REQUESTED_BY_SCHEDULE);

    loanSummaryService.listAccountSummaries(
        executionContext, Executions.insurance_get_loan_summaries, loanSummaryRequestHelper, loanSummaryResponseHelper);

    // IRP Account Summary
    irpAccountSummaryService.listAccountSummaries(executionContext);

    CompletableFuture.allOf(

        CompletableFuture
            .runAsync(
                () -> loanBasicService
                    .listAccountInfos(executionContext, Executions.insurance_get_loan_basic, loanBasicRequestHelper,
                        loanBasicResponseHelper)),

        // IRP Account Summary, Basic(정기적 전송주기 : 기본)
        CompletableFuture.runAsync(() -> irpAccountService.listIrpAccountBasics(executionContext))
    ).join();
  }

  //  @Override
  public void scheduledAdditionalRequestApi(long banksaladUserId, String organizationId,
      String syncRequestId) throws ResponseNotOkException {

    Organization organization = collectmydataConnectClientService.getOrganization(organizationId);
    OauthToken accessToken = collectmydataConnectClientService.getAccessToken(banksaladUserId, organizationId);
//    String accessToken = "fixme"; //TODO 토큰 조회 로직 추가하여 적용

    ExecutionContext executionContext = generateExecutionContext(banksaladUserId, organizationId, syncRequestId,
        accessToken, organization, REQUESTED_BY_SCHEDULE);

    loanSummaryService.listAccountSummaries(
        executionContext, Executions.insurance_get_loan_summaries, loanSummaryRequestHelper, loanSummaryResponseHelper);

    CompletableFuture.allOf(

        CompletableFuture
            .runAsync(
                () -> loanDetailService
                    .listAccountInfos(executionContext, Executions.insurance_get_loan_detail, loanDetailRequestHelper,
                        loanDetailResponseHelper)),

        CompletableFuture
            .runAsync(
                () -> loanTransactionService
                    .listTransactions(executionContext, Executions.insurance_get_loan_transactions,
                        loanTransactionRequestHelper,
                        loanTransactionResponseHelper)),

        // IRP Account Detail, Transaction(정기적 전송주기 : 추가)
        CompletableFuture.runAsync(() -> irpAccountService.listIrpAccountDetails(executionContext)),
        CompletableFuture.runAsync(() -> irpAccountTransactionService.listTransactions(executionContext))
    ).join();
  }

  private ExecutionContext generateExecutionContext(long banksaladUserId, String organizationId, String syncRequestId,
      OauthToken accessToken, Organization organization, String requestedBy) {

    return ExecutionContext.builder()
        .consentId(accessToken.getConsentId())
        .banksaladUserId(banksaladUserId)
        .executionRequestId(syncRequestId)
        .organizationId(organizationId)
        .organizationCode(organization.getOrganizationCode())
        .organizationHost(organization.getHostUrl())
        .accessToken(accessToken.getAccessToken())
        .syncStartedAt(LocalDateTime.now(DateUtil.UTC_ZONE_ID))
        .syncRequestId(syncRequestId)
        .requestedBy(requestedBy)
        .build();
  }
}
