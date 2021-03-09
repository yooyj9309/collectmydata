package com.banksalad.collectmydata.capital.common.service;

import com.banksalad.collectmydata.capital.account.dto.AccountBasicRequest;
import com.banksalad.collectmydata.capital.account.dto.AccountBasicResponse;
import com.banksalad.collectmydata.capital.account.dto.AccountDetailRequest;
import com.banksalad.collectmydata.capital.account.dto.AccountDetailResponse;
import com.banksalad.collectmydata.capital.account.dto.AccountTransaction;
import com.banksalad.collectmydata.capital.account.dto.AccountTransactionRequest;
import com.banksalad.collectmydata.capital.account.dto.AccountTransactionResponse;
import com.banksalad.collectmydata.capital.common.collect.Executions;
import com.banksalad.collectmydata.capital.common.db.entity.mapper.AccountTransactionMapper;
import com.banksalad.collectmydata.capital.common.dto.AccountSummary;
import com.banksalad.collectmydata.capital.common.dto.AccountSummaryRequest;
import com.banksalad.collectmydata.capital.common.dto.AccountSummaryResponse;
import com.banksalad.collectmydata.capital.common.dto.Organization;
import com.banksalad.collectmydata.capital.common.util.ExecutionUtil;
import com.banksalad.collectmydata.capital.oplease.dto.OperatingLeaseBasicRequest;
import com.banksalad.collectmydata.capital.oplease.dto.OperatingLeaseBasicResponse;
import com.banksalad.collectmydata.capital.oplease.dto.OperatingLeaseTransactionRequest;
import com.banksalad.collectmydata.capital.oplease.dto.OperatingLeaseTransactionResponse;
import com.banksalad.collectmydata.common.collect.execution.Execution;
import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.collect.execution.ExecutionRequest;
import com.banksalad.collectmydata.common.collect.execution.ExecutionResponse;
import com.banksalad.collectmydata.common.collect.executor.CollectExecutor;
import com.banksalad.collectmydata.common.exception.CollectRuntimeException;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.banksalad.collectmydata.capital.common.collect.Executions.capital_get_account_detail;
import static com.banksalad.collectmydata.capital.common.collect.Executions.capital_get_accounts;
import static com.banksalad.collectmydata.capital.common.collect.Executions.capital_get_operating_lease_basic;
import static com.banksalad.collectmydata.capital.common.collect.Executions.capital_get_operating_lease_transactions;

@Service
@RequiredArgsConstructor
public class ExternalApiServiceImpl implements ExternalApiService {

  private final CollectExecutor collectExecutor;
  private static final String AUTHORIZATION = "Authorization";
  private static final int MAX_LIMIT = 2;

  @Override
  public AccountSummaryResponse getAccounts(ExecutionContext executionContext, String orgCode, long searchTimeStamp) {
    // executionId 생성.
    executionContext.generateAndsUpdateExecutionRequestId();

    Map<String, String> headers = Map.of(AUTHORIZATION, executionContext.getAccessToken());
    AccountSummaryRequest accountSummaryRequest = AccountSummaryRequest.builder()
        .searchTimestamp(searchTimeStamp)
        .orgCode(orgCode)
        .build();

    ExecutionRequest<AccountSummaryRequest> executionRequest = ExecutionUtil
        .executionRequestAssembler(headers, accountSummaryRequest);

    return execute(executionContext, capital_get_accounts, executionRequest);
  }

  @Override
  public AccountBasicResponse getAccountBasic(ExecutionContext executionContext, Organization organization,
      AccountSummary accountSummary) {
    // executionId 생성.
    executionContext.generateAndsUpdateExecutionRequestId();

    Map<String, String> headers = Map.of(AUTHORIZATION, executionContext.getAccessToken());
    AccountBasicRequest request = AccountBasicRequest.builder()
        .orgCode(organization.getOrganizationCode())
        .accountNum(accountSummary.getAccountNum())
        .seqno(accountSummary.getSeqno())
        .searchTimestamp(0L) // TODO
        .build();

    ExecutionRequest<AccountBasicRequest> executionRequest = ExecutionUtil
        .executionRequestAssembler(headers, request);

    return execute(executionContext, Executions.capital_get_account_basic, executionRequest);
  }

  @Override
  public AccountDetailResponse getAccountDetail(ExecutionContext executionContext, Organization organization,
      AccountSummary accountSummary) {
    // executionId 생성.
    executionContext.generateAndsUpdateExecutionRequestId();

    Map<String, String> headers = Map.of(AUTHORIZATION, executionContext.getAccessToken());
    AccountDetailRequest accountDetailRequest = AccountDetailRequest.builder()
        .orgCode(organization.getOrganizationCode())
        .accountNum(accountSummary.getAccountNum())
        .seqno(accountSummary.getSeqno())
        .searchTimestamp(0L) // TODO
        .build();

    ExecutionRequest<AccountDetailRequest> executionRequest = ExecutionUtil
        .executionRequestAssembler(headers, accountDetailRequest);

    return execute(executionContext, capital_get_account_detail, executionRequest);
  }

  @Override
  public AccountTransactionResponse getAccountTransactions(ExecutionContext executionContext,
      Organization organization, AccountSummary accountSummary) {
    // executionId 생성.
    executionContext.generateAndsUpdateExecutionRequestId();
    Map<String, String> header = Map.of("Authorization", executionContext.getAccessToken());
    AccountTransactionRequest request = AccountTransactionRequest.builder()
        .orgCode(organization.getOrganizationCode())
        .accountNum(accountSummary.getAccountNum())
        .seqno(accountSummary.getSeqno())
        .fromDate("20210121") // fixme : user_sync_stat.synced_at
        .toDate("20210122") // fixme : kstCurrentDatetime(); // a new method of util.DateUtil
        .limit(MAX_LIMIT)
        .build();
    ExecutionRequest<AccountTransactionRequest> executionRequest = ExecutionUtil
        .executionRequestAssembler(header, request);
    AccountTransactionResponse response = AccountTransactionResponse.builder()
        .nextPage(null)
        .transCnt(0)
        .transList(new ArrayList<>())
        .build();
    final AccountTransaction defaultTransaction = AccountTransaction.builder()
        .accountNum(accountSummary.getAccountNum())
        .seqno(accountSummary.getSeqno())
        .build();
    final AccountTransactionMapper accountTransactionMapper = Mappers.getMapper(AccountTransactionMapper.class);

    //TODO
    //  Change to flex-like instead of do-while.
    do {
      AccountTransactionResponse page = execute(executionContext, Executions.capital_get_account_transactions,
          executionRequest);
      response.setRspCode(page.getRspCode());
      response.setRspMsg(page.getRspMsg());
      response.setNextPage(page.getNextPage());
      response.setTransCnt(response.getTransCnt() + page.getTransCnt());
      response.getTransList().addAll(
          page.getTransList().stream()
              .peek(accountTransaction -> accountTransactionMapper
                  .updateDtoFromDto(defaultTransaction, accountTransaction))
              .collect(Collectors.toList())
      );
      executionRequest.getRequest().setNextPage(page.getNextPage());
    } while (response.getNextPage() != null);
    return response;
  }

  @Override
  public OperatingLeaseBasicResponse getOperatingLeaseBasic(ExecutionContext executionContext,
      Organization organization, AccountSummary accountSummary) {
    // executionId 생성.
    executionContext.generateAndsUpdateExecutionRequestId();

    Map<String, String> headers = Map.of(AUTHORIZATION, executionContext.getAccessToken());
    OperatingLeaseBasicRequest request = OperatingLeaseBasicRequest.builder()
        .orgCode(organization.getOrganizationCode())
        .accountNum(accountSummary.getAccountNum())
        .seqno(accountSummary.getSeqno())
        .searchTimestamp(accountSummary.getOperatingLeaseBasicSearchTimestamp())
        .build();

    ExecutionRequest<OperatingLeaseBasicRequest> executionRequest = ExecutionUtil
        .executionRequestAssembler(headers, request);

    return execute(executionContext, capital_get_operating_lease_basic, executionRequest);
  }

  @Override
  public OperatingLeaseTransactionResponse listOperatingLeaseTransactions(ExecutionContext executionContext,
      Organization organization, AccountSummary accountSummary) {
    // executionId 생성.
    executionContext.generateAndsUpdateExecutionRequestId();

    Map<String, String> headers = Map.of(AUTHORIZATION, executionContext.getAccessToken());

    OperatingLeaseTransactionResponse response = OperatingLeaseTransactionResponse.builder().build();
    OperatingLeaseTransactionRequest request = OperatingLeaseTransactionRequest.builder()
        .orgCode(organization.getOrganizationCode())
        .accountNum(accountSummary.getAccountNum())
        .seqno(accountSummary.getSeqno())
        .fromDtime("20210121000000") // fixme : user_sync_stat.synced_at
        .toDtime("20210122000000") // fixme : kstCurrentDatetime
        .limit(MAX_LIMIT)
        .build();
    do {
      ExecutionRequest<OperatingLeaseTransactionRequest> executionRequest = ExecutionUtil
          .executionRequestAssembler(headers, request);
      OperatingLeaseTransactionResponse pageResponse = execute(executionContext,
          capital_get_operating_lease_transactions, executionRequest);

      response.updateFrom(pageResponse);
      request.updateNextPage(pageResponse.getNextPage());
    } while (Objects.nonNull(response.getNextPage()));

    return response;
  }

  private <T, R> R execute(ExecutionContext executionContext, Execution execution,
      ExecutionRequest<T> executionRequest) {

    ExecutionResponse<R> executionResponse = collectExecutor.execute(executionContext, execution, executionRequest);

    if (executionResponse.getHttpStatusCode() != HttpStatus.OK.value()) {
      throw new CollectRuntimeException("execution Statue is not OK");
      //TODO Throw 추후 적용
      // logging
      // execution monitoring
      // throw
    }

    if (executionResponse.getResponse() == null) {
      throw new CollectRuntimeException("response is null");
    }
    return executionResponse.getResponse();
  }
}
