package com.banksalad.collectmydata.capital;

import com.banksalad.collectmydata.capital.account.AccountService;
import com.banksalad.collectmydata.capital.common.dto.AccountSummary;
import com.banksalad.collectmydata.capital.common.dto.CapitalApiResponse;
import com.banksalad.collectmydata.capital.common.dto.CapitalApiResponse.CapitalApiResponseBuilder;
import com.banksalad.collectmydata.capital.common.dto.Organization;
import com.banksalad.collectmydata.capital.common.service.AccountSummaryService;
import com.banksalad.collectmydata.capital.grpc.client.CollectmydataConnectClientService;
import com.banksalad.collectmydata.capital.oplease.OperatingLeaseService;
import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CapitalApiServiceImpl implements CapitalApiService {

  private final AccountSummaryService accountSummaryService;
  private final AccountService accountService;
  private final CollectmydataConnectClientService collectmydataConnectClientService;
  private final OperatingLeaseService operatingLeaseService;
  private final CapitalPublishService capitalPublishService;

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

    List<AccountSummary> accountSummaries = accountSummaryService.listAccountSummaries(executionContext, organization);

    CapitalApiResponseBuilder capitalApiResponseBuilder = CapitalApiResponse.builder();

    // 이부분을 비동기로 진행하는경우, executionRequestId를 덮어쓰는 문제 발생. 해당부분 해결후 수정.
    List<AccountSummary> operatingLeaseAccountSummaries = accountSummaries.stream()
        .filter(account -> OPERATING_LEASE_ACCOUNT_TYPE.equals(account.getAccountType()) && account.getIsConsent())
        .collect(Collectors.toList());
    capitalApiResponseBuilder.operatingLeases(
        operatingLeaseService.listOperatingLeases(executionContext, organization, operatingLeaseAccountSummaries));
    capitalApiResponseBuilder.operatingLeasesTransactions(
        operatingLeaseService.listOperatingLeaseTransactions(executionContext, organization,
            operatingLeaseAccountSummaries));

    List<AccountSummary> anotherAccountSummaries = accountSummaries.stream()
        .filter(account -> !OPERATING_LEASE_ACCOUNT_TYPE.equals(account.getAccountType()) && account.getIsConsent())
        .collect(Collectors.toList());
//    capitalApiResponseBuilder.loanAccounts(
//        accountService.listLoanAccounts(executionContext, organization, anotherAccountSummaries));
    capitalApiResponseBuilder.accountTransactions(
        accountService.listAccountTransactions(executionContext, organization, anotherAccountSummaries));

    return capitalApiResponseBuilder.build();
  }
}
