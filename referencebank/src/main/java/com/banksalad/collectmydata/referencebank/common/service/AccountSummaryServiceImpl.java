package com.banksalad.collectmydata.referencebank.common.service;

import com.banksalad.collectmydata.referencebank.common.db.repository.AccountSummaryRepository;
import com.banksalad.collectmydata.referencebank.common.enums.BankAccountType;
import com.banksalad.collectmydata.referencebank.common.mapper.AccountSummaryMapper;
import com.banksalad.collectmydata.referencebank.summaries.dto.AccountSummary;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AccountSummaryServiceImpl implements AccountSummaryService {

  private final AccountSummaryRepository accountSummaryRepository;
  private final AccountSummaryMapper accountSummaryMapper = Mappers.getMapper(AccountSummaryMapper.class);

  public List<AccountSummary> listSummariesConsented(long banksaladUserId, String organizationId,
      BankAccountType bankAccountType) {
    return
        accountSummaryRepository.findByBanksaladUserIdAndOrganizationIdAndIsConsentIsTrueAndAccountTypeIn(
            banksaladUserId,
            organizationId,
            getBankAccountTypeCodes(bankAccountType)
        ).stream()
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
        .findByBanksaladUserIdAndOrganizationIdAndAccountNumAndSeqno(
            banksaladUserId, organizationId, accountSummary.getAccountNum(), accountSummary.getSeqno())
        .ifPresent(accountSummaryEntity -> {
          accountSummaryEntity.setBasicSearchTimestamp(basicSearchTimestamp);
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
  public void updateTransactionSyncedAt(long banksaladUserId, String organizationId, AccountSummary accountSummary,
      LocalDateTime transactionSyncedAt) {

    accountSummaryRepository
        .findByBanksaladUserIdAndOrganizationIdAndAccountNumAndSeqno(
            banksaladUserId, organizationId, accountSummary.getAccountNum(), accountSummary.getSeqno())
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
