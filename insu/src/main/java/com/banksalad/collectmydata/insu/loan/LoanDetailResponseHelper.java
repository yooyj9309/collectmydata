package com.banksalad.collectmydata.insu.loan;

import org.springframework.stereotype.Component;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.util.ObjectComparator;
import com.banksalad.collectmydata.finance.api.accountinfo.AccountInfoResponseHelper;
import com.banksalad.collectmydata.finance.api.accountinfo.dto.AccountResponse;
import com.banksalad.collectmydata.insu.common.db.entity.LoanDetailEntity;
import com.banksalad.collectmydata.insu.common.db.repository.LoanDetailHistoryRepository;
import com.banksalad.collectmydata.insu.common.db.repository.LoanDetailRepository;
import com.banksalad.collectmydata.insu.common.mapper.LoanDetailHistoryMapper;
import com.banksalad.collectmydata.insu.common.service.LoanSummaryService;
import com.banksalad.collectmydata.insu.loan.dto.GetLoanDetailResponse;
import com.banksalad.collectmydata.insu.loan.dto.LoanDetail;
import com.banksalad.collectmydata.insu.summary.dto.LoanSummary;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;

import static com.banksalad.collectmydata.finance.common.constant.FinanceConstant.ENTITY_EXCLUDE_FIELD;

@Component
@RequiredArgsConstructor
public class LoanDetailResponseHelper implements AccountInfoResponseHelper<LoanSummary, LoanDetail> {

  private final LoanSummaryService loanSummaryService;
  private final LoanDetailRepository loanDetailRepository;
  private final LoanDetailHistoryRepository loanDetailHistoryRepository;
  private final LoanDetailHistoryMapper loanDetailHistoryMapper = Mappers.getMapper(LoanDetailHistoryMapper.class);

  @Override
  public LoanDetail getAccountFromResponse(AccountResponse accountResponse) {
    return ((GetLoanDetailResponse) accountResponse).getLoanDetail();
  }

  @Override
  public void saveAccountAndHistory(ExecutionContext executionContext, LoanSummary loanSummary, LoanDetail loanDetail) {
    final long banksaladUserId = executionContext.getBanksaladUserId();
    final String organizationId = executionContext.getOrganizationId();
    final String accountNum = loanSummary.getAccountNum();

    LoanDetailEntity loanDetailEntity = LoanDetailEntity.builder()
        .syncedAt(executionContext.getSyncStartedAt())
        .banksaladUserId(banksaladUserId)
        .organizationId(organizationId)
        .accountNum(accountNum)
        .currencyCode(loanDetail.getCurrencyCode())
        .balanceAmt(loanDetail.getBalanceAmt())
        .loanPrincipal(loanDetail.getLoanPrincipal())
        .nextRepayDate(loanDetail.getNextRepayDate())
        .build();

    LoanDetailEntity existingLoanDetailEntity = loanDetailRepository
        .findByBanksaladUserIdAndOrganizationIdAndAccountNum(
            banksaladUserId, organizationId, accountNum
        ).orElse(null);

    if (existingLoanDetailEntity != null) {
      loanDetailEntity.setId(existingLoanDetailEntity.getId());
    }

    if (!ObjectComparator.isSame(loanDetailEntity, existingLoanDetailEntity, ENTITY_EXCLUDE_FIELD)) {
      loanDetailRepository.save(loanDetailEntity);
      loanDetailHistoryRepository.save(loanDetailHistoryMapper.toHistoryEntity(loanDetailEntity));
    }
  }

  @Override
  public void saveSearchTimestamp(ExecutionContext executionContext, LoanSummary loanSummary, long searchTimestamp) {
    loanSummaryService
        .updateDetailSearchTimestamp(executionContext.getBanksaladUserId(), executionContext.getOrganizationId(),
            loanSummary.getAccountNum(), searchTimestamp);
  }

  @Override
  public void saveResponseCode(ExecutionContext executionContext, LoanSummary loanSummary, String responseCode) {
    loanSummaryService
        .updateDetailResponseCode(executionContext.getBanksaladUserId(), executionContext.getOrganizationId(),
            loanSummary.getAccountNum(), responseCode);
  }
}
