package com.banksalad.collectmydata.bank.loan;

import org.springframework.stereotype.Component;

import com.banksalad.collectmydata.bank.common.db.entity.LoanAccountDetailEntity;
import com.banksalad.collectmydata.bank.common.db.entity.LoanAccountDetailHistoryEntity;
import com.banksalad.collectmydata.bank.common.db.repository.LoanAccountDetailHistoryRepository;
import com.banksalad.collectmydata.bank.common.db.repository.LoanAccountDetailRepository;
import com.banksalad.collectmydata.bank.common.mapper.LoanAccountDetailHistoryMapper;
import com.banksalad.collectmydata.bank.common.mapper.LoanAccountDetailMapper;
import com.banksalad.collectmydata.bank.common.service.AccountSummaryService;
import com.banksalad.collectmydata.bank.loan.dto.GetLoanAccountDetailResponse;
import com.banksalad.collectmydata.bank.loan.dto.LoanAccountDetail;
import com.banksalad.collectmydata.bank.summary.dto.AccountSummary;
import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.util.ObjectComparator;
import com.banksalad.collectmydata.finance.api.accountinfo.AccountInfoResponseHelper;
import com.banksalad.collectmydata.finance.api.accountinfo.dto.AccountResponse;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;

import static com.banksalad.collectmydata.common.util.ObjectComparator.*;
import static com.banksalad.collectmydata.finance.common.constant.FinanceConstant.ENTITY_EXCLUDE_FIELD;

@Component
@RequiredArgsConstructor
public class LoanAccountDetailInfoResponseHelper implements
    AccountInfoResponseHelper<AccountSummary, LoanAccountDetail> {

  private final AccountSummaryService accountSummaryService;

  private final LoanAccountDetailRepository loanAccountDetailRepository;
  private final LoanAccountDetailHistoryRepository loanAccountDetailHistoryRepository;

  private final LoanAccountDetailMapper loanAccountDetailMapper = Mappers.getMapper(LoanAccountDetailMapper.class);
  private final LoanAccountDetailHistoryMapper loanAccountDetailHistoryMapper = Mappers
      .getMapper(LoanAccountDetailHistoryMapper.class);

  @Override
  public LoanAccountDetail getAccountFromResponse(AccountResponse accountResponse) {
    return ((GetLoanAccountDetailResponse) accountResponse).getLoanAccountDetail();
  }

  @Override
  public void saveAccountAndHistory(ExecutionContext executionContext, AccountSummary accountSummary,
      LoanAccountDetail loanAccountDetail) {
    LoanAccountDetailEntity loanAccountDetailEntity = loanAccountDetailMapper.dtoToEntity(loanAccountDetail);
    loanAccountDetailEntity.setBanksaladUserId(executionContext.getBanksaladUserId());
    loanAccountDetailEntity.setOrganizationId(executionContext.getOrganizationId());
    loanAccountDetailEntity.setSyncedAt(executionContext.getSyncStartedAt());
    loanAccountDetailEntity.setAccountNum(accountSummary.getAccountNum());
    loanAccountDetailEntity.setSeqno(accountSummary.getSeqno());
    loanAccountDetailEntity.setConsentId(executionContext.getConsentId());
    loanAccountDetailEntity.setSyncRequestId(executionContext.getSyncRequestId());
    loanAccountDetailEntity.setCreatedBy(String.valueOf(executionContext.getBanksaladUserId()));
    loanAccountDetailEntity.setUpdatedBy(String.valueOf(executionContext.getBanksaladUserId()));

    LoanAccountDetailEntity existingLoanAccountDetailEntity = loanAccountDetailRepository
        .findByBanksaladUserIdAndOrganizationIdAndAccountNumAndSeqno(
            executionContext.getBanksaladUserId(),
            executionContext.getOrganizationId(),
            accountSummary.getAccountNum(),
            accountSummary.getSeqno())
        .orElse(null);

    if (existingLoanAccountDetailEntity != null) {
      loanAccountDetailEntity.setId(existingLoanAccountDetailEntity.getId());
      loanAccountDetailEntity.setCreatedBy(existingLoanAccountDetailEntity.getCreatedBy());
    }

    if (!isSame(loanAccountDetailEntity, existingLoanAccountDetailEntity, ENTITY_EXCLUDE_FIELD)) {
      loanAccountDetailRepository.save(loanAccountDetailEntity);

      LoanAccountDetailHistoryEntity loanAccountDetailHistoryEntity = loanAccountDetailHistoryMapper
          .entityToHistoryEntity(loanAccountDetailEntity, LoanAccountDetailHistoryEntity.builder().build());
      loanAccountDetailHistoryRepository.save(loanAccountDetailHistoryEntity);
    }
  }

  @Override
  public void saveSearchTimestamp(ExecutionContext executionContext, AccountSummary accountSummary,
      long searchTimestamp) {
    accountSummaryService.updateDetailSearchTimestamp(
        executionContext.getBanksaladUserId(),
        executionContext.getOrganizationId(),
        accountSummary,
        searchTimestamp
    );
  }

  @Override
  public void saveResponseCode(ExecutionContext executionContext, AccountSummary accountSummary, String responseCode) {
    accountSummaryService.updateDetailResponseCode(
        executionContext.getBanksaladUserId(),
        executionContext.getOrganizationId(),
        accountSummary,
        responseCode
    );
  }
}
