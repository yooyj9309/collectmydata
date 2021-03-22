package com.banksalad.collectmydata.capital.oplease;

import org.springframework.stereotype.Component;

import com.banksalad.collectmydata.capital.common.db.entity.OperatingLeaseEntity;
import com.banksalad.collectmydata.capital.common.mapper.OperatingLeaseHistoryMapper;
import com.banksalad.collectmydata.capital.common.db.repository.OperatingLeaseHistoryRepository;
import com.banksalad.collectmydata.capital.common.db.repository.OperatingLeaseRepository;
import com.banksalad.collectmydata.capital.common.service.AccountSummaryService;
import com.banksalad.collectmydata.capital.oplease.dto.GetOperatingLeaseBasicResponse;
import com.banksalad.collectmydata.capital.oplease.dto.OperatingLeaseBasic;
import com.banksalad.collectmydata.capital.summary.dto.AccountSummary;
import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.util.ObjectComparator;
import com.banksalad.collectmydata.finance.api.accountinfo.AccountInfoResponseHelper;
import com.banksalad.collectmydata.finance.api.accountinfo.dto.AccountResponse;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;

import static com.banksalad.collectmydata.finance.common.constant.FinanceConstant.ENTITY_EXCLUDE_FIELD;

@Component
@RequiredArgsConstructor
public class OperatingLeaseBasicResponseHelper implements
    AccountInfoResponseHelper<AccountSummary, OperatingLeaseBasic> {

  private final AccountSummaryService accountSummaryService;
  private final OperatingLeaseRepository operatingLeaseRepository;
  private final OperatingLeaseHistoryRepository operatingLeaseHistoryRepository;
  private final OperatingLeaseHistoryMapper operatingLeaseHistoryMapper = Mappers
      .getMapper(OperatingLeaseHistoryMapper.class);

  @Override
  public OperatingLeaseBasic getAccountFromResponse(AccountResponse accountResponse) {
    return ((GetOperatingLeaseBasicResponse) accountResponse).getOperatingLeaseBasic();
  }

  @Override
  public void saveAccountAndHistory(ExecutionContext executionContext, AccountSummary accountSummary,
      OperatingLeaseBasic operatingLeaseBasic) {
    long banksaladUserId = executionContext.getBanksaladUserId();
    String organizationId = executionContext.getOrganizationId();

    // response to entity
    OperatingLeaseEntity entity = OperatingLeaseEntity.builder()
        .syncedAt(executionContext.getSyncStartedAt())
        .banksaladUserId(banksaladUserId)
        .organizationId(organizationId)
        .accountNum(accountSummary.getAccountNum())
        .seqno(accountSummary.getSeqno())
        .holderName(operatingLeaseBasic.getHolderName())
        .issueDate(operatingLeaseBasic.getIssueDate())
        .expDate(operatingLeaseBasic.getExpDate())
        .repayDate(operatingLeaseBasic.getRepayDate())
        .repayMethod(operatingLeaseBasic.getRepayMethod())
        .repayOrgCode(operatingLeaseBasic.getRepayOrgCode())
        .repayAccountNum(operatingLeaseBasic.getRepayAccountNum())
        .nextRepayDate(operatingLeaseBasic.getNextRepayDate())
        .build();

    // find existing db
    OperatingLeaseEntity existingEntity = operatingLeaseRepository
        .findByBanksaladUserIdAndOrganizationIdAndAccountNumAndSeqno(
            banksaladUserId,
            organizationId,
            accountSummary.getAccountNum(),
            accountSummary.getSeqno()
        ).orElse(OperatingLeaseEntity.builder().build());

    if (existingEntity != null) {
      entity.setId(existingEntity.getId());
    }

    if (!ObjectComparator.isSame(entity, existingEntity, ENTITY_EXCLUDE_FIELD)) {
      operatingLeaseRepository.save(entity);
      operatingLeaseHistoryRepository.save(operatingLeaseHistoryMapper.toHistoryEntity(entity));
    }
  }

  @Override
  public void saveSearchTimestamp(ExecutionContext executionContext, AccountSummary accountSummary,
      long searchTimestamp) {
    accountSummaryService.updateOperatingLeaseBasicSearchTimestamp(executionContext.getBanksaladUserId(),
        executionContext.getOrganizationId(), accountSummary, searchTimestamp);
  }

  @Override
  public void saveResponseCode(ExecutionContext executionContext, AccountSummary accountSummary, String responseCode) {
    accountSummaryService.updateOperatingLeaseBasicResponseCode(executionContext.getBanksaladUserId(),
        executionContext.getOrganizationId(), accountSummary, responseCode);
  }
}
