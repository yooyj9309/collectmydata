package com.banksalad.collectmydata.bank.deposit;

import org.springframework.stereotype.Component;

import com.banksalad.collectmydata.bank.common.db.entity.DepositAccountBasicEntity;
import com.banksalad.collectmydata.bank.common.db.repository.DepositAccountBasicHistoryRepository;
import com.banksalad.collectmydata.bank.common.db.repository.DepositAccountBasicRepository;
import com.banksalad.collectmydata.bank.common.mapper.DepositAccountBasicHistoryMapper;
import com.banksalad.collectmydata.bank.common.mapper.DepositAccountBasicMapper;
import com.banksalad.collectmydata.bank.common.service.AccountSummaryService;
import com.banksalad.collectmydata.bank.deposit.dto.DepositAccountBasic;
import com.banksalad.collectmydata.bank.deposit.dto.GetDepositAccountBasicResponse;
import com.banksalad.collectmydata.bank.summary.dto.AccountSummary;
import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.util.ObjectComparator;
import com.banksalad.collectmydata.finance.api.accountinfo.AccountInfoResponseHelper;
import com.banksalad.collectmydata.finance.api.accountinfo.dto.AccountResponse;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;

import static com.banksalad.collectmydata.finance.common.constant.FinanceConstant.ENTITY_EXCLUDE_FIELD;

@Component
@RequiredArgsConstructor
public class DepositAccountBasicInfoResponseHelper implements
    AccountInfoResponseHelper<AccountSummary, DepositAccountBasic> {

  private final AccountSummaryService accountSummaryService;

  private final DepositAccountBasicRepository depositAccountBasicRepository;
  private final DepositAccountBasicHistoryRepository depositAccountBasicHistoryRepository;

  private final DepositAccountBasicMapper depositAccountBasicMapper = Mappers
      .getMapper(DepositAccountBasicMapper.class);
  private final DepositAccountBasicHistoryMapper depositAccountBasicHistoryMapper = Mappers
      .getMapper(DepositAccountBasicHistoryMapper.class);

  @Override
  public DepositAccountBasic getAccountFromResponse(AccountResponse accountResponse) {
    return ((GetDepositAccountBasicResponse) accountResponse).getDepositAccountBasic();
  }

  @Override
  public void saveAccountAndHistory(ExecutionContext executionContext, AccountSummary accountSummary,
      DepositAccountBasic depositAccountBasic) {
    // convert to entity
    DepositAccountBasicEntity depositAccountBasicEntity = depositAccountBasicMapper.dtoToEntity(depositAccountBasic);

    depositAccountBasicEntity.setBanksaladUserId(executionContext.getBanksaladUserId());
    depositAccountBasicEntity.setOrganizationId(executionContext.getOrganizationId());
    depositAccountBasicEntity.setSyncedAt(executionContext.getSyncStartedAt());
    depositAccountBasicEntity.setAccountNum(accountSummary.getAccountNum());
    depositAccountBasicEntity.setSeqno(accountSummary.getSeqno());

    // load existing account entity
    DepositAccountBasicEntity existingDepositAccountBasicEntity = depositAccountBasicRepository
        .findByBanksaladUserIdAndOrganizationIdAndAccountNumAndSeqno(
            executionContext.getBanksaladUserId(),
            executionContext.getOrganizationId(),
            accountSummary.getAccountNum(),
            accountSummary.getSeqno())
        .orElse(null);

    // copy PK for update
    if (existingDepositAccountBasicEntity != null) {
      depositAccountBasicEntity.setId(existingDepositAccountBasicEntity.getId());
    }

    // upsert deposit account basic and insert history if needed
    if (!ObjectComparator.isSame(depositAccountBasicEntity, existingDepositAccountBasicEntity, ENTITY_EXCLUDE_FIELD)) {
      depositAccountBasicRepository.save(depositAccountBasicEntity);
      depositAccountBasicHistoryRepository
          .save(depositAccountBasicHistoryMapper.toHistoryEntity(depositAccountBasicEntity));
    }
  }

  @Override
  public void saveSearchTimestamp(ExecutionContext executionContext, AccountSummary accountSummary,
      long searchTimestamp) {

    accountSummaryService
        .updateBasicSearchTimestamp(executionContext.getBanksaladUserId(), executionContext.getOrganizationId(),
            accountSummary,
            searchTimestamp);
  }

  @Override
  public void saveResponseCode(ExecutionContext executionContext, AccountSummary accountSummary, String responseCode) {

    accountSummaryService
        .updateBasicResponseCode(executionContext.getBanksaladUserId(), executionContext.getOrganizationId(),
            accountSummary,
            responseCode);
  }
}
