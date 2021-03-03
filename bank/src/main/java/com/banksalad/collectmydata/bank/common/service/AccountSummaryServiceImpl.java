package com.banksalad.collectmydata.bank.common.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.banksalad.collectmydata.bank.common.collect.Apis;
import com.banksalad.collectmydata.bank.common.db.entity.AccountSummaryEntity;
import com.banksalad.collectmydata.bank.common.db.entity.mapper.AccountSummaryMapper;
import com.banksalad.collectmydata.bank.common.db.repository.AccountSummaryRepository;
import com.banksalad.collectmydata.bank.common.dto.AccountSummary;
import com.banksalad.collectmydata.bank.common.dto.ListAccountSummariesResponse;
import com.banksalad.collectmydata.bank.common.dto.UserSyncStatus;
import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.organization.Organization;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountSummaryServiceImpl implements AccountSummaryService {

  private final UserSyncStatusService userSyncStatusService;
  private final ExternalApiService externalApiService;
  private final AccountSummaryRepository accountSummaryRepository;

  private final AccountSummaryMapper accountSummaryMapper = Mappers.getMapper(AccountSummaryMapper.class);

  @Override
  @Transactional
  public List<AccountSummary> listAccountSummaries(ExecutionContext executionContext) {
    Organization organization = getOrganization(executionContext);
    long searchTimestamp = getSearchTimestamp(executionContext);

    ListAccountSummariesResponse listAccountSummariesResponse = externalApiService
        .exchangeListAccountSummaries(executionContext, organization.getOrganizationCode(), searchTimestamp);

    saveAccountSummaries(executionContext, listAccountSummariesResponse);

    // TODO jayden-lee OrganizationUser Entity 생성 및 수정 로직
    // organizationUserService.upsert(listAccountSummariesResponse);

    // TODO jayden-lee UserSyncStatus Entity 생성 및 수정 로직
    // Api 200 Ok, userSyncStatusService.upsert(executionContext);

    List<AccountSummary> requiringConsentAccountSummaries = getRequiringConsentAccountSummaries(executionContext);

    return requiringConsentAccountSummaries;
  }

  private Organization getOrganization(ExecutionContext executionContext) {
    return Organization.builder()
        .organizationCode("020")
        .build();
  }

  private long getSearchTimestamp(ExecutionContext executionContext) {
    UserSyncStatus userSyncStatus = userSyncStatusService
        .getUserSyncStatus(executionContext.getBanksaladUserId(), executionContext.getOrganizationId(),
            Apis.finance_bank_accounts.getId());

    return Optional.ofNullable(userSyncStatus)
        .map(UserSyncStatus::getSearchTimestamp)
        .orElse(0L);
  }


  private void saveAccountSummaries(ExecutionContext executionContext,
      ListAccountSummariesResponse listAccountSummariesResponse) {

    List<AccountSummary> accountSummaryList = listAccountSummariesResponse.getAccountSummaries();

    for (AccountSummary accountSummary : accountSummaryList) {
      AccountSummaryEntity accountSummaryEntity = accountSummaryRepository
          .findByBanksaladUserIdAndOrganizationIdAndAccountNumAndSeqnoAndIsForeignDeposit(
              executionContext.getBanksaladUserId(), executionContext.getOrganizationId(),
              accountSummary.getAccountNum(),
              accountSummary.getSeqno(), accountSummary.isForeignDeposit());

      if (accountSummaryEntity == null) {
        accountSummaryEntity = AccountSummaryEntity.builder().build();
      }

      accountSummaryMapper.merge(executionContext, accountSummary, accountSummaryEntity);
      accountSummaryEntity.setSyncedAt(executionContext.getSyncStartedAt());

      try {
        accountSummaryRepository.save(accountSummaryEntity);
      } catch (Exception e) {
        log.error("Failed to save account summary", e);
      }
    }
  }

  private List<AccountSummary> getRequiringConsentAccountSummaries(ExecutionContext executionContext) {
    List<AccountSummaryEntity> accountListEntities = accountSummaryRepository
        .findByBanksaladUserIdAndOrganizationIdAndIsConsent(executionContext.getBanksaladUserId(),
            executionContext.getOrganizationId(), true);

    return accountListEntities.stream()
        .map(accountSummaryMapper::entityToDto)
        .collect(Collectors.toList());
  }
}
