package com.banksalad.collectmydata.capital;

import org.springframework.stereotype.Service;

import com.banksalad.collectmydata.capital.account.AccountService;
import com.banksalad.collectmydata.capital.common.dto.CapitalApiResponse;
import com.banksalad.collectmydata.capital.common.dto.Organization;
import com.banksalad.collectmydata.capital.common.service.AccountSummaryService;
import com.banksalad.collectmydata.capital.grpc.client.CollectmydataConnectClientService;
import com.banksalad.collectmydata.capital.oplease.OperatingLeaseService;
import com.banksalad.collectmydata.capital.summary.dto.AccountSummary;
import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Service
@RequiredArgsConstructor
public class CapitalApiServiceImpl implements CapitalApiService {

  private final AccountSummaryService accountSummaryService;
  private final AccountService accountService;
  private final CollectmydataConnectClientService collectmydataConnectClientService;
  private final OperatingLeaseService operatingLeaseService;

  private static final String OPERATING_LEASE_ACCOUNT_TYPE = "3710";

  /**
   * kafka consumer 에서 호출, 최초 API를 연동하는 서비스
   *
   * @param banksaladUserId
   * @param organizationId
   * @param syncRequestId
   */
  @Override
  public CapitalApiResponse requestApi(long banksaladUserId, String organizationId, String syncRequestId) {
    Organization organization = collectmydataConnectClientService.getOrganization(organizationId);
    String accessToken = "fixme"; //TODO 토큰 조회 로직 추가하여 적용

    ExecutionContext executionContext = ExecutionContext.builder()
        .organizationId(organizationId)
        .banksaladUserId(banksaladUserId)
        .accessToken(accessToken)
        .organizationHost(organization.getDomain())
        .syncStartedAt(LocalDateTime.now())
        .build();

    //TODO service 제거, getAccountType따라 서비스 가져오도록 수정.
//    List<AccountSummary> leaseAccountSummaries = accountSummaries.stream()
//        .filter(account -> OPERATING_LEASE_ACCOUNT_TYPE.equals(account.getAccountType()) && account.getIsConsent())
//        .collect(Collectors.toList());
//    List<AccountSummary> otherAccountSummaries = accountSummaries.stream()
//        .filter(account -> !OPERATING_LEASE_ACCOUNT_TYPE.equals(account.getAccountType()) && account.getIsConsent())
//        .collect(Collectors.toList());

    AtomicReference<CapitalApiResponse> atomicReference = new AtomicReference<>();
    atomicReference.set(CapitalApiResponse.builder().build());

    CompletableFuture.allOf(
//        CompletableFuture
//            .supplyAsync(
//                () -> accountService.listAccountBasics(executionContext, organization, otherAccountSummaries))
//            .thenAccept(atomicReference.get()::setAccountBasics),
//
//        CompletableFuture
//            .supplyAsync(
//                () -> accountService.listAccountDetails(executionContext, organization, otherAccountSummaries))
//            .thenAccept(atomicReference.get()::setAccountDetails),
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
