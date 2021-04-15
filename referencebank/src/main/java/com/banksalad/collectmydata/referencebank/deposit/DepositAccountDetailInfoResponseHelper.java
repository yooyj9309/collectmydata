package com.banksalad.collectmydata.referencebank.deposit;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.util.ObjectComparator;
import com.banksalad.collectmydata.finance.api.accountinfo.AccountInfoResponseHelper;
import com.banksalad.collectmydata.finance.api.accountinfo.dto.AccountResponse;
import com.banksalad.collectmydata.referencebank.common.db.entity.DepositAccountDetailEntity;
import com.banksalad.collectmydata.referencebank.common.db.repository.DepositAccountDetailHistoryRepository;
import com.banksalad.collectmydata.referencebank.common.db.repository.DepositAccountDetailRepository;
import com.banksalad.collectmydata.referencebank.common.mapper.DepositAccountDetailHistoryMapper;
import com.banksalad.collectmydata.referencebank.common.mapper.DepositAccountDetailMapper;
import com.banksalad.collectmydata.referencebank.common.service.AccountSummaryService;
import com.banksalad.collectmydata.referencebank.deposit.dto.DepositAccountDetail;
import com.banksalad.collectmydata.referencebank.deposit.dto.GetDepositAccountDetailResponse;
import com.banksalad.collectmydata.referencebank.summary.dto.AccountSummary;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;

import java.util.List;

import static com.banksalad.collectmydata.finance.common.constant.FinanceConstant.ENTITY_EXCLUDE_FIELD;

@Component
@RequiredArgsConstructor
public class DepositAccountDetailInfoResponseHelper implements
    AccountInfoResponseHelper<AccountSummary, List<DepositAccountDetail>> {

  private final String CURRENCY_KRW = "KRW";

  private final AccountSummaryService accountSummaryService;
  private final DepositAccountDetailRepository depositAccountDetailRepository;
  private final DepositAccountDetailHistoryRepository depositAccountDetailHistoryRepository;

  private final DepositAccountDetailMapper depositAccountDetailMapper = Mappers.getMapper(DepositAccountDetailMapper.class);

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
      saveAccountAndHistory(executionContext, accountSummary, depositAccountDetail);
    }
  }

  private void saveAccountAndHistory(ExecutionContext executionContext, AccountSummary accountSummary,
      DepositAccountDetail depositAccountDetail) {

    DepositAccountDetailEntity depositAccountDetailEntity = depositAccountDetailMapper
        .dtoToEntity(depositAccountDetail);

    depositAccountDetailEntity.setBanksaladUserId(executionContext.getBanksaladUserId());
    depositAccountDetailEntity.setOrganizationId(executionContext.getOrganizationId());
    depositAccountDetailEntity.setSyncedAt(executionContext.getSyncStartedAt());
    depositAccountDetailEntity.setAccountNum(accountSummary.getAccountNum());
    depositAccountDetailEntity.setSeqno(accountSummary.getSeqno());

    if (depositAccountDetailEntity.getCurrencyCode() == null || depositAccountDetailEntity.getCurrencyCode().length() == 0) {
      depositAccountDetailEntity.setCurrencyCode(CURRENCY_KRW);
    }

    // load existing account detail entity
    DepositAccountDetailEntity existingDepositAccountDetailEntity = depositAccountDetailRepository
        .findByBanksaladUserIdAndOrganizationIdAndAccountNumAndSeqnoAndCurrencyCode(
            executionContext.getBanksaladUserId(), executionContext.getOrganizationId(),
            accountSummary.getAccountNum(),
            accountSummary.getSeqno(), depositAccountDetail.getCurrencyCode())
        .orElse(null);

    // copy PK for update
    if (existingDepositAccountDetailEntity != null) {
      depositAccountDetailEntity.setId(existingDepositAccountDetailEntity.getId());
    }

    // upsert deposit account detail and insert history if needed
    if (!ObjectComparator.isSame(depositAccountDetailEntity, existingDepositAccountDetailEntity, ENTITY_EXCLUDE_FIELD)) {
      depositAccountDetailRepository.save(depositAccountDetailEntity);
      depositAccountDetailHistoryRepository
          .save(depositAccountDetailHistoryMapper.toHistoryEntity(depositAccountDetailEntity));
    }
  }

  @Override
  public void saveSearchTimestamp(ExecutionContext executionContext, AccountSummary accountSummary, long searchTimeastamp) {
    accountSummaryService
        .updateDetailSearchTimestamp(executionContext.getBanksaladUserId(), executionContext.getOrganizationId(), accountSummary,
            searchTimeastamp);
  }

  @Override
  public void saveResponseCode(ExecutionContext executionContext, AccountSummary accountSummary, String responseCode) {
    accountSummaryService
        .updateDetailResponseCode(executionContext.getBanksaladUserId(), executionContext.getOrganizationId(), accountSummary,
            responseCode);
  }
}
