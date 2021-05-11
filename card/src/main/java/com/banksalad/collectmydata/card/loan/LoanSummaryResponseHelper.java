package com.banksalad.collectmydata.card.loan;

import org.springframework.stereotype.Component;

import com.banksalad.collectmydata.card.common.db.entity.LoanSummaryEntity;
import com.banksalad.collectmydata.card.common.db.repository.LoanSummaryRepository;
import com.banksalad.collectmydata.card.common.mapper.LoanSummaryMapper;
import com.banksalad.collectmydata.card.loan.dto.GetLoanSummaryResponse;
import com.banksalad.collectmydata.card.loan.dto.LoanSummary;
import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.util.ObjectComparator;
import com.banksalad.collectmydata.finance.api.userbase.UserBaseResponseHelper;
import com.banksalad.collectmydata.finance.api.userbase.dto.UserBaseResponse;
import com.banksalad.collectmydata.finance.common.constant.FinanceConstant;
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

  /**
   * 6.3.9 DB 저장로직 : upsert
   * @author hyunjun
   */
  @Override
  public void saveUserBaseInfo(ExecutionContext executionContext, LoanSummary loanSummary) {

    LoanSummaryEntity loanSummaryEntity = loanSummaryMapper
        .dtoToEntity(loanSummary, LoanSummaryEntity.builder().build());
    loanSummaryEntity.setSyncedAt(executionContext.getSyncStartedAt());
    loanSummaryEntity.setBanksaladUserId(executionContext.getBanksaladUserId());
    loanSummaryEntity.setOrganizationId(executionContext.getOrganizationId());
    loanSummaryEntity.setCreatedBy(String.valueOf(executionContext.getBanksaladUserId()));
    loanSummaryEntity.setUpdatedBy(String.valueOf(executionContext.getBanksaladUserId()));
    loanSummaryEntity.setConsentId(executionContext.getConsentId());
    loanSummaryEntity.setSyncRequestId(executionContext.getSyncRequestId());

    LoanSummaryEntity existingEntity = loanSummaryRepository
        .findByBanksaladUserIdAndOrganizationId(executionContext.getBanksaladUserId(),
            executionContext.getOrganizationId()).orElse(null);

    if (existingEntity != null) {
      loanSummaryEntity.setId(existingEntity.getId());
      loanSummaryEntity.setCreatedBy(existingEntity.getCreatedBy());
      loanSummaryEntity.setCreatedAt(existingEntity.getCreatedAt());
    }

    /* update if entity has changed */
    if (!ObjectComparator.isSame(existingEntity, loanSummaryEntity, FinanceConstant.ENTITY_EXCLUDE_FIELD)) {
      loanSummaryRepository.save(loanSummaryEntity);
    }
  }
}
