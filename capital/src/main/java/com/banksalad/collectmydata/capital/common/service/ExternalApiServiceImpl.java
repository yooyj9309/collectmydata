package com.banksalad.collectmydata.capital.common.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.banksalad.collectmydata.capital.account.dto.AccountDetailRequest;
import com.banksalad.collectmydata.capital.account.dto.AccountDetailResponse;
import com.banksalad.collectmydata.capital.common.dto.Organization;
import com.banksalad.collectmydata.capital.oplease.dto.OperatingLeaseTransactionRequest;
import com.banksalad.collectmydata.capital.oplease.dto.OperatingLeaseTransactionResponse;
import com.banksalad.collectmydata.capital.summary.dto.AccountSummary;
import com.banksalad.collectmydata.common.collect.execution.Execution;
import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.collect.execution.ExecutionRequest;
import com.banksalad.collectmydata.common.collect.execution.ExecutionResponse;
import com.banksalad.collectmydata.common.collect.executor.CollectExecutor;
import com.banksalad.collectmydata.common.exception.CollectRuntimeException;
import com.banksalad.collectmydata.common.util.DateUtil;
import com.banksalad.collectmydata.common.util.ExecutionUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.util.Map;
import java.util.Objects;

import static com.banksalad.collectmydata.capital.collect.Executions.capital_get_account_detail;
import static com.banksalad.collectmydata.capital.collect.Executions.capital_get_operating_lease_transactions;

@Deprecated
@Slf4j
@Service
@RequiredArgsConstructor
public class ExternalApiServiceImpl implements ExternalApiService {

  private static final String AUTHORIZATION = "Authorization";
  private static final int LIMIT = 2; // FIXME: from an external property
  private final CollectExecutor collectExecutor;

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
        .assembleExecutionRequest(headers, accountDetailRequest);

    return execute(executionContext, capital_get_account_detail, executionRequest);
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
          .assembleExecutionRequest(headers, request);
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
