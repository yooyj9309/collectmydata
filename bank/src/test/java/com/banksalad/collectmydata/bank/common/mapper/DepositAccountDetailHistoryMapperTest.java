package com.banksalad.collectmydata.bank.common.mapper;

import com.banksalad.collectmydata.bank.common.db.entity.DepositAccountDetailEntity;
import com.banksalad.collectmydata.bank.common.db.entity.DepositAccountDetailHistoryEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("수신계좌 추가정보 이력 매퍼 테스트")
class DepositAccountDetailHistoryMapperTest {

  private DepositAccountDetailHistoryMapper depositAccountDetailHistoryMapper = Mappers
      .getMapper(DepositAccountDetailHistoryMapper.class);

  @Test
  void toHistoryEntityTest() {
    DepositAccountDetailEntity entity = DepositAccountDetailEntity.builder()
        .id(1L)
        .syncedAt(LocalDateTime.of(2021, 3, 3, 10, 0))
        .banksaladUserId(1L)
        .organizationId("organizaionId")
        .accountNum("1234567890")
        .seqno("ab1234")
        .currencyCode("KRW")
        .balanceAmt(BigDecimal.valueOf(150000, 3))
        .lastPaidInCnt(5)
        .offeredRate(BigDecimal.valueOf(5.12345))
        .withdrawableAmt(BigDecimal.valueOf(15000, 3))
        .build();

    DepositAccountDetailHistoryEntity historyEntity = depositAccountDetailHistoryMapper
        .toHistoryEntity(entity);

    assertAll(
        () -> assertEquals(entity.getSyncedAt(), historyEntity.getSyncedAt()),
        () -> assertEquals(entity.getBanksaladUserId(), historyEntity.getBanksaladUserId()),
        () -> assertEquals(entity.getOrganizationId(), historyEntity.getOrganizationId()),
        () -> assertEquals(entity.getAccountNum(), historyEntity.getAccountNum()),
        () -> assertEquals(entity.getSeqno(), historyEntity.getSeqno()),
        () -> assertEquals(entity.getCurrencyCode(), historyEntity.getCurrencyCode()),
        () -> assertEquals(entity.getBalanceAmt(), historyEntity.getBalanceAmt()),
        () -> assertEquals(entity.getLastPaidInCnt(), historyEntity.getLastPaidInCnt()),
        () -> assertEquals(entity.getOfferedRate(), historyEntity.getOfferedRate()),
        () -> assertEquals(entity.getWithdrawableAmt(), historyEntity.getWithdrawableAmt())
    );
  }
}
