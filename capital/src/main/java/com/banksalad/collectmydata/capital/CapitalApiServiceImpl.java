package com.banksalad.collectmydata.capital;

import com.banksalad.collectmydata.capital.account.dto.AccountBasic;
import com.banksalad.collectmydata.capital.account.dto.AccountDetail;
import com.banksalad.collectmydata.capital.account.dto.AccountTransaction;
import com.banksalad.collectmydata.capital.account.dto.GetAccountBasicRequest;
import com.banksalad.collectmydata.capital.account.dto.GetAccountDetailRequest;
import com.banksalad.collectmydata.capital.account.dto.ListAccountTransactionsRequest;
import com.banksalad.collectmydata.capital.collect.Executions;
import com.banksalad.collectmydata.capital.oplease.dto.GetOperatingLeaseBasicRequest;
import com.banksalad.collectmydata.capital.oplease.dto.ListOperatingLeaseTransactionsRequest;
import com.banksalad.collectmydata.capital.oplease.dto.OperatingLeaseBasic;
import com.banksalad.collectmydata.capital.oplease.dto.OperatingLeaseTransaction;
import com.banksalad.collectmydata.capital.summary.dto.AccountSummary;
import com.banksalad.collectmydata.capital.summary.dto.ListAccountSummariesRequest;
import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.enums.SyncRequestType;
import com.banksalad.collectmydata.common.exception.CollectException;
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

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class CapitalApiServiceImpl implements CapitalApiService {

  private final CollectmydataConnectClientService collectmydataConnectClientService;

  private final SummaryService<ListAccountSummariesRequest, AccountSummary> accountSummaryService;
  private final SummaryRequestHelper<ListAccountSummariesRequest> accountSummaryRequestHelper;
  private final SummaryResponseHelper<AccountSummary> accountSummaryResponseHelper;

  private final AccountInfoService<AccountSummary, GetAccountBasicRequest, AccountBasic> accountBasicService;
  private final AccountInfoRequestHelper<GetAccountBasicRequest, AccountSummary> accountBasicRequestHelper;
  private final AccountInfoResponseHelper<AccountSummary, AccountBasic> accountBasicResponseHelper;

  private final AccountInfoService<AccountSummary, GetAccountDetailRequest, AccountDetail> accountDetailService;
  private final AccountInfoRequestHelper<GetAccountDetailRequest, AccountSummary> accountDetailRequestHelper;
  private final AccountInfoResponseHelper<AccountSummary, AccountDetail> accountDetailResponseHelper;

  private final TransactionApiService<AccountSummary, ListAccountTransactionsRequest, AccountTransaction> accountTransactionService;
  private final TransactionRequestHelper<AccountSummary, ListAccountTransactionsRequest> accountTransactionRequestHelper;
  private final TransactionResponseHelper<AccountSummary, AccountTransaction> accountTransactionResponseHelper;

  private final AccountInfoService<AccountSummary, GetOperatingLeaseBasicRequest, OperatingLeaseBasic> operatingLeaseBasicService;
  private final AccountInfoRequestHelper<GetOperatingLeaseBasicRequest, AccountSummary> operatingLeaseRequestHelper;
  private final AccountInfoResponseHelper<AccountSummary, OperatingLeaseBasic> operatingLeaseResponseHelper;

  private final TransactionApiService<AccountSummary, ListOperatingLeaseTransactionsRequest, OperatingLeaseTransaction> operatingLeaseTransactionService;
  private final TransactionRequestHelper<AccountSummary, ListOperatingLeaseTransactionsRequest> operatingLeaseTransactionRequestHelper;
  private final TransactionResponseHelper<AccountSummary, OperatingLeaseTransaction> operatingLeaseTransactionResponseHelper;

  @Override
  public void requestApi(long banksaladUserId, String organizationId, String syncRequestId,
      SyncRequestType syncRequestType) throws ResponseNotOkException, CollectException {
    switch (syncRequestType) {
      case ONDEMAND:
        requestApiOnDemand(banksaladUserId, organizationId, syncRequestId, syncRequestType);
        break;
      case SCHEDULED_BASIC:
        requestApiScheduledBasic(banksaladUserId, organizationId, syncRequestId, syncRequestType);
        break;
      case SCHEDULED_ADDITIONAL:
        requestApiScheduledAdditional(banksaladUserId, organizationId, syncRequestId, syncRequestType);
        break;
      default:
        log.error("Fail to specify RequestType: {}", syncRequestType);
        throw new CollectException("undefined syncRequestType"); // TODO
    }
  }

  private void requestApiOnDemand(long banksaladUserId, String organizationId, String syncRequestId,
      SyncRequestType syncRequestType)
      throws ResponseNotOkException {
    Organization organization = collectmydataConnectClientService.getOrganization(organizationId);
    String accessToken = "fixme"; //TODO 토큰 조회 로직 추가하여 적용

    ExecutionContext executionContext = generateExecutionContext(banksaladUserId, organizationId, syncRequestId,
        accessToken, organization);

    accountSummaryService.listAccountSummaries(
        executionContext, Executions.capital_get_accounts, accountSummaryRequestHelper, accountSummaryResponseHelper);

    CompletableFuture.allOf(
        CompletableFuture
            .runAsync(
                () -> accountBasicService
                    .listAccountInfos(executionContext, Executions.capital_get_account_basic, accountBasicRequestHelper,
                        accountBasicResponseHelper)),

        CompletableFuture
            .runAsync(
                () -> accountDetailService.listAccountInfos(executionContext, Executions.capital_get_account_detail,
                    accountDetailRequestHelper, accountDetailResponseHelper)),

        CompletableFuture
            .runAsync(
                () -> accountTransactionService
                    .listTransactions(executionContext, Executions.capital_get_account_transactions,
                        accountTransactionRequestHelper, accountTransactionResponseHelper)),

        CompletableFuture
            .runAsync(
                () -> operatingLeaseBasicService
                    .listAccountInfos(executionContext, Executions.capital_get_operating_lease_basic,
                        operatingLeaseRequestHelper, operatingLeaseResponseHelper)),

        CompletableFuture
            .runAsync(() -> operatingLeaseTransactionService
                .listTransactions(executionContext, Executions.capital_get_operating_lease_transactions,
                    operatingLeaseTransactionRequestHelper, operatingLeaseTransactionResponseHelper))
    ).join();
  }

  private void requestApiScheduledBasic(long banksaladUserId, String organizationId, String syncRequestId,
      SyncRequestType syncRequestType) throws ResponseNotOkException {
    requestApiOnDemand(banksaladUserId, organizationId, syncRequestId, syncRequestType);
  }

  private void requestApiScheduledAdditional(long banksaladUserId, String organizationId,
      String syncRequestId, SyncRequestType syncRequestType) throws ResponseNotOkException {
    Organization organization = collectmydataConnectClientService.getOrganization(organizationId);
    String accessToken = "fixme"; //TODO 토큰 조회 로직 추가하여 적용

    ExecutionContext executionContext = generateExecutionContext(banksaladUserId, organizationId, syncRequestId,
        accessToken, organization);

    accountSummaryService.listAccountSummaries(
        executionContext, Executions.capital_get_accounts, accountSummaryRequestHelper, accountSummaryResponseHelper);

    CompletableFuture.allOf(
        CompletableFuture
            .runAsync(
                () -> accountDetailService.listAccountInfos(executionContext, Executions.capital_get_account_detail,
                    accountDetailRequestHelper, accountDetailResponseHelper)),
        CompletableFuture
            .runAsync(
                () -> accountTransactionService
                    .listTransactions(executionContext, Executions.capital_get_account_transactions,
                        accountTransactionRequestHelper, accountTransactionResponseHelper)),
        CompletableFuture
            .runAsync(
                () -> operatingLeaseTransactionService
                    .listTransactions(executionContext, Executions.capital_get_operating_lease_transactions,
                        operatingLeaseTransactionRequestHelper, operatingLeaseTransactionResponseHelper))
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
        .build();
  }
}
