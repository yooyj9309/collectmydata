package com.banksalad.collectmydata.insu.common.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.collect.execution.ExecutionRequest;
import com.banksalad.collectmydata.common.collect.execution.ExecutionResponse;
import com.banksalad.collectmydata.common.collect.executor.CollectExecutor;
import com.banksalad.collectmydata.common.exception.CollectRuntimeException;
import com.banksalad.collectmydata.common.util.ExecutionUtil;
import com.banksalad.collectmydata.insu.collect.Executions;
import com.banksalad.collectmydata.insu.common.dto.ListLoanSummariesRequest;
import com.banksalad.collectmydata.insu.common.dto.ListLoanSummariesResponse;
import com.banksalad.collectmydata.insu.common.dto.LoanSummary;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoanSummaryServiceImpl implements LoanSummaryService {

  private final CollectExecutor collectExecutor;
  private static final String AUTHORIZATION = "Authorization";

  @Override
  public List<LoanSummary> listLoanSummaries(ExecutionContext executionContext, String organizationCode) {
    return null;
  }

  private ListLoanSummariesResponse listLoanSummariesResponse(ExecutionContext executionContext,
      String organizationCode, long searchTimestamp) {

    Map<String, String> headers = Map.of(AUTHORIZATION, executionContext.getAccessToken());
    ListLoanSummariesRequest request = ListLoanSummariesRequest.builder()
        .orgCode(organizationCode)
        .searchTimestamp(searchTimestamp)
        .build();

    ExecutionRequest<ListLoanSummariesRequest> executionRequest = ExecutionUtil
        .assembleExecutionRequest(headers, request);

    ExecutionResponse<ListLoanSummariesResponse> executionResponse = collectExecutor
        .execute(executionContext, Executions.insurance_get_loan_summaries, executionRequest);

    if (executionResponse == null || executionResponse.getHttpStatusCode() != HttpStatus.OK.value()) {
      throw new CollectRuntimeException("execution Statue is not OK");
    }

    if (executionResponse.getResponse() == null) {
      throw new CollectRuntimeException("response is null");
    }

    return executionResponse.getResponse();
  }
}
