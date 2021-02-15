package com.banksalad.collectmydata.capital.common.service;

import com.banksalad.collectmydata.capital.account.dto.Account;
import com.banksalad.collectmydata.capital.account.dto.AccountBasicRequest;
import com.banksalad.collectmydata.capital.account.dto.AccountBasicResponse;
import com.banksalad.collectmydata.capital.account.dto.AccountDetailRequest;
import com.banksalad.collectmydata.capital.account.dto.AccountDetailResponse;
import com.banksalad.collectmydata.capital.account.dto.AccountRequest;
import com.banksalad.collectmydata.capital.account.dto.AccountResponse;
import com.banksalad.collectmydata.capital.account.dto.AccountTransactionRequest;
import com.banksalad.collectmydata.capital.account.dto.AccountTransactionResponse;
import com.banksalad.collectmydata.capital.common.collect.Executions;
import com.banksalad.collectmydata.capital.common.dto.Organization;
import com.banksalad.collectmydata.capital.common.util.ExecutionUtil;
import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.collect.execution.ExecutionRequest;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.Map;

import static com.banksalad.collectmydata.capital.common.collect.Executions.capital_get_account_detail;
import static com.banksalad.collectmydata.capital.common.collect.Executions.capital_get_accounts;

@Service
@RequiredArgsConstructor
public class ExternalApiServiceImpl implements ExternalApiService {

  private final ExecutionService executionService;
  private static final String AUTHORIZATION = "Authorization";
  //FIXME
  //  Change MAX_LIMIT to 500
  private static final int MAX_LIMIT = 2;

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

    return executionService.execute(executionContext, Executions.capital_get_account_basic, executionRequest);
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

  @Override
  public AccountTransactionResponse getAccountTransactions(ExecutionContext executionContext, Organization organization,
      Account account) {
    Map<String, String> header = Map.of("Authorization", executionContext.getAccessToken());

    //FIXME
    //  fromDate = user_sync_stat.synced_at
    //  toDate = kstCurrentDatetime(); // a new method of util.DateUtil
    String fromDate = "20210121000000";
    String toDate = "20210122000000";
    AccountTransactionRequest request = AccountTransactionRequest.builder()
        .orgCode(organization.getOrganizationCode())
        .accountNum(account.getAccountNum())
        .fromDtime(fromDate)
        .toDtime(toDate)
        .limit(MAX_LIMIT)
        .build();
    ExecutionRequest<AccountTransactionRequest> executionRequest = ExecutionUtil
        .executionRequestAssembler(header, request);
    AccountTransactionResponse response = AccountTransactionResponse.builder()
        .nextPage(null)
        .transCnt(0)
        .transList(new ArrayList<>())
        .build();

    //TODO
    //  Change to flex-like instead of do-while.
    do {
      AccountTransactionResponse page = executionService
          .execute(executionContext, Executions.capital_get_account_transactions, executionRequest);
      response.setRspCode(page.getRspCode());
      response.setRspMsg(page.getRspMsg());
      response.setNextPage(page.getNextPage());
      response.setTransCnt(response.getTransCnt() + page.getTransCnt());
      response.getTransList().addAll(page.getTransList());
      executionRequest.getRequest().setNextPage(page.getNextPage());
    } while (response.getNextPage() != null);

    return response;
  }
}
