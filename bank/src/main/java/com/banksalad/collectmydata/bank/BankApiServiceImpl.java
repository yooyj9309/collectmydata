package com.banksalad.collectmydata.bank;

import org.springframework.stereotype.Service;

import com.banksalad.collectmydata.bank.common.collect.Executions;
import com.banksalad.collectmydata.bank.common.dto.BankApiResponse;
import com.banksalad.collectmydata.bank.invest.dto.GetInvestAccountBasicRequest;
import com.banksalad.collectmydata.bank.invest.dto.GetInvestAccountDetailRequest;
import com.banksalad.collectmydata.bank.invest.dto.InvestAccountBasic;
import com.banksalad.collectmydata.bank.invest.dto.InvestAccountDetail;
import com.banksalad.collectmydata.bank.invest.dto.InvestAccountTransaction;
import com.banksalad.collectmydata.bank.invest.dto.ListInvestAccountTransactionsRequest;
import com.banksalad.collectmydata.bank.summary.dto.AccountSummary;
import com.banksalad.collectmydata.bank.summary.dto.ListAccountSummariesRequest;
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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Service
@RequiredArgsConstructor
public class BankApiServiceImpl implements BankApiService {

  private final SummaryService<ListAccountSummariesRequest, AccountSummary> accountSummaryService;
  private final SummaryRequestHelper<ListAccountSummariesRequest> summaryRequestHelper;
  private final SummaryResponseHelper<AccountSummary> summaryResponseHelper;

  // DEPOSIT
  // INVEST
  private final AccountInfoService<AccountSummary, GetInvestAccountBasicRequest, InvestAccountBasic> investAccountBasicApiService;
  private final AccountInfoService<AccountSummary, GetInvestAccountDetailRequest, InvestAccountDetail> investAccountDetailApiService;
  private final TransactionApiService<AccountSummary, ListInvestAccountTransactionsRequest, InvestAccountTransaction> investTransactionApiService;

  private final AccountInfoRequestHelper<GetInvestAccountBasicRequest, AccountSummary> investAccountBasicInfoRequestHelper;
  private final AccountInfoResponseHelper<AccountSummary, InvestAccountBasic> investAccountInfoBasicResponseHelper;

  private final AccountInfoRequestHelper<GetInvestAccountDetailRequest, AccountSummary> investAccountDetailInfoRequestHelper;
  private final AccountInfoResponseHelper<AccountSummary, InvestAccountDetail> investAccountDetailInfoResponseHelper;

  private final TransactionRequestHelper<AccountSummary, ListInvestAccountTransactionsRequest> investAccountTransactionRequestHelper;
  private final TransactionResponseHelper<AccountSummary, InvestAccountTransaction> investAccountTransactionResponseHelper;

  // LOAN
  @Override
  public BankApiResponse requestApi(long banksaladUserId, String organizationId, String syncRequestId,
      SyncRequestType syncRequestType) throws ResponseNotOkException {

    // TODO jayden-lee organizaion service 호출 해서 Organization 정보 가져 오기

    ExecutionContext executionContext = ExecutionContext.builder()
        .banksaladUserId(banksaladUserId)
        .executionRequestId(UUID.randomUUID().toString())
        .organizationId(organizationId)
        .accessToken("fixme")
        .organizationHost("http://whatever")
        .syncStartedAt(LocalDateTime.now(DateUtil.UTC_ZONE_ID))
        .build();

    accountSummaryService.listAccountSummaries(executionContext, Executions.finance_bank_summaries,
        summaryRequestHelper, summaryResponseHelper);

    AtomicReference<BankApiResponse> bankApiResponseAtomicReference = new AtomicReference<>();
    bankApiResponseAtomicReference.set(BankApiResponse.builder().build());

    CompletableFuture.allOf(
        // TODO 각 계좌별 서비스 호출 하고 응답을 bankApiResponse 에 저장
        CompletableFuture.supplyAsync(() -> investAccountBasicApiService.listAccountInfos(
            executionContext, Executions.finance_bank_invest_account_basic, investAccountBasicInfoRequestHelper,
            investAccountInfoBasicResponseHelper)),
        CompletableFuture.supplyAsync(() -> investAccountDetailApiService.listAccountInfos(
            executionContext, Executions.finance_bank_invest_account_detail, investAccountDetailInfoRequestHelper,
            investAccountDetailInfoResponseHelper)),
        CompletableFuture.supplyAsync(
            () -> investTransactionApiService.listTransactions(
                executionContext, Executions.finance_bank_invest_account_transaction,
                investAccountTransactionRequestHelper, investAccountTransactionResponseHelper))
            .thenAccept(investAccountTransactions -> bankApiResponseAtomicReference.get()
                .setInvestAccountTransactions(investAccountTransactions))
    ).join();

    return bankApiResponseAtomicReference.get();
  }
}
