package com.banksalad.collectmydata.telecom.telecom;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.util.ObjectComparator;
import com.banksalad.collectmydata.finance.api.accountinfo.AccountInfoResponseHelper;
import com.banksalad.collectmydata.finance.api.accountinfo.dto.AccountResponse;
import com.banksalad.collectmydata.telecom.common.db.entity.TelecomBillEntity;
import com.banksalad.collectmydata.telecom.common.db.repository.TelecomBillHistoryRepository;
import com.banksalad.collectmydata.telecom.common.db.repository.TelecomBillRepository;
import com.banksalad.collectmydata.telecom.common.mapper.TelecomBillHistoryMapper;
import com.banksalad.collectmydata.telecom.common.mapper.TelecomBillMapper;
import com.banksalad.collectmydata.telecom.telecom.dto.ListTelecomBillsResponse;
import com.banksalad.collectmydata.telecom.telecom.dto.TelecomBill;
import com.banksalad.collectmydata.telecom.telecom.dto.TelecomBillRequestSupporter;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;

import java.util.List;

import static com.banksalad.collectmydata.finance.common.constant.FinanceConstant.ENTITY_EXCLUDE_FIELD;

@Component
@RequiredArgsConstructor
public class TelecomBillResponseHelper implements
    AccountInfoResponseHelper<TelecomBillRequestSupporter, List<TelecomBill>> {

  private final TelecomBillRepository telecomBillRepository;
  private final TelecomBillHistoryRepository telecomBillHistoryRepository;

  private final TelecomBillMapper telecomBillMapper = Mappers.getMapper(TelecomBillMapper.class);
  private final TelecomBillHistoryMapper telecomBillHistoryMapper = Mappers.getMapper(TelecomBillHistoryMapper.class);


  @Override
  public List<TelecomBill> getAccountFromResponse(AccountResponse accountResponse) {
    return ((ListTelecomBillsResponse) accountResponse).getBillList();
  }

  @Override
  public void saveAccountAndHistory(ExecutionContext executionContext, TelecomBillRequestSupporter summary,
      List<TelecomBill> telecomBills) {

    long banksaladUserId = executionContext.getBanksaladUserId();
    String organizationId = executionContext.getOrganizationId();
    Integer chargeMonth = Integer.valueOf(summary.getChangeMonth());

    for (TelecomBill telecomBill : telecomBills) {
      TelecomBillEntity telecomBillEntity = telecomBillMapper.dtoToEntity(telecomBill);
      telecomBillEntity.setSyncedAt(executionContext.getSyncStartedAt());
      telecomBillEntity.setBanksaladUserId(banksaladUserId);
      telecomBillEntity.setOrganizationId(organizationId);
      telecomBillEntity.setChargeMonth(chargeMonth);
      telecomBillEntity.setConsentId(executionContext.getConsentId());
      telecomBillEntity.setSyncRequestId(executionContext.getSyncRequestId());
      telecomBillEntity.setCreatedBy(executionContext.getRequestedBy());
      telecomBillEntity.setUpdatedBy(executionContext.getRequestedBy());

      TelecomBillEntity existingTelecomBillEntity = telecomBillRepository
          .findByBanksaladUserIdAndOrganizationIdAndChargeMonthAndMgmtId(
              banksaladUserId, organizationId, chargeMonth, telecomBill.getMgmtId()
          ).orElse(null);

      if (existingTelecomBillEntity != null) {
        telecomBillEntity.setId(existingTelecomBillEntity.getId());
      }

      if (!ObjectComparator.isSame(telecomBillEntity, existingTelecomBillEntity, ENTITY_EXCLUDE_FIELD)) {
        telecomBillRepository.save(telecomBillEntity);
        telecomBillHistoryRepository.save(telecomBillHistoryMapper.toHistoryEntity(telecomBillEntity));
      }
    }
  }

  @Override
  public void saveSearchTimestamp(ExecutionContext executionContext, TelecomBillRequestSupporter summaryDto,
      long searchTimestamp) {
    // 구현부분 없음.
  }

  @Override
  public void saveResponseCode(ExecutionContext executionContext, TelecomBillRequestSupporter summaryDto,
      String responseCode) {
    // 구현부분 없음.
  }
}
