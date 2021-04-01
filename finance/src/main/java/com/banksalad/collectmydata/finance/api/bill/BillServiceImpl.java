package com.banksalad.collectmydata.finance.api.bill;

import com.banksalad.collectmydata.common.collect.execution.Execution;
import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.collect.execution.ExecutionRequest;
import com.banksalad.collectmydata.common.collect.execution.ExecutionResponse;
import com.banksalad.collectmydata.common.collect.executor.CollectExecutor;
import com.banksalad.collectmydata.common.util.DateUtil;
import com.banksalad.collectmydata.finance.api.bill.dto.BillResponse;
import com.banksalad.collectmydata.finance.common.exception.ResponseNotOkException;
import com.banksalad.collectmydata.finance.common.service.UserSyncStatusService;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class BillServiceImpl<BillRequest, Bill, BillDetail> implements
    BillService<BillRequest, Bill, BillDetail> {

  private static final String AUTHORIZATION = "Authorization";

  private final CollectExecutor collectExecutor;
  private final UserSyncStatusService userSyncStatusService;

  @Override
  public List<Bill> listBills(
      ExecutionContext executionContext,
      Execution execution,
      BillRequestHelper<BillRequest> requestHelper,
      BillResponseHelper<Bill> responseHelper
  ) throws ResponseNotOkException {

    List<Bill> billAll = new ArrayList<>();

    /* copy ExecutionContext for new executionRequestId */
    ExecutionContext executionContextLocal = executionContext.copyWith(ExecutionContext.generateExecutionRequestId());

    LocalDateTime fromDateTime = DateUtil
        .utcLocalDateTimeToKstLocalDateTime(requestHelper.getTransactionSyncedAt(executionContext));
    LocalDateTime toDateTime = DateUtil.utcLocalDateTimeToKstLocalDateTime(executionContext.getSyncStartedAt());

    String nextPage = null;
    ExecutionResponse<BillResponse> executionResponse;

    do {
      executionResponse = collectExecutor.execute(
          executionContextLocal,
          execution,
          ExecutionRequest.builder()
              .headers(Map.of(AUTHORIZATION, executionContext.getAccessToken()))
              .request(requestHelper.make(executionContext, fromDateTime.toLocalDate(), toDateTime.toLocalDate(), nextPage))
              .build());

      /* validate response and break pagination */
      checkResponseAndThrow(executionResponse);
      List<Bill> bills = responseHelper.getBillsFromResponse(executionResponse.getResponse());

      /* Skip saving when no transaction exist. */
      if (bills != null && !bills.isEmpty()) {
        responseHelper.saveBills(executionContext, bills);
        billAll.addAll(bills);
      }

      nextPage = executionResponse.getNextPage();
    } while (executionResponse.getNextPage() != null && executionResponse.getNextPage().length() > 0);

    userSyncStatusService.updateUserSyncStatus(
        executionContext.getBanksaladUserId(),
        executionContext.getOrganizationId(),
        execution.getApi().getId(),
        executionContext.getSyncStartedAt(),
        0L
    );

    return billAll;
  }

  @Override
  public List<BillDetail> listBillDetails(
      ExecutionContext executionContext,
      Execution execution,
      List<Bill> bills,
      BillDetailRequestHelper<Bill> requestHelper,
      BillDetailResponseHelper<BillDetail> responseHelper
  ) {

    return null;
  }


  private void checkResponseAndThrow(ExecutionResponse<BillResponse> executionResponse) throws ResponseNotOkException {
    BillResponse billResponse = executionResponse.getResponse();

    if (executionResponse.getHttpStatusCode() != HttpStatus.OK.value()) {

      throw new ResponseNotOkException(executionResponse.getHttpStatusCode(), billResponse.getRspCode(),
          billResponse.getRspMsg());
    }
  }
}