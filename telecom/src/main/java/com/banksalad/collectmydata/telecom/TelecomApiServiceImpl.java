package com.banksalad.collectmydata.telecom;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.util.DateUtil;
import com.banksalad.collectmydata.finance.api.summary.SummaryService;
import com.banksalad.collectmydata.finance.api.transaction.TransactionApiService;
import com.banksalad.collectmydata.finance.common.dto.OauthToken;
import com.banksalad.collectmydata.finance.common.dto.Organization;
import com.banksalad.collectmydata.finance.common.exception.ResponseNotOkException;
import com.banksalad.collectmydata.finance.common.grpc.CollectmydataConnectClientService;
import com.banksalad.collectmydata.telecom.collect.Executions;
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

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

import static com.banksalad.collectmydata.finance.common.constant.FinanceConstant.REQUESTED_BY_SCHEDULE;

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
  public void onDemandRequestApi(long banksaladUserId, String organizationId, String syncRequestId)
      throws ResponseNotOkException {

    final OauthToken oauthToken = collectmydataConnectClientService.getAccessToken(banksaladUserId, organizationId);
    final Organization organization = collectmydataConnectClientService.getOrganization(organizationId);

    // Make an execution context
    ExecutionContext executionContext = generateExecutionContext(syncRequestId, banksaladUserId, organizationId,
        oauthToken, organization, String.valueOf(banksaladUserId));

    // 6.9.1: 통신 계약 목록 조회
    summaryService
        .listAccountSummaries(executionContext, Executions.finance_telecom_summaries, telecomSummaryRequestHelper,
            telecomSummaryResponseHelper);

    // TODO: Replace with 6.9.2.
    CompletableFuture<Void> telecomBillFuture = CompletableFuture.completedFuture(null);

    // TODO: Replace with 6.9.3.
    CompletableFuture<Void> telecomTransactionFuture = CompletableFuture.completedFuture(null);

    CompletableFuture<Void> telecomPaidTransactionFuture = CompletableFuture
        .runAsync(() -> telecomPaidTransactionService
            .listTransactions(executionContext, Executions.finance_telecom_paid_transactions,
                telecomPaidTransactionRequestHelper, telecomPaidTransactionResponseHelper));

    Stream.of(telecomBillFuture, telecomTransactionFuture, telecomPaidTransactionFuture).map(CompletableFuture::join);
  }

  @Override
  public void scheduledBasicRequestApi(long banksaladUserId, String organizationId, String syncRequestId)
      throws ResponseNotOkException {

    onDemandRequestApi(banksaladUserId, organizationId, syncRequestId);
  }

  @Override
  public void scheduledAdditionalRequestApi(long banksaladUserId, String organizationId,
      String syncRequestId) throws ResponseNotOkException {
    final OauthToken oauthToken = collectmydataConnectClientService.getAccessToken(banksaladUserId, organizationId);
    final Organization organization = collectmydataConnectClientService.getOrganization(organizationId);

    // Make an execution context
    ExecutionContext executionContext = generateExecutionContext(syncRequestId, banksaladUserId, organizationId,
        oauthToken, organization, REQUESTED_BY_SCHEDULE);

    summaryService
        .listAccountSummaries(executionContext, Executions.finance_telecom_summaries, telecomSummaryRequestHelper,
            telecomSummaryResponseHelper);

    CompletableFuture.allOf(
        CompletableFuture
            .runAsync(
                () -> telecomTransactionService
                    .listTransactions(executionContext, Executions.finance_telecom_transactions,
                        telecomTransactionRequestHelper, telecomTransactionResponseHelper)),

        CompletableFuture
            .runAsync(
                () -> telecomPaidTransactionService
                    .listTransactions(executionContext, Executions.finance_telecom_paid_transactions,
                        telecomPaidTransactionRequestHelper,
                        telecomPaidTransactionResponseHelper))
    ).join();
  }

  private ExecutionContext generateExecutionContext(String syncRequestId, long banksaladUserId, String organizationId,
      OauthToken oauthToken, Organization organization, String requestedBy) {
    return ExecutionContext.builder()
        .consentId(oauthToken.getConsentId())
        .syncRequestId(syncRequestId)
        .executionRequestId(UUID.randomUUID().toString())
        .banksaladUserId(banksaladUserId)
        .organizationId(organizationId)
        .organizationCode(organization.getOrganizationCode())
        .organizationHost(organization.getHostUrl())
        .accessToken(oauthToken.getAccessToken())
        .syncStartedAt(LocalDateTime.now(DateUtil.UTC_ZONE_ID))
        .requestedBy(requestedBy)
        .build();
  }
}
