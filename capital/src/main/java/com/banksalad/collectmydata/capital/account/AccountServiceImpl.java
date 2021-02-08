package com.banksalad.collectmydata.capital.account;

import org.springframework.stereotype.Service;

import com.banksalad.collectmydata.capital.account.dto.Account;
import com.banksalad.collectmydata.capital.account.dto.AccountRequest;
import com.banksalad.collectmydata.capital.account.dto.AccountResponse;
import com.banksalad.collectmydata.capital.common.collect.Executions;
import com.banksalad.collectmydata.capital.common.dto.Organization;
import com.banksalad.collectmydata.capital.common.service.ExecutionService;
import com.banksalad.collectmydata.capital.common.util.ExecutionUtil;
import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.collect.execution.ExecutionRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

  private final ExecutionService executionService;

  public static final String AUTHORIZATION = "Authorization";

  @Override
  public void syncAccounts(ExecutionContext executionContext, Organization organization) {

    Long banksaladUserId = Long.valueOf(executionContext.getBanksaladUserId());
    String organizationId = executionContext.getOrganizationId();

    List<Account> accounts = getAccount(executionContext, organization);
    // 6.7.1 기본 조회

    // 6.7.2 조회
    // 6.7.3 조회
    //account 머지.

    // db 저장
    // userSyncStatus 저장.
  }

  public List<Account> getAccount(ExecutionContext executionContext, Organization organization) {
    String orgCode = organization.getOrganizationCode();
    long timestamp = 0L; // TODO api별 timestamp적재 테이블 확인후 entity 적용 및 수정

    Map<String, String> headers = Map.of(AUTHORIZATION, executionContext.getAccessToken());

    AccountRequest accountRequest = AccountRequest.builder()
        .searchTimestamp(timestamp)
        .orgCode(orgCode)
        .build();

    ExecutionRequest<AccountRequest> executionRequest = ExecutionUtil
        .executionRequestAssembler(headers, accountRequest);

    AccountResponse response = executionService.execute(
        executionContext,
        Executions.capital_get_accounts,
        executionRequest
    );
    return Optional.ofNullable(response.getAccountList()).orElse(List.of());
  }

}
