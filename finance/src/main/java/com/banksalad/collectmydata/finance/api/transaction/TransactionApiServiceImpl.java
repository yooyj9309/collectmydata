package com.banksalad.collectmydata.finance.api.transaction;

import com.banksalad.collectmydata.common.collect.execution.Execution;
import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.collect.execution.ExecutionRequest;
import com.banksalad.collectmydata.common.collect.execution.ExecutionResponse;
import com.banksalad.collectmydata.common.collect.executor.CollectExecutor;
import com.banksalad.collectmydata.finance.api.transaction.dto.TransactionResponse;
import com.banksalad.collectmydata.finance.common.service.UserSyncStatusService;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionApiServiceImpl<Summary, TransactionRequest, Transaction> implements
    TransactionApiService<Summary, TransactionRequest, Transaction> {

  private static final String AUTHORIZATION = "Authorization";

  private final CollectExecutor collectExecutor;
  private final UserSyncStatusService userSyncStatusService;

  private final Executor executor;

  public List<Transaction> listTransactions(
      ExecutionContext executionContext,
      Execution execution,
      TransactionRequestHelper<Summary, TransactionRequest> requestHelper,
      TransactionResponseHelper<Summary, Transaction> responseHelper
  ) {
    List<Transaction> transactionsAll = new ArrayList<>();

    List<Summary> summaries = requestHelper.listSummaries(executionContext);

    ExecutionResponse<TransactionResponse> executionResponse;
    TransactionResponse transactionResponse;

    for (Summary summary : summaries) {
      /* copy ExecutionContext for new executionRequestId */
      ExecutionContext executionContextLocal = executionContext.copyWith(ExecutionContext.generateExecutionRequestId());

      LocalDateTime fromDateTime = requestHelper.getTransactionSyncedAt(executionContext, summary);
      LocalDateTime endDateTime = executionContext.getSyncStartedAt();

      String nextPage = null;

      do {
        executionResponse = collectExecutor.execute(
            executionContextLocal,
            execution,
            ExecutionRequest.builder()
                .headers(Map.of(AUTHORIZATION, executionContext.getAccessToken()))
                .request(requestHelper
                    .make(executionContext, summary, fromDateTime.toLocalDate(), endDateTime.toLocalDate(), nextPage))
                .build());

        /* populate response */
        transactionResponse = executionResponse.getResponse();

        /* validate response and break pagination */
        if (executionResponse.getHttpStatusCode() != HttpStatus.OK.value()) {
          responseHelper.saveResponseCode(executionContext, summary, transactionResponse.getRspCode());
          break;
        }

        /* save transactions */
        List<Transaction> transactions = responseHelper.getTransactionsFromResponse(executionResponse.getResponse());
        responseHelper.saveTransactions(executionContext, summary, transactions);

        transactionsAll.addAll(transactions);

        nextPage = executionResponse.getNextPage();
      } while (executionResponse.getNextPage() != null && executionResponse.getNextPage().length() > 0);

      /* update transaction_synced_at, response_code */
      responseHelper.saveTransactionSyncedAt(executionContext, summary, executionContext.getSyncStartedAt());
    }

    userSyncStatusService.updateUserSyncStatus(
        executionContext.getBanksaladUserId(),
        executionContext.getOrganizationId(),
        execution.getApi().getId(),
        executionContext.getSyncStartedAt(),
        0L
    );

    return transactionsAll;
  }

//  @Override
//  public List<Transaction> listTransactions(
//      ExecutionContext executionContext,
//      Execution execution,
//      TransactionRequestHelper<Summary, TransactionRequest> requestHelper,
//      TransactionResponseHelper<Summary, Transaction> responseHelper
//  ) {
//    List<Summary> summaries = requestHelper.listSummaries(executionContext);
//
//    ExecutionContext executionContextLocal = executionContext.copyWith(ExecutionContext.generateExecutionRequestId());
//    AtomicReference<Boolean> isExceptionOccurredReference = new AtomicReference<>(false);
//
//    List<Transaction> transactions = summaries.stream()
//        .map(summary -> CompletableFuture
//            .supplyAsync(() -> processTransaction(executionContextLocal, execution, requestHelper, responseHelper, summary),
//                executor)
//            .exceptionally(e -> {
//              log.error("Fail to request transaction", e);
//              isExceptionOccurredReference.set(true);
//              return null;
//            })
//        )
//        .map(CompletableFuture::join)
//        .filter(Objects::nonNull)
//        .flatMap(List::stream)
//        .collect(Collectors.toList());
//
//    userSyncStatusService.updateUserSyncStatus(
//        executionContext.getBanksaladUserId(),
//        executionContext.getOrganizationId(),
//        execution.getApi().getId(),
//        executionContext.getSyncStartedAt(),
//        0L
//    );
//
//    return transactions;
//  }
//
//  private List<Transaction> processTransaction(
//      ExecutionContext executionContext,
//      Execution execution,
//      TransactionRequestHelper<Summary, TransactionRequest> requestHelper,
//      TransactionResponseHelper<Summary, Transaction> responseHelper,
//      Summary summary) {
//
//    LocalDateTime fromDateTime = requestHelper.getTransactionSyncedAt(executionContext, summary);
//    LocalDateTime endDateTime = executionContext.getSyncStartedAt();
//
//    List<Transaction> transactions = new ArrayList<>();
//
//    ExecutionResponse<TransactionResponse> executionResponse;
//    TransactionResponse transactionResponse;
//
//    String nextPage = null;
//
//    do {
//      executionResponse = collectExecutor.execute(
//          executionContext,
//          execution,
//          ExecutionRequest.builder()
//              .headers(Map.of(AUTHORIZATION, executionContext.getAccessToken()))
//              .request(requestHelper
//                  .make(executionContext, summary, fromDateTime.toLocalDate(), endDateTime.toLocalDate(), nextPage))
//              .build());
//
//      /* populate response */
//      transactionResponse = executionResponse.getResponse();
//
//      /* validate response and break pagination */
//      if (executionResponse.getHttpStatusCode() != HttpStatus.OK.value()) {
//        responseHelper.saveResponseCode(executionContext, summary, transactionResponse.getRspCode());
//        break;
//      }
//
//      /* save transactions */
//      List<Transaction> transactionsLocal = responseHelper.getTransactionsFromResponse(executionResponse.getResponse());
//      responseHelper.saveTransactions(executionContext, summary, transactionsLocal);
//
//      transactions.addAll(transactionsLocal);
//
//      nextPage = executionResponse.getNextPage();
//    } while (executionResponse.getNextPage() != null && executionResponse.getNextPage().length() > 0);
//
//    /* update transaction_synced_at, response_code */
//    responseHelper.saveTransactionSyncedAt(executionContext, summary, executionContext.getSyncStartedAt());
//
//    return transactions;
//  }


}
