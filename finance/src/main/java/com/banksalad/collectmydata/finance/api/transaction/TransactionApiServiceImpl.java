package com.banksalad.collectmydata.finance.api.transaction;

import com.banksalad.collectmydata.common.collect.execution.Execution;
import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.collect.execution.ExecutionRequest;
import com.banksalad.collectmydata.common.collect.execution.ExecutionResponse;
import com.banksalad.collectmydata.common.collect.executor.CollectExecutor;
import com.banksalad.collectmydata.common.util.DateUtil;
import com.banksalad.collectmydata.finance.api.transaction.dto.TransactionResponse;
import com.banksalad.collectmydata.finance.common.service.FinanceMessageService;
import com.banksalad.collectmydata.finance.common.service.HeaderService;
import com.banksalad.collectmydata.finance.common.service.UserSyncStatusService;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionApiServiceImpl<Summary, TransactionRequest, Transaction> implements
    TransactionApiService<Summary, TransactionRequest, Transaction> {

  private static final String AUTHORIZATION = "Authorization";

  private final CollectExecutor collectExecutor;
  private final UserSyncStatusService userSyncStatusService;
  private final FinanceMessageService financeMessageService;
  private final HeaderService headerService;

  public void listTransactions(
      ExecutionContext executionContext,
      Execution execution,
      TransactionRequestHelper<Summary, TransactionRequest> requestHelper,
      TransactionResponseHelper<Summary, Transaction> responseHelper
  ) {
    listTransactions(executionContext, execution, requestHelper, responseHelper, null);
  }

  @Override
  public void listTransactions(ExecutionContext executionContext, Execution execution,
      TransactionRequestHelper<Summary, TransactionRequest> requestHelper,
      TransactionResponseHelper<Summary, Transaction> responseHelper,
      TransactionPublishmentHelper<Summary> publishmentHelper) {

    List<Summary> summaries = requestHelper.listSummaries(executionContext);

    ExecutionResponse<TransactionResponse> executionResponse;
    TransactionResponse transactionResponse;

    for (Summary summary : summaries) {
      /* copy ExecutionContext for new executionRequestId */
      ExecutionContext executionContextLocal = executionContext.copyWith(ExecutionContext.generateExecutionRequestId());

      LocalDateTime fromDateTime = DateUtil
          .utcLocalDateTimeToKstLocalDateTime(requestHelper.getTransactionSyncedAt(executionContext, summary));
      LocalDateTime toDateTime = DateUtil.utcLocalDateTimeToKstLocalDateTime(executionContext.getSyncStartedAt());

      String nextPage = null;
      boolean hasNextPage = false;
      boolean isAllPaginationResponseOk = true;

      do {
        executionResponse = collectExecutor.execute(
            executionContextLocal,
            execution,
            ExecutionRequest.builder()
                .headers(headerService.makeHeader(executionContext))
                .request(requestHelper
                    .make(executionContext, summary, fromDateTime.toLocalDate(), toDateTime.toLocalDate(), nextPage))
                .build());

        /* populate response */
        transactionResponse = executionResponse.getResponse();

        /* update response code to summary table */
        responseHelper.saveResponseCode(executionContext, summary, transactionResponse.getRspCode());

        /* validate response and break pagination */
        if (executionResponse.getHttpStatusCode() != HttpStatus.OK.value()) {
          isAllPaginationResponseOk = false;
          break;
        }

        /* save transactions */
        List<Transaction> transactions = responseHelper.getTransactionsFromResponse(executionResponse.getResponse());

        /* Skip saving when no transaction exist. */
        if (transactions != null && transactions.size() != 0) {
          responseHelper.saveTransactions(executionContext, summary, transactions);
        }

        hasNextPage = StringUtils.hasLength(executionResponse.getNextPage()) && !executionResponse.getNextPage().equals(nextPage);
        nextPage = executionResponse.getNextPage();

        /* publish */
        // TODO : remove if condition after applying client code
        if (publishmentHelper != null) {
          financeMessageService.producePublishmentRequested(publishmentHelper.getMessageTopic(),
              publishmentHelper.makePublishmentRequestedMessage(executionContext, summary, hasNextPage));
        }

      } while (hasNextPage);

      /* update transaction_synced_at, response_code */
      if (isAllPaginationResponseOk) {
        responseHelper.saveTransactionSyncedAt(executionContext, summary, executionContext.getSyncStartedAt());
      }
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
