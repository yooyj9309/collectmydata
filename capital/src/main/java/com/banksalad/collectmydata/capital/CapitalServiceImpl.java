package com.banksalad.collectmydata.capital;

import org.springframework.stereotype.Service;

import com.banksalad.collectmydata.capital.common.service.AccountService;
import com.banksalad.collectmydata.capital.loan.LoanAccountService;
import com.banksalad.collectmydata.capital.common.dto.Account;
import com.banksalad.collectmydata.capital.common.dto.Organization;
import com.banksalad.collectmydata.capital.grpc.client.CollectmydataConnectClientService;
import com.banksalad.collectmydata.capital.oplease.OperatingLeaseService;
import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.logging.CollectLogbackJsonLayout;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CapitalServiceImpl implements CapitalService {

  private final AccountService accountService;
  private final LoanAccountService loanAccountService;
  private final CollectmydataConnectClientService collectmydataConnectClientService;
  private final OperatingLeaseService operatingLeaseService;
  private final CapitalPublishService capitalPublishService;

  private static final String OPERATING_LEASE_TYPE = "3710";

  /**
   * kafka consumer 에서 호출, 최초 API를 연동하는 서비스
   *
   * @param banksaladUserId
   * @param organizationId
   */
  @Override
  public void sync(long banksaladUserId, String organizationId, String syncRequestId) {

    try {
      // Organization 정보 조히 ( domain setting, scope조회를 위해서라도 필요)
      // 토큰갱신
      // ExecutionContext 생성
      // 서비스 호출

      // TODO organization idl 추가 후 로직수정 및 token logic 추가 후, null변경
      Organization organization = null;
      String accessToken = null;

      ExecutionContext executionContext = ExecutionContext.builder()
          .organizationId(organizationId)
          .banksaladUserId(banksaladUserId)
          .accessToken(accessToken)
          .organizationHost(organization.getDomain())
          .syncStartedAt(LocalDateTime.now())
          .build();

      MDC.put(CollectLogbackJsonLayout.JSON_KEY_BANKSALAD_USER_ID, String.valueOf(banksaladUserId));
      MDC.put(CollectLogbackJsonLayout.JSON_KEY_ORGANIZATION_ID, organizationId);
      log.info("CapitalService.sync start");

      List<Account> accounts = accountService.listAccounts(executionContext, organization);

      // * 6.7.5 조회 ,6.7.6 (운용리스) 이쪽도 비동기로 해야하는가.
      List<Account> operatingLeaseAccounts = accounts.stream()
          .filter(account -> account.getAccountType().equals(OPERATING_LEASE_TYPE))
          .collect(Collectors.toList());
      operatingLeaseService.listOperatingLeases(executionContext, organization, operatingLeaseAccounts);
      operatingLeaseService.listOperatingLeaseTransactions(executionContext, organization, operatingLeaseAccounts);

      List<Account> anotherAccounts = accounts.stream()
          .filter(account -> !account.getAccountType().equals(OPERATING_LEASE_TYPE))
          .collect(Collectors.toList());
      loanAccountService.listLoanAccounts(executionContext, organization, anotherAccounts);
      loanAccountService.listAccountTransactions(executionContext, organization, anotherAccounts);

      // publish 서비스
      // client 리턴 -> 이부분 갑자기 기억이 안나네요, collect에 주기위해 다시 producer 하는게 맞는지..?
    } catch (Exception e) {
      log.error("Sync user error: {}", e.getMessage(), e);
    } finally {
      log.info("CollectbankGrpcService.syncUser end");
      MDC.clear();
    }
  }
}
