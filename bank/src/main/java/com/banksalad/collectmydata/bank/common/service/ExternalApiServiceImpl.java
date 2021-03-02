package com.banksalad.collectmydata.bank.common.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.banksalad.collectmydata.bank.common.collect.Executions;
import com.banksalad.collectmydata.bank.common.dto.ListAccountsRequest;
import com.banksalad.collectmydata.bank.common.dto.ListAccountsResponse;
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
  public ListAccountsResponse exchangeListAccounts(ExecutionContext executionContext, String orgCode,
      long searchTimeStamp) {

    executionContext.generateAndsUpdateExecutionRequestId();

    ExecutionRequest<ListAccountsRequest> pagingExecutionRequest = ExecutionRequest.<ListAccountsRequest>builder()
        .headers(Map.of("Authorization", executionContext.getAccessToken()))
        .request(
            ListAccountsRequest.builder()
                .orgCode(orgCode)
                .searchTimestamp(searchTimeStamp)
                .limit(PAGING_MAXIMUM_LIMIT)
                .build())
        .build();

    ListAccountsResponse listAccountsResponse = ListAccountsResponse.builder()
        .build();

    do {
      ExecutionResponse<ListAccountsResponse> pagingExecutionResponse = collectExecutor
          .execute(executionContext, Executions.finance_bank_accounts, pagingExecutionRequest);

      if (pagingExecutionResponse == null || pagingExecutionResponse.getHttpStatusCode() != HttpStatus.OK.value()) {
        throw new RuntimeException("ListAccounts Status is not OK");
      }

      ListAccountsResponse pagingListAccountsResponse = pagingExecutionResponse.getResponse();

      if (pagingListAccountsResponse.getAccountCnt() != pagingListAccountsResponse.getAccountList().size()) {
        log.error("accounts size not equal. cnt: {}, size: {}", pagingListAccountsResponse.getAccountCnt(),
            pagingListAccountsResponse.getAccountList().size());
      }

      listAccountsResponse.setRspCode(pagingListAccountsResponse.getRspCode());
      listAccountsResponse.setRspMsg(pagingListAccountsResponse.getRspMsg());
      listAccountsResponse.setSearchTimestamp(pagingListAccountsResponse.getSearchTimestamp());
      listAccountsResponse.setRegDate(pagingListAccountsResponse.getRegDate());
      listAccountsResponse.setNextPage(pagingListAccountsResponse.getNextPage());
      listAccountsResponse
          .setAccountCnt(listAccountsResponse.getAccountCnt() + pagingListAccountsResponse.getAccountCnt());
      listAccountsResponse.getAccountList().addAll(pagingListAccountsResponse.getAccountList());

      pagingExecutionRequest.getRequest().setNextPage(pagingListAccountsResponse.getNextPage());

    } while (pagingExecutionRequest.getRequest().getNextPage() != null);

    return listAccountsResponse;
  }
}
