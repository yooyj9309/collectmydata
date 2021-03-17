package com.banksalad.collectmydata.bank.common.service;

import org.springframework.stereotype.Service;

import com.banksalad.collectmydata.bank.common.db.repository.AccountSummaryRepository;
import com.banksalad.collectmydata.bank.common.enums.BankAccountType;
import com.banksalad.collectmydata.bank.common.mapper.AccountSummaryMapper;
import com.banksalad.collectmydata.bank.summary.dto.AccountSummary;
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
  public List<AccountSummary> listSummariesConsented(long banksaladUserId, String organizationId,
      BankAccountType bankAccountType) {

    return accountSummaryRepository.findByBanksaladUserIdAndOrganizationIdAndConsentIsTrueAndAccountTypeIn(
        banksaladUserId, organizationId, getBankAccountTypeCodes(bankAccountType))
        .stream()
        .map(accountSummaryMapper::entityToDto)
        .collect(Collectors.toList());
  }

  private List<String> getBankAccountTypeCodes(BankAccountType bankAccountType) {
    switch (bankAccountType) {
      case DEPOSIT:
        return BankAccountType.depositAccountTypeCodes;

      case INVEST:
        return BankAccountType.investAccountTypeCodes;

      case LOAN:
        return BankAccountType.loanAccountTypeCodes;

      case UNKNOWN:
      default:
        return List.of();
    }
  }

  @Override
  public void updateBasicSearchTimestamp(long banksaladUserId, String organizationId, AccountSummary accountSummary,
      long basicSearchTimestamp) {

    accountSummaryRepository
        .findByBanksaladUserIdAndOrganizationIdAndAccountNumAndSeqno(banksaladUserId, organizationId,
            accountSummary.getAccountNum(), accountSummary.getSeqno())
        .ifPresent(accountSummaryEntity -> {
          accountSummaryEntity.setBasicSearchTimestamp(basicSearchTimestamp);
          accountSummaryRepository.save(accountSummaryEntity);
        });
  }

  @Override
  public void updateDetailSearchTimestamp(long banksaladUserId, String organizationId, AccountSummary accountSummary,
      long detailSearchTimestamp) {

    accountSummaryRepository
        .findByBanksaladUserIdAndOrganizationIdAndAccountNumAndSeqno(banksaladUserId, organizationId,
            accountSummary.getAccountNum(), accountSummary.getSeqno())
        .ifPresent(accountSummaryEntity -> {
          accountSummaryEntity.setDetailSearchTimestamp(detailSearchTimestamp);
          accountSummaryRepository.save(accountSummaryEntity);
        });
  }

  @Override
  public void updateTransactionSyncedAt(long banksaladUserId, String organizationId, AccountSummary accountSummary,
      LocalDateTime transactionSyncedAt) {

    accountSummaryRepository
        .findByBanksaladUserIdAndOrganizationIdAndAccountNumAndSeqno(banksaladUserId, organizationId,
            accountSummary.getAccountNum(), accountSummary.getSeqno())
        .ifPresent(accountSummaryEntity -> {
          accountSummaryEntity.setTransactionSyncedAt(transactionSyncedAt);
          accountSummaryRepository.save(accountSummaryEntity);
        });
  }

  @Override
  public void updateBasicResponseCode(long banksaladUserId, String organizationId, AccountSummary accountSummary,
      String responseCode) {
    accountSummaryRepository.findByBanksaladUserIdAndOrganizationIdAndAccountNumAndSeqno(
        banksaladUserId, organizationId, accountSummary.getAccountNum(), accountSummary.getSeqno())
        .ifPresent(accountSummaryEntity -> {
          accountSummaryEntity.setBasicResponseCode(responseCode);
          accountSummaryRepository.save(accountSummaryEntity);
        });
  }

  @Override
  public void updateDetailResponseCode(long banksaladUserId, String organizationId, AccountSummary accountSummary,
      String responseCode) {
    accountSummaryRepository.findByBanksaladUserIdAndOrganizationIdAndAccountNumAndSeqno(
        banksaladUserId, organizationId, accountSummary.getAccountNum(), accountSummary.getSeqno())
        .ifPresent(accountSummaryEntity -> {
          accountSummaryEntity.setDetailResponseCode(responseCode);
          accountSummaryRepository.save(accountSummaryEntity);
        });
  }

  @Override
  public void updateTransactionResponseCode(long banksaladUserId, String organizationId, AccountSummary accountSummary,
      String responseCode) {
    accountSummaryRepository.findByBanksaladUserIdAndOrganizationIdAndAccountNumAndSeqno(
        banksaladUserId, organizationId, accountSummary.getAccountNum(), accountSummary.getSeqno())
        .ifPresent(accountSummaryEntity -> {
          accountSummaryEntity.setTransactionResponseCode(responseCode);
          accountSummaryRepository.save(accountSummaryEntity);
        });
  }
}
