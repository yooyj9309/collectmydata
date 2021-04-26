package com.banksalad.collectmydata.efin.account;

import org.springframework.stereotype.Component;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.util.ObjectComparator;
import com.banksalad.collectmydata.efin.account.dto.AccountCharge;
import com.banksalad.collectmydata.efin.account.dto.GetAccountChargeResponse;
import com.banksalad.collectmydata.efin.common.db.entity.AccountChargeEntity;
import com.banksalad.collectmydata.efin.common.db.repository.AccountChargeHistoryRepository;
import com.banksalad.collectmydata.efin.common.db.repository.AccountChargeRepository;
import com.banksalad.collectmydata.efin.common.mapper.AccountChargeMapper;
import com.banksalad.collectmydata.efin.common.mapper.AccountChargeHistoryMapper;
import com.banksalad.collectmydata.efin.common.service.AccountSummaryService;
import com.banksalad.collectmydata.efin.summary.dto.AccountSummary;
import com.banksalad.collectmydata.finance.api.accountinfo.AccountInfoResponseHelper;
import com.banksalad.collectmydata.finance.api.accountinfo.dto.AccountResponse;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;

import static com.banksalad.collectmydata.finance.common.constant.FinanceConstant.ENTITY_EXCLUDE_FIELD;

@Component
@RequiredArgsConstructor
public class AccountChargeResponseHelper implements AccountInfoResponseHelper<AccountSummary, AccountCharge> {

  private final AccountSummaryService accountSummaryService;

  private final AccountChargeRepository accountChargeRepository;
  private final AccountChargeHistoryRepository accountChargeHistoryRepository;

  private final AccountChargeMapper accountChargeMapper = Mappers.getMapper(AccountChargeMapper.class);
  private final AccountChargeHistoryMapper accountChargeHistoryMapper = Mappers.getMapper(AccountChargeHistoryMapper.class);

  @Override
  public AccountCharge getAccountFromResponse(AccountResponse accountResponse) {
    return ((GetAccountChargeResponse) accountResponse).getAccountCharge();
  }

  @Override
  public void saveAccountAndHistory(ExecutionContext executionContext, AccountSummary accountSummary,
      AccountCharge accountCharge) {

    long banksaladUserId = executionContext.getBanksaladUserId();
    String organizationId = executionContext.getOrganizationId();
    LocalDateTime syncedAt = executionContext.getSyncStartedAt();

    AccountChargeEntity accountChargeEntity = accountChargeMapper.dtoToEntity(accountCharge);
    accountChargeEntity.setSyncedAt(syncedAt);
    accountChargeEntity.setBanksaladUserId(banksaladUserId);
    accountChargeEntity.setOrganizationId(organizationId);
    accountChargeEntity.setSubKey(accountSummary.getSubKey());

    AccountChargeEntity existingAccountChargeEntity = accountChargeRepository
        .findByBanksaladUserIdAndOrganizationIdAndSubKey(banksaladUserId, organizationId, accountSummary.getSubKey())
        .map(c -> {
          accountChargeEntity.setId(c.getId());
          return c;
        })
        .orElseGet(() -> AccountChargeEntity.builder().build());

    if (!ObjectComparator.isSame(accountChargeEntity, existingAccountChargeEntity, ENTITY_EXCLUDE_FIELD)) {
      accountChargeRepository.save(accountChargeEntity);
      accountChargeHistoryRepository.save(accountChargeHistoryMapper.toHistoryEntity(accountChargeEntity));
    }
  }

  @Override
  public void saveSearchTimestamp(ExecutionContext executionContext, AccountSummary accountSummary,
      long searchTimestamp) {
    accountSummaryService.updateChargeSearchTimestamp(executionContext.getBanksaladUserId(),
        executionContext.getOrganizationId(), accountSummary, searchTimestamp);

  }

  @Override
  public void saveResponseCode(ExecutionContext executionContext, AccountSummary accountSummary, String responseCode) {
    accountSummaryService.updateChargeResponseCode(executionContext.getBanksaladUserId(),
        executionContext.getOrganizationId(), accountSummary, responseCode);
  }
}
