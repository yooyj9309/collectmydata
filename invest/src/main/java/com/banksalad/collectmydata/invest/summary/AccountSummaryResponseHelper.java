package com.banksalad.collectmydata.invest.summary;

import org.springframework.stereotype.Component;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.finance.api.summary.SummaryResponseHelper;
import com.banksalad.collectmydata.finance.api.summary.dto.SummaryResponse;
import com.banksalad.collectmydata.invest.common.db.entity.AccountSummaryEntity;
import com.banksalad.collectmydata.invest.common.db.entity.OrganizationUserEntity;
import com.banksalad.collectmydata.invest.common.db.entity.mapper.AccountSummaryMapper;
import com.banksalad.collectmydata.invest.common.db.repository.AccountSummaryRepository;
import com.banksalad.collectmydata.invest.common.db.repository.OrganizationUserRepository;
import com.banksalad.collectmydata.invest.summary.dto.AccountSummary;
import com.banksalad.collectmydata.invest.summary.dto.ListAccountSummariesResponse;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;

import java.util.Iterator;

@Component
@RequiredArgsConstructor
public class AccountSummaryResponseHelper implements SummaryResponseHelper<AccountSummary> {

  private final AccountSummaryRepository accountSummaryRepository;
  private final OrganizationUserRepository organizationUserRepository;

  private final AccountSummaryMapper accountSummaryMapper = Mappers.getMapper(AccountSummaryMapper.class);

  @Override
  public Iterator<AccountSummary> iterator(SummaryResponse response) {
    return ((ListAccountSummariesResponse) response).getAccountSummaries().iterator();
  }

  @Override
  public void saveOrganizationUser(ExecutionContext executionContext, SummaryResponse response) {
    ListAccountSummariesResponse listAccountSummariesResponse = (ListAccountSummariesResponse) response;

    OrganizationUserEntity organizationUserEntity = organizationUserRepository
        .findByBanksaladUserIdAndOrganizationId(executionContext.getBanksaladUserId(),
            executionContext.getOrganizationId())
        .orElseGet(() -> {
          OrganizationUserEntity newOrganizationUserEntity = OrganizationUserEntity.builder()
              .syncedAt(executionContext.getSyncStartedAt())
              .banksaladUserId(executionContext.getBanksaladUserId())
              .organizationId(executionContext.getOrganizationId())
              .consentId(executionContext.getConsentId())
              .syncRequestId(executionContext.getSyncRequestId())
              .regDate(listAccountSummariesResponse.getRegDate())
              .consentId(executionContext.getConsentId())
              .organizationId(executionContext.getOrganizationId())
              .build();

          newOrganizationUserEntity.setCreatedBy(executionContext.getRequestedBy());
          return newOrganizationUserEntity;
        });

    organizationUserEntity.setUpdatedBy(executionContext.getRequestedBy());
    organizationUserRepository.save(organizationUserEntity);
  }

  @Override
  public void saveSummary(ExecutionContext executionContext, AccountSummary accountSummary) {
    AccountSummaryEntity accountSummaryEntity = accountSummaryRepository
        .findByBanksaladUserIdAndOrganizationIdAndAccountNum(executionContext.getBanksaladUserId(),
            executionContext.getOrganizationId(), accountSummary.getAccountNum())
        .orElseGet(() -> {
          AccountSummaryEntity newAccountSummaryEntity = AccountSummaryEntity.builder().build();
          newAccountSummaryEntity.setCreatedBy(executionContext.getRequestedBy());
          return newAccountSummaryEntity;
        });

    accountSummaryMapper.merge(accountSummary, accountSummaryEntity);
    accountSummaryEntity.setBanksaladUserId(executionContext.getBanksaladUserId());
    accountSummaryEntity.setOrganizationId(executionContext.getOrganizationId());
    accountSummaryEntity.setSyncedAt(executionContext.getSyncStartedAt());
    accountSummaryEntity.setConsentId(executionContext.getConsentId());
    accountSummaryEntity.setSyncRequestId(executionContext.getSyncRequestId());
    accountSummaryEntity.setUpdatedBy(executionContext.getRequestedBy());

    accountSummaryRepository.save(accountSummaryEntity);
  }
}
