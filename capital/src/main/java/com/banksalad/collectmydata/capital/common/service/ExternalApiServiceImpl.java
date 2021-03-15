package com.banksalad.collectmydata.capital.common.service;

import com.banksalad.collectmydata.capital.account.dto.AccountBasicRequest;
import com.banksalad.collectmydata.capital.account.dto.AccountBasicResponse;
import com.banksalad.collectmydata.capital.account.dto.AccountDetailRequest;
import com.banksalad.collectmydata.capital.account.dto.AccountDetailResponse;
import com.banksalad.collectmydata.capital.account.dto.AccountTransactionRequest;
import com.banksalad.collectmydata.capital.account.dto.AccountTransactionResponse;
import com.banksalad.collectmydata.capital.common.collect.Executions;
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
import com.banksalad.collectmydata.common.exception.CollectmydataRuntimeException;
import com.banksalad.collectmydata.common.util.DateUtil;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.banksalad.collectmydata.capital.common.collect.Executions.capital_get_account_detail;
import static com.banksalad.collectmydata.capital.common.collect.Executions.capital_get_accounts;
import static com.banksalad.collectmydata.capital.common.collect.Executions.capital_get_operating_lease_basic;
import static com.banksalad.collectmydata.capital.common.collect.Executions.capital_get_operating_lease_transactions;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExternalApiServiceImpl implements ExternalApiService {

  private static final String AUTHORIZATION = "Authorization";
  private static final int LIMIT = 2; // FIXME: from an external property
  private final CollectExecutor collectExecutor;

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

  /**
   * 6.7.4 대출상품계좌 거래내역 조회
   */
  @Override
  public AccountTransactionResponse getAccountTransactions(ExecutionContext executionContext,
      String orgCode, String accountNum, String seqno, String fromDate, String toDate) {

    final Execution execution = Executions.capital_get_account_transactions;
    // executionId 생성.
    Map<String, String> header = Map.of("Authorization", executionContext.getAccessToken());
    AccountTransactionRequest request = AccountTransactionRequest.builder()
        .orgCode(orgCode)
        .accountNum(accountNum)
        .seqno(seqno)
        .fromDate(fromDate)
        .toDate(toDate)
        .limit(LIMIT)
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
      AccountTransactionResponse page = null;
      try {
        page = execute(executionContext, execution, executionRequest);
        response.setRspCode(page.getRspCode());
        response.setRspMsg(page.getRspMsg());
        response.setNextPage(page.getNextPage());
        response.setTransCnt(response.getTransCnt() + page.getTransCnt());
        response.getTransList().addAll(
            page.getTransList().stream()
                .peek(accountTransaction -> {
                  accountTransaction.setAccountNum(accountNum);
                  accountTransaction.setSeqno(seqno);
                })
                .collect(Collectors.toList())
        );
        executionRequest.getRequest().setNextPage(page.getNextPage());
      } catch (CollectRuntimeException e) {
        if (page == null) {
          throw new AssertionError();
        }
        throw new CollectmydataRuntimeException(
            String.format("Mydata API is not OK: rspCode = %s, rspMsg = %s", page.getRspCode(), page.getRspMsg()), e);
      }
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
      Organization organization, AccountSummary accountSummary, LocalDate fromDate, LocalDate toDate) {
    // executionId 생성.
    executionContext.generateAndsUpdateExecutionRequestId();

    Map<String, String> headers = Map.of(AUTHORIZATION, executionContext.getAccessToken());

    OperatingLeaseTransactionResponse response = OperatingLeaseTransactionResponse.builder().build();
    OperatingLeaseTransactionRequest request = OperatingLeaseTransactionRequest.builder()
        .orgCode(organization.getOrganizationCode())
        .accountNum(accountSummary.getAccountNum())
        .seqno(accountSummary.getSeqno())
        .fromDate(DateUtil.toDateString(fromDate))
        .toDate(DateUtil.toDateString(toDate))
        .limit(LIMIT)
        .build();
    do {
      ExecutionRequest<OperatingLeaseTransactionRequest> executionRequest = ExecutionUtil
          .executionRequestAssembler(headers, request);
      OperatingLeaseTransactionResponse pageResponse = execute(executionContext,
          capital_get_operating_lease_transactions, executionRequest);

      response.updateFrom(pageResponse);
      request.updateNextPage(pageResponse.getNextPage());
    } while (Objects.nonNull(response.getNextPage()));

    if (response.getTransCnt() != response.getOperatingLeaseTransactions().size()) {
      log.error("transactions size not equal. cnt: {}, size: {}", response.getTransCnt(),
          response.getOperatingLeaseTransactions().size());
    }
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
