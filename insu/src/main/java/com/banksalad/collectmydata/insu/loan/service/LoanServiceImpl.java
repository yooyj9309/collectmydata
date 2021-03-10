package com.banksalad.collectmydata.insu.loan.service;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.collect.execution.ExecutionRequest;
import com.banksalad.collectmydata.common.collect.execution.ExecutionResponse;
import com.banksalad.collectmydata.common.collect.executor.CollectExecutor;
import com.banksalad.collectmydata.common.exception.CollectRuntimeException;
import com.banksalad.collectmydata.common.util.ExecutionUtil;
import com.banksalad.collectmydata.insu.collect.Executions;
import com.banksalad.collectmydata.insu.common.db.repository.LoanDetailHistoryRepository;
import com.banksalad.collectmydata.insu.common.db.repository.LoanDetailRepository;
import com.banksalad.collectmydata.insu.common.dto.LoanSummary;
import com.banksalad.collectmydata.insu.loan.dto.GetLoanBasicRequest;
import com.banksalad.collectmydata.insu.loan.dto.GetLoanBasicResponse;
import com.banksalad.collectmydata.insu.loan.dto.GetLoanDetailRequest;
import com.banksalad.collectmydata.insu.loan.dto.GetLoanDetailResponse;
import com.banksalad.collectmydata.insu.loan.dto.LoanBasic;
import com.banksalad.collectmydata.insu.loan.dto.LoanDetail;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoanServiceImpl implements LoanService {

  private final CollectExecutor collectExecutor;
  private static final String AUTHORIZATION = "Authorization";

  private final LoanDetailRepository loanDetailRepository;
  private final LoanDetailHistoryRepository loanDetailHistoryRepository;

  @Override
  public List<LoanBasic> listLoanBasics(ExecutionContext executionContext, String organizationCode,
      List<LoanSummary> loanSummaries) {
    return null;
  }

  @Override
  public List<LoanDetail> listLoanDetails(ExecutionContext executionContext, String organizationCode,
      List<LoanSummary> loanSummaries) {
    // Create a new ExecutionContext to avoid `executionId` conflicts.
    ExecutionContext context = executionContext.withExecutionRequestId(UUID.randomUUID().toString());

    // TODO: Do the followings for loanSummaries in parallel or in serial.
    // Post Mydata API against a data provider and get a GetLoanDetailResponse response.
    // Extract LoanDetail from the response.
    // Query a unique key against `loan_detail` table.
    // If a record (LoanDetailEntity) exists,
    //  Compare the contents between LoanDetail and the LoanDetailEntity.
    //    If both are different,
    //      Update LoanDetailEntity with LoanDetail and save it into the table.
    //    If equal,
    //      Do nothing.
    // If no record exits,
    //  Clone LoanDetailEntity from LoanDetail.
    //  Insert LoanDetailEntity into `loan_detail` table.
    // Clone LoanDetailHistoryEntity from LoanDetailEntity.
    // Insert LoanDetailHistory into `loan_detail_history` table.
    // If no exceptions are found, update `detail_search_timestamp` of `loan_summary` table.
    // Return LoanDetail
    // Join the returned LoanDetails then make a List.
    // If no exceptions occurred, upsert `user_sync_status` table.
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

  private GetLoanDetailResponse getLoanDetailResponse(ExecutionContext executionContext, String organizationCode, String accountNum,
      long searchTimestamp) {
    // Make a GetLoanDetailRequest.
    Map<String, String> headers = Map.of(AUTHORIZATION, executionContext.getAccessToken());
    GetLoanDetailRequest request = GetLoanDetailRequest.builder()
        .orgCode(organizationCode)
        .accountNum(accountNum)
        .searchTimestamp(searchTimestamp)
        .build();
    ExecutionRequest<GetLoanDetailRequest> executionRequest = ExecutionUtil.assembleExecutionRequest(headers, request);

    // Post a Mydata API and get the response.
    try {
      ExecutionResponse<GetLoanDetailResponse> executionResponse = collectExecutor
          .execute(executionContext, Executions.insurance_get_loan_detail, executionRequest);
      GetLoanDetailResponse getLoanDetailResponse = executionResponse.getResponse();
      // 아래 부분은 CollectExecutor에서 발생시키는 것이 나을 것 같음. Exception에 code도 추가
      if (executionResponse.getHttpStatusCode() != HttpStatus.OK.value()) {
        // TODO: throw new CollectRuntimeException("...", ex);
      }
      return getLoanDetailResponse;
    } catch (CollectRuntimeException ex) { // 송수신층으로부터 받은 예외 던짐
      // TODO: throw new CollectmydataRuntimeException("...", ex);
    } catch (NullPointerException ex) { // OK인데 알맹이 데이터가 없는 경우 예외 던짐
      // TODO: throw new CollectmydataRuntimeException("...", ex);
    }
    return null;
  }
}
