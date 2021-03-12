package com.banksalad.collectmydata.insu.loan.service;

import org.springframework.stereotype.Service;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.collect.execution.ExecutionRequest;
import com.banksalad.collectmydata.common.collect.execution.ExecutionResponse;
import com.banksalad.collectmydata.common.collect.executor.CollectExecutor;
import com.banksalad.collectmydata.common.organization.Organization;
import com.banksalad.collectmydata.common.util.ExecutionUtil;
import com.banksalad.collectmydata.insu.collect.Executions;
import com.banksalad.collectmydata.insu.common.dto.LoanSummary;
import com.banksalad.collectmydata.insu.loan.dto.ListLoanTransactionRequest;
import com.banksalad.collectmydata.insu.loan.dto.ListLoanTransactionResponse;
import com.banksalad.collectmydata.insu.loan.dto.LoanTransaction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoanTransactionServiceImpl implements LoanTransactionService {

  private final CollectExecutor collectExecutor;

  @Override
  public List<LoanTransaction> listLoanTransactions(ExecutionContext executionContext, String organizationCode,
      List<LoanSummary> loanSummaries) {
    return null;
  }

  private ListLoanTransactionResponse getLoanTransactionResponse(ExecutionContext executionContext,
      Organization organization, LoanSummary loanSummary) {

    executionContext.generateAndsUpdateExecutionRequestId();

    Map<String, String> header = Map.of("Authorization", executionContext.getAccessToken());
    ListLoanTransactionRequest loanTransactionRequest = ListLoanTransactionRequest.builder()
        .orgCode(organization.getOrganizationCode())
        .accountNum(loanSummary.getAccountNum())
        .fromDate("20000304") // FIXME
        .toDate("20000304")   // FIXME
        .limit(10)            // FIXME
        .build();

    ExecutionRequest<ListLoanTransactionRequest> executionRequest = ExecutionUtil
        .assembleExecutionRequest(header, loanTransactionRequest);

    ListLoanTransactionResponse responseResult = ListLoanTransactionResponse.builder().build();
    do {
      ExecutionResponse<ListLoanTransactionResponse> executionResponse = collectExecutor
          .execute(executionContext, Executions.insurance_get_loan_transactions, executionRequest);
      ListLoanTransactionResponse page = executionResponse.getResponse();

      responseResult.setRspCode(page.getRspCode());
      responseResult.setRspMsg(page.getRspMsg());
      responseResult.setNextPage(page.getNextPage());
      responseResult.setTransCnt(responseResult.getTransCnt() + page.getTransCnt());
      responseResult.getTransList().addAll(page.getTransList());

      executionRequest.getRequest().setNextPage(page.getNextPage());
    } while (executionRequest.getRequest().getNextPage() != null);

    return responseResult;
  }
}
