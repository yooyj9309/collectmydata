package com.banksalad.collectmydata.capital.common.service;

import org.springframework.stereotype.Service;

import com.banksalad.collectmydata.capital.common.collect.Executions;
import com.banksalad.collectmydata.capital.common.dto.Account;
import com.banksalad.collectmydata.capital.common.dto.AccountRequest;
import com.banksalad.collectmydata.capital.common.dto.AccountResponse;
import com.banksalad.collectmydata.capital.common.dto.Organization;
import com.banksalad.collectmydata.capital.common.util.ExecutionUtil;
import com.banksalad.collectmydata.capital.loan.dto.LoanAccountBasicRequest;
import com.banksalad.collectmydata.capital.loan.dto.LoanAccountBasicResponse;
import com.banksalad.collectmydata.capital.loan.dto.LoanAccountDetailRequest;
import com.banksalad.collectmydata.capital.loan.dto.LoanAccountDetailResponse;
import com.banksalad.collectmydata.capital.loan.dto.LoanAccountTransactionRequest;
import com.banksalad.collectmydata.capital.loan.dto.LoanAccountTransactionResponse;
import com.banksalad.collectmydata.capital.oplease.dto.OperatingLeaseBasicRequest;
import com.banksalad.collectmydata.capital.oplease.dto.OperatingLeaseBasicResponse;
import com.banksalad.collectmydata.capital.oplease.dto.OperatingLeaseTransactionRequest;
import com.banksalad.collectmydata.capital.oplease.dto.OperatingLeaseTransactionResponse;
import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.collect.execution.ExecutionRequest;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

import static com.banksalad.collectmydata.capital.common.collect.Executions.capital_get_account_detail;
import static com.banksalad.collectmydata.capital.common.collect.Executions.capital_get_accounts;
import static com.banksalad.collectmydata.capital.common.collect.Executions.capital_get_operating_lease_basic;
import static com.banksalad.collectmydata.capital.common.collect.Executions.capital_get_operating_lease_transactions;

@Service
@RequiredArgsConstructor
public class ExternalApiServiceImpl implements ExternalApiService {

  private final ExecutionService executionService;
  private static final String AUTHORIZATION = "Authorization";
  //FIXME
  //  Change MAX_LIMIT to 500
  private static final int MAX_LIMIT = 2;

  @Override
  public AccountResponse getAccounts(ExecutionContext executionContext, String orgCode, long searchTimeStamp) {
    // executionId 생성.
    executionContext.generateAndsUpdateExecutionRequestId();

    Map<String, String> headers = Map.of(AUTHORIZATION, executionContext.getAccessToken());
    AccountRequest accountRequest = AccountRequest.builder()
        .searchTimestamp(searchTimeStamp)
        .orgCode(orgCode)
        .build();

    ExecutionRequest<AccountRequest> executionRequest = ExecutionUtil
        .executionRequestAssembler(headers, accountRequest);

    return executionService.execute(executionContext, capital_get_accounts, executionRequest);
  }

  @Override
  public LoanAccountBasicResponse getAccountBasic(ExecutionContext executionContext, Organization organization,
      Account account) {
    // executionId 생성.
    executionContext.generateAndsUpdateExecutionRequestId();

    Map<String, String> headers = Map.of(AUTHORIZATION, executionContext.getAccessToken());
    LoanAccountBasicRequest request = LoanAccountBasicRequest.builder()
        .orgCode(organization.getOrganizationCode())
        .accountNum(account.getAccountNum())
        .seqno(account.getSeqno())
        .searchTimestamp(0L) // TODO
        .build();

    ExecutionRequest<LoanAccountBasicRequest> executionRequest = ExecutionUtil
        .executionRequestAssembler(headers, request);

    return executionService.execute(executionContext, Executions.capital_get_account_basic, executionRequest);
  }

  @Override
  public LoanAccountDetailResponse getAccountDetail(ExecutionContext executionContext, Organization organization,
      Account account) {
    // executionId 생성.
    executionContext.generateAndsUpdateExecutionRequestId();

    Map<String, String> headers = Map.of(AUTHORIZATION, executionContext.getAccessToken());
    LoanAccountDetailRequest loanAccountDetailRequest = LoanAccountDetailRequest.builder()
        .orgCode(organization.getOrganizationCode())
        .accountNum(account.getAccountNum())
        .seqno(account.getSeqno())
        .searchTimestamp(0L) // TODO
        .build();

    ExecutionRequest<LoanAccountDetailRequest> executionRequest = ExecutionUtil
        .executionRequestAssembler(headers, loanAccountDetailRequest);

    return executionService.execute(executionContext, capital_get_account_detail, executionRequest);
  }

  @Override
  public LoanAccountTransactionResponse getAccountTransactions(ExecutionContext executionContext,
      Organization organization,
      Account account) {
    // executionId 생성.
    executionContext.generateAndsUpdateExecutionRequestId();

    Map<String, String> header = Map.of("Authorization", executionContext.getAccessToken());
    LoanAccountTransactionRequest request = LoanAccountTransactionRequest.builder()
        .orgCode(organization.getOrganizationCode())
        .accountNum(account.getAccountNum())
        .seqno(account.getSeqno())
        .fromDtime("20210121000000") // fixme : user_sync_stat.synced_at
        .toDtime("20210122000000") // fixme : kstCurrentDatetime(); // a new method of util.DateUtil
        .limit(MAX_LIMIT)
        .build();
    ExecutionRequest<LoanAccountTransactionRequest> executionRequest = ExecutionUtil
        .executionRequestAssembler(header, request);
    LoanAccountTransactionResponse response = LoanAccountTransactionResponse.builder()
        .nextPage(null)
        .transCnt(0)
        .transList(new ArrayList<>())
        .build();

    //TODO
    //  Change to flex-like instead of do-while.
    do {
      LoanAccountTransactionResponse page = executionService
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

  @Override
  public OperatingLeaseBasicResponse getOperatingLeaseBasic(ExecutionContext executionContext,
      Organization organization, Account account) {
    // executionId 생성.
    executionContext.generateAndsUpdateExecutionRequestId();

    Map<String, String> headers = Map.of(AUTHORIZATION, executionContext.getAccessToken());
    OperatingLeaseBasicRequest request = OperatingLeaseBasicRequest.builder()
        .orgCode(organization.getOrganizationCode())
        .accountNum(account.getAccountNum())
        .seqno(account.getSeqno())
        .searchTimestamp(account.getOperatingLeaseBasicSearchTimestamp())
        .build();

    ExecutionRequest<OperatingLeaseBasicRequest> executionRequest = ExecutionUtil
        .executionRequestAssembler(headers, request);

    return executionService.execute(executionContext, capital_get_operating_lease_basic, executionRequest);
  }

  @Override
  public OperatingLeaseTransactionResponse getOperatingLeaseTransactions(ExecutionContext executionContext,
      Organization organization, Account account) {
    // executionId 생성.
    executionContext.generateAndsUpdateExecutionRequestId();

    Map<String, String> headers = Map.of(AUTHORIZATION, executionContext.getAccessToken());

    OperatingLeaseTransactionResponse response = OperatingLeaseTransactionResponse.builder().build();
    OperatingLeaseTransactionRequest request = OperatingLeaseTransactionRequest.builder()
        .orgCode(organization.getOrganizationCode())
        .accountNum(account.getAccountNum())
        .seqno(account.getSeqno())
        .fromDtime("20210121000000") // fixme : user_sync_stat.synced_at
        .toDtime("20210122000000") // fixme : kstCurrentDatetime
        .limit(MAX_LIMIT)
        .build();
    do {
      ExecutionRequest<OperatingLeaseTransactionRequest> executionRequest = ExecutionUtil
          .executionRequestAssembler(headers, request);
      OperatingLeaseTransactionResponse pageResponse = executionService
          .execute(executionContext, capital_get_operating_lease_transactions, executionRequest);

      response.updateFrom(pageResponse);
      request.updateNextPage(pageResponse.getNextPage());
    } while (Objects.nonNull(response.getNextPage()));

    return response;
  }
}
