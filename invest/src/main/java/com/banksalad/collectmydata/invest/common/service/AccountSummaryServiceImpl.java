package com.banksalad.collectmydata.invest.common.service;

import org.springframework.stereotype.Service;

import com.banksalad.collectmydata.invest.common.db.entity.mapper.AccountSummaryMapper;
import com.banksalad.collectmydata.invest.common.db.repository.AccountSummaryRepository;
import com.banksalad.collectmydata.invest.summary.dto.AccountSummary;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.stream.Collectors;

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
  public void updateBasicSearchTimestamp(long banksaladUserId, String organizationId, String accountNum,
      long basicSearchTimestamp) {

    accountSummaryRepository.findByBanksaladUserIdAndOrganizationIdAndAccountNum(banksaladUserId, organizationId, accountNum)
        .ifPresent(accountSummaryEntity -> {
          accountSummaryEntity.setBasicSearchTimestamp(basicSearchTimestamp);
          accountSummaryRepository.save(accountSummaryEntity);
        });
  }

  @Override
  public void updateBasicResponseCode(long banksaladUserId, String organizationId, String accountNum,
      String responseCode) {

    accountSummaryRepository.findByBanksaladUserIdAndOrganizationIdAndAccountNum(banksaladUserId, organizationId, accountNum)
        .ifPresent(accountSummaryEntity -> {
          accountSummaryEntity.setBasicResponseCode(responseCode);
          accountSummaryRepository.save(accountSummaryEntity);
        });
  }
}
