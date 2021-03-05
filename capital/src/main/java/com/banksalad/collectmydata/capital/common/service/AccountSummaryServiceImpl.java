package com.banksalad.collectmydata.capital.common.service;

import org.springframework.stereotype.Service;

import com.banksalad.collectmydata.capital.common.collect.Apis;
import com.banksalad.collectmydata.capital.common.db.entity.AccountSummaryEntity;
import com.banksalad.collectmydata.capital.common.db.entity.OrganizationUserEntity;
import com.banksalad.collectmydata.capital.common.db.entity.mapper.AccountListMapper;
import com.banksalad.collectmydata.capital.common.db.repository.AccountListRepository;
import com.banksalad.collectmydata.capital.common.db.repository.OrganizationUserRepository;
import com.banksalad.collectmydata.capital.common.dto.AccountSummary;
import com.banksalad.collectmydata.capital.common.dto.AccountSummaryResponse;
import com.banksalad.collectmydata.capital.common.dto.Organization;
import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.util.DateUtil;
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
  private final OrganizationUserRepository organizationUserRepository;

  private final AccountListMapper accountListMapper = Mappers.getMapper(AccountListMapper.class);

  /**
   * 6.7.1 계좌 목록 조회
   */
  @Override
  public List<AccountSummary> listAccountSummaries(ExecutionContext executionContext, Organization organization) {

    long banksaladUserId = executionContext.getBanksaladUserId();
    String organizationId = executionContext.getOrganizationId();
    String organizationCode = organization.getOrganizationCode();

    // UserSyncStatus 조회 -> timestamp 조회
    long searchTimeStamp = userSyncStatusService
        .getSearchTimestamp(executionContext.getBanksaladUserId(), organizationId, Apis.capital_get_accounts);

    // api 호출 try-catch
    AccountSummaryResponse accountSummaryResponse = externalApiService
        .getAccounts(executionContext, organizationCode, searchTimeStamp);

    // OragnizationUser 등록
    if (!organizationUserRepository.existsByBanksaladUserIdAndOrganizationId(banksaladUserId, organizationId)) {
      organizationUserRepository.save(
          OrganizationUserEntity.builder()
              .syncedAt(executionContext.getSyncStartedAt())
              .banksaladUserId(banksaladUserId)
              .organizationId(organizationId)
              .regDate(DateUtil.toLocalDate(accountSummaryResponse.getRegDate()))
              .build()
      );
    }

    if (accountSummaryResponse.getAccountSummaries() != null) {
      for (AccountSummary accountSummary : accountSummaryResponse.getAccountSummaries()) {
        //find
        AccountSummaryEntity accountSummaryEntity = accountListRepository
            .findByBanksaladUserIdAndOrganizationIdAndAccountNumAndSeqno(
                banksaladUserId, organizationId, accountSummary.getAccountNum(), accountSummary.getSeqno()
            ).orElse(AccountSummaryEntity.builder().build());

        // merge
        accountListMapper.merge(accountSummary, accountSummaryEntity);

        // save (insert, update)
        accountSummaryEntity.setBanksaladUserId(executionContext.getBanksaladUserId());
        accountSummaryEntity.setOrganizationId(executionContext.getOrganizationId());
        accountSummaryEntity.setSyncedAt(executionContext.getSyncStartedAt());
        accountListRepository.save(accountSummaryEntity);
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
            true //목록조회에서는 실패시 throw를 하기에 true값 전달
        );

    // db에 적재되어있는 항목을 꺼내어 리턴
    List<AccountSummaryEntity> accountListEntities = accountListRepository
        .findByBanksaladUserIdAndOrganizationIdAndIsConsent(banksaladUserId, organizationId, true);

    List<AccountSummary> responseAccountSummaries = accountListEntities.stream()
        .map(entity -> accountListMapper.entityToDto(entity))
        .collect(Collectors.toList());

    return responseAccountSummaries;
  }
}
