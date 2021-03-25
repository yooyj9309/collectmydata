package com.banksalad.collectmydata.efin.account;

import org.springframework.stereotype.Component;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.util.ObjectComparator;
import com.banksalad.collectmydata.efin.account.dto.AccountCharge;
import com.banksalad.collectmydata.efin.account.dto.GetAccountChargeResponse;
import com.banksalad.collectmydata.efin.common.db.entity.ChargeEntity;
import com.banksalad.collectmydata.efin.common.db.repository.ChargeHistoryRepository;
import com.banksalad.collectmydata.efin.common.db.repository.ChargeRepository;
import com.banksalad.collectmydata.efin.common.mapper.ChargeHistoryMapper;
import com.banksalad.collectmydata.efin.common.mapper.ChargeMapper;
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

  private final ChargeRepository chargeRepository;
  private final ChargeHistoryRepository chargeHistoryRepository;

  private final ChargeMapper chargeMapper = Mappers.getMapper(ChargeMapper.class);
  private final ChargeHistoryMapper chargeHistoryMapper = Mappers.getMapper(ChargeHistoryMapper.class);

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

    ChargeEntity chargeEntity = chargeMapper.dtoToEntity(accountCharge);
    chargeEntity.setSyncedAt(syncedAt);
    chargeEntity.setBanksaladUserId(banksaladUserId);
    chargeEntity.setOrganizationId(organizationId);
    chargeEntity.setSubKey(accountSummary.getSubKey());

    ChargeEntity existingChargeEntity = chargeRepository
        .findByBanksaladUserIdAndOrganizationIdAndSubKey(banksaladUserId, organizationId, accountSummary.getSubKey())
        .map(c -> {
          chargeEntity.setId(c.getId());
          return c;
        })
        .orElseGet(() -> ChargeEntity.builder().build());

    if (!ObjectComparator.isSame(chargeEntity, existingChargeEntity, ENTITY_EXCLUDE_FIELD)) {
      chargeRepository.save(chargeEntity);
      chargeHistoryRepository.save(chargeHistoryMapper.toHistoryEntity(chargeEntity));
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
