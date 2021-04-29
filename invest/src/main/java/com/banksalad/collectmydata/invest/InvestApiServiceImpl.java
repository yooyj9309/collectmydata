package com.banksalad.collectmydata.invest;

import org.springframework.stereotype.Service;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.enums.SyncRequestType;
import com.banksalad.collectmydata.common.message.MessageTopic;
import com.banksalad.collectmydata.common.message.SyncCompletedMessage;
import com.banksalad.collectmydata.common.util.DateUtil;
import com.banksalad.collectmydata.finance.api.accountinfo.AccountInfoRequestHelper;
import com.banksalad.collectmydata.finance.api.accountinfo.AccountInfoResponseHelper;
import com.banksalad.collectmydata.finance.api.accountinfo.AccountInfoService;
import com.banksalad.collectmydata.finance.api.summary.SummaryPublishmentHelper;
import com.banksalad.collectmydata.finance.api.summary.SummaryRequestHelper;
import com.banksalad.collectmydata.finance.api.summary.SummaryResponseHelper;
import com.banksalad.collectmydata.finance.api.summary.SummaryService;
import com.banksalad.collectmydata.finance.api.transaction.TransactionApiService;
import com.banksalad.collectmydata.finance.api.transaction.TransactionPublishmentHelper;
import com.banksalad.collectmydata.finance.api.transaction.TransactionRequestHelper;
import com.banksalad.collectmydata.finance.api.transaction.TransactionResponseHelper;
import com.banksalad.collectmydata.finance.common.dto.OauthToken;
import com.banksalad.collectmydata.finance.common.dto.Organization;
import com.banksalad.collectmydata.finance.common.exception.ResponseNotOkException;
import com.banksalad.collectmydata.finance.common.grpc.CollectmydataConnectClientService;
import com.banksalad.collectmydata.finance.common.service.FinanceMessageService;
import com.banksalad.collectmydata.invest.account.AccountBasicInfoPublishmentHelper;
import com.banksalad.collectmydata.invest.account.AccountProductInfoPublishmentHelper;
import com.banksalad.collectmydata.invest.account.dto.AccountBasic;
import com.banksalad.collectmydata.invest.account.dto.AccountProduct;
import com.banksalad.collectmydata.invest.account.dto.AccountTransaction;
import com.banksalad.collectmydata.invest.account.dto.GetAccountBasicRequest;
import com.banksalad.collectmydata.invest.account.dto.ListAccountProductsRequest;
import com.banksalad.collectmydata.invest.account.dto.ListAccountTransactionsRequest;
import com.banksalad.collectmydata.invest.collect.Executions;
import com.banksalad.collectmydata.invest.summary.dto.AccountSummary;
import com.banksalad.collectmydata.invest.summary.dto.ListAccountSummariesRequest;
import com.banksalad.collectmydata.irp.account.IrpAccountService;
import com.banksalad.collectmydata.irp.account.IrpAccountTransactionService;
import com.banksalad.collectmydata.irp.summary.IrpAccountSummaryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class InvestApiServiceImpl implements InvestApiService {

  private final FinanceMessageService financeMessageService;
  private final CollectmydataConnectClientService connectClientService;

  private final SummaryService<ListAccountSummariesRequest, AccountSummary> accountSummaryService;
  private final SummaryRequestHelper<ListAccountSummariesRequest> accountSummaryRequestHelper;
  private final SummaryResponseHelper<AccountSummary> accountSummaryResponseHelper;
  private final SummaryPublishmentHelper summaryPublishmentHelper;

  private final AccountInfoService<AccountSummary, GetAccountBasicRequest, AccountBasic> accountBasicInfoService;
  private final AccountInfoRequestHelper<GetAccountBasicRequest, AccountSummary> accountBasicInfoRequestHelper;
  private final AccountInfoResponseHelper<AccountSummary, AccountBasic> accountBasicInfoResponseHelper;
  private final AccountBasicInfoPublishmentHelper accountBasicInfoPublishmentHelper;

  private final TransactionApiService<AccountSummary, ListAccountTransactionsRequest, AccountTransaction> accountTransactionApiService;
  private final TransactionRequestHelper<AccountSummary, ListAccountTransactionsRequest> accountTransactionRequestHelper;
  private final TransactionResponseHelper<AccountSummary, AccountTransaction> accountTransactionResponseHelper;
  private final TransactionPublishmentHelper<AccountSummary> transactionPublishmentHelper;

  private final AccountInfoService<AccountSummary, ListAccountProductsRequest, List<AccountProduct>> accountProductInfoService;
  private final AccountInfoRequestHelper<ListAccountProductsRequest, AccountSummary> accountProductInfoRequestHelper;
  private final AccountInfoResponseHelper<AccountSummary, List<AccountProduct>> accountProductInfoResponseHelper;
  private final AccountProductInfoPublishmentHelper accountProductInfoPublishmentHelper;

  // IRP
  private final IrpAccountSummaryService irpAccountSummaryService;
  private final IrpAccountService irpAccountService;
  private final IrpAccountTransactionService irpAccountTransactionService;

  @Override
  public void onDemandRequestApi(long banksaladUserId, String organizationId, String syncRequestId,
      SyncRequestType syncRequestType) throws ResponseNotOkException {

    OauthToken oauthToken = connectClientService.getAccessToken(banksaladUserId, organizationId);
    Organization organization = connectClientService.getOrganization(organizationId);

    ExecutionContext executionContext = ExecutionContext.builder()
        .consentId(oauthToken.getConsentId())
        .syncRequestId(syncRequestId)
        .executionRequestId(UUID.randomUUID().toString())
        .banksaladUserId(banksaladUserId)
        .organizationId(organization.getOrganizationId())
        .organizationCode(organization.getOrganizationCode())
        .organizationHost(organization.getHostUrl())
        .accessToken(oauthToken.getAccessToken())
        .syncStartedAt(LocalDateTime.now(DateUtil.UTC_ZONE_ID))
        .requestedBy(String.valueOf(banksaladUserId))
        .build();

    accountSummaryService
        .listAccountSummaries(executionContext, Executions.finance_invest_accounts, accountSummaryRequestHelper,
            accountSummaryResponseHelper, summaryPublishmentHelper);

    // IRP Account Summary
    irpAccountSummaryService.listAccountSummaries(executionContext);

    CompletableFuture.allOf(
        CompletableFuture.runAsync(() -> accountBasicInfoService
            .listAccountInfos(executionContext, Executions.finance_invest_account_basic, accountBasicInfoRequestHelper,
                accountBasicInfoResponseHelper, accountBasicInfoPublishmentHelper)),

        CompletableFuture.runAsync(() -> accountTransactionApiService
            .listTransactions(executionContext, Executions.finance_invest_account_transactions,
                accountTransactionRequestHelper, accountTransactionResponseHelper, transactionPublishmentHelper)),

        CompletableFuture.runAsync(() -> accountProductInfoService
            .listAccountInfos(executionContext, Executions.finance_invest_account_products,
                accountProductInfoRequestHelper, accountProductInfoResponseHelper,
                accountProductInfoPublishmentHelper)),

        // IRP Account Basic, Detail, Transaction
        CompletableFuture.runAsync(() -> irpAccountService.listIrpAccountBasics(executionContext)),
        CompletableFuture.runAsync(() -> irpAccountService.listIrpAccountDetails(executionContext)),
        CompletableFuture.runAsync(() -> irpAccountTransactionService.listTransactions(executionContext))
    ).join();

    financeMessageService.produceSyncCompleted(
        MessageTopic.investSyncCompleted,
        SyncCompletedMessage.builder()
            .banksaladUserId(executionContext.getBanksaladUserId())
            .organizationId(executionContext.getOrganizationId())
            .syncRequestId(executionContext.getSyncRequestId())
            .syncRequestType(syncRequestType)
            .build());
  }

  // TODO jaeseong: 스케쥴 request api 구현
}

