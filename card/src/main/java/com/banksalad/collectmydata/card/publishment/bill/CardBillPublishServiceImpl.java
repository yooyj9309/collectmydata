package com.banksalad.collectmydata.card.publishment.bill;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.banksalad.collectmydata.card.common.db.repository.BillDetailRepository;
import com.banksalad.collectmydata.card.common.db.repository.BillRepository;
import com.banksalad.collectmydata.card.common.mapper.BillDetailMapper;
import com.banksalad.collectmydata.card.common.mapper.BillMapper;
import com.banksalad.collectmydata.card.publishment.bill.dto.BillBasicPublishment;
import com.banksalad.collectmydata.card.publishment.bill.dto.BillDetailPublishment;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CardBillPublishServiceImpl implements CardBillPublishService {

  private final BillRepository billRepository;
  private final BillMapper billMapper = Mappers.getMapper(BillMapper.class);

  private final BillDetailRepository billDetailRepository;
  private final BillDetailMapper billDetailMapper = Mappers.getMapper(BillDetailMapper.class);

  @Override
  public List<BillBasicPublishment> getCardBillBasicResponse(long banksaladUserId, String organizationId,
      LocalDateTime createdAt, int limit) {

    return billRepository.findAllByBanksaladUserIdAndOrganizationIdAndCreatedAtAfter(
        banksaladUserId, organizationId, createdAt, PageRequest.of(0, limit)
    ).stream().map(billMapper::entityToPublishmentDto).collect(Collectors.toList());
  }

  @Override
  public List<BillDetailPublishment> getCardBillDetailResponse(long banksaladUserId, String organizationId, String seqNo,
      String chargeMonth) {
    return billDetailRepository.findByBanksaladUserIdAndOrganizationIdAndChargeMonthAndSeqno(
        banksaladUserId, organizationId, Integer.valueOf(chargeMonth), seqNo
    ).stream().map(billDetailMapper::entityToPublishmentDto).collect(Collectors.toList());
  }
}
