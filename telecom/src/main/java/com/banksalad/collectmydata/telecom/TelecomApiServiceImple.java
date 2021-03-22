package com.banksalad.collectmydata.telecom;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.enums.SyncRequestType;
import com.banksalad.collectmydata.finance.api.summary.SummaryService;
import com.banksalad.collectmydata.finance.api.transaction.TransactionApiService;
import com.banksalad.collectmydata.finance.common.dto.OauthToken;
import com.banksalad.collectmydata.finance.common.dto.Organization;
import com.banksalad.collectmydata.finance.common.exception.ResponseNotOkException;
import com.banksalad.collectmydata.finance.common.service.OauthTokenService;
import com.banksalad.collectmydata.finance.common.service.OrganizationService;
import com.banksalad.collectmydata.telecom.collect.Executions;
import com.banksalad.collectmydata.telecom.common.dto.TelecomApiResponse;
import com.banksalad.collectmydata.telecom.summary.TelecomSummaryRequestHelper;
import com.banksalad.collectmydata.telecom.summary.TelecomSummaryResponseHelper;
import com.banksalad.collectmydata.telecom.summary.dto.ListTelecomSummariesRequest;
import com.banksalad.collectmydata.telecom.summary.dto.TelecomSummary;
import com.banksalad.collectmydata.telecom.telecom.TelecomPaidTransactionRequestHelper;
import com.banksalad.collectmydata.telecom.telecom.TelecomPaidTransactionResponseHelper;
import com.banksalad.collectmydata.telecom.telecom.dto.ListTelecomPaidTransactionsRequest;
import com.banksalad.collectmydata.telecom.telecom.dto.TelecomPaidTransaction;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class TelecomApiServiceImple implements TelecomApiService {

  private final OrganizationService organizationService;
  private final OauthTokenService oauthTokenService;

  private final SummaryService<ListTelecomSummariesRequest, TelecomSummary> summaryService;
  private final TransactionApiService<TelecomSummary, ListTelecomPaidTransactionsRequest, TelecomPaidTransaction> telecomPaidTransactionService;

  private final TelecomSummaryRequestHelper telecomSummaryRequestHelper;
  private final TelecomSummaryResponseHelper telecomSummaryResponseHelper;

  private final TelecomPaidTransactionRequestHelper telecomPaidTransactionRequestHelper;
  private final TelecomPaidTransactionResponseHelper telecomPaidTransactionResponseHelper;

  @Override
  public TelecomApiResponse requestApi(long banksaladUserId, String organizationId, String syncRequestId,
      SyncRequestType syncRequestType) throws ResponseNotOkException {

    final OauthToken oauthToken = oauthTokenService.getOauthToken(banksaladUserId, organizationId);
    final Organization organization = organizationService.getOrganizationById(organizationId);

    // Make an execution context
    ExecutionContext executionContext = ExecutionContext.builder()
        .banksaladUserId(banksaladUserId)
        .organizationId(organizationId)
        .organizationCode(organization.getOrganizationCode())
        .organizationHost(organization.getHostUrl())
        .accessToken(oauthToken.getAccessToken())
        .build();

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
}
