package com.banksalad.collectmydata.finance.api.summary;

import com.banksalad.collectmydata.common.collect.execution.Execution;
import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.collect.execution.ExecutionRequest;
import com.banksalad.collectmydata.common.collect.execution.ExecutionResponse;
import com.banksalad.collectmydata.common.collect.executor.CollectExecutor;
import com.banksalad.collectmydata.common.exception.CollectRuntimeException;
import com.banksalad.collectmydata.finance.api.summary.dto.SummaryResponse;
import com.banksalad.collectmydata.finance.common.exception.ResponseNotOkException;
import com.banksalad.collectmydata.finance.common.service.UserSyncStatusService;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import java.util.Iterator;
import java.util.Map;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class SummaryServiceImpl<SummaryRequest, Summary> implements SummaryService<SummaryRequest, Summary> {

  private static final String AUTHORIZATION = "Authorization";

  private final CollectExecutor collectExecutor;
  private final UserSyncStatusService userSyncStatusService;

  public void listAccountSummaries(
      ExecutionContext executionContext,
      Execution execution,
      SummaryRequestHelper<SummaryRequest> requestHelper,
      SummaryResponseHelper<Summary> responseHelper
  ) throws ResponseNotOkException {

    /* copy ExecutionContext for new executionRequestId */
    ExecutionContext executionContextLocal = executionContext.copyWith(ExecutionContext.generateExecutionRequestId());

    if (executionContextLocal.getSyncRequestId() == null) {
      throw new CollectRuntimeException("syncRequestId is not setted");
    }

    long searchTimeStamp = userSyncStatusService.getSearchTimestamp(
        executionContext.getBanksaladUserId(), executionContext.getOrganizationId(), execution.getApi());

    ExecutionResponse<SummaryResponse> executionResponse;
    SummaryResponse summaryResponse;

    String nextPage = null;

    do {
      executionResponse = collectExecutor.execute(
          executionContextLocal,
          execution,
          ExecutionRequest.builder()
              .headers(Map.of(AUTHORIZATION, executionContext.getAccessToken()))
              .request(requestHelper.make(executionContext, searchTimeStamp, nextPage))
              .build());

      /* validate response  */
      checkResponseAndThrow(executionResponse);
      summaryResponse = executionResponse.getResponse();
      nextPage = executionResponse.getNextPage();

      /* upsert organization user */
      responseHelper.saveOragnizationUser(executionContext, summaryResponse);

      /* upsert detail */
      Iterator<Summary> iterator = responseHelper.iterator(summaryResponse);

      StreamSupport.stream(Spliterators.spliteratorUnknownSize(iterator, Spliterator.ORDERED), false)
          .forEach(summary -> responseHelper.saveSummary(executionContext, summary));

    } while (executionResponse.getNextPage() != null && executionResponse.getNextPage().length() > 0);

    userSyncStatusService.updateUserSyncStatus(
        executionContext.getBanksaladUserId(),
        executionContext.getOrganizationId(),
        execution.getApi().getId(),
        executionContext.getSyncStartedAt(),
        summaryResponse.getSearchTimestamp()
    );
  }

  private void checkResponseAndThrow(ExecutionResponse<SummaryResponse> executionResponse) throws ResponseNotOkException {
    SummaryResponse summaryResponse = executionResponse.getResponse();

    if (executionResponse.getHttpStatusCode() != HttpStatus.OK.value()) {

      throw new ResponseNotOkException(executionResponse.getHttpStatusCode(), summaryResponse.getRspCode(),
          summaryResponse.getRspMsg());
    }
  }
}
