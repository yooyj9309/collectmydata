package com.banksalad.collectmydata.capital.common.service;

import org.springframework.stereotype.Service;

import com.banksalad.collectmydata.capital.account.dto.Account;
import com.banksalad.collectmydata.capital.account.dto.AccountDetailRequest;
import com.banksalad.collectmydata.capital.account.dto.AccountDetailResponse;
import com.banksalad.collectmydata.capital.account.dto.AccountBasicRequest;
import com.banksalad.collectmydata.capital.account.dto.AccountBasicResponse;
import com.banksalad.collectmydata.capital.account.dto.AccountRequest;
import com.banksalad.collectmydata.capital.account.dto.AccountResponse;
import com.banksalad.collectmydata.capital.common.dto.Organization;
import com.banksalad.collectmydata.capital.common.util.ExecutionUtil;
import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.collect.execution.ExecutionRequest;
import lombok.RequiredArgsConstructor;

import java.util.Map;

import static com.banksalad.collectmydata.capital.common.collect.Executions.*;

@Service
@RequiredArgsConstructor
public class ExternalApiServiceImpl implements ExternalApiService {

  private final ExecutionService executionService;
  private static final String AUTHORIZATION = "Authorization";

  @Override
  public AccountResponse getAccounts(ExecutionContext executionContext, Organization organization) {
    Map<String, String> headers = Map.of(AUTHORIZATION, executionContext.getAccessToken());
    AccountRequest accountRequest = AccountRequest.builder()
        .searchTimestamp(0L) // TODO
        .orgCode(organization.getOrganizationCode())
        .build();

    ExecutionRequest<AccountRequest> executionRequest = ExecutionUtil
        .executionRequestAssembler(headers, accountRequest);

    return executionService.execute(executionContext, capital_get_accounts, executionRequest);
  }

  @Override
  public AccountBasicResponse getAccountBasic(ExecutionContext executionContext, Organization organization,
      Account account) {
    Map<String, String> headers = Map.of(AUTHORIZATION, executionContext.getAccessToken());
    AccountBasicRequest request = AccountBasicRequest.builder()
        .orgCode(organization.getOrganizationCode())
        .accountNum(account.getAccountNum())
        .seqno(account.getSeqno())
        .searchTimestamp(0L) // TODO
        .build();

    ExecutionRequest<AccountBasicRequest> executionRequest = ExecutionUtil
        .executionRequestAssembler(headers, request);

    return executionService.execute(executionContext, capital_get_account_basic, executionRequest);
  }

  @Override
  public AccountDetailResponse getAccountDetail(ExecutionContext executionContext, Organization organization,
      Account account) {
    Map<String, String> headers = Map.of(AUTHORIZATION, executionContext.getAccessToken());
    AccountDetailRequest accountDetailRequest = AccountDetailRequest.builder()
        .orgCode(organization.getOrganizationCode())
        .accountNum(account.getAccountNum())
        .seqno(account.getSeqno())
        .searchTimestamp(0L) // TODO
        .build();

    ExecutionRequest<AccountDetailRequest> executionRequest = ExecutionUtil
        .executionRequestAssembler(headers, accountDetailRequest);

    return executionService.execute(executionContext, capital_get_account_detail, executionRequest);
  }
}
