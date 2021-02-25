package com.banksalad.collectmydata.capital.common.service;

import org.springframework.stereotype.Service;

import com.banksalad.collectmydata.capital.common.collect.Apis;
import com.banksalad.collectmydata.capital.common.db.entity.AccountListEntity;
import com.banksalad.collectmydata.capital.common.db.entity.mapper.AccountListMapper;
import com.banksalad.collectmydata.capital.common.db.repository.AccountListRepository;
import com.banksalad.collectmydata.capital.common.dto.Account;
import com.banksalad.collectmydata.capital.common.dto.AccountResponse;
import com.banksalad.collectmydata.capital.common.dto.Organization;
import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

  private final ExternalApiService externalApiService;
  private final UserSyncStatusService userSyncStatusService;
  private final AccountListRepository accountListRepository;

  private final AccountListMapper accountListMapper = Mappers.getMapper(AccountListMapper.class);

  /**
   * 6.7.1 계좌 목록 조회
   */
  @Override
  public List<Account> listAccounts(ExecutionContext executionContext, Organization organization) {

    long banksaladUserId = executionContext.getBanksaladUserId();
    String organizationId = executionContext.getOrganizationId();
    String organizationCode = organization.getOrganizationCode();

    AccountResponse accountResponse = null;
    boolean isExceptionOccurred = false;

    // UserSyncStatus 조회 -> timestamp 조회
    long searchTimeStamp = userSyncStatusService
        .getSearchTimestamp(executionContext.getBanksaladUserId(), organizationId, Apis.capital_get_accounts);

    // api 호출 try-catch
    try {
      accountResponse = externalApiService.getAccounts(executionContext, organizationCode, searchTimeStamp);
    } catch (Exception e) {
      isExceptionOccurred = true;
    }

    // TODO ORGANIZAITON_USER 적재로직 추가.
    
    List<Account> apiResponseAccounts = Optional.ofNullable(accountResponse.getAccountList()).orElse(List.of());
    for (Account account : apiResponseAccounts) {
      //find
      AccountListEntity accountListEntity = accountListRepository
          .findByBanksaladUserIdAndOrganizationIdAndAccountNumAndSeqno(
              banksaladUserId, organizationId, account.getAccountNum(), account.getSeqno()
          ).orElse(AccountListEntity.builder().build());

      // merge
      accountListMapper.merge(executionContext, account, accountListEntity);

      // save (insert, update)
      accountListEntity.setSyncedAt(executionContext.getSyncStartedAt());
      accountListRepository.save(accountListEntity);
    }

    // userSyncStatus table update
    userSyncStatusService
        .updateUserSyncStatus(
            banksaladUserId,
            organizationId,
            Apis.capital_get_accounts.getId(),
            executionContext.getSyncStartedAt(),
            accountResponse.getSearchTimestamp(),
            true // TODO https://github.com/banksalad/collectmydata/pull/89 머지 후 수정.
        );

    // db에 적재되어있는 항목을 꺼내어 리턴
    List<AccountListEntity> accountListEntities = accountListRepository
        .findByBanksaladUserIdAndOrganizationIdAndIsConsent(banksaladUserId, organizationId, true);

    List<Account> responseAccounts = accountListEntities.stream()
        .map(entity -> accountListMapper.entityToDto(entity))
        .collect(Collectors.toList());

    return responseAccounts;
  }
}
