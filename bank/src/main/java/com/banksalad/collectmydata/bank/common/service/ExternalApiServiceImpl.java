package com.banksalad.collectmydata.bank.common.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.banksalad.collectmydata.bank.common.collect.Executions;
import com.banksalad.collectmydata.bank.deposit.dto.GetDepositAccountBasicRequest;
import com.banksalad.collectmydata.bank.deposit.dto.GetDepositAccountBasicResponse;
import com.banksalad.collectmydata.bank.deposit.dto.GetDepositAccountDetailRequest;
import com.banksalad.collectmydata.bank.deposit.dto.GetDepositAccountDetailResponse;
import com.banksalad.collectmydata.bank.deposit.dto.ListDepositAccountTransactionsRequest;
import com.banksalad.collectmydata.bank.deposit.dto.ListDepositAccountTransactionsResponse;
import com.banksalad.collectmydata.bank.invest.dto.GetInvestAccountBasicRequest;
import com.banksalad.collectmydata.bank.invest.dto.GetInvestAccountBasicResponse;
import com.banksalad.collectmydata.bank.invest.dto.GetInvestAccountDetailRequest;
import com.banksalad.collectmydata.bank.invest.dto.GetInvestAccountDetailResponse;
import com.banksalad.collectmydata.bank.invest.dto.ListInvestAccountTransactionsRequest;
import com.banksalad.collectmydata.bank.invest.dto.ListInvestAccountTransactionsResponse;
import com.banksalad.collectmydata.bank.loan.dto.GetLoanAccountBasicRequest;
import com.banksalad.collectmydata.bank.loan.dto.GetLoanAccountBasicResponse;
import com.banksalad.collectmydata.bank.loan.dto.GetLoanAccountDetailRequest;
import com.banksalad.collectmydata.bank.loan.dto.GetLoanAccountDetailResponse;
import com.banksalad.collectmydata.bank.summary.dto.AccountSummary;
import com.banksalad.collectmydata.bank.summary.dto.ListAccountSummariesRequest;
import com.banksalad.collectmydata.bank.summary.dto.ListAccountSummariesResponse;
import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.collect.execution.ExecutionRequest;
import com.banksalad.collectmydata.common.collect.execution.ExecutionResponse;
import com.banksalad.collectmydata.common.collect.executor.CollectExecutor;
import com.banksalad.collectmydata.common.organization.Organization;
import com.banksalad.collectmydata.common.util.DateRange;
import com.banksalad.collectmydata.common.util.DateUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.util.Map;

@Deprecated
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
          .execute(executionContext, Executions.finance_bank_summaries, pagingExecutionRequest);

      if (pagingExecutionResponse.getResponse() == null || HttpStatus.OK.value() != pagingExecutionResponse
          .getHttpStatusCode()) {
        throw new RuntimeException("List accounts status is not OK");
      }

      ListAccountSummariesResponse pagingListAccountSummariesResponse = pagingExecutionResponse.getResponse();

      if (pagingListAccountSummariesResponse.getAccountCnt() != pagingListAccountSummariesResponse
          .getAccountList().size()) {
        log.error("accounts size not equal. cnt: {}, size: {}", pagingListAccountSummariesResponse.getAccountCnt(),
            pagingListAccountSummariesResponse.getAccountList().size());
      }

      listAccountSummariesResponse.setRspCode(pagingListAccountSummariesResponse.getRspCode());
      listAccountSummariesResponse.setRspMsg(pagingListAccountSummariesResponse.getRspMsg());
      listAccountSummariesResponse.setSearchTimestamp(pagingListAccountSummariesResponse.getSearchTimestamp());
      listAccountSummariesResponse.setRegDate(pagingListAccountSummariesResponse.getRegDate());
      listAccountSummariesResponse.setNextPage(pagingListAccountSummariesResponse.getNextPage());
      listAccountSummariesResponse
          .setAccountCnt(
              listAccountSummariesResponse.getAccountCnt() + pagingListAccountSummariesResponse.getAccountCnt());
      listAccountSummariesResponse.getAccountList()
          .addAll(pagingListAccountSummariesResponse.getAccountList());

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

    if (executionResponse.getResponse() == null || executionResponse.getHttpStatusCode() != HttpStatus.OK.value()) {
      throw new RuntimeException("Get deposit account basic status is not OK");
    }

    return executionResponse.getResponse();
  }

  @Override
  public GetDepositAccountDetailResponse getDepositAccountDetail(ExecutionContext executionContext,
      String orgCode, String accountNum, String seqno, long searchTimestamp) {

    executionContext.generateAndsUpdateExecutionRequestId();

    ExecutionRequest<GetDepositAccountDetailRequest> executionRequest = ExecutionRequest.<GetDepositAccountDetailRequest>builder()
        .headers(Map.of(AUTHORIZATION, executionContext.getAccessToken()))
        .request(
            GetDepositAccountDetailRequest.builder()
                .orgCode(orgCode)
                .accountNum(accountNum)
                .seqno(seqno)
                .searchTimestamp(searchTimestamp)
                .build())
        .build();

    ExecutionResponse<GetDepositAccountDetailResponse> executionResponse = collectExecutor
        .execute(executionContext, Executions.finance_bank_deposit_account_detail, executionRequest);

    if (executionResponse.getResponse() == null || executionResponse.getHttpStatusCode() != HttpStatus.OK.value()) {
      throw new RuntimeException("Get deposit account detail status is not OK");
    }

    GetDepositAccountDetailResponse response = executionResponse.getResponse();

    if (response.getDetailCnt() != response.getDepositAccountDetails().size()) {
      log.error("account details size not equal. cnt: {}, size: {}", response.getDetailCnt(),
          response.getDepositAccountDetails().size());
    }

    return executionResponse.getResponse();
  }

  @Override
  public ListDepositAccountTransactionsResponse listDepositAccountTransactions(ExecutionContext executionContext,
      String orgCode, String accountNum, String seqno, LocalDate fromDate, LocalDate toDate) {

    executionContext.generateAndsUpdateExecutionRequestId();

    ExecutionRequest<ListDepositAccountTransactionsRequest> pagingExecutionRequest = ExecutionRequest.<ListDepositAccountTransactionsRequest>builder()
        .headers(Map.of(AUTHORIZATION, executionContext.getAccessToken()))
        .request(
            ListDepositAccountTransactionsRequest.builder()
                .orgCode(orgCode)
                .accountNum(accountNum)
                .seqno(seqno)
                .fromDate(DateUtil.toDateString(fromDate))
                .toDate(DateUtil.toDateString(toDate))
                .limit(PAGING_MAXIMUM_LIMIT)
                .build())
        .build();

    ListDepositAccountTransactionsResponse listDepositAccountTransactionsResponse = ListDepositAccountTransactionsResponse
        .builder()
        .build();

    do {
      ExecutionResponse<ListDepositAccountTransactionsResponse> pagingExecutionResponse = collectExecutor
          .execute(executionContext, Executions.finance_bank_deposit_account_transaction, pagingExecutionRequest);

      if (pagingExecutionResponse.getResponse() == null ||
          HttpStatus.OK.value() != pagingExecutionResponse.getHttpStatusCode()) {
        throw new RuntimeException("List deposit account transactions status is not OK");
      }

      ListDepositAccountTransactionsResponse pagingListDepositAccountTransactionsResponse = pagingExecutionResponse
          .getResponse();

      if (pagingListDepositAccountTransactionsResponse.getTransCnt() != pagingListDepositAccountTransactionsResponse
          .getDepositAccountTransactions().size()) {
        log.error("transactions size not equal. cnt: {}, size: {}",
            pagingListDepositAccountTransactionsResponse.getTransCnt(),
            pagingListDepositAccountTransactionsResponse.getDepositAccountTransactions().size());
      }

      listDepositAccountTransactionsResponse.setRspCode(pagingListDepositAccountTransactionsResponse.getRspCode());
      listDepositAccountTransactionsResponse.setRspMsg(pagingListDepositAccountTransactionsResponse.getRspMsg());
      listDepositAccountTransactionsResponse.setNextPage(pagingListDepositAccountTransactionsResponse.getNextPage());
      listDepositAccountTransactionsResponse.setTransCnt(
          listDepositAccountTransactionsResponse.getTransCnt() + pagingListDepositAccountTransactionsResponse
              .getTransCnt());
      listDepositAccountTransactionsResponse.getDepositAccountTransactions()
          .addAll(pagingListDepositAccountTransactionsResponse.getDepositAccountTransactions());

      pagingExecutionRequest.getRequest().setNextPage(pagingListDepositAccountTransactionsResponse.getNextPage());

    } while (pagingExecutionRequest.getRequest().getNextPage() != null);

    return listDepositAccountTransactionsResponse;
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

    if (investAccountBasicResponse.getResponse() == null
        || investAccountBasicResponse.getHttpStatusCode() != HttpStatus.OK.value()) {
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

    if (investAccountDetailResponse.getResponse() == null
        || investAccountDetailResponse.getHttpStatusCode() != HttpStatus.OK.value()) {
      throw new RuntimeException("Invest account detail Status is not OK");
    }

    return investAccountDetailResponse.getResponse();
  }

  @Override
  public ListInvestAccountTransactionsResponse listInvestAccountTransactions(ExecutionContext executionContext,
      AccountSummary accountSummary, Organization organization, DateRange dateRange) {

    executionContext.generateAndsUpdateExecutionRequestId();

    ExecutionRequest<ListInvestAccountTransactionsRequest> pagingExecutionRequest = ExecutionRequest.<ListInvestAccountTransactionsRequest>builder()
        .headers(Map.of(AUTHORIZATION, executionContext.getAccessToken()))
        .request(
            ListInvestAccountTransactionsRequest.builder()
                .orgCode(organization.getOrganizationCode())
                .accountNum(accountSummary.getAccountNum())
                .seqno(accountSummary.getSeqno())
                .fromDate(DateUtil.toDateString(dateRange.getStartDate()))
                .toDate(DateUtil.toDateString(dateRange.getEndDate()))
                .limit(PAGING_MAXIMUM_LIMIT)
                .build())
        .build();

    ListInvestAccountTransactionsResponse listInvestAccountTransactionsResponse = ListInvestAccountTransactionsResponse
        .builder()
        .build();

    do {
      ExecutionResponse<ListInvestAccountTransactionsResponse> pagingExecutionResponse = collectExecutor
          .execute(executionContext, Executions.finance_bank_invest_account_transaction, pagingExecutionRequest);

      if (pagingExecutionResponse.getResponse() == null ||
          HttpStatus.OK.value() != pagingExecutionResponse.getHttpStatusCode()) {
        throw new RuntimeException("List invest account transactions status is not OK");
      }

      ListInvestAccountTransactionsResponse pagingListInvestAccountTransactionsResponse = pagingExecutionResponse
          .getResponse();

      if (pagingListInvestAccountTransactionsResponse.getTransCnt() != pagingListInvestAccountTransactionsResponse
          .getInvestAccountTransactions().size()) {
        log.error("Invest transactions size not equal. cnt: {}, size: {}",
            pagingListInvestAccountTransactionsResponse.getTransCnt(),
            pagingListInvestAccountTransactionsResponse.getInvestAccountTransactions().size());
      }

      listInvestAccountTransactionsResponse.setRspCode(pagingListInvestAccountTransactionsResponse.getRspCode());
      listInvestAccountTransactionsResponse.setRspMsg(pagingListInvestAccountTransactionsResponse.getRspMsg());
      listInvestAccountTransactionsResponse.setNextPage(pagingListInvestAccountTransactionsResponse.getNextPage());
      listInvestAccountTransactionsResponse.setTransCnt(
          listInvestAccountTransactionsResponse.getTransCnt() + pagingListInvestAccountTransactionsResponse
              .getTransCnt());
      listInvestAccountTransactionsResponse.getInvestAccountTransactions()
          .addAll(pagingListInvestAccountTransactionsResponse.getInvestAccountTransactions());

      pagingExecutionRequest.getRequest().setNextPage(pagingListInvestAccountTransactionsResponse.getNextPage());

    } while (pagingExecutionRequest.getRequest().getNextPage() != null);

    return listInvestAccountTransactionsResponse;
  }


  public GetLoanAccountBasicResponse getLoanAccountBasic(ExecutionContext executionContext,
      AccountSummary accountSummary, Organization organization, long searchTimestamp) {
    executionContext.generateAndsUpdateExecutionRequestId();

    ExecutionRequest<GetLoanAccountBasicRequest> request = ExecutionRequest.<GetLoanAccountBasicRequest>builder()
        .headers(Map.of(AUTHORIZATION, executionContext.getAccessToken()))
        .request(
            GetLoanAccountBasicRequest.builder()
                .accountNum(accountSummary.getAccountNum())
                .orgCode(organization.getOrganizationCode())
                .seqno(accountSummary.getSeqno())
                .searchTimestamp(searchTimestamp)
                .build())
        .build();

    ExecutionResponse<GetLoanAccountBasicResponse> loanAccountBasicResponseExecutionResponse = collectExecutor
        .execute(executionContext, Executions.finance_bank_loan_account_basic, request);

    if (loanAccountBasicResponseExecutionResponse.getResponse() == null
        || loanAccountBasicResponseExecutionResponse.getHttpStatusCode() != HttpStatus.OK.value()) {
      throw new RuntimeException("Loan account basic Status is not OK");
    }

    return loanAccountBasicResponseExecutionResponse.getResponse();
  }

  @Override
  public GetLoanAccountDetailResponse getLoanAccountDetail(ExecutionContext executionContext,
      AccountSummary accountSummary, Organization organization, long searchTimestamp) {
    executionContext.generateAndsUpdateExecutionRequestId();

    ExecutionRequest<GetLoanAccountDetailRequest> request = ExecutionRequest.<GetLoanAccountDetailRequest>builder()
        .headers(Map.of(AUTHORIZATION, executionContext.getAccessToken()))
        .request(
            GetLoanAccountDetailRequest.builder()
                .accountNum(accountSummary.getAccountNum())
                .orgCode(organization.getOrganizationCode())
                .seqno(accountSummary.getSeqno())
                .searchTimestamp(searchTimestamp)
                .build())
        .build();

    ExecutionResponse<GetLoanAccountDetailResponse> loanAccountDetailResponseExecutionResponse = collectExecutor
        .execute(executionContext, Executions.finance_bank_loan_account_detail, request);

    if (loanAccountDetailResponseExecutionResponse.getResponse() == null
        || loanAccountDetailResponseExecutionResponse.getHttpStatusCode() != HttpStatus.OK.value()) {
      throw new RuntimeException("Loan account detail Status is not OK");
    }

    return loanAccountDetailResponseExecutionResponse.getResponse();
  }
}
