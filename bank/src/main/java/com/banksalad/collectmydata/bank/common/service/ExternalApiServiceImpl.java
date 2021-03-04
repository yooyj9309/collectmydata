package com.banksalad.collectmydata.bank.common.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.banksalad.collectmydata.bank.common.collect.Executions;
import com.banksalad.collectmydata.bank.common.dto.AccountSummary;
import com.banksalad.collectmydata.bank.common.dto.ListAccountSummariesRequest;
import com.banksalad.collectmydata.bank.common.dto.ListAccountSummariesResponse;
import com.banksalad.collectmydata.bank.depoist.dto.GetDepositAccountBasicRequest;
import com.banksalad.collectmydata.bank.depoist.dto.GetDepositAccountBasicResponse;
import com.banksalad.collectmydata.bank.invest.dto.GetInvestAccountBasicRequest;
import com.banksalad.collectmydata.bank.invest.dto.GetInvestAccountBasicResponse;
import com.banksalad.collectmydata.bank.invest.dto.GetInvestAccountDetailRequest;
import com.banksalad.collectmydata.bank.invest.dto.GetInvestAccountDetailResponse;
import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.collect.execution.ExecutionRequest;
import com.banksalad.collectmydata.common.collect.execution.ExecutionResponse;
import com.banksalad.collectmydata.common.collect.executor.CollectExecutor;
import com.banksalad.collectmydata.common.organization.Organization;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExternalApiServiceImpl implements ExternalApiService {

  private static final String AUTHORIZATION = "Authorization";
  private static final int PAGING_MAXIMUM_LIMIT = 500;
  private final CollectExecutor collectExecutor;

  @Override
  public ListAccountSummariesResponse listAccountSummaries(ExecutionContext executionContext, String orgCode,
      long searchTimestamp) {

    executionContext.generateAndsUpdateExecutionRequestId();

    ExecutionRequest<ListAccountSummariesRequest> pagingExecutionRequest = ExecutionRequest.<ListAccountSummariesRequest>builder()
        .headers(Map.of(AUTHORIZATION, executionContext.getAccessToken()))
        .request(
            ListAccountSummariesRequest.builder()
                .orgCode(orgCode)
                .searchTimestamp(searchTimestamp)
                .limit(PAGING_MAXIMUM_LIMIT)
                .build())
        .build();

    ListAccountSummariesResponse listAccountSummariesResponse = ListAccountSummariesResponse.builder()
        .build();

    do {
      ExecutionResponse<ListAccountSummariesResponse> pagingExecutionResponse = collectExecutor
          .execute(executionContext, Executions.finance_bank_accounts, pagingExecutionRequest);

      if (pagingExecutionResponse == null || pagingExecutionResponse.getHttpStatusCode() != HttpStatus.OK.value()) {
        throw new RuntimeException("List accounts status is not OK");
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

  @Override
  public GetDepositAccountBasicResponse getDepositAccountBasic(ExecutionContext executionContext,
      String orgCode, String accountNum, String seqno, long searchTimestamp) {

    executionContext.generateAndsUpdateExecutionRequestId();

    ExecutionRequest<GetDepositAccountBasicRequest> executionRequest = ExecutionRequest.<GetDepositAccountBasicRequest>builder()
        .headers(Map.of(AUTHORIZATION, executionContext.getAccessToken()))
        .request(
            GetDepositAccountBasicRequest.builder()
                .orgCode(orgCode)
                .accountNum(accountNum)
                .seqno(seqno)
                .searchTimestamp(searchTimestamp)
                .build())
        .build();

    ExecutionResponse<GetDepositAccountBasicResponse> executionResponse = collectExecutor
        .execute(executionContext, Executions.finance_bank_deposit_account_basic, executionRequest);

    if (executionResponse == null || executionResponse.getHttpStatusCode() != HttpStatus.OK.value()) {
      throw new RuntimeException("Get deposit account basic status is not OK");
    }

    return executionResponse.getResponse();
  }

  @Override
  public GetInvestAccountBasicResponse getInvestAccountBasic(ExecutionContext executionContext,
      AccountSummary accountSummary, Organization organization, long searchTimestamp) {
    executionContext.generateAndsUpdateExecutionRequestId();

    ExecutionRequest<GetInvestAccountBasicRequest> request = ExecutionRequest.<GetInvestAccountBasicRequest>builder()
        .headers(Map.of(AUTHORIZATION, executionContext.getAccessToken()))
        .request(
            GetInvestAccountBasicRequest.builder()
                .accountNum(accountSummary.getAccountNum())
                .orgCode(organization.getOrganizationCode())
                .seqno(accountSummary.getSeqno())
                .searchTimestamp(searchTimestamp)
                .build())
        .build();

    ExecutionResponse<GetInvestAccountBasicResponse> investAccountBasicResponse = collectExecutor
        .execute(executionContext, Executions.finance_bank_invest_account_basic, request);

    if (investAccountBasicResponse == null || investAccountBasicResponse.getHttpStatusCode() != HttpStatus.OK.value()) {
      throw new RuntimeException("Invest account basic Status is not OK");
    }

    return investAccountBasicResponse.getResponse();
  }

  @Override
  public GetInvestAccountDetailResponse getInvestAccountDetail(ExecutionContext executionContext,
      AccountSummary accountSummary, Organization organization, long searchTimestamp) {
    executionContext.generateAndsUpdateExecutionRequestId();

    ExecutionRequest<GetInvestAccountDetailRequest> request = ExecutionRequest.<GetInvestAccountDetailRequest>builder()
        .headers(Map.of(AUTHORIZATION, executionContext.getAccessToken()))
        .request(
            GetInvestAccountDetailRequest.builder()
                .accountNum(accountSummary.getAccountNum())
                .orgCode(organization.getOrganizationCode())
                .seqno(accountSummary.getSeqno())
                .searchTimestamp(searchTimestamp)
                .build())
        .build();

    ExecutionResponse<GetInvestAccountDetailResponse> investAccountDetailResponse = collectExecutor
        .execute(executionContext, Executions.finance_bank_invest_account_detail, request);

    if (investAccountDetailResponse == null || investAccountDetailResponse.getHttpStatusCode() != HttpStatus.OK
        .value()) {
      throw new RuntimeException("Invest account detail Status is not OK");
    }

    return investAccountDetailResponse.getResponse();
  }
}
