package com.banksalad.collectmydata.insu;

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
import com.banksalad.collectmydata.finance.common.dto.Organization;
import com.banksalad.collectmydata.finance.common.exception.ResponseNotOkException;
import com.banksalad.collectmydata.finance.common.grpc.CollectmydataConnectClientService;
import com.banksalad.collectmydata.insu.car.dto.CarInsurance;
import com.banksalad.collectmydata.insu.car.dto.CarInsuranceTransaction;
import com.banksalad.collectmydata.insu.car.dto.GetCarInsuranceRequest;
import com.banksalad.collectmydata.insu.car.dto.ListCarInsuranceTransactionsRequest;
import com.banksalad.collectmydata.insu.collect.Executions;
import com.banksalad.collectmydata.insu.insurance.dto.GetInsuranceBasicRequest;
import com.banksalad.collectmydata.insu.insurance.dto.GetInsurancePaymentRequest;
import com.banksalad.collectmydata.insu.insurance.dto.InsuranceBasic;
import com.banksalad.collectmydata.insu.insurance.dto.InsuranceContract;
import com.banksalad.collectmydata.insu.insurance.dto.InsurancePayment;
import com.banksalad.collectmydata.insu.insurance.dto.InsuranceTransaction;
import com.banksalad.collectmydata.insu.insurance.dto.Insured;
import com.banksalad.collectmydata.insu.insurance.dto.ListInsuranceContractsRequest;
import com.banksalad.collectmydata.insu.insurance.dto.ListInsuranceTransactionsRequest;
import com.banksalad.collectmydata.insu.loan.dto.GetLoanBasicRequest;
import com.banksalad.collectmydata.insu.loan.dto.GetLoanDetailRequest;
import com.banksalad.collectmydata.insu.loan.dto.ListLoanTransactionRequest;
import com.banksalad.collectmydata.insu.loan.dto.LoanBasic;
import com.banksalad.collectmydata.insu.loan.dto.LoanDetail;
import com.banksalad.collectmydata.insu.loan.dto.LoanTransaction;
import com.banksalad.collectmydata.insu.summary.dto.InsuranceSummary;
import com.banksalad.collectmydata.insu.summary.dto.ListInsuranceSummariesRequest;
import com.banksalad.collectmydata.insu.summary.dto.ListLoanSummariesRequest;
import com.banksalad.collectmydata.insu.summary.dto.LoanSummary;
import com.banksalad.collectmydata.irp.account.IrpAccountService;
import com.banksalad.collectmydata.irp.account.IrpAccountTransactionService;
import com.banksalad.collectmydata.irp.summary.IrpAccountSummaryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class InsuApiServiceImpl implements InsuApiService {

  // SUMMARY
  private final SummaryService<ListInsuranceSummariesRequest, InsuranceSummary> insuranceSummaryService;
  private final SummaryRequestHelper<ListInsuranceSummariesRequest> insuranceSummaryRequestHelper;
  private final SummaryResponseHelper<InsuranceSummary> insuranceSummaryResponseHelper;

  private final SummaryService<ListLoanSummariesRequest, LoanSummary> loanSummaryService;
  private final SummaryRequestHelper<ListLoanSummariesRequest> loanSummaryRequestHelper;
  private final SummaryResponseHelper<LoanSummary> loanSummaryResponseHelper;

  // INSURANCE
  private final AccountInfoService<InsuranceSummary, GetInsuranceBasicRequest, InsuranceBasic> insuranceBasicService;
  private final AccountInfoRequestHelper<GetInsuranceBasicRequest, InsuranceSummary> insuranceBasicRequestHelper;
  private final AccountInfoResponseHelper<InsuranceSummary, InsuranceBasic> insuranceBasicResponseHelper;

  private final AccountInfoService<Insured, ListInsuranceContractsRequest, List<InsuranceContract>> insuranceContractService;
  private final AccountInfoRequestHelper<ListInsuranceContractsRequest, Insured> insuranceContractRequestHelper;
  private final AccountInfoResponseHelper<Insured, List<InsuranceContract>> insuranceContractResponseHelper;

  private final AccountInfoService<InsuranceSummary, GetInsurancePaymentRequest, InsurancePayment> insurancePaymentService;
  private final AccountInfoRequestHelper<GetInsurancePaymentRequest, InsuranceSummary> insurancePaymentRequestHelper;
  private final AccountInfoResponseHelper<InsuranceSummary, InsurancePayment> insurancePaymentResponseHelper;

  private final TransactionApiService<InsuranceSummary, ListInsuranceTransactionsRequest, InsuranceTransaction> insuranceTransactionService;
  private final TransactionRequestHelper<InsuranceSummary, ListInsuranceTransactionsRequest> insuranceTransactionRequestHelper;
  private final TransactionResponseHelper<InsuranceSummary, InsuranceTransaction> insuranceTransactionResponseHelper;

  // CAR
  private final AccountInfoService<InsuranceSummary, GetCarInsuranceRequest, List<CarInsurance>> carInsuranceService;
  private final AccountInfoRequestHelper<GetCarInsuranceRequest, InsuranceSummary> carInsuranceRequestHelper;
  private final AccountInfoResponseHelper<InsuranceSummary, List<CarInsurance>> carInsuranceResponseHelper;

  private final TransactionApiService<CarInsurance, ListCarInsuranceTransactionsRequest, CarInsuranceTransaction> carInsuranceTransactionService;
  private final TransactionRequestHelper<CarInsurance, ListCarInsuranceTransactionsRequest> carInsuranceTransactionRequestHelper;
  private final TransactionResponseHelper<CarInsurance, CarInsuranceTransaction> carInsuranceTransactionResponseHelper;

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

  @Override
  public void onDemandRequestApi(long banksaladUserId, String organizationId, String syncRequestId)
      throws ResponseNotOkException {

    Organization organization = collectmydataConnectClientService.getOrganization(organizationId);
    String accessToken = "fixme"; //TODO 토큰 조회 로직 추가하여 적용

    ExecutionContext executionContext = generateExecutionContext(banksaladUserId, organizationId, syncRequestId,
        accessToken, organization);

    insuranceSummaryService.listAccountSummaries(
        executionContext, Executions.insurance_get_summaries, insuranceSummaryRequestHelper,
        insuranceSummaryResponseHelper);

    loanSummaryService.listAccountSummaries(
        executionContext, Executions.insurance_get_loan_summaries, loanSummaryRequestHelper, loanSummaryResponseHelper);

    // IRP Account Summary
    irpAccountSummaryService.listAccountSummaries(executionContext);

    CompletableFuture.allOf(
        CompletableFuture
            .runAsync(
                () -> insuranceBasicService
                    .listAccountInfos(executionContext, Executions.insurance_get_basic, insuranceBasicRequestHelper,
                        insuranceBasicResponseHelper)),

        CompletableFuture
            .runAsync(
                () -> insuranceContractService
                    .listAccountInfos(executionContext, Executions.insurance_get_contract,
                        insuranceContractRequestHelper,
                        insuranceContractResponseHelper)),

        CompletableFuture
            .runAsync(
                () -> insurancePaymentService
                    .listAccountInfos(executionContext, Executions.insurance_get_payment, insurancePaymentRequestHelper,
                        insurancePaymentResponseHelper)),

        CompletableFuture
            .runAsync(
                () -> insuranceTransactionService
                    .listTransactions(executionContext, Executions.insurance_get_transactions,
                        insuranceTransactionRequestHelper,
                        insuranceTransactionResponseHelper)),
// TODO :
//        CompletableFuture
//            .supplyAsync(
//                () -> carInsuranceService
//                    .listAccountInfos(executionContext, Executions.insurance_get_car, carInsuranceRequestHelper,
//                        carInsuranceResponseHelper))
//            .thenAccept(atomicReference.get()::setCarInsurances),

        CompletableFuture
            .runAsync(
                () -> carInsuranceTransactionService
                    .listTransactions(executionContext, Executions.insurance_get_car_transactions,
                        carInsuranceTransactionRequestHelper,
                        carInsuranceTransactionResponseHelper)),

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

  @Override
  public void scheduledBasicRequestApi(long banksaladUserId, String organizationId, String syncRequestId)
      throws ResponseNotOkException {

    Organization organization = collectmydataConnectClientService.getOrganization(organizationId);
    String accessToken = "fixme"; //TODO 토큰 조회 로직 추가하여 적용

    ExecutionContext executionContext = generateExecutionContext(banksaladUserId, organizationId, syncRequestId,
        accessToken, organization);

    insuranceSummaryService.listAccountSummaries(
        executionContext, Executions.insurance_get_summaries, insuranceSummaryRequestHelper,
        insuranceSummaryResponseHelper);

    loanSummaryService.listAccountSummaries(
        executionContext, Executions.insurance_get_loan_summaries, loanSummaryRequestHelper, loanSummaryResponseHelper);

    // IRP Account Summary
    irpAccountSummaryService.listAccountSummaries(executionContext);

    CompletableFuture.allOf(
        CompletableFuture
            .runAsync(
                () -> insuranceBasicService
                    .listAccountInfos(executionContext, Executions.insurance_get_basic, insuranceBasicRequestHelper,
                        insuranceBasicResponseHelper)),

        CompletableFuture
            .runAsync(
                () -> insuranceContractService
                    .listAccountInfos(executionContext, Executions.insurance_get_contract,
                        insuranceContractRequestHelper,
                        insuranceContractResponseHelper)),

        CompletableFuture
            .runAsync(
                () -> insurancePaymentService
                    .listAccountInfos(executionContext, Executions.insurance_get_payment, insurancePaymentRequestHelper,
                        insurancePaymentResponseHelper)),

        CompletableFuture
            .runAsync(
                () -> insuranceTransactionService
                    .listTransactions(executionContext, Executions.insurance_get_transactions,
                        insuranceTransactionRequestHelper,
                        insuranceTransactionResponseHelper)),
// TODO :
//        CompletableFuture
//            .supplyAsync(
//                () -> carInsuranceService
//                    .listAccountInfos(executionContext, Executions.insurance_get_car, carInsuranceRequestHelper,
//                        carInsuranceResponseHelper))
//            .thenAccept(atomicReference.get()::setCarInsurances),

        CompletableFuture
            .runAsync(
                () -> carInsuranceTransactionService
                    .listTransactions(executionContext, Executions.insurance_get_car_transactions,
                        carInsuranceTransactionRequestHelper,
                        carInsuranceTransactionResponseHelper)),

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

        // IRP Account Summary, Basic(정기적 전송주기 : 기본)
        CompletableFuture.runAsync(() -> irpAccountService.listIrpAccountBasics(executionContext))
    ).join();
  }

  @Override
  public void scheduledAdditionalRequestApi(long banksaladUserId, String organizationId,
      String syncRequestId) throws ResponseNotOkException {

    Organization organization = collectmydataConnectClientService.getOrganization(organizationId);
    String accessToken = "fixme"; //TODO 토큰 조회 로직 추가하여 적용

    ExecutionContext executionContext = generateExecutionContext(banksaladUserId, organizationId, syncRequestId,
        accessToken, organization);

    insuranceSummaryService.listAccountSummaries(
        executionContext, Executions.insurance_get_summaries, insuranceSummaryRequestHelper,
        insuranceSummaryResponseHelper);

    loanSummaryService.listAccountSummaries(
        executionContext, Executions.insurance_get_loan_summaries, loanSummaryRequestHelper, loanSummaryResponseHelper);

    CompletableFuture.allOf(
        CompletableFuture
            .runAsync(
                () -> insuranceTransactionService
                    .listTransactions(executionContext, Executions.insurance_get_transactions,
                        insuranceTransactionRequestHelper,
                        insuranceTransactionResponseHelper)),

        CompletableFuture
            .runAsync(
                () -> carInsuranceTransactionService
                    .listTransactions(executionContext, Executions.insurance_get_car_transactions,
                        carInsuranceTransactionRequestHelper,
                        carInsuranceTransactionResponseHelper)),

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
      String accessToken, Organization organization) {
    return ExecutionContext.builder()
        .banksaladUserId(banksaladUserId)
        .executionRequestId(syncRequestId)
        .organizationId(organizationId)
        .organizationCode(organization.getOrganizationCode())
        .organizationHost(organization.getHostUrl())
        .accessToken(accessToken)
        .syncStartedAt(LocalDateTime.now(DateUtil.UTC_ZONE_ID))
        .syncRequestId(syncRequestId)
        .build();
  }
}
