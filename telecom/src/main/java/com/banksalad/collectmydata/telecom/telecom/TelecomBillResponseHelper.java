package com.banksalad.collectmydata.telecom.telecom;

import org.springframework.stereotype.Component;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.util.ObjectComparator;
import com.banksalad.collectmydata.finance.api.accountinfo.AccountInfoResponseHelper;
import com.banksalad.collectmydata.finance.api.accountinfo.dto.AccountResponse;
import com.banksalad.collectmydata.telecom.common.db.entity.BillEntity;
import com.banksalad.collectmydata.telecom.common.mapper.BillHistoryMapper;
import com.banksalad.collectmydata.telecom.common.db.repository.BillHistoryRepository;
import com.banksalad.collectmydata.telecom.common.db.repository.BillRepository;
import com.banksalad.collectmydata.telecom.telecom.dto.ListTelecomBillsResponse;
import com.banksalad.collectmydata.telecom.telecom.dto.TelecomBill;
import com.banksalad.collectmydata.telecom.telecom.dto.TelecomBillRequestSupporter;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;

import java.util.List;

import static com.banksalad.collectmydata.finance.common.constant.FinanceConstant.ENTITY_EXCLUDE_FIELD;

@Component
@RequiredArgsConstructor
public class TelecomBillResponseHelper implements
    AccountInfoResponseHelper<TelecomBillRequestSupporter, List<TelecomBill>> {

  private final BillRepository billRepository;
  private final BillHistoryRepository billHistoryRepository;

  private final BillHistoryMapper billHistoryMapper = Mappers.getMapper(BillHistoryMapper.class);


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
      BillEntity billEntity = BillEntity.builder()
          .syncedAt(executionContext.getSyncStartedAt())
          .banksaladUserId(banksaladUserId)
          .organizationId(organizationId)
          .chargeMonth(chargeMonth)
          .mgmtId(telecomBill.getMgmtId())
          .chargeAmt(telecomBill.getChargeAmt())
          .chargeDate(telecomBill.getChargeDate())
          .build();

      BillEntity existingBillEntity = billRepository.findByBanksaladUserIdAndOrganizationIdAndChargeMonthAndMgmtId(
          banksaladUserId, organizationId, chargeMonth, telecomBill.getMgmtId()
      ).orElse(null);

      if (existingBillEntity != null) {
        billEntity.setId(existingBillEntity.getId());
      }

      if (!ObjectComparator.isSame(billEntity, existingBillEntity, ENTITY_EXCLUDE_FIELD)) {
        billRepository.save(billEntity);
        billHistoryRepository.save(billHistoryMapper.toHistoryEntity(billEntity));
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
