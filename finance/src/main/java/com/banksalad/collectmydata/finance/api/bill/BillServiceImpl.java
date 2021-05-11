package com.banksalad.collectmydata.finance.api.bill;

import com.banksalad.collectmydata.common.collect.execution.Execution;
import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.collect.execution.ExecutionRequest;
import com.banksalad.collectmydata.common.collect.execution.ExecutionResponse;
import com.banksalad.collectmydata.common.collect.executor.CollectExecutor;
import com.banksalad.collectmydata.common.util.DateUtil;
import com.banksalad.collectmydata.finance.api.bill.dto.BillResponse;
import com.banksalad.collectmydata.finance.api.bill.dto.BillTransactionResponse;
import com.banksalad.collectmydata.finance.common.service.FinanceMessageService;
import com.banksalad.collectmydata.finance.common.service.HeaderService;
import com.banksalad.collectmydata.finance.common.service.UserSyncStatusService;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BillServiceImpl<BillRequest, Bill, BillTransactionRequest, BillTransaction> implements
    BillService<BillRequest, Bill, BillTransactionRequest, BillTransaction> {

  private static final String AUTHORIZATION = "Authorization";

  private final CollectExecutor collectExecutor;
  private final UserSyncStatusService userSyncStatusService;
  private final FinanceMessageService financeMessageService;
  private final HeaderService headerService;

  @Override
  public List<Bill> listBills(
      ExecutionContext executionContext,
      Execution execution,
      BillRequestHelper<BillRequest> requestHelper,
      BillResponseHelper<Bill> responseHelper
  ) {
    return listBills(executionContext, execution, requestHelper, responseHelper, null);
  }

  @Override
  public List<Bill> listBills(ExecutionContext executionContext, Execution execution,
      BillRequestHelper<BillRequest> requestHelper, BillResponseHelper<Bill> responseHelper,
      BillPublishmentHelper billPublishmentHelper) {
    List<Bill> billAll = new ArrayList<>();

    /* copy ExecutionContext for new executionRequestId */
    ExecutionContext executionContextLocal = executionContext.copyWith(ExecutionContext.generateExecutionRequestId());

    LocalDateTime fromDateTime = DateUtil
        .utcLocalDateTimeToKstLocalDateTime(requestHelper.getTransactionSyncedAt(executionContext));
    LocalDateTime toDateTime = DateUtil.utcLocalDateTimeToKstLocalDateTime(executionContext.getSyncStartedAt());

    String nextPage = null;
    boolean hasNextPage = false;
    boolean isAllPaginationResponseOk = true;
    ExecutionResponse<BillResponse> executionResponse;

    do {
      executionResponse = collectExecutor.execute(
          executionContextLocal,
          execution,
          ExecutionRequest.builder()
              .headers(headerService.makeHeader(executionContext))
              .request(
                  requestHelper.make(executionContext, fromDateTime.toLocalDate(), toDateTime.toLocalDate(), nextPage))
              .build());

      /* validate response and break pagination */
      if (executionResponse.getHttpStatusCode() != HttpStatus.OK.value()) {
        isAllPaginationResponseOk = false;
        break;
      }
      List<Bill> bills = responseHelper.getBillsFromResponse(executionResponse.getResponse());

      /* Skip saving when no transaction exist. */
      if (bills != null && !bills.isEmpty()) {
        responseHelper.saveBills(executionContext, bills);
        billAll.addAll(bills);

        System.out.println(
            "***\nnext_page: " + nextPage + "\nbills: " + bills + "\nnext_page: " + executionResponse.getNextPage()
                + "\n***");
      }

      hasNextPage =
          StringUtils.hasLength(executionResponse.getNextPage()) && !executionResponse.getNextPage().equals(nextPage);
      nextPage = executionResponse.getNextPage();

      if (billPublishmentHelper != null) {
        financeMessageService.producePublishmentRequested(billPublishmentHelper.getMessageTopic(),
            billPublishmentHelper.makePublishmentRequestedMessage(executionContext));
      }

    } while (hasNextPage);

    /** bill pagination 성공할때만 sync_at 업데이트 필요.. 사용자 기반이라 userSyncStatus에 업데이트.
     *  @author hyunjun
     */
    if (isAllPaginationResponseOk) {
      userSyncStatusService.updateUserSyncStatus(
          executionContext.getBanksaladUserId(),
          executionContext.getOrganizationId(),
          execution.getApi().getId(),
          executionContext.getSyncStartedAt(),
          0L
      );
    }

    return billAll;
  }

  @Override
  public void listBillDetails(
      ExecutionContext executionContext,
      Execution execution,
      List<Bill> bills,
      BillTransactionRequestHelper<BillTransactionRequest, Bill> requestHelper,
      BillTransactionResponseHelper<Bill, BillTransaction> responseHelper
  ) {
    listBillDetails(executionContext, execution, bills, requestHelper, responseHelper, null);
  }

  @Override
  public void listBillDetails(ExecutionContext executionContext, Execution execution,
      List<Bill> bills, BillTransactionRequestHelper<BillTransactionRequest, Bill> requestHelper,
      BillTransactionResponseHelper<Bill, BillTransaction> responseHelper,
      BillPublishmentHelper billPublishmentHelper) {
    List<BillTransaction> billDetailsAll = new ArrayList<>();

    ExecutionResponse<BillTransactionResponse> executionResponse;

    for (Bill bill : bills) {
      /* copy ExecutionContext for new executionRequestId */
      ExecutionContext executionContextLocal = executionContext.copyWith(ExecutionContext.generateExecutionRequestId());

      String nextPage = null;
      boolean hasNextPage = false;

      do {
        executionResponse = collectExecutor.execute(
            executionContextLocal,
            execution,
            ExecutionRequest.builder()
                .headers(headerService.makeHeader(executionContext))
                .request(requestHelper
                    .make(executionContext, bill, nextPage))
                .build());

        /* validate response and break pagination */
        if (executionResponse.getHttpStatusCode() != HttpStatus.OK.value()) {
          break;
        }

        /* save transactions */
        List<BillTransaction> billTransactions = responseHelper
            .getBillTransactionsFromResponse(executionResponse.getResponse());

        /* Skip saving when no transaction exist. */
        if (billTransactions != null && billTransactions.size() != 0) {
          responseHelper.saveBillTransactions(executionContext, bill, billTransactions);
          billDetailsAll.addAll(billTransactions);
        }

        hasNextPage =
            StringUtils.hasLength(executionResponse.getNextPage()) && !executionResponse.getNextPage().equals(nextPage);
        nextPage = executionResponse.getNextPage();
      } while (hasNextPage);
    }

    /** transaction DB 저장 후 publishment message produce
     *  @author hyunjun
     */
    if (billPublishmentHelper != null) {
      financeMessageService.producePublishmentRequested(billPublishmentHelper.getMessageTopic(),
          billPublishmentHelper.makePublishmentRequestedMessage(executionContext));
    }

    userSyncStatusService.updateUserSyncStatus(
        executionContext.getBanksaladUserId(),
        executionContext.getOrganizationId(),
        execution.getApi().getId(),
        executionContext.getSyncStartedAt(),
        0L
    );
  }
}
