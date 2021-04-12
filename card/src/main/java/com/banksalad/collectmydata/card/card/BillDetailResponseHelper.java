package com.banksalad.collectmydata.card.card;

import org.springframework.stereotype.Component;

import com.banksalad.collectmydata.card.card.dto.BillBasic;
import com.banksalad.collectmydata.card.card.dto.BillDetail;
import com.banksalad.collectmydata.card.card.dto.ListBillDetailResponse;
import com.banksalad.collectmydata.card.common.db.entity.BillDetailEntity;
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

    List<BillDetailEntity> existingBillDetailEntities =
        billDetailRepository.findByBanksaladUserIdAndOrganizationIdAndChargeMonthAndSeqno(
            executionContext.getBanksaladUserId(), executionContext.getOrganizationId(),
            billBasic.getChargeMonth(), billBasic.getSeqno());

    if (!existingBillDetailEntities.isEmpty()) {

      billDetailRepository.deleteByBanksaladUserIdAndOrganizationIdAndChargeMonthAndSeqno(
          executionContext.getBanksaladUserId(), executionContext.getOrganizationId(),
          billBasic.getChargeMonth(), billBasic.getSeqno());
    }

    AtomicInteger nextBillDetailNo = new AtomicInteger(existingBillDetailEntities.stream()
        .mapToInt(BillDetailEntity::getBillDetailNo)
        .max()
        .orElse(-1));

    List<BillDetailEntity> billDetailEntities = billDetails
        .stream()
        .map(billDetail -> {
          BillDetailEntity billDetailEntity = billDetailMapper.dtoToEntity(billDetail);
          billDetailEntity.setSyncedAt(executionContext.getSyncStartedAt());
          billDetailEntity.setBanksaladUserId(executionContext.getBanksaladUserId());
          billDetailEntity.setOrganizationId(executionContext.getOrganizationId());
          billDetailEntity.setChargeMonth(billBasic.getChargeMonth());
          billDetailEntity.setSeqno(billBasic.getSeqno());
          billDetailEntity.setBillDetailNo(nextBillDetailNo.incrementAndGet());
          return billDetailEntity;
        }).collect(Collectors.toList());

    billDetailRepository.saveAll(billDetailEntities);
    billDetailHistoryRepository.saveAll(
        billDetailEntities.stream().map(billDetailHistoryMapper::toHistoryEntity).collect(Collectors.toList()));

  }
}