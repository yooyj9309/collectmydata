package com.banksalad.collectmydata.ginsu;

import org.springframework.stereotype.Service;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.enums.SyncRequestType;
import com.banksalad.collectmydata.common.util.DateUtil;
import com.banksalad.collectmydata.finance.api.summary.SummaryRequestHelper;
import com.banksalad.collectmydata.finance.api.summary.SummaryResponseHelper;
import com.banksalad.collectmydata.finance.api.summary.SummaryService;
import com.banksalad.collectmydata.finance.common.exception.ResponseNotOkException;
import com.banksalad.collectmydata.ginsu.collect.Executions;
import com.banksalad.collectmydata.ginsu.common.dto.GinsuApiResponse;
import com.banksalad.collectmydata.ginsu.summary.dto.GinsuSummary;
import com.banksalad.collectmydata.ginsu.summary.dto.ListGinsuSummariesRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Service
@RequiredArgsConstructor
public class GinsuApiServiceImpl implements GinsuApiService {

  private final SummaryService<ListGinsuSummariesRequest, GinsuSummary> ginsuSummaryService;
  private final SummaryRequestHelper<ListGinsuSummariesRequest> summaryRequestHelper;
  private final SummaryResponseHelper<GinsuSummary> summaryResponseHelper;

  @Override
  public GinsuApiResponse requestApi(long banksaladUserId, String organizationId, String syncRequestId,
      SyncRequestType syncRequestType) throws ResponseNotOkException {

    ExecutionContext executionContext = ExecutionContext.builder()
        .banksaladUserId(banksaladUserId)
        .executionRequestId(UUID.randomUUID().toString())
        .organizationId(organizationId)
        .accessToken("fixme")
        .organizationHost("http://whatever")
        .syncStartedAt(LocalDateTime.now(DateUtil.UTC_ZONE_ID))
        .build();

    ginsuSummaryService.listAccountSummaries(
        executionContext,
        Executions.finance_ginsu_summaries,
        summaryRequestHelper,
        summaryResponseHelper
    );

    AtomicReference<GinsuApiResponse> ginsuApiResponse = new AtomicReference<>();
    ginsuApiResponse.set(GinsuApiResponse.builder().build());

    CompletableFuture.allOf().join();
    return ginsuApiResponse.get();
  }
}
