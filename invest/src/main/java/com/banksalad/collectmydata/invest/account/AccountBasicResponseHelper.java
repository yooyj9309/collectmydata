package com.banksalad.collectmydata.invest.account;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.util.ObjectComparator;
import com.banksalad.collectmydata.finance.api.accountinfo.AccountInfoResponseHelper;
import com.banksalad.collectmydata.finance.api.accountinfo.dto.AccountResponse;
import com.banksalad.collectmydata.finance.common.constant.FinanceConstant;
import com.banksalad.collectmydata.invest.account.dto.AccountBasic;
import com.banksalad.collectmydata.invest.account.dto.GetAccountBasicResponse;
import com.banksalad.collectmydata.invest.common.db.entity.AccountBasicEntity;
import com.banksalad.collectmydata.invest.common.db.entity.mapper.AccountBasicHistoryMapper;
import com.banksalad.collectmydata.invest.common.db.entity.mapper.AccountBasicMapper;
import com.banksalad.collectmydata.invest.common.db.repository.AccountBasicHistoryRepository;
import com.banksalad.collectmydata.invest.common.db.repository.AccountBasicRepository;
import com.banksalad.collectmydata.invest.common.service.AccountSummaryService;
import com.banksalad.collectmydata.invest.summary.dto.AccountSummary;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;

@Component
@RequiredArgsConstructor
public class AccountBasicResponseHelper implements AccountInfoResponseHelper<AccountSummary, AccountBasic> {

  private final AccountSummaryService accountSummaryService;
  private final AccountBasicRepository accountBasicRepository;
  private final AccountBasicHistoryRepository accountBasicHistoryRepository;

  private final AccountBasicMapper accountBasicMapper = Mappers.getMapper(AccountBasicMapper.class);
  private final AccountBasicHistoryMapper accountBasicHistoryMapper = Mappers.getMapper(AccountBasicHistoryMapper.class);

  @Override
  public AccountBasic getAccountFromResponse(AccountResponse accountResponse) {
    return ((GetAccountBasicResponse) accountResponse).getAccountBasic();
  }

  @Override
  @Transactional
  public void saveAccountAndHistory(ExecutionContext executionContext, AccountSummary accountSummary,
      AccountBasic accountBasic) {

    AccountBasicEntity accountBasicEntity = accountBasicMapper.dtoToEntity(accountBasic);
    accountBasicEntity.setBanksaladUserId(executionContext.getBanksaladUserId());
    accountBasicEntity.setOrganizationId(executionContext.getOrganizationId());
    accountBasicEntity.setSyncedAt(executionContext.getSyncStartedAt());
    accountBasicEntity.setAccountNum(accountSummary.getAccountNum());

    AccountBasicEntity existingAccountBasicEntity = accountBasicRepository
        .findByBanksaladUserIdAndOrganizationIdAndAccountNum(executionContext.getBanksaladUserId(),
            executionContext.getOrganizationId(), accountSummary.getAccountNum())
        .orElse(null);

    if (existingAccountBasicEntity != null) {
      accountBasicEntity.setId(existingAccountBasicEntity.getId());
    }

    if (!ObjectComparator
        .isSame(accountBasicEntity, existingAccountBasicEntity, FinanceConstant.ENTITY_EXCLUDE_FIELD)) {
      accountBasicRepository.save(accountBasicEntity);
      accountBasicHistoryRepository.save(accountBasicHistoryMapper.toHistoryEntity(accountBasicEntity));
    }
  }

  @Override
  public void saveSearchTimestamp(ExecutionContext executionContext, AccountSummary accountSummary,
      long searchTimestamp) {

    accountSummaryService
        .updateBasicSearchTimestamp(executionContext.getBanksaladUserId(), executionContext.getOrganizationId(),
            accountSummary.getAccountNum(), searchTimestamp);
  }

  @Override
  public void saveResponseCode(ExecutionContext executionContext, AccountSummary accountSummary, String responseCode) {
    accountSummaryService
        .updateBasicResponseCode(executionContext.getBanksaladUserId(), executionContext.getOrganizationId(),
            accountSummary.getAccountNum(), responseCode);
  }
}
