package com.banksalad.collectmydata.bank.deposit;

import org.springframework.stereotype.Component;

import com.banksalad.collectmydata.bank.common.db.entity.DepositAccountDetailEntity;
import com.banksalad.collectmydata.bank.common.db.entity.DepositAccountDetailHistoryEntity;
import com.banksalad.collectmydata.bank.common.db.repository.DepositAccountDetailHistoryRepository;
import com.banksalad.collectmydata.bank.common.db.repository.DepositAccountDetailRepository;
import com.banksalad.collectmydata.bank.common.mapper.DepositAccountDetailHistoryMapper;
import com.banksalad.collectmydata.bank.common.mapper.DepositAccountDetailMapper;
import com.banksalad.collectmydata.bank.common.service.AccountSummaryService;
import com.banksalad.collectmydata.bank.deposit.dto.DepositAccountDetail;
import com.banksalad.collectmydata.bank.deposit.dto.GetDepositAccountDetailResponse;
import com.banksalad.collectmydata.bank.summary.dto.AccountSummary;
import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.util.ObjectComparator;
import com.banksalad.collectmydata.finance.api.accountinfo.AccountInfoResponseHelper;
import com.banksalad.collectmydata.finance.api.accountinfo.dto.AccountResponse;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.factory.Mappers;

import java.util.List;

import static com.banksalad.collectmydata.common.util.ObjectComparator.*;
import static com.banksalad.collectmydata.finance.common.constant.FinanceConstant.CURRENCY_KRW;
import static com.banksalad.collectmydata.finance.common.constant.FinanceConstant.ENTITY_EXCLUDE_FIELD;

@Component
@RequiredArgsConstructor
public class DepositAccountDetailInfoResponseHelper implements
    AccountInfoResponseHelper<AccountSummary, List<DepositAccountDetail>> {

  private final AccountSummaryService accountSummaryService;

  private final DepositAccountDetailRepository depositAccountDetailRepository;
  private final DepositAccountDetailHistoryRepository depositAccountDetailHistoryRepository;

  private final DepositAccountDetailMapper depositAccountDetailMapper = Mappers
      .getMapper(DepositAccountDetailMapper.class);
  private final DepositAccountDetailHistoryMapper depositAccountDetailHistoryMapper = Mappers
      .getMapper(DepositAccountDetailHistoryMapper.class);

  @Override
  public List<DepositAccountDetail> getAccountFromResponse(AccountResponse accountResponse) {
    return ((GetDepositAccountDetailResponse) accountResponse).getDepositAccountDetails();
  }

  @Override
  public void saveAccountAndHistory(ExecutionContext executionContext, AccountSummary accountSummary,
      List<DepositAccountDetail> depositAccountDetails) {

    for (DepositAccountDetail depositAccountDetail : depositAccountDetails) {
      DepositAccountDetailEntity depositAccountDetailEntity = depositAccountDetailMapper
          .dtoToEntity(depositAccountDetail);

      depositAccountDetailEntity.setBanksaladUserId(executionContext.getBanksaladUserId());
      depositAccountDetailEntity.setOrganizationId(executionContext.getOrganizationId());
      depositAccountDetailEntity.setSyncedAt(executionContext.getSyncStartedAt());
      depositAccountDetailEntity.setAccountNum(accountSummary.getAccountNum());
      depositAccountDetailEntity.setSeqno(accountSummary.getSeqno());
      depositAccountDetailEntity.setConsentId(executionContext.getConsentId());
      depositAccountDetailEntity.setSyncRequestId(executionContext.getSyncRequestId());
      depositAccountDetailEntity.setCreatedBy(String.valueOf(executionContext.getRequestedBy()));
      depositAccountDetailEntity.setUpdatedBy(String.valueOf(executionContext.getRequestedBy()));

      if (StringUtils.isEmpty(depositAccountDetailEntity.getCurrencyCode())) {
        depositAccountDetailEntity.setCurrencyCode(CURRENCY_KRW);
      }

      // load existing account detail entity
      DepositAccountDetailEntity existingDepositAccountDetailEntity = depositAccountDetailRepository
          .findByBanksaladUserIdAndOrganizationIdAndAccountNumAndSeqnoAndCurrencyCode(
              executionContext.getBanksaladUserId(),
              executionContext.getOrganizationId(),
              accountSummary.getAccountNum(),
              accountSummary.getSeqno(), depositAccountDetail.getCurrencyCode())
          .orElse(null);

      // copy PK for update
      if (existingDepositAccountDetailEntity != null) {
        depositAccountDetailEntity.setId(existingDepositAccountDetailEntity.getId());
        depositAccountDetailEntity.setCreatedBy(existingDepositAccountDetailEntity.getCreatedBy());
      }

      // upsert deposit account detail and insert history if needed
      if (!isSame(depositAccountDetailEntity, existingDepositAccountDetailEntity, ENTITY_EXCLUDE_FIELD)) {
        depositAccountDetailRepository.save(depositAccountDetailEntity);

        DepositAccountDetailHistoryEntity depositAccountDetailHistoryEntity = depositAccountDetailHistoryMapper
            .entityToHistoryEntity(depositAccountDetailEntity, DepositAccountDetailHistoryEntity.builder().build());
        depositAccountDetailHistoryRepository.save(depositAccountDetailHistoryEntity);
      }
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
