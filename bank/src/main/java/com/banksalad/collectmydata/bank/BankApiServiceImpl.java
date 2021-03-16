package com.banksalad.collectmydata.bank;

import org.springframework.stereotype.Service;

import com.banksalad.collectmydata.bank.common.collect.Executions;
import com.banksalad.collectmydata.bank.common.dto.BankApiResponse;
import com.banksalad.collectmydata.bank.summary.dto.AccountSummary;
import com.banksalad.collectmydata.bank.summary.dto.ListAccountSummariesRequest;
import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.enums.SyncRequestType;
import com.banksalad.collectmydata.common.util.DateUtil;
import com.banksalad.collectmydata.finance.api.summary.SummaryRequestHelper;
import com.banksalad.collectmydata.finance.api.summary.SummaryResponseHelper;
import com.banksalad.collectmydata.finance.api.summary.SummaryService;
import com.banksalad.collectmydata.finance.common.exception.ResponseNotOkException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Service
@RequiredArgsConstructor
public class BankApiServiceImpl implements BankApiService {

  private final SummaryService<ListAccountSummariesRequest, AccountSummary> accountSummaryService;

  private final SummaryRequestHelper<ListAccountSummariesRequest> summaryRequestHelper;
  private final SummaryResponseHelper<AccountSummary> summaryResponseHelper;

  @Override
  public BankApiResponse requestApi(long banksaladUserId, String organizationId, String syncRequestId,
      SyncRequestType syncRequestType) throws ResponseNotOkException {

    // TODO jayden-lee organizaion service 호출 해서 Organization 정보 가져 오기

    ExecutionContext executionContext = ExecutionContext.builder()
        .banksaladUserId(banksaladUserId)
        .executionRequestId(UUID.randomUUID().toString())
        .organizationId(organizationId)
        .accessToken("fixme")
        .organizationHost("http://whatever")
        .syncStartedAt(LocalDateTime.now(DateUtil.UTC_ZONE_ID))
        .build();

    accountSummaryService.listAccountSummaries(executionContext, Executions.finance_bank_summaries,
        summaryRequestHelper, summaryResponseHelper);

    AtomicReference<BankApiResponse> bankApiResponseAtomicReference = new AtomicReference<>();
    bankApiResponseAtomicReference.set(BankApiResponse.builder().build());

    CompletableFuture.allOf(
        // TODO 각 계좌별 서비스 호출 하고 응답을 bankApiResponse 에 저장
    ).join();

    return bankApiResponseAtomicReference.get();
  }
}
