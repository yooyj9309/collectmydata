package com.banksalad.collectmydata.capital.common.service;

import org.springframework.stereotype.Service;

import com.banksalad.collectmydata.capital.common.db.entity.AccountSummaryEntity;
import com.banksalad.collectmydata.capital.common.db.mapper.AccountSummaryMapper;
import com.banksalad.collectmydata.capital.common.db.repository.AccountSummaryRepository;
import com.banksalad.collectmydata.capital.summary.dto.AccountSummary;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;

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

    // TODO dusang, if else 걷어내고 그냥 리턴으로 할까..?
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
}
