package com.banksalad.collectmydata.capital.account;

import org.springframework.stereotype.Service;

import com.banksalad.collectmydata.capital.account.dto.AccountDetail;
import com.banksalad.collectmydata.capital.account.dto.AccountDetailResponse;
import com.banksalad.collectmydata.capital.common.db.entity.AccountDetailEntity;
import com.banksalad.collectmydata.capital.common.db.entity.AccountSummaryEntity;
import com.banksalad.collectmydata.capital.common.db.mapper.AccountDetailHistoryMapper;
import com.banksalad.collectmydata.capital.common.db.mapper.AccountDetailMapper;
import com.banksalad.collectmydata.capital.common.db.repository.AccountDetailHistoryRepository;
import com.banksalad.collectmydata.capital.common.db.repository.AccountDetailRepository;
import com.banksalad.collectmydata.capital.common.db.repository.AccountSummaryRepository;
import com.banksalad.collectmydata.capital.common.dto.Organization;
import com.banksalad.collectmydata.capital.common.service.ExternalApiService;
import com.banksalad.collectmydata.capital.summary.dto.AccountSummary;
import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.util.ObjectComparator;
import javax.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.factory.Mappers;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

@Deprecated
@Slf4j
@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

  private final ExternalApiService externalApiService;
  private final AccountSummaryRepository accountSummaryRepository;
  private final AccountDetailRepository accountDetailRepository;
  private final AccountDetailHistoryRepository accountDetailHistoryRepository;

  private final AccountDetailMapper accountDetailMapper = Mappers.getMapper(AccountDetailMapper.class);
  private final AccountDetailHistoryMapper accountDetailHistoryMapper = Mappers
      .getMapper(AccountDetailHistoryMapper.class);

  @Override
  public List<AccountDetail> listAccountDetails(ExecutionContext executionContext, Organization organization,
      List<AccountSummary> accountSummaries) {
    List<AccountDetail> accountDetails = new ArrayList<>();

    boolean isExceptionOccurred = FALSE;
    for (AccountSummary accountSummary : accountSummaries) {
      try {
        AccountDetailResponse response = externalApiService
            .getAccountDetail(executionContext, organization, accountSummary);
        AccountDetailEntity accountDetailEntity = saveAccountDetailWithHistory(executionContext, accountSummary,
            response);
        accountDetails.add(accountDetailMapper.toAccountDetailFrom(accountDetailEntity));
        updateSearchTimestamp(executionContext, accountSummary, response);
      } catch (Exception e) {
        isExceptionOccurred = TRUE;
        log.error("Failed to save account detail", e);
      }
    }
    return accountDetails;
  }

  private AccountDetailEntity saveAccountDetailWithHistory(ExecutionContext executionContext,
      AccountSummary accountSummary, AccountDetailResponse accountDetailResponse) {
    AccountDetailEntity accountDetailEntity = accountDetailMapper.toAccountDetailEntityFrom(accountDetailResponse);
    accountDetailEntity.setSyncedAt(executionContext.getSyncStartedAt());
    accountDetailEntity.setBanksaladUserId(executionContext.getBanksaladUserId());
    accountDetailEntity.setOrganizationId(executionContext.getOrganizationId());
    accountDetailEntity.setAccountNum(accountSummary.getAccountNum());
    accountDetailEntity.setSeqno(accountSummary.getSeqno());

    AccountDetailEntity existingAccountDetailEntity = accountDetailRepository
        .findByBanksaladUserIdAndOrganizationIdAndAccountNumAndSeqno(executionContext.getBanksaladUserId(),
            executionContext.getOrganizationId(), accountSummary.getAccountNum(), accountSummary.getSeqno())
        .orElse((AccountDetailEntity.builder().build()));

    if (existingAccountDetailEntity.getId() != null) {
      accountDetailEntity.setId(existingAccountDetailEntity.getId());
    }

    if (!ObjectComparator.isSame(accountDetailEntity, existingAccountDetailEntity,
        "syncedAt", "createdAt", "createdBy", "updatedAt", "updatedBy")) {
      accountDetailRepository.save(accountDetailEntity);
      accountDetailHistoryRepository
          .save(accountDetailHistoryMapper.toAccountDetailHistoryEntityFrom(accountDetailEntity));
    }

    return accountDetailEntity;
  }

  private void updateSearchTimestamp(ExecutionContext executionContext, AccountSummary accountSummary,
      AccountDetailResponse accountDetailResponse) {
    AccountSummaryEntity accountSummaryEntity = accountSummaryRepository
        .findByBanksaladUserIdAndOrganizationIdAndAccountNumAndSeqno(
            executionContext.getBanksaladUserId(),
            executionContext.getOrganizationId(),
            accountSummary.getAccountNum(),
            accountSummary.getSeqno())
        .orElseThrow(EntityNotFoundException::new);

    accountSummaryEntity.setDetailSearchTimestamp(accountDetailResponse.getSearchTimestamp());
    accountSummaryRepository.save(accountSummaryEntity);
  }
}
