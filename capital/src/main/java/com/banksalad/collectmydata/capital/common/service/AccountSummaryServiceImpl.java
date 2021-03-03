package com.banksalad.collectmydata.capital.common.service;

import org.springframework.stereotype.Service;

import com.banksalad.collectmydata.capital.common.collect.Apis;
import com.banksalad.collectmydata.capital.common.db.entity.AccountListEntity;
import com.banksalad.collectmydata.capital.common.db.entity.mapper.AccountListMapper;
import com.banksalad.collectmydata.capital.common.db.repository.AccountListRepository;
import com.banksalad.collectmydata.capital.common.dto.AccountSummary;
import com.banksalad.collectmydata.capital.common.dto.AccountSummaryResponse;
import com.banksalad.collectmydata.capital.common.dto.Organization;
import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AccountSummaryServiceImpl implements AccountSummaryService {

  private final ExternalApiService externalApiService;
  private final UserSyncStatusService userSyncStatusService;
  private final AccountListRepository accountListRepository;

  private final AccountListMapper accountListMapper = Mappers.getMapper(AccountListMapper.class);

  /**
   * 6.7.1 계좌 목록 조회
   */
  @Override
  public List<AccountSummary> listAccountSummaries(ExecutionContext executionContext, Organization organization) {

    long banksaladUserId = executionContext.getBanksaladUserId();
    String organizationId = executionContext.getOrganizationId();
    String organizationCode = organization.getOrganizationCode();

    AccountSummaryResponse accountSummaryResponse = null;
    boolean isExceptionOccurred = false;

    // UserSyncStatus 조회 -> timestamp 조회
    long searchTimeStamp = userSyncStatusService
        .getSearchTimestamp(executionContext.getBanksaladUserId(), organizationId, Apis.capital_get_accounts);

    // api 호출 try-catch
    accountSummaryResponse = externalApiService.getAccounts(executionContext, organizationCode, searchTimeStamp);

    // TODO ORGANIZAITON_USER 적재로직 추가.

    if (accountSummaryResponse.getAccountSummaries() != null) {
      for (AccountSummary accountSummary : accountSummaryResponse.getAccountSummaries()) {
        //find
        AccountListEntity accountListEntity = accountListRepository
            .findByBanksaladUserIdAndOrganizationIdAndAccountNumAndSeqno(
                banksaladUserId, organizationId, accountSummary.getAccountNum(), accountSummary.getSeqno()
            ).orElse(AccountListEntity.builder().build());

        // merge
        accountListMapper.merge(accountSummary, accountListEntity);

        // save (insert, update)
        accountListEntity.setBanksaladUserId(executionContext.getBanksaladUserId());
        accountListEntity.setOrganizationId(executionContext.getOrganizationId());
        accountListEntity.setSyncedAt(executionContext.getSyncStartedAt());
        accountListRepository.save(accountListEntity);
      }
    }

    // userSyncStatus table update
    userSyncStatusService
        .updateUserSyncStatus(
            banksaladUserId,
            organizationId,
            Apis.capital_get_accounts.getId(),
            executionContext.getSyncStartedAt(),
            accountSummaryResponse.getSearchTimestamp(),
            true // TODO https://github.com/banksalad/collectmydata/pull/89 머지 후 수정.
        );

    // db에 적재되어있는 항목을 꺼내어 리턴
    List<AccountListEntity> accountListEntities = accountListRepository
        .findByBanksaladUserIdAndOrganizationIdAndIsConsent(banksaladUserId, organizationId, true);

    List<AccountSummary> responseAccountSummaries = accountListEntities.stream()
        .map(entity -> accountListMapper.entityToDto(entity))
        .collect(Collectors.toList());

    return responseAccountSummaries;
  }
}
