package com.banksalad.collectmydata.efin.common.service;

import org.springframework.stereotype.Service;

import com.banksalad.collectmydata.common.exception.CollectRuntimeException;
import com.banksalad.collectmydata.efin.common.db.entity.AccountSummaryEntity;
import com.banksalad.collectmydata.efin.common.db.repository.AccountSummaryRepository;
import com.banksalad.collectmydata.efin.common.mapper.AccountSummaryMapper;
import com.banksalad.collectmydata.efin.summary.dto.AccountSummary;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountSummaryServiceImpl implements AccountSummaryService {

  private final AccountSummaryRepository accountSummaryRepository;

  private final AccountSummaryMapper accountSummaryMapper = Mappers.getMapper(AccountSummaryMapper.class);

  @Override
  public List<AccountSummary> listSummariesConsented(long banksaladUserId, String organizationId) {
    return accountSummaryRepository
        .findByBanksaladUserIdAndOrganizationIdAndConsentIsTrue(banksaladUserId, organizationId)
        .stream()
        .map(accountSummaryMapper::entityToDto)
        .collect(Collectors.toList());
  }

  @Override
  public void updateBalanceResponseCode(long banksaladUserId, String organizationId, AccountSummary accountSummary,
      String responseCode) {
    AccountSummaryEntity accountSummaryEntity = getAccountSummaryEntity(banksaladUserId, organizationId,
        accountSummary.getSubKey(), accountSummary.getAccountId());
    accountSummaryEntity.setBalanceResponseCode(responseCode);
    accountSummaryRepository.save(accountSummaryEntity);
  }

  @Override
  public void updateBalanceSearchTimestamp(long banksaladUserId, String organizationId, AccountSummary accountSummary,
      long searchTimestamp) {
    AccountSummaryEntity accountSummaryEntity = getAccountSummaryEntity(banksaladUserId, organizationId,
        accountSummary.getSubKey(), accountSummary.getAccountId());
    accountSummaryEntity.setBalanceSearchTimestamp(searchTimestamp);
    accountSummaryRepository.save(accountSummaryEntity);
  }

  private AccountSummaryEntity getAccountSummaryEntity(long banksaladUserId, String organizationId, String subKey,
      String accountId) {
    return accountSummaryRepository.findByBanksaladUserIdAndOrganizationIdAndSubKeyAndAccountId(
        banksaladUserId,
        organizationId,
        subKey,
        accountId
    ).orElseThrow(() -> new CollectRuntimeException("No data AccountSummaryEntity"));
  }

}
