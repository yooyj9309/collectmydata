package com.banksalad.collectmydata.referencebank;

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
import com.banksalad.collectmydata.finance.common.dto.OauthToken;
import com.banksalad.collectmydata.finance.common.dto.Organization;
import com.banksalad.collectmydata.finance.common.exception.ResponseNotOkException;
import com.banksalad.collectmydata.finance.common.grpc.CollectmydataConnectClientService;
import com.banksalad.collectmydata.referencebank.collect.Executions;
import com.banksalad.collectmydata.referencebank.common.dto.BankApiResponse;
import com.banksalad.collectmydata.referencebank.deposit.dto.DepositAccountBasic;
import com.banksalad.collectmydata.referencebank.deposit.dto.DepositAccountDetail;
import com.banksalad.collectmydata.referencebank.deposit.dto.DepositAccountTransaction;
import com.banksalad.collectmydata.referencebank.deposit.dto.GetDepositAccountBasicRequest;
import com.banksalad.collectmydata.referencebank.deposit.dto.GetDepositAccountDetailRequest;
import com.banksalad.collectmydata.referencebank.deposit.dto.ListDepositAccountTransactionsRequest;
import com.banksalad.collectmydata.referencebank.summary.dto.AccountSummary;
import com.banksalad.collectmydata.referencebank.summary.dto.ListAccountSummariesRequest;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Service
@RequiredArgsConstructor
public class BankApiServiceImpl implements BankApiService {

  private final CollectmydataConnectClientService connectClientService;

  private final SummaryService<ListAccountSummariesRequest, AccountSummary> accountSummaryService;
  private final AccountInfoService<AccountSummary, GetDepositAccountBasicRequest, DepositAccountBasic> depositAccountBasicApiService;
  private final AccountInfoService<AccountSummary, GetDepositAccountDetailRequest, List<DepositAccountDetail>> depositAccountDetailApiService;
  private final TransactionApiService<AccountSummary, ListDepositAccountTransactionsRequest, DepositAccountTransaction> depositTransactionApiService;

  private final SummaryRequestHelper<ListAccountSummariesRequest> bankSummaryRequestHelper;
  private final SummaryResponseHelper<AccountSummary> bankSummaryResponseHelper;

  private final AccountInfoRequestHelper<GetDepositAccountBasicRequest, AccountSummary> depositAccountBasicInfoRequestHelper;
  private final AccountInfoResponseHelper<AccountSummary, DepositAccountBasic> depositAccountInfoBasicResponseHelper;

  private final AccountInfoRequestHelper<GetDepositAccountDetailRequest, AccountSummary> depositAccountDetailInfoRequestHelper;
  private final AccountInfoResponseHelper<AccountSummary, List<DepositAccountDetail>> depositAccountDetailInfoResponseHelper;

  private final TransactionRequestHelper<AccountSummary, ListDepositAccountTransactionsRequest> depositAccountTransactionRequestHelper;
  private final TransactionResponseHelper<AccountSummary, DepositAccountTransaction> depositAccountTransactionResponseHelper;

  @Override
  public BankApiResponse requestApi(long banksaladUserId, String organizationId, String syncRequestId,
      SyncRequestType syncRequestType) throws ResponseNotOkException {

    final OauthToken oauthToken = connectClientService.getAccessToken(banksaladUserId, organizationId);
    final Organization organization = connectClientService.getOrganization(organizationId);

    // TODO : Organization service
    ExecutionContext executionContext = ExecutionContext.builder()
        .banksaladUserId(banksaladUserId)
        .consentId(oauthToken.getConsentId())
        .syncRequestId(syncRequestId)
        .organizationId(organization.getOrganizationId())
        .organizationCode(organization.getOrganizationCode())
        .organizationHost(organization.getHostUrl())
        .accessToken(oauthToken.getAccessToken())
        .syncStartedAt(LocalDateTime.now(DateUtil.UTC_ZONE_ID))
        .build();

    AtomicReference<BankApiResponse> bankApiResponseAtomicReference = new AtomicReference<>();
    bankApiResponseAtomicReference.set(BankApiResponse.builder().build());

    accountSummaryService.listAccountSummaries(
        executionContext, Executions.finance_bank_summaries, bankSummaryRequestHelper, bankSummaryResponseHelper);

    // TODO : decide how to set response
    CompletableFuture.allOf(
        CompletableFuture.supplyAsync(() -> depositAccountBasicApiService.listAccountInfos(
            executionContext, Executions.finance_bank_deposit_account_basic, depositAccountBasicInfoRequestHelper,
            depositAccountInfoBasicResponseHelper))
//            .thenAccept(
//                depositAccountBasics -> bankApiResponseAtomicReference.get().setDepositAccountBasicss(depositAccountBasics))
        ,

        CompletableFuture.supplyAsync(() -> depositAccountDetailApiService.listAccountInfos(
            executionContext, Executions.finance_bank_deposit_account_detail, depositAccountDetailInfoRequestHelper,
            depositAccountDetailInfoResponseHelper))
//            .thenAccept(
//                depositAccountDetails -> bankApiResponseAtomicReference.get().setDepositAccountDetails(depositAccountDetails))
        ,

        CompletableFuture.supplyAsync(
            () -> depositTransactionApiService
                .listTransactions(executionContext, Executions.finance_bank_deposit_account_transaction,
                    depositAccountTransactionRequestHelper, depositAccountTransactionResponseHelper))
            .thenAccept(depositAccountTransactions -> bankApiResponseAtomicReference.get()
                .setDepositAccountTransactions(depositAccountTransactions))

    ).join();

    return bankApiResponseAtomicReference.get();
  }
}
