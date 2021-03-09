package com.banksalad.collectmydata.insu.loan.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.collect.execution.ExecutionRequest;
import com.banksalad.collectmydata.common.collect.execution.ExecutionResponse;
import com.banksalad.collectmydata.common.collect.executor.CollectExecutor;
import com.banksalad.collectmydata.common.exception.CollectRuntimeException;
import com.banksalad.collectmydata.common.util.ExecutionUtil;
import com.banksalad.collectmydata.insu.collect.Executions;
import com.banksalad.collectmydata.insu.common.dto.LoanSummary;
import com.banksalad.collectmydata.insu.loan.dto.GetLoanBasicRequest;
import com.banksalad.collectmydata.insu.loan.dto.GetLoanBasicResponse;
import com.banksalad.collectmydata.insu.loan.dto.LoanBasic;
import com.banksalad.collectmydata.insu.loan.dto.LoanDetail;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoanServiceImpl implements LoanService {

  private final CollectExecutor collectExecutor;
  private static final String AUTHORIZATION = "Authorization";

  @Override
  public List<LoanBasic> listLoanBasics(ExecutionContext executionContext, String organizationCode,
      List<LoanSummary> loanSummaries) {
    return null;
  }

  @Override
  public List<LoanDetail> listLoanDetails(ExecutionContext executionContext, String organizationCode,
      List<LoanSummary> loanSummaries) {
    return null;
  }

  private GetLoanBasicResponse getLoanBasicResponse(ExecutionContext executionContext, String organizationCode,
      String accountNum, long searchTimestamp) {
    Map<String, String> headers = Map.of(AUTHORIZATION, executionContext.getAccessToken());
    GetLoanBasicRequest request = GetLoanBasicRequest.builder()
        .orgCode(organizationCode)
        .accountNum(accountNum)
        .searchTimestamp(searchTimestamp)
        .build();

    ExecutionRequest<GetLoanBasicRequest> executionRequest = ExecutionUtil
        .assembleExecutionRequest(headers, request);

    ExecutionResponse<GetLoanBasicResponse> executionResponse = collectExecutor
        .execute(executionContext, Executions.insurance_get_loan_basic, executionRequest);

    if (executionResponse == null || executionResponse.getHttpStatusCode() != HttpStatus.OK.value()) {
      throw new CollectRuntimeException("execution Statue is not OK");
    }

    if (executionResponse.getResponse() == null) {
      throw new CollectRuntimeException("response is null");
    }

    return executionResponse.getResponse();
  }
}
