package com.banksalad.collectmydata.irp.account;

import org.springframework.stereotype.Component;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.finance.api.transaction.TransactionRequestHelper;
import com.banksalad.collectmydata.irp.common.db.entity.IrpAccountSummaryEntity;
import com.banksalad.collectmydata.irp.common.db.repository.IrpAccountSummaryRepository;
import com.banksalad.collectmydata.irp.common.dto.IrpAccountSummary;
import com.banksalad.collectmydata.irp.common.dto.IrpAccountTransactionRequest;
import com.banksalad.collectmydata.irp.summary.IrpAccountSummaryService;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static com.banksalad.collectmydata.finance.common.constant.FinanceConstant.DEFAULT_SEARCH_YEAR;

@Component
@RequiredArgsConstructor
public class IrpAccountTransactionRequestHelper implements
    TransactionRequestHelper<IrpAccountSummary, IrpAccountTransactionRequest> {

  private static final int DEFAULT_LIMIT = 500;
  private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");

  private final IrpAccountSummaryService irpAccountSummaryService;
  private final IrpAccountSummaryRepository irpAccountSummaryRepository;

  @Override
  public List<IrpAccountSummary> listSummaries(ExecutionContext executionContext) {
    return irpAccountSummaryService
        .listConsentedAccountSummaries(executionContext.getBanksaladUserId(), executionContext.getOrganizationId());
  }

  @Override
  public LocalDateTime getTransactionSyncedAt(ExecutionContext executionContext, IrpAccountSummary irpAccountSummary) {
    return irpAccountSummaryRepository
        .findByBanksaladUserIdAndOrganizationIdAndAccountNumAndSeqno(executionContext.getBanksaladUserId(),
            executionContext.getOrganizationId(), irpAccountSummary.getAccountNum(), irpAccountSummary.getSeqno())
        .map(IrpAccountSummaryEntity::getTransactionSyncedAt)
        .orElseGet(() -> executionContext.getSyncStartedAt().minusYears(DEFAULT_SEARCH_YEAR));
  }

  @Override
  public IrpAccountTransactionRequest make(ExecutionContext executionContext, IrpAccountSummary accountSummary,
      LocalDate fromDate, LocalDate toDate, String nextPage) {

    return IrpAccountTransactionRequest.builder()
        .orgCode(executionContext.getOrganizationCode())
        .accountNum(accountSummary.getAccountNum())
        .seqno(accountSummary.getSeqno())
        .fromDate(dateFormatter.format(fromDate))
        .toDate(dateFormatter.format(toDate))
        .nextPage(nextPage)
        .limit(DEFAULT_LIMIT)
        .build();
  }
}
