package com.banksalad.collectmydata.insu.loan;

import org.springframework.stereotype.Component;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.util.ObjectComparator;
import com.banksalad.collectmydata.finance.api.accountinfo.AccountInfoResponseHelper;
import com.banksalad.collectmydata.finance.api.accountinfo.dto.AccountResponse;
import com.banksalad.collectmydata.insu.common.db.entity.LoanBasicEntity;
import com.banksalad.collectmydata.insu.common.db.repository.LoanBasicHistoryRepository;
import com.banksalad.collectmydata.insu.common.db.repository.LoanBasicRepository;
import com.banksalad.collectmydata.insu.common.mapper.LoanBasicHistoryMapper;
import com.banksalad.collectmydata.insu.common.service.LoanSummaryService;
import com.banksalad.collectmydata.insu.loan.dto.GetLoanBasicResponse;
import com.banksalad.collectmydata.insu.loan.dto.LoanBasic;
import com.banksalad.collectmydata.insu.summary.dto.LoanSummary;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;

import static com.banksalad.collectmydata.finance.common.constant.FinanceConstant.ENTITY_EXCLUDE_FIELD;

@Component
@RequiredArgsConstructor
public class LoanBasicResponseHelper implements AccountInfoResponseHelper<LoanSummary, LoanBasic> {

  private final LoanSummaryService loanSummaryService;
  private final LoanBasicRepository loanBasicRepository;
  private final LoanBasicHistoryRepository loanBasicHistoryRepository;

  private final LoanBasicHistoryMapper loanBasicHistoryMapper = Mappers.getMapper(LoanBasicHistoryMapper.class);

  @Override
  public LoanBasic getAccountFromResponse(AccountResponse accountResponse) {
    return ((GetLoanBasicResponse) accountResponse).getLoanBasic();
  }

  @Override
  public void saveAccountAndHistory(ExecutionContext executionContext, LoanSummary loanSummary, LoanBasic loanBasic) {
    String accountNum = loanSummary.getAccountNum();
    long banksaladUserId = executionContext.getBanksaladUserId();
    String organizationId = executionContext.getOrganizationId();

    LoanBasicEntity loanBasicEntity = LoanBasicEntity.builder()
        .syncedAt(executionContext.getSyncStartedAt())
        .banksaladUserId(banksaladUserId)
        .organizationId(organizationId)
        .accountNum(accountNum)
        .loanStartDate(loanBasic.getLoanStartDate())
        .loanExpDate(loanBasic.getLoanExpDate())
        .repayMethod(loanBasic.getRepayMethod())
        .insuNum(loanBasic.getInsuNum())
        .build();

    LoanBasicEntity existingLoanBasicEntity = loanBasicRepository
        .findByBanksaladUserIdAndOrganizationIdAndAccountNum(banksaladUserId, organizationId, accountNum)
        .orElse(null);

    if (existingLoanBasicEntity != null) {
      loanBasicEntity.setId(existingLoanBasicEntity.getId());
    }

    if (!ObjectComparator.isSame(loanBasicEntity, existingLoanBasicEntity, ENTITY_EXCLUDE_FIELD)) {

      loanBasicRepository.save(loanBasicEntity);
      loanBasicHistoryRepository.save(loanBasicHistoryMapper.toHistoryEntity(loanBasicEntity));
    }
  }

  @Override
  public void saveSearchTimestamp(ExecutionContext executionContext, LoanSummary loanSummary, long searchTimestamp) {
    loanSummaryService
        .updateBasicSearchTimestamp(executionContext.getBanksaladUserId(), executionContext.getOrganizationId(),
            loanSummary.getAccountNum(), searchTimestamp);
  }

  @Override
  public void saveResponseCode(ExecutionContext executionContext, LoanSummary loanSummary, String responseCode) {
    loanSummaryService
        .updateBasicResponseCode(executionContext.getBanksaladUserId(), executionContext.getOrganizationId(),
            loanSummary.getAccountNum(), responseCode);
  }
}
