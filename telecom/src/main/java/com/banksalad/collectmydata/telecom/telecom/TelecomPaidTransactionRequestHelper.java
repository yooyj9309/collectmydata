package com.banksalad.collectmydata.telecom.telecom;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.finance.api.transaction.TransactionRequestHelper;
import com.banksalad.collectmydata.telecom.common.db.repository.TelecomSummaryRepository;
import com.banksalad.collectmydata.telecom.common.service.TelecomSummaryService;
import com.banksalad.collectmydata.telecom.summary.dto.TelecomSummary;
import com.banksalad.collectmydata.telecom.telecom.dto.ListTelecomPaidTransactionsRequest;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
@RequiredArgsConstructor
public class TelecomPaidTransactionRequestHelper implements
    TransactionRequestHelper<TelecomSummary, ListTelecomPaidTransactionsRequest> {

  // TODO: 이것도 finance constant로부터?
  private static final int DEFAULT_LIMIT = 500;
  // TODO: common DateUtil 경과에 따라 변경
  private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");

  private final TelecomSummaryService telecomSummaryService;
  private final TelecomSummaryRepository telecomSummaryRepository;

  @Override
  public List<TelecomSummary> listSummaries(ExecutionContext executionContext) {

    return telecomSummaryService
        .listSummariesConsented(executionContext.getBanksaladUserId(), executionContext.getOrganizationId());
  }

  @Override
  public LocalDateTime getTransactionSyncedAt(ExecutionContext executionContext, TelecomSummary telecomSummary) {

    return telecomSummaryService.getPaidTransactionSyncedAt(executionContext.getBanksaladUserId(),
        executionContext.getOrganizationId(), telecomSummary);
  }

  @Override
  public ListTelecomPaidTransactionsRequest make(ExecutionContext executionContext, TelecomSummary telecomSummary,
      LocalDate fromDate, LocalDate toDate, String nextPage) {

    return ListTelecomPaidTransactionsRequest.builder()
        .orgCode(executionContext.getOrganizationCode())
        .mgmtId(telecomSummary.getMgmtId())
        .fromDate(dateFormatter.format(fromDate))
        .toDate(dateFormatter.format(toDate))
        .nextPage(nextPage)
        .limit(DEFAULT_LIMIT)
        .build();
  }
}
