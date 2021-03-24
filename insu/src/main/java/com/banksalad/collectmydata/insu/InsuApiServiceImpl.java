package com.banksalad.collectmydata.insu;

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
import com.banksalad.collectmydata.insu.car.dto.CarInsurance;
import com.banksalad.collectmydata.insu.car.dto.CarInsuranceTransaction;
import com.banksalad.collectmydata.insu.car.dto.GetCarInsuranceRequest;
import com.banksalad.collectmydata.insu.car.dto.ListCarInsuranceTransactionsRequest;
import com.banksalad.collectmydata.insu.collect.Executions;
import com.banksalad.collectmydata.insu.common.dto.InsuApiResponse;
import com.banksalad.collectmydata.insu.common.grpc.CollectmydataConnectClientService;
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

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

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

  @Override
  public InsuApiResponse onDemandRequestApi(long banksaladUserId, String organizationId, String syncRequestId)
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

    AtomicReference<InsuApiResponse> atomicReference = new AtomicReference<>();
    atomicReference.set(InsuApiResponse.builder().build());

    CompletableFuture.allOf(
        CompletableFuture
            .supplyAsync(
                () -> insuranceBasicService
                    .listAccountInfos(executionContext, Executions.insurance_get_basic, insuranceBasicRequestHelper,
                        insuranceBasicResponseHelper))
            .thenAccept(atomicReference.get()::setInsuranceBasics),

        CompletableFuture
            .supplyAsync(
                () -> insuranceContractService
                    .listAccountInfos(executionContext, Executions.insurance_get_contract,
                        insuranceContractRequestHelper,
                        insuranceContractResponseHelper))
            .thenApply(lists -> lists.stream().flatMap(Collection::stream).collect(Collectors.toList()))
            .thenAccept(atomicReference.get()::setInsuranceContracts),

        CompletableFuture
            .supplyAsync(
                () -> insurancePaymentService
                    .listAccountInfos(executionContext, Executions.insurance_get_payment, insurancePaymentRequestHelper,
                        insurancePaymentResponseHelper))
            .thenAccept(atomicReference.get()::setInsurancePayments),

        CompletableFuture
            .supplyAsync(
                () -> insuranceTransactionService
                    .listTransactions(executionContext, Executions.insurance_get_transactions,
                        insuranceTransactionRequestHelper,
                        insuranceTransactionResponseHelper))
            .thenAccept(atomicReference.get()::setInsuranceTransactions),
// TODO :
//        CompletableFuture
//            .supplyAsync(
//                () -> carInsuranceService
//                    .listAccountInfos(executionContext, Executions.insurance_get_car, carInsuranceRequestHelper,
//                        carInsuranceResponseHelper))
//            .thenAccept(atomicReference.get()::setCarInsurances),

        CompletableFuture
            .supplyAsync(
                () -> carInsuranceTransactionService
                    .listTransactions(executionContext, Executions.insurance_get_car_transactions,
                        carInsuranceTransactionRequestHelper,
                        carInsuranceTransactionResponseHelper))
            .thenAccept(atomicReference.get()::setCarInsuranceTransactions),

        CompletableFuture
            .supplyAsync(
                () -> loanBasicService
                    .listAccountInfos(executionContext, Executions.insurance_get_loan_basic, loanBasicRequestHelper,
                        loanBasicResponseHelper))
            .thenAccept(atomicReference.get()::setLoanBasics),

        CompletableFuture
            .supplyAsync(
                () -> loanDetailService
                    .listAccountInfos(executionContext, Executions.insurance_get_loan_detail, loanDetailRequestHelper,
                        loanDetailResponseHelper))
            .thenAccept(atomicReference.get()::setLoanDetails),

        CompletableFuture
            .supplyAsync(
                () -> loanTransactionService
                    .listTransactions(executionContext, Executions.insurance_get_loan_transactions,
                        loanTransactionRequestHelper,
                        loanTransactionResponseHelper))
            .thenAccept(atomicReference.get()::setLoanTransactions)
    ).join();

    return atomicReference.get();
  }

  @Override
  public InsuApiResponse scheduledBasicRequestApi(long banksaladUserId, String organizationId, String syncRequestId)
      throws ResponseNotOkException {
    return onDemandRequestApi(banksaladUserId, organizationId, syncRequestId);
  }

  @Override
  public InsuApiResponse scheduledAdditionalRequestApi(long banksaladUserId, String organizationId,
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

    AtomicReference<InsuApiResponse> atomicReference = new AtomicReference<>();
    atomicReference.set(InsuApiResponse.builder().build());

    CompletableFuture.allOf(
        CompletableFuture
            .supplyAsync(
                () -> insuranceTransactionService
                    .listTransactions(executionContext, Executions.insurance_get_transactions,
                        insuranceTransactionRequestHelper,
                        insuranceTransactionResponseHelper))
            .thenAccept(atomicReference.get()::setInsuranceTransactions),

        CompletableFuture
            .supplyAsync(
                () -> carInsuranceTransactionService
                    .listTransactions(executionContext, Executions.insurance_get_car_transactions,
                        carInsuranceTransactionRequestHelper,
                        carInsuranceTransactionResponseHelper))
            .thenAccept(atomicReference.get()::setCarInsuranceTransactions),

        CompletableFuture
            .supplyAsync(
                () -> loanDetailService
                    .listAccountInfos(executionContext, Executions.insurance_get_loan_detail, loanDetailRequestHelper,
                        loanDetailResponseHelper))
            .thenAccept(atomicReference.get()::setLoanDetails),

        CompletableFuture
            .supplyAsync(
                () -> loanTransactionService
                    .listTransactions(executionContext, Executions.insurance_get_loan_transactions,
                        loanTransactionRequestHelper,
                        loanTransactionResponseHelper))
            .thenAccept(atomicReference.get()::setLoanTransactions)
    ).join();

    return atomicReference.get();
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
        .build();
  }
}
