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

import java.time.LocalDateTime;
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

  @Override
  public void updateChargeResponseCode(long banksaladUserId, String organizationId, AccountSummary accountSummary,
      String responseCode) {
    AccountSummaryEntity accountSummaryEntity = getAccountSummaryEntity(banksaladUserId, organizationId,
        accountSummary.getSubKey(), accountSummary.getAccountId());
    accountSummaryEntity.setChargeResponseCode(responseCode);
    accountSummaryRepository.save(accountSummaryEntity);
  }

  @Override
  public void updateChargeSearchTimestamp(long banksaladUserId, String organizationId, AccountSummary accountSummary,
      long searchTimestamp) {
    AccountSummaryEntity accountSummaryEntity = getAccountSummaryEntity(banksaladUserId, organizationId,
        accountSummary.getSubKey(), accountSummary.getAccountId());
    accountSummaryEntity.setChargeSearchTimestamp(searchTimestamp);
    accountSummaryRepository.save(accountSummaryEntity);
  }

  @Override
  public void updatePrepaidTransactionResponseCode(long banksaladUserId, String organizationId,
      AccountSummary accountSummary, String responseCode) {
    // 권면 상관없이 subkey 기준으로 선불거래내역을 주기때문에 subkey 일치하는 모든 계좌 responseCode 업데이트
    List<AccountSummaryEntity> accountSummaryEntities =
        getAccountSummaryEntities(banksaladUserId, organizationId, accountSummary.getSubKey());
    accountSummaryEntities.forEach(accountSummaryEntity ->
        accountSummaryEntity.setPrepaidTransactionResponseCode(responseCode));
    accountSummaryRepository.saveAll(accountSummaryEntities);
  }

  @Override
  public void updatePrepaidTransactionSyncedAt(long banksaladUserId, String organizationId,
      AccountSummary accountSummary, LocalDateTime syncStartedAt) {
    // 권면 상관없이 subkey 기준으로 선불거래내역을 주기때문에 subkey 일치하는 모든 계좌 syncedAt 업데이트
    List<AccountSummaryEntity> accountSummaryEntities =
        getAccountSummaryEntities(banksaladUserId, organizationId, accountSummary.getSubKey());
    accountSummaryEntities.forEach(accountSummaryEntity ->
        accountSummaryEntity.setPrepaidTransactionSyncedAt(syncStartedAt));
    accountSummaryRepository.saveAll(accountSummaryEntities);
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

  private List<AccountSummaryEntity> getAccountSummaryEntities(long banksaladUserId, String organizationId,
      String subKey) {
    List<AccountSummaryEntity> accountSummaryEntities = accountSummaryRepository
        .findByBanksaladUserIdAndOrganizationIdAndSubKey(banksaladUserId, organizationId, subKey);
    if (accountSummaryEntities.isEmpty()) {
      throw new CollectRuntimeException("No data AccountSummaryEntity");
    }
    return accountSummaryEntities;
  }

}
