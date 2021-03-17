package com.banksalad.collectmydata.bank.invest;

import org.springframework.stereotype.Component;

import com.banksalad.collectmydata.bank.common.db.entity.InvestAccountBasicEntity;
import com.banksalad.collectmydata.bank.common.db.repository.InvestAccountBasicHistoryRepository;
import com.banksalad.collectmydata.bank.common.db.repository.InvestAccountBasicRepository;
import com.banksalad.collectmydata.bank.common.mapper.InvestAccountBasicHistoryMapper;
import com.banksalad.collectmydata.bank.common.mapper.InvestAccountBasicMapper;
import com.banksalad.collectmydata.bank.common.service.AccountSummaryService;
import com.banksalad.collectmydata.bank.invest.dto.GetInvestAccountBasicResponse;
import com.banksalad.collectmydata.bank.invest.dto.InvestAccountBasic;
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
public class InvestAccountBasicInfoResponseHelper implements
    AccountInfoResponseHelper<AccountSummary, InvestAccountBasic> {

  private final AccountSummaryService accountSummaryService;

  private final InvestAccountBasicRepository investAccountBasicRepository;
  private final InvestAccountBasicHistoryRepository investAccountBasicHistoryRepository;

  private final InvestAccountBasicMapper investAccountBasicMapper = Mappers.getMapper(InvestAccountBasicMapper.class);
  private final InvestAccountBasicHistoryMapper investAccountBasicHistoryMapper = Mappers
      .getMapper(InvestAccountBasicHistoryMapper.class);

  @Override
  public InvestAccountBasic getAccountFromResponse(AccountResponse accountResponse) {
    return ((GetInvestAccountBasicResponse) accountResponse).getInvestAccountBasic();
  }

  @Override
  public void saveAccountAndHistory(ExecutionContext executionContext, AccountSummary accountSummary,
      InvestAccountBasic investAccountBasic) {
    InvestAccountBasicEntity investAccountBasicEntity = investAccountBasicMapper.dtoToEntity(investAccountBasic);
    investAccountBasicEntity.setBanksaladUserId(executionContext.getBanksaladUserId());
    investAccountBasicEntity.setOrganizationId(executionContext.getOrganizationId());
    investAccountBasicEntity.setSyncedAt(executionContext.getSyncStartedAt());
    investAccountBasicEntity.setAccountNum(accountSummary.getAccountNum());
    investAccountBasicEntity.setSeqno(accountSummary.getSeqno());

    InvestAccountBasicEntity existingInvestAccountBasicEntity = investAccountBasicRepository
        .findByBanksaladUserIdAndOrganizationIdAndAccountNumAndSeqno(
            executionContext.getBanksaladUserId(),
            executionContext.getOrganizationId(),
            accountSummary.getAccountNum(),
            accountSummary.getSeqno()
        ).orElse(null);

    if (existingInvestAccountBasicEntity != null) {
      investAccountBasicEntity.setId(existingInvestAccountBasicEntity.getId());
    }

    if (!ObjectComparator.isSame(investAccountBasicEntity, existingInvestAccountBasicEntity, ENTITY_EXCLUDE_FIELD)) {
      investAccountBasicRepository.save(investAccountBasicEntity);
      investAccountBasicHistoryRepository
          .save(investAccountBasicHistoryMapper.toInvestAccountBasicHistoryEntity(investAccountBasicEntity));
    }
  }

  @Override
  public void saveSearchTimestamp(ExecutionContext executionContext, AccountSummary accountSummary,
      long searchTimestamp) {
    accountSummaryService.updateBasicSearchTimestamp(
        executionContext.getBanksaladUserId(),
        executionContext.getOrganizationId(),
        accountSummary,
        searchTimestamp
    );
  }

  @Override
  public void saveResponseCode(ExecutionContext executionContext, AccountSummary accountSummary, String responseCode) {
    accountSummaryService.updateBasicResponseCode(
        executionContext.getBanksaladUserId(),
        executionContext.getOrganizationId(),
        accountSummary,
        responseCode
    );
  }
}
