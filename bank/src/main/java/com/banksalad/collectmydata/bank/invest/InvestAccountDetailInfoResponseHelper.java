package com.banksalad.collectmydata.bank.invest;

import org.springframework.stereotype.Component;

import com.banksalad.collectmydata.bank.common.db.entity.InvestAccountDetailEntity;
import com.banksalad.collectmydata.bank.common.db.entity.mapper.InvestAccountDetailHistoryMapper;
import com.banksalad.collectmydata.bank.common.db.entity.mapper.InvestAccountDetailMapper;
import com.banksalad.collectmydata.bank.common.db.repository.InvestAccountDetailHistoryRepository;
import com.banksalad.collectmydata.bank.common.db.repository.InvestAccountDetailRepository;
import com.banksalad.collectmydata.bank.common.service.AccountSummaryService;
import com.banksalad.collectmydata.bank.invest.dto.GetInvestAccountDetailResponse;
import com.banksalad.collectmydata.bank.invest.dto.InvestAccountDetail;
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
public class InvestAccountDetailInfoResponseHelper implements
    AccountInfoResponseHelper<AccountSummary, InvestAccountDetail> {

  private final AccountSummaryService accountSummaryService;
  private final InvestAccountDetailRepository investAccountDetailRepository;
  private final InvestAccountDetailHistoryRepository investAccountDetailHistoryRepository;

  private final InvestAccountDetailMapper investAccountDetailMapper = Mappers
      .getMapper(InvestAccountDetailMapper.class);

  private final InvestAccountDetailHistoryMapper investAccountDetailHistoryMapper = Mappers
      .getMapper(InvestAccountDetailHistoryMapper.class);

  @Override
  public InvestAccountDetail getAccountFromResponse(AccountResponse accountResponse) {
    return ((GetInvestAccountDetailResponse) accountResponse).getInvestAccountDetail();
  }

  @Override
  public void saveAccountAndHistory(ExecutionContext executionContext, AccountSummary accountSummary,
      InvestAccountDetail investAccountDetail) {
    InvestAccountDetailEntity investAccountDetailEntity = investAccountDetailMapper.dtoToEntity(investAccountDetail);
    investAccountDetailEntity.setBanksaladUserId(executionContext.getBanksaladUserId());
    investAccountDetailEntity.setOrganizationId(executionContext.getOrganizationId());
    investAccountDetailEntity.setSyncedAt(executionContext.getSyncStartedAt());
    investAccountDetailEntity.setAccountNum(accountSummary.getAccountNum());
    investAccountDetailEntity.setSeqno(accountSummary.getSeqno());

    InvestAccountDetailEntity existingInvestAccountDetailEntity = investAccountDetailRepository
        .findByBanksaladUserIdAndOrganizationIdAndAccountNumAndSeqnoAndCurrencyCode(
            executionContext.getBanksaladUserId(),
            executionContext.getOrganizationId(),
            accountSummary.getAccountNum(),
            accountSummary.getSeqno(),
            investAccountDetail.getCurrencyCode()
        );

    if (existingInvestAccountDetailEntity != null) {
      investAccountDetailEntity.setId(existingInvestAccountDetailEntity.getId());
    }

    if (!ObjectComparator.isSame(investAccountDetailEntity, existingInvestAccountDetailEntity, ENTITY_EXCLUDE_FIELD)) {
      investAccountDetailRepository.save(investAccountDetailEntity);
      investAccountDetailHistoryRepository
          .save(investAccountDetailHistoryMapper.toInvestAccountDetailHistoryEntity(investAccountDetailEntity));
    }
  }

  @Override
  public void saveSearchTimestamp(ExecutionContext executionContext, AccountSummary accountSummary,
      long searchTimestamp) {
    accountSummaryService.updateDetailSearchTimestamp(
        executionContext.getBanksaladUserId(),
        executionContext.getOrganizationId(),
        accountSummary,
        searchTimestamp
    );
  }

  @Override
  public void saveResponseCode(ExecutionContext executionContext, AccountSummary accountSummary, String responseCode) {
    accountSummaryService
        .updateDetailResponseCode(
            executionContext.getBanksaladUserId(),
            executionContext.getOrganizationId(),
            accountSummary,
            responseCode
        );
  }
}
