package com.banksalad.collectmydata.referencebank.summary;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.finance.api.summary.SummaryResponseHelper;
import com.banksalad.collectmydata.finance.api.summary.dto.SummaryResponse;
import com.banksalad.collectmydata.referencebank.common.db.entity.AccountSummaryEntity;
import com.banksalad.collectmydata.referencebank.common.db.entity.OrganizationUserEntity;
import com.banksalad.collectmydata.referencebank.common.db.repository.AccountSummaryRepository;
import com.banksalad.collectmydata.referencebank.common.db.repository.OrganizationUserRepository;
import com.banksalad.collectmydata.referencebank.common.mapper.AccountSummaryMapper;
import com.banksalad.collectmydata.referencebank.summary.dto.AccountSummary;
import com.banksalad.collectmydata.referencebank.summary.dto.ListAccountSummariesResponse;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;

import java.util.Iterator;

@Component
@RequiredArgsConstructor
public class BankSummaryResponseHelper implements SummaryResponseHelper<AccountSummary> {

  private final AccountSummaryRepository accountSummaryRepository;
  private final OrganizationUserRepository organizationUserRepository;

  private final AccountSummaryMapper accountSummaryMapper = Mappers.getMapper(AccountSummaryMapper.class);


  @Override
  public Iterator<AccountSummary> iterator(SummaryResponse summaryResponse) {
    return ((ListAccountSummariesResponse) summaryResponse).getAccountList().iterator();
  }

  @Override
  public void saveOrganizationUser(ExecutionContext executionContext, SummaryResponse response) {
    ListAccountSummariesResponse listAccountSummariesResponse = (ListAccountSummariesResponse) response;

    OrganizationUserEntity organizationUserEntity = organizationUserRepository
        .findByBanksaladUserIdAndOrganizationId(executionContext.getBanksaladUserId(),
            executionContext.getOrganizationId())
        .orElse(OrganizationUserEntity.builder()
            .syncedAt(executionContext.getSyncStartedAt())
            .banksaladUserId(executionContext.getBanksaladUserId())
            .organizationId(executionContext.getOrganizationId())
            .regDate(listAccountSummariesResponse.getRegDate())
            .build());
    organizationUserEntity.setCreatedBy(String.valueOf(executionContext.getBanksaladUserId()));
    organizationUserEntity.setUpdatedBy(String.valueOf(executionContext.getBanksaladUserId()));

    organizationUserRepository.save(organizationUserEntity);
  }

  @Override
  public void saveSummary(ExecutionContext executionContext, AccountSummary accountSummary) {
    AccountSummaryEntity accountSummaryEntity = accountSummaryRepository
        .findByBanksaladUserIdAndOrganizationIdAndAccountNumAndSeqno(
            executionContext.getBanksaladUserId(),
            executionContext.getOrganizationId(),
            accountSummary.getAccountNum(),
            accountSummary.getSeqno()
        ).orElse(AccountSummaryEntity.builder().build());

    // merge
    accountSummaryMapper.mergeDtoToEntity(accountSummary, accountSummaryEntity);

    // save
    accountSummaryEntity.setBanksaladUserId(executionContext.getBanksaladUserId());
    accountSummaryEntity.setOrganizationId(executionContext.getOrganizationId());
    accountSummaryEntity.setSyncedAt(executionContext.getSyncStartedAt());
    accountSummaryEntity.setCreatedBy(String.valueOf(executionContext.getBanksaladUserId()));
    accountSummaryEntity.setUpdatedBy(String.valueOf(executionContext.getBanksaladUserId()));
    accountSummaryRepository.save(accountSummaryEntity);
  }
}
