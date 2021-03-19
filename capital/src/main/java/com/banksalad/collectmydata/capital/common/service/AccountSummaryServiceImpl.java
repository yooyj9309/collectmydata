package com.banksalad.collectmydata.capital.common.service;

import org.springframework.stereotype.Service;

import com.banksalad.collectmydata.capital.common.db.entity.AccountSummaryEntity;
import com.banksalad.collectmydata.capital.common.db.mapper.AccountSummaryMapper;
import com.banksalad.collectmydata.capital.common.db.repository.AccountSummaryRepository;
import com.banksalad.collectmydata.capital.summary.dto.AccountSummary;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AccountSummaryServiceImpl implements AccountSummaryService {

  private final AccountSummaryRepository accountSummaryRepository;
  private static final String OPERATING_LEASE_ACCOUNT_TYPE = "3710";

  private final AccountSummaryMapper accountSummaryMapper = Mappers.getMapper(AccountSummaryMapper.class);

  @Override
  public List<AccountSummary> listSummariesConsented(long banksaladUserId, String organizationId,
      boolean demandOperatingLeaseAccount) {

    List<AccountSummaryEntity> response = null;
    // TODO dusang, if else 걷어내고 그냥 리턴으로 할지 고민이됩니다.
    if (demandOperatingLeaseAccount) {
      response = accountSummaryRepository.findByBanksaladUserIdAndOrganizationIdAndIsConsent(
          banksaladUserId, organizationId, true)
          .stream()
          .filter(accountSummaryEntity -> OPERATING_LEASE_ACCOUNT_TYPE.equals(accountSummaryEntity.getAccountType()))
          .collect(Collectors.toList());
    } else {
      response = accountSummaryRepository.findByBanksaladUserIdAndOrganizationIdAndIsConsent(
          banksaladUserId, organizationId, true)
          .stream()
          .filter(accountSummaryEntity -> !OPERATING_LEASE_ACCOUNT_TYPE.equals(accountSummaryEntity.getAccountType()))
          .collect(Collectors.toList());
    }
    
    return response.stream().map(accountSummaryMapper::entityToDto).collect(Collectors.toList());
  }

  @Override
  public void updateBasicSearchTimestamp(long banksaladUserId, String organizationId, AccountSummary accountSummary,
      long basicSearchTimestamp) {
    accountSummaryRepository
        .findByBanksaladUserIdAndOrganizationIdAndAccountNumAndSeqno(
            banksaladUserId, organizationId, accountSummary.getAccountNum(), accountSummary.getSeqno())
        .ifPresent(accountSummaryEntity -> {
          accountSummaryEntity.setBasicSearchTimestamp(basicSearchTimestamp);
          accountSummaryRepository.save(accountSummaryEntity);
        });
  }

  @Override
  public void updateBasicResponseCode(long banksaladUserId, String organizationId, AccountSummary accountSummary,
      String responseCode) {
    accountSummaryRepository
        .findByBanksaladUserIdAndOrganizationIdAndAccountNumAndSeqno(
            banksaladUserId, organizationId, accountSummary.getAccountNum(), accountSummary.getSeqno())
        .ifPresent(accountSummaryEntity -> {
          accountSummaryEntity.setBasicResponseCode(responseCode);
          accountSummaryRepository.save(accountSummaryEntity);
        });
  }

  @Override
  public void updateTransactionSyncedAt(long banksaladUserId, String organizationId, AccountSummary accountSummary,
      LocalDateTime syncedAt) {
    accountSummaryRepository
        .findByBanksaladUserIdAndOrganizationIdAndAccountNumAndSeqno(
            banksaladUserId, organizationId, accountSummary.getAccountNum(), accountSummary.getSeqno())
        .ifPresent(accountSummaryEntity -> {
          accountSummaryEntity.setTransactionSyncedAt(syncedAt);
          accountSummaryRepository.save(accountSummaryEntity);
        });
  }
  
  @Override
  public void updateOperatingLeaseBasicSearchTimestamp(long banksaladUserId, String organizationId,
      AccountSummary accountSummary, long operatingLeaseBasicSearchTimestamp) {
    accountSummaryRepository
        .findByBanksaladUserIdAndOrganizationIdAndAccountNumAndSeqno(
            banksaladUserId, organizationId, accountSummary.getAccountNum(), accountSummary.getSeqno())
        .ifPresent(accountSummaryEntity -> {
          accountSummaryEntity.setOperatingLeaseBasicSearchTimestamp(operatingLeaseBasicSearchTimestamp);
          accountSummaryRepository.save(accountSummaryEntity);
        });
  }

  @Override
  public void updateTransactionResponseCode(long banksaladUserId, String organizationId, AccountSummary accountSummary,
      String responseCode) {
    accountSummaryRepository
        .findByBanksaladUserIdAndOrganizationIdAndAccountNumAndSeqno(
            banksaladUserId, organizationId, accountSummary.getAccountNum(), accountSummary.getSeqno())
        .ifPresent(accountSummaryEntity -> {
          accountSummaryEntity.setTransactionResponseCode(responseCode);
          accountSummaryRepository.save(accountSummaryEntity);
        });
  }
  
  @Override                                   
  public void updateOperatingLeaseBasicResponseCode(long banksaladUserId, String organizationId, AccountSummary accountSummary,
      String responseCode) {
    accountSummaryRepository
        .findByBanksaladUserIdAndOrganizationIdAndAccountNumAndSeqno(
            banksaladUserId, organizationId, accountSummary.getAccountNum(), accountSummary.getSeqno())
        .ifPresent(accountSummaryEntity -> {
          accountSummaryEntity.setOperatingLeaseBasicResponseCode(responseCode);
          accountSummaryRepository.save(accountSummaryEntity);
        });
  }
}
