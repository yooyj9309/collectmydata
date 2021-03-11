package com.banksalad.collectmydata.bank.loan;

import org.springframework.stereotype.Service;

import com.banksalad.collectmydata.bank.common.db.entity.LoanAccountBasicEntity;
import com.banksalad.collectmydata.bank.common.db.entity.mapper.LoanAccountBasicHistoryMapper;
import com.banksalad.collectmydata.bank.common.db.entity.mapper.LoanAccountBasicMapper;
import com.banksalad.collectmydata.bank.common.db.repository.LoanAccountBasicHistoryRepository;
import com.banksalad.collectmydata.bank.common.db.repository.LoanAccountBasicRepository;
import com.banksalad.collectmydata.bank.common.dto.AccountSummary;
import com.banksalad.collectmydata.bank.common.service.AccountSummaryService;
import com.banksalad.collectmydata.bank.common.service.ExternalApiService;
import com.banksalad.collectmydata.bank.common.service.UserSyncStatusService;
import com.banksalad.collectmydata.bank.loan.dto.GetLoanAccountBasicResponse;
import com.banksalad.collectmydata.bank.loan.dto.LoanAccountBasic;
import com.banksalad.collectmydata.bank.loan.dto.LoanAccountDetail;
import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.organization.Organization;
import com.banksalad.collectmydata.common.util.ObjectComparator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.factory.Mappers;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoanAccountServiceImpl implements LoanAccountService {

  private static final String[] EXCLUDE_FIELDS = {"syncedAt", "createdAt", "updatedAt", "createdBy", "updatedBy"};
  private final AccountSummaryService accountSummaryService;
  private final UserSyncStatusService userSyncStatusService;
  private final ExternalApiService externalApiService;

  private final LoanAccountBasicRepository loanAccountBasicRepository;
  private final LoanAccountBasicHistoryRepository loanAccountBasicHistoryRepository;

  private final LoanAccountBasicMapper loanAccountBasicMapper = Mappers.getMapper(LoanAccountBasicMapper.class);
  private final LoanAccountBasicHistoryMapper loanAccountBasicHistoryMapper = Mappers
      .getMapper(LoanAccountBasicHistoryMapper.class);

  @Override
  public List<LoanAccountBasic> listLoanAccountBasics(ExecutionContext executionContext,
      List<AccountSummary> accountSummaries) {
    Organization organization = getOrganization(executionContext);

    List<LoanAccountBasic> loanAccountBasics = new ArrayList<>();
    for (AccountSummary accountSummary : accountSummaries) {
      GetLoanAccountBasicResponse loanAccountBasicResponse = externalApiService.getLoanAccountBasic(
          executionContext, accountSummary, organization, accountSummary.getBasicSearchTimestamp());

      LoanAccountBasic loanAccountBasic = loanAccountBasicResponse.getLoanAccountBasic();
      try {
        saveLoanAccountBasic(executionContext, accountSummary, loanAccountBasic);
        loanAccountBasics.add(loanAccountBasic);
        accountSummaryService.updateBasicSearchTimestamp(
            executionContext.getBanksaladUserId(),
            executionContext.getOrganizationId(),
            accountSummary,
            loanAccountBasic.getSearchTimestamp());
      } catch (Exception e) {
        log.error("Failed to save loan account basic", e);
      }
    }

    return loanAccountBasics;
  }

  private void saveLoanAccountBasic(ExecutionContext executionContext, AccountSummary accountSummary,
      LoanAccountBasic loanAccountBasic) {
    LoanAccountBasicEntity loanAccountBasicEntity = loanAccountBasicMapper.dtoToEntity(loanAccountBasic);
    loanAccountBasicEntity.setBanksaladUserId(executionContext.getBanksaladUserId());
    loanAccountBasicEntity.setOrganizationId(executionContext.getOrganizationId());
    loanAccountBasicEntity.setSyncedAt(executionContext.getSyncStartedAt());
    loanAccountBasicEntity.setAccountNum(accountSummary.getAccountNum());
    loanAccountBasicEntity.setSeqno(accountSummary.getSeqno());

    LoanAccountBasicEntity existingLoanAccountBasicEntity = loanAccountBasicRepository
        .findByBanksaladUserIdAndOrganizationIdAndAccountNumAndSeqno(
            executionContext.getBanksaladUserId(),
            executionContext.getOrganizationId(),
            accountSummary.getAccountNum(),
            accountSummary.getSeqno()
        ).orElse(LoanAccountBasicEntity.builder().build());

    if (existingLoanAccountBasicEntity.getId() != null) {
      loanAccountBasicEntity.setId(existingLoanAccountBasicEntity.getId());
    }

    if (!ObjectComparator.isSame(loanAccountBasicEntity, existingLoanAccountBasicEntity, EXCLUDE_FIELDS)) {
      loanAccountBasicRepository.save(loanAccountBasicEntity);
      loanAccountBasicHistoryRepository
          .save(loanAccountBasicHistoryMapper.toLoanAccountBasicHistoryEntity(loanAccountBasicEntity));
    }
  }

  @Override
  public List<LoanAccountDetail> listLoanAccountDetails(ExecutionContext executionContext,
      List<AccountSummary> accountSummaries) {
    return null;
  }

  private Organization getOrganization(ExecutionContext executionContext) {
    return Organization.builder()
        .organizationCode("020")
        .build();
  }
}
