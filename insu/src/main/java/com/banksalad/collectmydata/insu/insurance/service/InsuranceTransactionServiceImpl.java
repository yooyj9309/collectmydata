package com.banksalad.collectmydata.insu.insurance.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.collect.execution.ExecutionRequest;
import com.banksalad.collectmydata.common.collect.execution.ExecutionResponse;
import com.banksalad.collectmydata.common.collect.executor.CollectExecutor;
import com.banksalad.collectmydata.common.exception.CollectRuntimeException;
import com.banksalad.collectmydata.common.util.ExecutionUtil;
import com.banksalad.collectmydata.insu.collect.Executions;
import com.banksalad.collectmydata.insu.common.dto.InsuranceSummary;
import com.banksalad.collectmydata.insu.insurance.dto.InsuranceTransaction;
import com.banksalad.collectmydata.insu.insurance.dto.ListInsuranceTransactionsRequest;
import com.banksalad.collectmydata.insu.insurance.dto.ListInsuranceTransactionsResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class InsuranceTransactionServiceImpl implements InsuranceTransactionService {

  private final CollectExecutor collectExecutor;
  private static final String AUTHORIZATION = "Authorization";
  private static final int LIMIT = 100;

  @Override
  public List<InsuranceTransaction> listInsuranceTransactions(ExecutionContext executionContext,
      String organizationCode, List<InsuranceSummary> insuranceSummaries) {
    return null;
  }

  private ListInsuranceTransactionsResponse listInsuranceTransactions(ExecutionContext executionContext, String orgCode,
      String insuNum, String fromDate, String toDate) {
    Map<String, String> headers = Map.of(AUTHORIZATION, executionContext.getAccessToken());
    ListInsuranceTransactionsRequest request = ListInsuranceTransactionsRequest.builder()
        .orgCode(orgCode)
        .insuNum(insuNum)
        .fromDate(fromDate)
        .toDate(toDate)
        .limit(LIMIT)
        .build();

    ListInsuranceTransactionsResponse response = null;
    List<InsuranceTransaction> responseInsuranceTransactionList = new ArrayList<>();
    int responseTransCnt = 0;
    do {
      ExecutionRequest<ListInsuranceTransactionsRequest> executionRequest = ExecutionUtil
          .assembleExecutionRequest(headers, request);

      ExecutionResponse<ListInsuranceTransactionsResponse> executionResponse = collectExecutor
          .execute(executionContext, Executions.insurance_get_transactions, executionRequest);

      if (executionResponse == null || executionResponse.getHttpStatusCode() != HttpStatus.OK.value()) {
        throw new CollectRuntimeException("execution Statue is not OK");
      }

      if (executionResponse.getResponse() == null) {
        throw new CollectRuntimeException("response is null");
      }

      ListInsuranceTransactionsResponse page = executionResponse.getResponse();
      if (page.getTransCnt() != page.getTransList().size()) {
        log.info("The transaction count is different, organizationId: {},", executionContext.getOrganizationId());
      }

      response.setRspCode(page.getRspCode());
      responseTransCnt += page.getTransCnt();
      responseInsuranceTransactionList.addAll(page.getTransList());

      request.setNextPage(response.getNextPage());
    } while (response.getNextPage() != null);

    response.setTransCnt(responseTransCnt);
    response.setTransList(responseInsuranceTransactionList);

    return response;
  }
}
