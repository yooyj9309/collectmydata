package com.banksalad.collectmydata.card.card;

import com.banksalad.collectmydata.card.card.dto.ListApprovalOverseasRequest;
import com.banksalad.collectmydata.card.common.service.CardSummaryService;
import com.banksalad.collectmydata.card.summary.dto.CardSummary;
import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.finance.api.transaction.TransactionRequestHelper;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import static com.banksalad.collectmydata.finance.common.constant.FinanceConstant.DEFAULT_PAGING_LIMIT;
import static com.banksalad.collectmydata.finance.common.constant.FinanceConstant.DEFAULT_SEARCH_YEAR;

@Component
@RequiredArgsConstructor
public class ApprovalOverseasRequestHelper implements
    TransactionRequestHelper<CardSummary, ListApprovalOverseasRequest> {

  private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");

  private final CardSummaryService cardSummaryService;

  @Override
  public List<CardSummary> listSummaries(ExecutionContext executionContext) {

    return cardSummaryService.listSummariesConsented(executionContext.getBanksaladUserId(),
        executionContext.getOrganizationId());
  }

  @Override
  public LocalDateTime getTransactionSyncedAt(ExecutionContext executionContext, CardSummary cardSummary) {

    return Optional.ofNullable(cardSummary.getApprovalOverseasTransactionSyncedAt())
        .orElseGet(() -> executionContext.getSyncStartedAt().minusYears(DEFAULT_SEARCH_YEAR));
  }

  @Override
  public ListApprovalOverseasRequest make(ExecutionContext executionContext, CardSummary cardSummary,
      LocalDate fromDate, LocalDate toDate, String nextPage) {

    return ListApprovalOverseasRequest.builder()
        .orgCode(executionContext.getOrganizationCode())
        .fromDate(dateFormatter.format(fromDate))
        .toDate(dateFormatter.format(toDate))
        .nextPage(nextPage)
        .limit(DEFAULT_PAGING_LIMIT)
        .build();
  }
}
