package com.banksalad.collectmydata.invest;

import org.springframework.stereotype.Service;

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
import com.banksalad.collectmydata.invest.account.dto.AccountBasic;
import com.banksalad.collectmydata.invest.account.dto.AccountProduct;
import com.banksalad.collectmydata.invest.account.dto.AccountTransaction;
import com.banksalad.collectmydata.invest.account.dto.GetAccountBasicRequest;
import com.banksalad.collectmydata.invest.account.dto.ListAccountProductsRequest;
import com.banksalad.collectmydata.invest.account.dto.ListAccountTransactionsRequest;
import com.banksalad.collectmydata.invest.collect.Executions;
import com.banksalad.collectmydata.invest.common.dto.InvestApiResponse;
import com.banksalad.collectmydata.invest.summary.dto.AccountSummary;
import com.banksalad.collectmydata.invest.summary.dto.ListAccountSummariesRequest;
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
public class InvestApiServiceImpl implements InvestApiService {

  private final SummaryService<ListAccountSummariesRequest, AccountSummary> accountSummaryService;
  private final SummaryRequestHelper<ListAccountSummariesRequest> accountSummaryRequestHelper;
  private final SummaryResponseHelper<AccountSummary> accountSummaryResponseHelper;

  private final AccountInfoService<AccountSummary, GetAccountBasicRequest, AccountBasic> accountBasicInfoService;
  private final AccountInfoRequestHelper<GetAccountBasicRequest, AccountSummary> accountBasicInfoRequestHelper;
  private final AccountInfoResponseHelper<AccountSummary, AccountBasic> accountBasicInfoResponseHelper;

  private final TransactionApiService<AccountSummary, ListAccountTransactionsRequest, AccountTransaction> accountTransactionApiService;
  private final TransactionRequestHelper<AccountSummary, ListAccountTransactionsRequest> accountTransactionRequestHelper;
  private final TransactionResponseHelper<AccountSummary, AccountTransaction> accountTransactionResponseHelper;

  private final AccountInfoService<AccountSummary, ListAccountProductsRequest, List<AccountProduct>> accountProductInfoService;
  private final AccountInfoRequestHelper<ListAccountProductsRequest, AccountSummary> accountProductInfoRequestHelper;
  private final AccountInfoResponseHelper<AccountSummary, List<AccountProduct>> accountProductInfoResponseHelper;

  @Override
  public InvestApiResponse requestApi(long banksaladUserId, String organizationId, String syncRequestId,
      SyncRequestType syncRequestType) throws ResponseNotOkException {

    ExecutionContext executionContext = ExecutionContext.builder()
        .banksaladUserId(banksaladUserId)
        .executionRequestId(UUID.randomUUID().toString())
        .organizationId(organizationId)
        .accessToken("fixme")
        .organizationHost("http://whatever")
        .syncStartedAt(LocalDateTime.now(DateUtil.UTC_ZONE_ID))
        .build();

    accountSummaryService
        .listAccountSummaries(executionContext, Executions.finance_invest_accounts, accountSummaryRequestHelper,
            accountSummaryResponseHelper);

    AtomicReference<InvestApiResponse> investApiResponseAtomicReference = new AtomicReference<>();
    investApiResponseAtomicReference.set(InvestApiResponse.builder().build());

    CompletableFuture.allOf(
        CompletableFuture.supplyAsync(() -> accountBasicInfoService
            .listAccountInfos(executionContext, Executions.finance_invest_account_basic, accountBasicInfoRequestHelper,
                accountBasicInfoResponseHelper)),

        CompletableFuture.supplyAsync(() -> accountTransactionApiService
            .listTransactions(executionContext, Executions.finance_invest_account_transactions,
                accountTransactionRequestHelper, accountTransactionResponseHelper)),

        CompletableFuture.supplyAsync(() -> accountProductInfoService
            .listAccountInfos(executionContext, Executions.finance_invest_account_products,
                accountProductInfoRequestHelper, accountProductInfoResponseHelper))
    ).join();

    return investApiResponseAtomicReference.get();
  }
}

