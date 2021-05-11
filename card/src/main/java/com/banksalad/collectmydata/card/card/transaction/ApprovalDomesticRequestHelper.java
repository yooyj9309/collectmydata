package com.banksalad.collectmydata.card.card.transaction;

import org.springframework.stereotype.Component;

import com.banksalad.collectmydata.card.card.dto.ListApprovalDomesticRequest;
import com.banksalad.collectmydata.card.common.service.CardSummaryService;
import com.banksalad.collectmydata.card.summary.dto.CardSummary;
import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.finance.api.transaction.TransactionRequestHelper;
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
public class ApprovalDomesticRequestHelper implements
    TransactionRequestHelper<CardSummary, ListApprovalDomesticRequest> {

  private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");

  private final CardSummaryService cardSummaryService;

  @Override
  public List<CardSummary> listSummaries(ExecutionContext executionContext) {
    return cardSummaryService.listSummariesConsented(executionContext.getBanksaladUserId(),
        executionContext.getOrganizationId());
  }

  @Override
  public LocalDateTime getTransactionSyncedAt(ExecutionContext executionContext, CardSummary cardSummary) {
    return Optional.ofNullable(cardSummary.getApprovalDomesticTransactionSyncedAt())
        .orElseGet(() -> executionContext.getSyncStartedAt().minusYears(DEFAULT_SEARCH_YEAR));
  }

  @Override
  public ListApprovalDomesticRequest make(ExecutionContext executionContext, CardSummary cardSummary,
      LocalDate fromDate, LocalDate toDate, String nextPage) {

    return ListApprovalDomesticRequest.builder()
        .cardId(cardSummary.getCardId())
        .orgCode(executionContext.getOrganizationCode())
        .fromDate(dateFormatter.format(fromDate))
        .toDate(dateFormatter.format(toDate))
        .nextPage(nextPage)
        .limit(DEFAULT_PAGING_LIMIT)
        .build();
  }
}
