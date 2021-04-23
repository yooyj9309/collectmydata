package com.banksalad.collectmydata.bank.loan;

import org.springframework.stereotype.Component;

import com.banksalad.collectmydata.bank.common.db.entity.LoanAccountBasicEntity;
import com.banksalad.collectmydata.bank.common.db.repository.LoanAccountBasicHistoryRepository;
import com.banksalad.collectmydata.bank.common.db.repository.LoanAccountBasicRepository;
import com.banksalad.collectmydata.bank.common.mapper.LoanAccountBasicHistoryMapper;
import com.banksalad.collectmydata.bank.common.mapper.LoanAccountBasicMapper;
import com.banksalad.collectmydata.bank.common.service.AccountSummaryService;
import com.banksalad.collectmydata.bank.loan.dto.GetLoanAccountBasicResponse;
import com.banksalad.collectmydata.bank.loan.dto.LoanAccountBasic;
import com.banksalad.collectmydata.bank.summary.dto.AccountSummary;
import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.util.ObjectComparator;
import com.banksalad.collectmydata.finance.api.accountinfo.AccountInfoResponseHelper;
import com.banksalad.collectmydata.finance.api.accountinfo.dto.AccountResponse;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;

import static com.banksalad.collectmydata.finance.common.constant.FinanceConstant.ENTITY_EXCLUDE_FIELD;

@Component
@RequiredArgsConstructor
public class LoanAccountBasicInfoResponseHelper implements AccountInfoResponseHelper<AccountSummary, LoanAccountBasic> {

  private final AccountSummaryService accountSummaryService;

  private final LoanAccountBasicRepository loanAccountBasicRepository;
  private final LoanAccountBasicHistoryRepository loanAccountBasicHistoryRepository;

  private final LoanAccountBasicMapper loanAccountBasicMapper = Mappers.getMapper(LoanAccountBasicMapper.class);
  private final LoanAccountBasicHistoryMapper loanAccountBasicHistoryMapper = Mappers
      .getMapper(LoanAccountBasicHistoryMapper.class);

  @Override
  public LoanAccountBasic getAccountFromResponse(AccountResponse accountResponse) {
    return ((GetLoanAccountBasicResponse) accountResponse).getLoanAccountBasic();
  }

  @Override
  public void saveAccountAndHistory(ExecutionContext executionContext, AccountSummary accountSummary,
      LoanAccountBasic loanAccountBasic) {
    LoanAccountBasicEntity loanAccountBasicEntity = loanAccountBasicMapper.dtoToEntity(loanAccountBasic);
    loanAccountBasicEntity.setBanksaladUserId(executionContext.getBanksaladUserId());
    loanAccountBasicEntity.setOrganizationId(executionContext.getOrganizationId());
    loanAccountBasicEntity.setSyncedAt(executionContext.getSyncStartedAt());
    loanAccountBasicEntity.setAccountNum(accountSummary.getAccountNum());
    loanAccountBasicEntity.setSeqno(accountSummary.getSeqno());

    // TODO : on-demand, scheduler
    loanAccountBasicEntity.setCreatedBy(String.valueOf(executionContext.getBanksaladUserId()));
    loanAccountBasicEntity.setUpdatedBy(String.valueOf(executionContext.getBanksaladUserId()));
    loanAccountBasicEntity.setConsentId(executionContext.getConsentId());

    LoanAccountBasicEntity existingLoanAccountBasicEntity = loanAccountBasicRepository
        .findByBanksaladUserIdAndOrganizationIdAndAccountNumAndSeqno(
            executionContext.getBanksaladUserId(),
            executionContext.getOrganizationId(),
            accountSummary.getAccountNum(),
            accountSummary.getSeqno()
        ).orElse(null);

    if (existingLoanAccountBasicEntity != null) {
      loanAccountBasicEntity.setId(existingLoanAccountBasicEntity.getId());
    }

    if (!ObjectComparator.isSame(loanAccountBasicEntity, existingLoanAccountBasicEntity, ENTITY_EXCLUDE_FIELD)) {
      loanAccountBasicRepository.save(loanAccountBasicEntity);
      loanAccountBasicHistoryRepository
          .save(loanAccountBasicHistoryMapper.toLoanAccountBasicHistoryEntity(loanAccountBasicEntity));
    }
  }

  @Override
  public void saveSearchTimestamp(ExecutionContext executionContext, AccountSummary accountSummary,
      long searchTimestamp) {
    accountSummaryService.updateBasicSearchTimestamp(
        executionContext.getBanksaladUserId(),
        executionContext.getOrganizationId(),
        accountSummary,
        searchTimestamp
    );
  }

  @Override
  public void saveResponseCode(ExecutionContext executionContext, AccountSummary accountSummary, String responseCode) {
    accountSummaryService.updateBasicResponseCode(
        executionContext.getBanksaladUserId(),
        executionContext.getOrganizationId(),
        accountSummary,
        responseCode
    );
  }
}
