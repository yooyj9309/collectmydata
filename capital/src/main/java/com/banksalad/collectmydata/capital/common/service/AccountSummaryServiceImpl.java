package com.banksalad.collectmydata.capital.common.service;

import org.springframework.stereotype.Service;

import com.banksalad.collectmydata.capital.common.db.entity.AccountSummaryEntity;
import com.banksalad.collectmydata.capital.common.mapper.AccountSummaryMapper;
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

    List<AccountSummaryEntity> response;
    // TODO dusang, if else 걷어내고 그냥 리턴으로 할지 고민이됩니다.
    // yonggeon: DB에서 미리 거르는 것이 효율적이라 생각되어 수정합니다.
    if (demandOperatingLeaseAccount) {
      response = accountSummaryRepository.findByBanksaladUserIdAndOrganizationIdAndIsConsentIsTrueAndAccountType(
          banksaladUserId, organizationId, OPERATING_LEASE_ACCOUNT_TYPE);
    } else {
      response = accountSummaryRepository.findByBanksaladUserIdAndOrganizationIdAndIsConsentIsTrueAndAccountTypeNot(
          banksaladUserId, organizationId, OPERATING_LEASE_ACCOUNT_TYPE);
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
  public void updateDetailSearchTimestamp(long banksaladUserId, String organizationId, AccountSummary accountSummary,
      long detailSearchTimestamp) {

    accountSummaryRepository
        .findByBanksaladUserIdAndOrganizationIdAndAccountNumAndSeqno(
            banksaladUserId, organizationId, accountSummary.getAccountNum(), accountSummary.getSeqno())
        .ifPresent(accountSummaryEntity -> {
          accountSummaryEntity.setDetailSearchTimestamp(detailSearchTimestamp);
          accountSummaryRepository.save(accountSummaryEntity);
        });
  }

  @Override
  public void updateDetailResponseCode(long banksaladUserId, String organizationId, AccountSummary accountSummary,
      String responseCode) {

    accountSummaryRepository
        .findByBanksaladUserIdAndOrganizationIdAndAccountNumAndSeqno(
            banksaladUserId, organizationId, accountSummary.getAccountNum(), accountSummary.getSeqno())
        .ifPresent(accountSummaryEntity -> {
          accountSummaryEntity.setDetailResponseCode(responseCode);
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

  @Override
  public void updateOperatingLeaseTransactionSyncedAt(long banksaladUserId, String organizationId,
      AccountSummary accountSummary, LocalDateTime syncStartedAt) {
    accountSummaryRepository
        .findByBanksaladUserIdAndOrganizationIdAndAccountNumAndSeqno(
            banksaladUserId, organizationId, accountSummary.getAccountNum(), accountSummary.getSeqno())
        .ifPresent(accountSummaryEntity -> {
          accountSummaryEntity.setOperatingLeaseTransactionSyncedAt(syncStartedAt);
          accountSummaryRepository.save(accountSummaryEntity);
        });
  }

  @Override
  public void updateOperatingLeaseTransactionResponseCode(long banksaladUserId, String organizationId,
      AccountSummary accountSummary, String responseCode) {
    accountSummaryRepository
        .findByBanksaladUserIdAndOrganizationIdAndAccountNumAndSeqno(
            banksaladUserId, organizationId, accountSummary.getAccountNum(), accountSummary.getSeqno())
        .ifPresent(accountSummaryEntity -> {
          accountSummaryEntity.setOperatingLeaseTransactionResponseCode(responseCode);
          accountSummaryRepository.save(accountSummaryEntity);
        });
  }
}
