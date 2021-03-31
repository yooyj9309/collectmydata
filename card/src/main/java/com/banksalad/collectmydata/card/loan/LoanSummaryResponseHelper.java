package com.banksalad.collectmydata.card.loan;

import org.springframework.stereotype.Component;

import com.banksalad.collectmydata.card.common.db.entity.LoanSummaryEntity;
import com.banksalad.collectmydata.card.common.db.repository.LoanSummaryRepository;
import com.banksalad.collectmydata.card.common.mapper.LoanSummaryMapper;
import com.banksalad.collectmydata.card.loan.dto.GetLoanSummaryResponse;
import com.banksalad.collectmydata.card.loan.dto.LoanSummary;
import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.finance.api.userbase.UserBaseResponseHelper;
import com.banksalad.collectmydata.finance.api.userbase.dto.UserBaseResponse;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;

@Component
@RequiredArgsConstructor
public class LoanSummaryResponseHelper implements UserBaseResponseHelper<LoanSummary> {

  private final LoanSummaryRepository loanSummaryRepository;

  private final LoanSummaryMapper loanSummaryMapper = Mappers.getMapper(LoanSummaryMapper.class);

  @Override
  public LoanSummary getUserBaseInfoFromResponse(UserBaseResponse userBaseResponse) {
    return ((GetLoanSummaryResponse) userBaseResponse).getLoanSummary();
  }

  @Override
  public void saveUserBaseInfo(ExecutionContext executionContext, LoanSummary loanSummary) {
    /* load existing entity */
    LoanSummaryEntity loanSummaryEntity = loanSummaryRepository
        .findByBanksaladUserIdAndOrganizationId(
            executionContext.getBanksaladUserId(), executionContext.getOrganizationId())
        .orElseGet(() -> LoanSummaryEntity.builder().build());

    /* mapping dto to entity */
    loanSummaryEntity = loanSummaryMapper.dtoToEntity(loanSummary, loanSummaryEntity);

    loanSummaryEntity.setSyncedAt(executionContext.getSyncStartedAt());
    loanSummaryEntity.setBanksaladUserId(executionContext.getBanksaladUserId());
    loanSummaryEntity.setOrganizationId(executionContext.getOrganizationId());

    /* upsert entity */
    loanSummaryRepository.save(loanSummaryEntity);
  }
}
