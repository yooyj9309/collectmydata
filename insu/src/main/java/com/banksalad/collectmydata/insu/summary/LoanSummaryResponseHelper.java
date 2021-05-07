package com.banksalad.collectmydata.insu.summary;

import org.springframework.stereotype.Component;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.finance.api.summary.SummaryResponseHelper;
import com.banksalad.collectmydata.finance.api.summary.dto.SummaryResponse;
import com.banksalad.collectmydata.insu.common.db.entity.LoanSummaryEntity;
import com.banksalad.collectmydata.insu.common.db.repository.LoanSummaryRepository;
import com.banksalad.collectmydata.insu.common.mapper.LoanSummaryMapper;
import com.banksalad.collectmydata.insu.summary.dto.ListLoanSummariesResponse;
import com.banksalad.collectmydata.insu.summary.dto.LoanSummary;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;

import java.util.Iterator;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class LoanSummaryResponseHelper implements SummaryResponseHelper<LoanSummary> {

  private final LoanSummaryRepository loanSummaryRepository;
  private final LoanSummaryMapper loanSummaryMapper = Mappers.getMapper(LoanSummaryMapper.class);

  @Override
  public Iterator<LoanSummary> iterator(SummaryResponse response) {
    return ((ListLoanSummariesResponse) response).getLoanList().iterator();
  }

  @Override
  public void saveOrganizationUser(ExecutionContext executionContext, SummaryResponse response) {

  }

  @Override
  public void saveSummary(ExecutionContext executionContext, LoanSummary loanSummary) {
    long banksaladUserId = executionContext.getBanksaladUserId();
    String organizationId = executionContext.getOrganizationId();

    LoanSummaryEntity loanSummaryEntity = loanSummaryRepository
        .findByBanksaladUserIdAndOrganizationIdAndAccountNum(
            banksaladUserId, organizationId, loanSummary.getAccountNum()
        ).orElse(LoanSummaryEntity.builder().build());

    // merge
    loanSummaryMapper.mergeDtoToEntity(loanSummary, loanSummaryEntity);

    // save (insert, update)
    loanSummaryEntity.setBanksaladUserId(banksaladUserId);
    loanSummaryEntity.setOrganizationId(organizationId);
    loanSummaryEntity.setSyncedAt(executionContext.getSyncStartedAt());
    loanSummaryEntity.setConsentId(executionContext.getConsentId());
    loanSummaryEntity.setSyncRequestId(executionContext.getSyncRequestId());
    loanSummaryEntity.setCreatedBy(executionContext.getRequestedBy());
    loanSummaryEntity.setUpdatedBy(executionContext.getRequestedBy());

    loanSummaryRepository.save(loanSummaryEntity);
  }
}
