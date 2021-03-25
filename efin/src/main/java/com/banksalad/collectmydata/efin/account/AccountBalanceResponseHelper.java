package com.banksalad.collectmydata.efin.account;

import org.springframework.stereotype.Component;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.util.ObjectComparator;
import com.banksalad.collectmydata.efin.account.dto.AccountBalance;
import com.banksalad.collectmydata.efin.account.dto.ListAccountBalancesResponse;
import com.banksalad.collectmydata.efin.common.db.entity.BalanceEntity;
import com.banksalad.collectmydata.efin.common.db.repository.BalanceHistoryRepository;
import com.banksalad.collectmydata.efin.common.db.repository.BalanceRepository;
import com.banksalad.collectmydata.efin.common.mapper.BalanceHistoryMapper;
import com.banksalad.collectmydata.efin.common.mapper.BalanceMapper;
import com.banksalad.collectmydata.efin.common.service.AccountSummaryService;
import com.banksalad.collectmydata.efin.summary.dto.AccountSummary;
import com.banksalad.collectmydata.finance.api.accountinfo.AccountInfoResponseHelper;
import com.banksalad.collectmydata.finance.api.accountinfo.dto.AccountResponse;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.util.List;

import static com.banksalad.collectmydata.finance.common.constant.FinanceConstant.ENTITY_EXCLUDE_FIELD;

@Component
@RequiredArgsConstructor
public class AccountBalanceResponseHelper implements AccountInfoResponseHelper<AccountSummary, List<AccountBalance>> {

  private final AccountSummaryService accountSummaryService;

  private final BalanceRepository balanceRepository;
  private final BalanceHistoryRepository balanceHistoryRepository;

  private final BalanceMapper balanceMapper = Mappers.getMapper(BalanceMapper.class);
  private final BalanceHistoryMapper balanceHistoryMapper = Mappers.getMapper(BalanceHistoryMapper.class);

  @Override
  public List<AccountBalance> getAccountFromResponse(AccountResponse accountResponse) {
    return ((ListAccountBalancesResponse) accountResponse).getAccountBalances();
  }

  @Override
  public void saveAccountAndHistory(ExecutionContext executionContext, AccountSummary accountSummary,
      List<AccountBalance> accountBalances) {

    long banksaladUserId = executionContext.getBanksaladUserId();
    String organizationId = executionContext.getOrganizationId();
    LocalDateTime syncedAt = executionContext.getSyncStartedAt();

    accountBalances.forEach(accountBalance -> {
      BalanceEntity balanceEntity = balanceMapper.dtoToEntity(accountBalance);
      balanceEntity.setSyncedAt(syncedAt);
      balanceEntity.setBanksaladUserId(banksaladUserId);
      balanceEntity.setOrganizationId(organizationId);
      balanceEntity.setSubKey(accountSummary.getSubKey());

      BalanceEntity existingBalanceEntity = balanceRepository.findByBanksaladUserIdAndOrganizationIdAndSubKeyAndFobName(
          banksaladUserId, organizationId, accountSummary.getSubKey(), accountBalance.getFobName())
          .map(b -> {
            balanceEntity.setId(b.getId());
            return b;
          })
          .orElseGet(() -> BalanceEntity.builder().build());

      if (!ObjectComparator.isSame(balanceEntity, existingBalanceEntity, ENTITY_EXCLUDE_FIELD)) {
        balanceRepository.save(balanceEntity);
        balanceHistoryRepository.save(balanceHistoryMapper.toHistoryEntity(balanceEntity));
      }
    });
  }

  @Override
  public void saveSearchTimestamp(ExecutionContext executionContext, AccountSummary accountSummary,
      long searchTimestamp) {
    accountSummaryService.updateBalanceSearchTimestamp(executionContext.getBanksaladUserId(),
        executionContext.getOrganizationId(), accountSummary, searchTimestamp);
  }

  @Override
  public void saveResponseCode(ExecutionContext executionContext, AccountSummary accountSummary, String responseCode) {
    accountSummaryService.updateBalanceResponseCode(executionContext.getBanksaladUserId(),
        executionContext.getOrganizationId(), accountSummary, responseCode);
  }
}
