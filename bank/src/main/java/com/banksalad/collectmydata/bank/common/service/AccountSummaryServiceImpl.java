package com.banksalad.collectmydata.bank.common.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.banksalad.collectmydata.bank.common.collect.Apis;
import com.banksalad.collectmydata.bank.common.db.entity.AccountSummaryEntity;
import com.banksalad.collectmydata.bank.common.db.entity.mapper.AccountSummaryMapper;
import com.banksalad.collectmydata.bank.common.db.repository.AccountSummaryRepository;
import com.banksalad.collectmydata.bank.summary.dto.AccountSummary;
import com.banksalad.collectmydata.bank.summary.dto.ListAccountSummariesResponse;
import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.organization.Organization;
import com.banksalad.collectmydata.finance.common.service.UserSyncStatusService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.util.List;
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
        .listAccountSummaries(executionContext, organization.getOrganizationCode(), searchTimestamp);

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
        .organizationCode("020") // TODO jayden-lee implement organizationCode
        .build();
  }

  private long getSearchTimestamp(ExecutionContext executionContext) {
    return userSyncStatusService
        .getSearchTimestamp(executionContext.getBanksaladUserId(), executionContext.getOrganizationId(),
            Apis.finance_bank_accounts);
  }


  private void saveAccountSummaries(ExecutionContext executionContext,
      ListAccountSummariesResponse listAccountSummariesResponse) {

    List<AccountSummary> accountSummaryList = listAccountSummariesResponse.getAccountSummaries();

    for (AccountSummary accountSummary : accountSummaryList) {
      AccountSummaryEntity accountSummaryEntity = accountSummaryRepository
          .findByBanksaladUserIdAndOrganizationIdAndAccountNumAndSeqnoAndForeignDeposit(
              executionContext.getBanksaladUserId(), executionContext.getOrganizationId(),
              accountSummary.getAccountNum(),
              accountSummary.getSeqno(), accountSummary.isForeignDeposit());

      if (accountSummaryEntity == null) {
        accountSummaryEntity = AccountSummaryEntity.builder().build();
      }

      accountSummaryMapper.merge(accountSummary, accountSummaryEntity);
      accountSummaryEntity.setBanksaladUserId(executionContext.getBanksaladUserId());
      accountSummaryEntity.setOrganizationId(executionContext.getOrganizationId());
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
        .findByBanksaladUserIdAndOrganizationIdAndConsent(executionContext.getBanksaladUserId(),
            executionContext.getOrganizationId(), true);

    return accountListEntities.stream()
        .map(accountSummaryMapper::entityToDto)
        .collect(Collectors.toList());
  }

  @Override
  @Transactional(propagation = Propagation.REQUIRED)
  public void updateBasicSearchTimestamp(long banksaladUserId, String organizationId, AccountSummary accountSummary,
      long basicSearchTimestamp) {

    AccountSummaryEntity accountSummaryEntity = accountSummaryRepository
        .findByBanksaladUserIdAndOrganizationIdAndAccountNumAndSeqnoAndForeignDeposit(
            banksaladUserId, organizationId, accountSummary.getAccountNum(), accountSummary.getSeqno(),
            accountSummary.isForeignDeposit());

    accountSummaryEntity.setBasicSearchTimestamp(basicSearchTimestamp);

    accountSummaryRepository.save(accountSummaryEntity);
  }

  @Override
  @Transactional(propagation = Propagation.REQUIRED)
  public void updateDetailSearchTimestamp(long banksaladUserId, String organizationId, AccountSummary accountSummary,
      long detailSearchTimestamp) {

    AccountSummaryEntity accountSummaryEntity = accountSummaryRepository
        .findByBanksaladUserIdAndOrganizationIdAndAccountNumAndSeqnoAndForeignDeposit(
            banksaladUserId, organizationId, accountSummary.getAccountNum(), accountSummary.getSeqno(),
            accountSummary.isForeignDeposit());

    accountSummaryEntity.setDetailSearchTimestamp(detailSearchTimestamp);

    accountSummaryRepository.save(accountSummaryEntity);
  }

  @Override
  @Transactional
  public void updateTransactionSyncedAt(long banksaladUserId, String organizationId, AccountSummary accountSummary,
      LocalDateTime transactionSyncedAt) {

    AccountSummaryEntity accountSummaryEntity = accountSummaryRepository
        .findByBanksaladUserIdAndOrganizationIdAndAccountNumAndSeqnoAndForeignDeposit(
            banksaladUserId, organizationId, accountSummary.getAccountNum(), accountSummary.getSeqno(),
            accountSummary.isForeignDeposit());

    accountSummaryEntity.setTransactionSyncedAt(transactionSyncedAt);

    accountSummaryRepository.save(accountSummaryEntity);
  }
}
