package com.banksalad.collectmydata.capital.account;

import org.springframework.stereotype.Component;

import com.banksalad.collectmydata.capital.account.dto.AccountBasic;
import com.banksalad.collectmydata.capital.account.dto.GetAccountBasicResponse;
import com.banksalad.collectmydata.capital.common.db.entity.AccountBasicEntity;
import com.banksalad.collectmydata.capital.common.db.mapper.AccountBasicHistoryMapper;
import com.banksalad.collectmydata.capital.common.db.mapper.AccountBasicMapper;
import com.banksalad.collectmydata.capital.common.db.repository.AccountBasicHistoryRepository;
import com.banksalad.collectmydata.capital.common.db.repository.AccountBasicRepository;
import com.banksalad.collectmydata.capital.common.service.AccountSummaryService;
import com.banksalad.collectmydata.capital.summary.dto.AccountSummary;
import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.util.ObjectComparator;
import com.banksalad.collectmydata.finance.api.accountinfo.AccountInfoResponseHelper;
import com.banksalad.collectmydata.finance.api.accountinfo.dto.AccountResponse;
import com.banksalad.collectmydata.finance.common.constant.FinanceConstant;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;

@Component
@RequiredArgsConstructor
public class AccountBasicResponseHelper implements AccountInfoResponseHelper<AccountSummary, AccountBasic> {

  private final AccountSummaryService accountSummaryService;
  private final AccountBasicRepository accountBasicRepository;
  private final AccountBasicHistoryRepository accountBasicHistoryRepository;
  private final AccountBasicMapper accountBasicMapper = Mappers.getMapper(AccountBasicMapper.class);
  private final AccountBasicHistoryMapper accountBasicHistoryMapper = Mappers
      .getMapper(AccountBasicHistoryMapper.class);

  @Override
  public AccountBasic getAccountFromResponse(AccountResponse accountResponse) {
    return ((GetAccountBasicResponse) accountResponse).getAccountBasic();
  }

  @Override
  public void saveAccountAndHistory(ExecutionContext executionContext, AccountSummary accountSummary,
      AccountBasic accountBasic) {

    long banksaladUserId = executionContext.getBanksaladUserId();
    String organizationId = executionContext.getOrganizationId();
    String accountNum = accountSummary.getAccountNum();
    String seqno = accountSummary.getSeqno();

    accountBasic.setAccountNum(accountNum);
    accountBasic.setSeqno(seqno);

    AccountBasicEntity accountBasicEntity = accountBasicMapper.toAccountBasicEntityFrom(accountBasic);
    accountBasicEntity.setSyncedAt(executionContext.getSyncStartedAt());
    accountBasicEntity.setBanksaladUserId(banksaladUserId);
    accountBasicEntity.setOrganizationId(organizationId);
    accountBasicEntity.setAccountNum(accountNum);
    accountBasicEntity.setSeqno(seqno);

    AccountBasicEntity existingAccountBasicEntity = accountBasicRepository
        .findByBanksaladUserIdAndOrganizationIdAndAccountNumAndSeqno(banksaladUserId, organizationId, accountNum, seqno)
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
            accountSummary, searchTimestamp);
  }

  @Override
  public void saveResponseCode(ExecutionContext executionContext, AccountSummary accountSummary, String responseCode) {
    accountSummaryService
        .updateBasicResponseCode(executionContext.getBanksaladUserId(), executionContext.getOrganizationId(),
            accountSummary, responseCode);
  }
}
