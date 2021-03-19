package com.banksalad.collectmydata.capital.account;

import com.banksalad.collectmydata.capital.account.dto.AccountDetail;
import com.banksalad.collectmydata.capital.account.dto.GetAccountDetailResponse;
import com.banksalad.collectmydata.capital.common.db.entity.AccountDetailEntity;
import com.banksalad.collectmydata.capital.common.db.mapper.AccountDetailHistoryMapper;
import com.banksalad.collectmydata.capital.common.db.mapper.AccountDetailMapper;
import com.banksalad.collectmydata.capital.common.db.repository.AccountDetailHistoryRepository;
import com.banksalad.collectmydata.capital.common.db.repository.AccountDetailRepository;
import com.banksalad.collectmydata.capital.common.service.AccountSummaryService;
import com.banksalad.collectmydata.capital.summary.dto.AccountSummary;
import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.util.ObjectComparator;
import com.banksalad.collectmydata.finance.api.accountinfo.AccountInfoResponseHelper;
import com.banksalad.collectmydata.finance.api.accountinfo.dto.AccountResponse;
import com.banksalad.collectmydata.finance.common.constant.FinanceConstant;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;

@Component
@RequiredArgsConstructor
public class AccountDetailResponseHelper implements AccountInfoResponseHelper<AccountSummary, AccountDetail> {

  private final AccountSummaryService accountSummaryService;
  private final AccountDetailRepository accountDetailRepository;
  private final AccountDetailHistoryRepository accountDetailHistoryRepository;
  private final AccountDetailMapper accountDetailMapper = Mappers.getMapper(AccountDetailMapper.class);
  private final AccountDetailHistoryMapper accountDetailHistoryMapper = Mappers
      .getMapper(AccountDetailHistoryMapper.class);

  @Override
  public AccountDetail getAccountFromResponse(AccountResponse accountResponse) {
    return ((GetAccountDetailResponse) accountResponse).getAccountDetail();
  }

  @Override
  public void saveAccountAndHistory(ExecutionContext executionContext, AccountSummary accountSummary,
      AccountDetail accountDetail) {

    long banksaladUserId = executionContext.getBanksaladUserId();
    String organizationId = executionContext.getOrganizationId();
    String accountNum = accountSummary.getAccountNum();
    String seqno = accountSummary.getSeqno();

    accountDetail.setAccountNum(accountNum);
    accountDetail.setSeqno(seqno);

    AccountDetailEntity accountDetailEntity = accountDetailMapper.dtoToEntity(accountDetail);
    accountDetailEntity.setSyncedAt(executionContext.getSyncStartedAt());
    accountDetailEntity.setBanksaladUserId(banksaladUserId);
    accountDetailEntity.setOrganizationId(organizationId);
    accountDetailEntity.setAccountNum(accountNum);
    accountDetailEntity.setSeqno(seqno);

    AccountDetailEntity existingAccountDetailEntity = accountDetailRepository
        .findByBanksaladUserIdAndOrganizationIdAndAccountNumAndSeqno(banksaladUserId, organizationId, accountNum, seqno)
        .orElse(null);

    if (existingAccountDetailEntity != null) {
      accountDetailEntity.setId(existingAccountDetailEntity.getId());
    }

    if (!ObjectComparator
        .isSame(accountDetailEntity, existingAccountDetailEntity, FinanceConstant.ENTITY_EXCLUDE_FIELD)) {
      accountDetailRepository.save(accountDetailEntity);
      accountDetailHistoryRepository.save(accountDetailHistoryMapper.toHistoryEntity(accountDetailEntity));
    }
  }

  @Override
  public void saveSearchTimestamp(ExecutionContext executionContext, AccountSummary accountSummary,
      long searchTimestamp) {
    accountSummaryService
        .updateDetailSearchTimestamp(executionContext.getBanksaladUserId(), executionContext.getOrganizationId(),
            accountSummary, searchTimestamp);
  }

  @Override
  public void saveResponseCode(ExecutionContext executionContext, AccountSummary accountSummary, String responseCode) {
    accountSummaryService
        .updateDetailResponseCode(executionContext.getBanksaladUserId(), executionContext.getOrganizationId(),
            accountSummary, responseCode);
  }
}
