package com.banksalad.collectmydata.bank.common.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.banksalad.collectmydata.bank.common.collect.Apis;
import com.banksalad.collectmydata.bank.common.db.entity.AccountListEntity;
import com.banksalad.collectmydata.bank.common.db.entity.mapper.AccountListMapper;
import com.banksalad.collectmydata.bank.common.db.repository.AccountListRepository;
import com.banksalad.collectmydata.bank.common.dto.Account;
import com.banksalad.collectmydata.bank.common.dto.ListAccountsResponse;
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
public class AccountServiceImpl implements AccountService {

  private final UserSyncStatusService userSyncStatusService;
  private final ExternalApiService externalApiService;
  private final AccountListRepository accountListRepository;

  private final AccountListMapper accountListMapper = Mappers.getMapper(AccountListMapper.class);

  @Override
  @Transactional
  public List<Account> listAccounts(ExecutionContext executionContext) {
    Organization organization = getOrganization(executionContext);
    long searchTimestamp = getSearchTimestamp(executionContext);

    ListAccountsResponse listAccountsResponse = externalApiService
        .exchangeListAccounts(executionContext, organization.getOrganizationCode(), searchTimestamp);

    saveAccounts(executionContext, listAccountsResponse);

    // TODO jayden-lee OrganizationUser Entity 생성 및 수정 로직
    // organizationUserService.upsert(accountsResponse);

    // TODO jayden-lee UserSyncStatus Entity 생성 및 수정 로직
    // Api 200 Ok, userSyncStatusService.upsert(executionContext);

    List<Account> requiringConsentAccounts = getRequiringConsentAccounts(executionContext);

    return requiringConsentAccounts;
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


  private void saveAccounts(ExecutionContext executionContext, ListAccountsResponse listAccountsResponse) {
    List<Account> accountList = listAccountsResponse.getAccountList();

    for (Account account : accountList) {
      AccountListEntity accountListEntity = accountListRepository
          .findByBanksaladUserIdAndOrganizationIdAndAccountNumAndSeqnoAndIsForeignDeposit(
              executionContext.getBanksaladUserId(), executionContext.getOrganizationId(), account.getAccountNum(),
              account.getSeqno(), account.isForeignDeposit());

      if (accountListEntity == null) {
        accountListEntity = AccountListEntity.builder().build();
      }

      accountListMapper.merge(executionContext, account, accountListEntity);
      accountListEntity.setSyncedAt(executionContext.getSyncStartedAt());

      try {
        accountListRepository.save(accountListEntity);
      } catch (Exception e) {
        log.error("Failed to save account", e);
      }
    }
  }

  private List<Account> getRequiringConsentAccounts(ExecutionContext executionContext) {
    List<AccountListEntity> accountListEntities = accountListRepository
        .findByBanksaladUserIdAndOrganizationIdAndIsConsent(executionContext.getBanksaladUserId(),
            executionContext.getOrganizationId(), true);

    return accountListEntities.stream()
        .map(accountListMapper::entityToDto)
        .collect(Collectors.toList());
  }
}
