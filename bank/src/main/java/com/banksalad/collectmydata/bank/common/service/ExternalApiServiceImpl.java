package com.banksalad.collectmydata.bank.common.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.banksalad.collectmydata.bank.common.collect.Executions;
import com.banksalad.collectmydata.bank.common.dto.ListAccountSummariesRequest;
import com.banksalad.collectmydata.bank.common.dto.ListAccountSummariesResponse;
import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.collect.execution.ExecutionRequest;
import com.banksalad.collectmydata.common.collect.execution.ExecutionResponse;
import com.banksalad.collectmydata.common.collect.executor.CollectExecutor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExternalApiServiceImpl implements ExternalApiService {

  private static final int PAGING_MAXIMUM_LIMIT = 500;

  private final CollectExecutor collectExecutor;

  @Override
  public ListAccountSummariesResponse exchangeListAccountSummaries(ExecutionContext executionContext, String orgCode,
      long searchTimeStamp) {

    executionContext.generateAndsUpdateExecutionRequestId();

    ExecutionRequest<ListAccountSummariesRequest> pagingExecutionRequest = ExecutionRequest.<ListAccountSummariesRequest>builder()
        .headers(Map.of("Authorization", executionContext.getAccessToken()))
        .request(
            ListAccountSummariesRequest.builder()
                .orgCode(orgCode)
                .searchTimestamp(searchTimeStamp)
                .limit(PAGING_MAXIMUM_LIMIT)
                .build())
        .build();

    ListAccountSummariesResponse listAccountSummariesResponse = ListAccountSummariesResponse.builder()
        .build();

    do {
      ExecutionResponse<ListAccountSummariesResponse> pagingExecutionResponse = collectExecutor
          .execute(executionContext, Executions.finance_bank_accounts, pagingExecutionRequest);

      if (pagingExecutionResponse == null || pagingExecutionResponse.getHttpStatusCode() != HttpStatus.OK.value()) {
        throw new RuntimeException("ListAccounts Status is not OK");
      }

      ListAccountSummariesResponse pagingListAccountSummariesResponse = pagingExecutionResponse.getResponse();

      if (pagingListAccountSummariesResponse.getAccountCnt() != pagingListAccountSummariesResponse
          .getAccountSummaries().size()) {
        log.error("accounts size not equal. cnt: {}, size: {}", pagingListAccountSummariesResponse.getAccountCnt(),
            pagingListAccountSummariesResponse.getAccountSummaries().size());
      }

      listAccountSummariesResponse.setRspCode(pagingListAccountSummariesResponse.getRspCode());
      listAccountSummariesResponse.setRspMsg(pagingListAccountSummariesResponse.getRspMsg());
      listAccountSummariesResponse.setSearchTimestamp(pagingListAccountSummariesResponse.getSearchTimestamp());
      listAccountSummariesResponse.setRegDate(pagingListAccountSummariesResponse.getRegDate());
      listAccountSummariesResponse.setNextPage(pagingListAccountSummariesResponse.getNextPage());
      listAccountSummariesResponse
          .setAccountCnt(
              listAccountSummariesResponse.getAccountCnt() + pagingListAccountSummariesResponse.getAccountCnt());
      listAccountSummariesResponse.getAccountSummaries()
          .addAll(pagingListAccountSummariesResponse.getAccountSummaries());

      pagingExecutionRequest.getRequest().setNextPage(pagingListAccountSummariesResponse.getNextPage());

    } while (pagingExecutionRequest.getRequest().getNextPage() != null);

    return listAccountSummariesResponse;
  }
}
