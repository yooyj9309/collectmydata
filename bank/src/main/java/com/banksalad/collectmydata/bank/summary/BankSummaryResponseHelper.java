package com.banksalad.collectmydata.bank.summary;

import org.springframework.stereotype.Component;

import com.banksalad.collectmydata.bank.common.db.entity.AccountSummaryEntity;
import com.banksalad.collectmydata.bank.common.db.entity.OrganizationUserEntity;
import com.banksalad.collectmydata.bank.common.db.repository.AccountSummaryRepository;
import com.banksalad.collectmydata.bank.common.db.repository.OrganizationUserRepository;
import com.banksalad.collectmydata.bank.common.mapper.AccountSummaryMapper;
import com.banksalad.collectmydata.bank.summary.dto.AccountSummary;
import com.banksalad.collectmydata.bank.summary.dto.ListAccountSummariesResponse;
import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.finance.api.summary.SummaryResponseHelper;
import com.banksalad.collectmydata.finance.api.summary.dto.SummaryResponse;
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
  public Iterator<AccountSummary> iterator(SummaryResponse response) {
    return ((ListAccountSummariesResponse) response).getAccountList().iterator();
  }

  @Override
  public void saveOrganizationUser(ExecutionContext executionContext, SummaryResponse response) {
    ListAccountSummariesResponse listAccountSummariesResponse = (ListAccountSummariesResponse) response;

    OrganizationUserEntity organizationUserEntity = organizationUserRepository
        .findByBanksaladUserIdAndOrganizationId(executionContext.getBanksaladUserId(),
            executionContext.getOrganizationId())
        .orElseGet(() -> {
          OrganizationUserEntity createdUserEntity = OrganizationUserEntity.builder()
              .syncedAt(executionContext.getSyncStartedAt())
              .banksaladUserId(executionContext.getBanksaladUserId())
              .organizationId(executionContext.getOrganizationId())
              .consentId(executionContext.getConsentId())
              .syncRequestId(executionContext.getSyncRequestId())
              .regDate(listAccountSummariesResponse.getRegDate())
              .build();
          createdUserEntity.setCreatedBy(String.valueOf(executionContext.getRequestedBy()));
          createdUserEntity.setUpdatedBy(String.valueOf(executionContext.getRequestedBy()));

          return createdUserEntity;
        });

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
        )
        .orElseGet(() -> {
          AccountSummaryEntity newAccountSummaryEntity = AccountSummaryEntity.builder().build();
          newAccountSummaryEntity.setCreatedBy(executionContext.getRequestedBy());
          return newAccountSummaryEntity;
        });

    // merge
    accountSummaryMapper.mergeDtoToEntity(accountSummary, accountSummaryEntity);

    // save
    accountSummaryEntity.setBanksaladUserId(executionContext.getBanksaladUserId());
    accountSummaryEntity.setOrganizationId(executionContext.getOrganizationId());
    accountSummaryEntity.setSyncedAt(executionContext.getSyncStartedAt());
    accountSummaryEntity.setCreatedBy(String.valueOf(executionContext.getRequestedBy()));
    accountSummaryEntity.setUpdatedBy(String.valueOf(executionContext.getRequestedBy()));
    accountSummaryEntity.setConsentId(executionContext.getConsentId());
    accountSummaryEntity.setSyncRequestId(executionContext.getSyncRequestId());

    accountSummaryRepository.save(accountSummaryEntity);
  }
}
