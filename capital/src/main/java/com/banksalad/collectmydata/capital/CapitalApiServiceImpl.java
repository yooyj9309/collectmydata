package com.banksalad.collectmydata.capital;

import com.banksalad.collectmydata.capital.account.dto.AccountDetail;
import com.banksalad.collectmydata.capital.account.dto.GetAccountDetailRequest;
import com.banksalad.collectmydata.capital.collect.Executions;
import com.banksalad.collectmydata.capital.common.dto.CapitalApiResponse;
import com.banksalad.collectmydata.capital.common.dto.Organization;
import com.banksalad.collectmydata.capital.grpc.client.CollectmydataConnectClientService;
import com.banksalad.collectmydata.capital.summary.dto.AccountSummary;
import com.banksalad.collectmydata.capital.summary.dto.ListAccountSummariesRequest;
import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.finance.api.accountinfo.AccountInfoRequestHelper;
import com.banksalad.collectmydata.finance.api.accountinfo.AccountInfoResponseHelper;
import com.banksalad.collectmydata.finance.api.accountinfo.AccountInfoService;
import com.banksalad.collectmydata.finance.api.summary.SummaryRequestHelper;
import com.banksalad.collectmydata.finance.api.summary.SummaryResponseHelper;
import com.banksalad.collectmydata.finance.api.summary.SummaryService;
import com.banksalad.collectmydata.finance.common.exception.ResponseNotOkException;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Service
@RequiredArgsConstructor
public class CapitalApiServiceImpl implements CapitalApiService {

  private final SummaryService<ListAccountSummariesRequest, AccountSummary> summaryService;
  private final SummaryRequestHelper<ListAccountSummariesRequest> summaryRequestHelper;
  private final SummaryResponseHelper<AccountSummary> summaryResponseHelper;

  private final AccountInfoService<AccountSummary, GetAccountDetailRequest, AccountDetail> accountDetailService;
  private final AccountInfoRequestHelper<GetAccountDetailRequest, AccountSummary> accountDetailRequestHelper;
  private final AccountInfoResponseHelper<AccountSummary, AccountDetail> accountDetailResponseHelper;

  private final CollectmydataConnectClientService collectmydataConnectClientService;

  /**
   * kafka consumer 에서 호출, 최초 API를 연동하는 서비스
   *
   * @param banksaladUserId
   * @param organizationId
   * @param syncRequestId
   */
  @Override
  public CapitalApiResponse requestApi(long banksaladUserId, String organizationId, String syncRequestId)
      throws ResponseNotOkException {
    Organization organization = collectmydataConnectClientService.getOrganization(organizationId);
    String accessToken = "fixme"; //TODO 토큰 조회 로직 추가하여 적용

    ExecutionContext executionContext = ExecutionContext.builder()
        .organizationId(organizationId)
        .banksaladUserId(banksaladUserId)
        .accessToken(accessToken)
        .organizationHost(organization.getDomain())
        .syncStartedAt(LocalDateTime.now())
        .build();

    summaryService.listAccountSummaries(
        executionContext, Executions.capital_get_accounts, summaryRequestHelper, summaryResponseHelper);

    AtomicReference<CapitalApiResponse> atomicReference = new AtomicReference<>();
    atomicReference.set(CapitalApiResponse.builder().build());

    CompletableFuture.allOf(
//        CompletableFuture
//            .supplyAsync(
//                () -> accountService.listAccountBasics(executionContext, organization, otherAccountSummaries))
//            .thenAccept(atomicReference.get()::setAccountBasics),
//
        CompletableFuture
            .supplyAsync(
                () -> accountDetailService.listAccountInfos(executionContext, Executions.capital_get_account_detail,
                    accountDetailRequestHelper, accountDetailResponseHelper))
            .thenAccept(atomicReference.get()::setAccountDetails)
//
//        CompletableFuture
//            .supplyAsync(
//                () -> accountService.listAccountTransactions(executionContext, organization, otherAccountSummaries))
//            .thenAccept(atomicReference.get()::setAccountTransactions),
//
//        CompletableFuture
//            .supplyAsync(
//                () -> operatingLeaseService.listOperatingLeases(executionContext, organization, leaseAccountSummaries))
//            .thenAccept(atomicReference.get()::setOperatingLeases),
//
//        CompletableFuture
//            .supplyAsync(() -> operatingLeaseService
//                .listOperatingLeaseTransactions(executionContext, organization, leaseAccountSummaries))
//            .thenAccept(atomicReference.get()::setOperatingLeasesTransactions)
    ).join();

    return atomicReference.get();
  }
}
