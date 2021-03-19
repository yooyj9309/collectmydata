package com.banksalad.collectmydata.telecom.telecom;

import org.springframework.stereotype.Component;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.finance.api.transaction.TransactionRequestHelper;
import com.banksalad.collectmydata.telecom.common.service.TelecomSummaryService;
import com.banksalad.collectmydata.telecom.summary.dto.TelecomSummary;
import com.banksalad.collectmydata.telecom.telecom.dto.ListTelecomTransactionsRequest;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static com.banksalad.collectmydata.finance.common.constant.FinanceConstant.*;

@Component
@RequiredArgsConstructor
public class TelecomTransactionRequestHelper implements
    TransactionRequestHelper<TelecomSummary, ListTelecomTransactionsRequest> {

  private final TelecomSummaryService telecomSummaryService;

  private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyyMM");

  @Override
  public List<TelecomSummary> listSummaries(ExecutionContext executionContext) {
    return telecomSummaryService
        .listSummariesConsented(executionContext.getBanksaladUserId(), executionContext.getOrganizationId());
  }

  @Override
  public LocalDateTime getTransactionSyncedAt(ExecutionContext executionContext, TelecomSummary telecomSummary) {
    if (telecomSummary.getTransactionSyncedAt() == null) {
      return executionContext.getSyncStartedAt().minusYears(DEFAULT_SEARCH_YEAR);
    }
    return telecomSummary.getTransactionSyncedAt();
  }

  @Override
  public ListTelecomTransactionsRequest make(ExecutionContext executionContext, TelecomSummary telecomSummary,
      LocalDate fromDate, LocalDate toDate, String nextPage) {
    return ListTelecomTransactionsRequest.builder()
        .orgCode(executionContext.getOrganizationCode())
        .mgmtId(telecomSummary.getMgmtId())
        .fromMonth(dateFormatter.format(fromDate))
        .toMonth(dateFormatter.format(toDate))
        .build();
  }
}
