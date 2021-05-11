package com.banksalad.collectmydata.card.card.bill;

import org.springframework.stereotype.Component;

import com.banksalad.collectmydata.card.card.dto.BillBasic;
import com.banksalad.collectmydata.card.card.dto.BillDetail;
import com.banksalad.collectmydata.card.card.dto.ListBillDetailResponse;
import com.banksalad.collectmydata.card.common.db.entity.BillDetailEntity;
import com.banksalad.collectmydata.card.common.db.entity.BillDetailHistoryEntity;
import com.banksalad.collectmydata.card.common.db.repository.BillDetailHistoryRepository;
import com.banksalad.collectmydata.card.common.db.repository.BillDetailRepository;
import com.banksalad.collectmydata.card.common.mapper.BillDetailHistoryMapper;
import com.banksalad.collectmydata.card.common.mapper.BillDetailMapper;
import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.finance.api.bill.BillTransactionResponseHelper;
import com.banksalad.collectmydata.finance.api.bill.dto.BillTransactionResponse;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class BillDetailResponseHelper implements BillTransactionResponseHelper<BillBasic, BillDetail> {

  private final BillDetailRepository billDetailRepository;
  private final BillDetailHistoryRepository billDetailHistoryRepository;

  private final BillDetailMapper billDetailMapper = Mappers.getMapper(BillDetailMapper.class);
  private final BillDetailHistoryMapper billDetailHistoryMapper = Mappers.getMapper(BillDetailHistoryMapper.class);

  @Override
  public List<BillDetail> getBillTransactionsFromResponse(BillTransactionResponse response) {
    return ((ListBillDetailResponse) response).getBillDetails();
  }

  @Override
  public void saveBillTransactions(ExecutionContext executionContext, BillBasic billBasic,
      List<BillDetail> billDetails) {

    /* delete & insert */
    billDetailRepository.deleteAllByBanksaladUserIdAndOrganizationIdAndChargeMonthAndSeqnoInQuery(
        executionContext.getBanksaladUserId(), executionContext.getOrganizationId(),
        billBasic.getChargeMonth(), billBasic.getSeqno()
    );

    AtomicInteger atomicInteger = new AtomicInteger(1);

    List<BillDetailEntity> billDetailEntities = billDetails
        .stream()
        .map(billDetail -> {
          BillDetailEntity billDetailEntity = billDetailMapper.dtoToEntity(billDetail);
          billDetailEntity.setSyncedAt(executionContext.getSyncStartedAt());
          billDetailEntity.setBanksaladUserId(executionContext.getBanksaladUserId());
          billDetailEntity.setOrganizationId(executionContext.getOrganizationId());
          billDetailEntity.setChargeMonth(billBasic.getChargeMonth());
          billDetailEntity.setSeqno(billBasic.getSeqno());
          billDetailEntity.setBillDetailNo((short) atomicInteger.getAndIncrement());
          billDetailEntity.setCreatedBy(String.valueOf(executionContext.getBanksaladUserId()));
          billDetailEntity.setUpdatedBy(String.valueOf(executionContext.getBanksaladUserId()));
          billDetailEntity.setConsentId(executionContext.getConsentId());
          billDetailEntity.setSyncRequestId(executionContext.getSyncRequestId());
          return billDetailEntity;
        }).collect(Collectors.toList());

    billDetailRepository.saveAll(billDetailEntities);
    billDetailHistoryRepository.saveAll(
        billDetailEntities.stream().map(billDetailEntity -> billDetailHistoryMapper
            .toHistoryEntity(billDetailEntity, BillDetailHistoryEntity.builder().build()))
            .collect(Collectors.toList()));

  }
}
