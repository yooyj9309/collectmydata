package com.banksalad.collectmydata.telecom;

import org.springframework.stereotype.Service;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.finance.api.summary.SummaryService;
import com.banksalad.collectmydata.finance.api.transaction.TransactionApiService;
import com.banksalad.collectmydata.finance.common.dto.OauthToken;
import com.banksalad.collectmydata.finance.common.dto.Organization;
import com.banksalad.collectmydata.finance.common.exception.ResponseNotOkException;
import com.banksalad.collectmydata.finance.common.grpc.CollectmydataConnectClientService;
import com.banksalad.collectmydata.telecom.collect.Executions;
import com.banksalad.collectmydata.telecom.common.dto.TelecomApiResponse;
import com.banksalad.collectmydata.telecom.summary.TelecomSummaryRequestHelper;
import com.banksalad.collectmydata.telecom.summary.TelecomSummaryResponseHelper;
import com.banksalad.collectmydata.telecom.summary.dto.ListTelecomSummariesRequest;
import com.banksalad.collectmydata.telecom.summary.dto.TelecomSummary;
import com.banksalad.collectmydata.telecom.telecom.TelecomPaidTransactionRequestHelper;
import com.banksalad.collectmydata.telecom.telecom.TelecomPaidTransactionResponseHelper;
import com.banksalad.collectmydata.telecom.telecom.TelecomTransactionRequestHelper;
import com.banksalad.collectmydata.telecom.telecom.TelecomTransactionResponseHelper;
import com.banksalad.collectmydata.telecom.telecom.dto.ListTelecomPaidTransactionsRequest;
import com.banksalad.collectmydata.telecom.telecom.dto.ListTelecomTransactionsRequest;
import com.banksalad.collectmydata.telecom.telecom.dto.TelecomPaidTransaction;
import com.banksalad.collectmydata.telecom.telecom.dto.TelecomTransaction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class TelecomApiServiceImpl implements TelecomApiService {

  private final CollectmydataConnectClientService collectmydataConnectClientService;

  private final SummaryService<ListTelecomSummariesRequest, TelecomSummary> summaryService;
  private final TransactionApiService<TelecomSummary, ListTelecomTransactionsRequest, TelecomTransaction> telecomTransactionService;
  private final TransactionApiService<TelecomSummary, ListTelecomPaidTransactionsRequest, TelecomPaidTransaction> telecomPaidTransactionService;

  private final TelecomSummaryRequestHelper telecomSummaryRequestHelper;
  private final TelecomSummaryResponseHelper telecomSummaryResponseHelper;

  private final TelecomTransactionRequestHelper telecomTransactionRequestHelper;
  private final TelecomTransactionResponseHelper telecomTransactionResponseHelper;

  private final TelecomPaidTransactionRequestHelper telecomPaidTransactionRequestHelper;
  private final TelecomPaidTransactionResponseHelper telecomPaidTransactionResponseHelper;

  @Override
  public TelecomApiResponse onDemandRequestApi(long banksaladUserId, String organizationId, String syncRequestId)
      throws ResponseNotOkException {

    final OauthToken oauthToken = collectmydataConnectClientService.getAccessToken(banksaladUserId, organizationId);
    final Organization organization = collectmydataConnectClientService.getOrganization(organizationId);

    // Make an execution context
    ExecutionContext executionContext = generateExecutionContext(banksaladUserId, organizationId, oauthToken,
        organization);

    // 6.9.1: 통신 계약 목록 조회
    summaryService
        .listAccountSummaries(executionContext, Executions.finance_telecom_summaries, telecomSummaryRequestHelper,
            telecomSummaryResponseHelper);

    AtomicReference<TelecomApiResponse> telecomApiResponseAtomicReference = new AtomicReference<>();
    telecomApiResponseAtomicReference.set(TelecomApiResponse.builder().build());

    // TODO: Replace with 6.9.2.
    CompletableFuture<Void> telecomBillFuture = CompletableFuture.completedFuture(null);

    // TODO: Replace with 6.9.3.
    CompletableFuture<Void> telecomTransactionFuture = CompletableFuture.completedFuture(null);

    CompletableFuture<Void> telecomPaidTransactionFuture = CompletableFuture
        .supplyAsync(() -> telecomPaidTransactionService
            .listTransactions(executionContext, Executions.finance_telecom_paid_transactions,
                telecomPaidTransactionRequestHelper, telecomPaidTransactionResponseHelper))
        .thenAccept(telecomPaidTransactions -> telecomApiResponseAtomicReference.get()
            .setTelecomPaidTransactions(telecomPaidTransactions));

    Stream.of(telecomBillFuture, telecomTransactionFuture, telecomPaidTransactionFuture).map(CompletableFuture::join);

    return telecomApiResponseAtomicReference.get();
  }

  @Override
  public TelecomApiResponse scheduledBasicRequestApi(long banksaladUserId, String organizationId, String syncRequestId)
      throws ResponseNotOkException {
    return onDemandRequestApi(banksaladUserId, organizationId, syncRequestId);
  }

  @Override
  public TelecomApiResponse scheduledAdditionalRequestApi(long banksaladUserId, String organizationId,
      String syncRequestId) throws ResponseNotOkException {
    final OauthToken oauthToken = collectmydataConnectClientService.getAccessToken(banksaladUserId, organizationId);
    final Organization organization = collectmydataConnectClientService.getOrganization(organizationId);

    // Make an execution context
    ExecutionContext executionContext = generateExecutionContext(banksaladUserId, organizationId, oauthToken,
        organization);

    summaryService
        .listAccountSummaries(executionContext, Executions.finance_telecom_summaries, telecomSummaryRequestHelper,
            telecomSummaryResponseHelper);

    AtomicReference<TelecomApiResponse> atomicReference = new AtomicReference<>();
    atomicReference.set(TelecomApiResponse.builder().build());

    CompletableFuture.allOf(
        CompletableFuture
            .supplyAsync(
                () -> telecomTransactionService
                    .listTransactions(executionContext, Executions.finance_telecom_transactions,
                        telecomTransactionRequestHelper, telecomTransactionResponseHelper))
            .thenAccept(atomicReference.get()::setTelecomTransactions),

        CompletableFuture
            .supplyAsync(
                () -> telecomPaidTransactionService
                    .listTransactions(executionContext, Executions.finance_telecom_paid_transactions,
                        telecomPaidTransactionRequestHelper,
                        telecomPaidTransactionResponseHelper))
            .thenAccept(atomicReference.get()::setTelecomPaidTransactions)
    ).join();

    return atomicReference.get();
  }

  private ExecutionContext generateExecutionContext(long banksaladUserId, String organizationId, OauthToken oauthToken,
      Organization organization) {
    return ExecutionContext.builder()
        .banksaladUserId(banksaladUserId)
        .organizationId(organizationId)
        .organizationCode(organization.getOrganizationCode())
        .organizationHost(organization.getHostUrl())
        .accessToken(oauthToken.getAccessToken())
        .build();
  }
}
