package com.banksalad.collectmydata.efin.summary;

import org.springframework.stereotype.Component;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.util.ObjectComparator;
import com.banksalad.collectmydata.efin.common.db.entity.AccountSummaryEntity;
import com.banksalad.collectmydata.efin.common.db.entity.AccountSummaryPayEntity;
import com.banksalad.collectmydata.efin.common.db.entity.OrganizationUserEntity;
import com.banksalad.collectmydata.efin.common.db.repository.AccountSummaryPayRepository;
import com.banksalad.collectmydata.efin.common.db.repository.AccountSummaryRepository;
import com.banksalad.collectmydata.efin.common.db.repository.OrganizationUserRepository;
import com.banksalad.collectmydata.efin.common.mapper.AccountSummaryMapper;
import com.banksalad.collectmydata.efin.common.mapper.AccountSummaryPayMapper;
import com.banksalad.collectmydata.efin.summary.dto.AccountSummary;
import com.banksalad.collectmydata.efin.summary.dto.AccountSummaryPay;
import com.banksalad.collectmydata.efin.summary.dto.ListAccountSummariesResponse;
import com.banksalad.collectmydata.finance.api.summary.SummaryResponseHelper;
import com.banksalad.collectmydata.finance.api.summary.dto.SummaryResponse;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class AccountSummaryResponseHelper implements SummaryResponseHelper<AccountSummary> {

  private final AccountSummaryRepository accountSummaryRepository;
  private final AccountSummaryPayRepository accountSummaryPayRepository;
  private final OrganizationUserRepository organizationUserRepository;

  private final AccountSummaryMapper accountSummaryMapper = Mappers.getMapper(AccountSummaryMapper.class);
  private final AccountSummaryPayMapper accountSummaryPayMapper = Mappers.getMapper(AccountSummaryPayMapper.class);

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
        .orElseGet(() ->
            OrganizationUserEntity.builder()
                .syncedAt(executionContext.getSyncStartedAt())
                .banksaladUserId(executionContext.getBanksaladUserId())
                .organizationId(executionContext.getOrganizationId())
                .name(listAccountSummariesResponse.getName())
                .regDate(listAccountSummariesResponse.getRegDate())
                .build());

    organizationUserRepository.save(organizationUserEntity);
  }

  @Override
  public void saveSummary(ExecutionContext executionContext, AccountSummary accountSummary) {

    final long banksaladUserId = executionContext.getBanksaladUserId();
    final String organizationId = executionContext.getOrganizationId();
    final String subKey = accountSummary.getSubKey();
    final String accountId = accountSummary.getAccountId();
    final LocalDateTime syncedAt = executionContext.getSyncStartedAt();

    AccountSummaryEntity accountSummaryEntity = accountSummaryRepository
        .findByBanksaladUserIdAndOrganizationIdAndSubKeyAndAccountId(banksaladUserId, organizationId, subKey, accountId)
        .orElse(AccountSummaryEntity.builder().build());

    accountSummaryMapper.mergeDtoToEntity(accountSummary, accountSummaryEntity);
    accountSummaryEntity.setBanksaladUserId(banksaladUserId);
    accountSummaryEntity.setOrganizationId(organizationId);
    accountSummaryEntity.setSyncedAt(syncedAt);
    accountSummaryRepository.save(accountSummaryEntity);

    List<AccountSummaryPay> existingAccountSummaryPays = accountSummaryPayRepository
        .findByBanksaladUserIdAndOrganizationIdAndSubKeyAndAccountId(banksaladUserId, organizationId, subKey, accountId)
        .stream()
        .map(accountSummaryPayMapper::entityToDto)
        .collect(Collectors.toList());

    if (ObjectComparator.isSameListIgnoreOrder(accountSummary.getAccountSummaryPays(), existingAccountSummaryPays)) {
      return;
    }

    accountSummaryPayRepository.deleteAllByBanksaladUserIdAndOrganizationIdAndSubKeyAndAccountId(banksaladUserId,
        organizationId, subKey, accountId);
    accountSummaryPayRepository.flush();
    accountSummaryPayRepository.saveAll(accountSummary.getAccountSummaryPays()
        .stream()
        .map(accountSummaryPay -> {
          AccountSummaryPayEntity accountSummaryPayEntity = accountSummaryPayMapper.dtoToEntity(accountSummaryPay);
          accountSummaryPayEntity.setSyncedAt(syncedAt);
          accountSummaryPayEntity.setBanksaladUserId(banksaladUserId);
          accountSummaryPayEntity.setOrganizationId(organizationId);
          accountSummaryPayEntity.setSubKey(subKey);
          accountSummaryPayEntity.setAccountId(accountId);
          return accountSummaryPayEntity;
        })
        .collect(Collectors.toList()));
  }
}
