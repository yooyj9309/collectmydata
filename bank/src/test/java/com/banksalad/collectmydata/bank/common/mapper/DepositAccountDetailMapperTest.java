package com.banksalad.collectmydata.bank.common.mapper;

import com.banksalad.collectmydata.bank.common.db.entity.DepositAccountDetailEntity;
import com.banksalad.collectmydata.bank.deposit.dto.DepositAccountDetail;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("수신계좌 추가정보 매퍼 테스트")
class DepositAccountDetailMapperTest {

  private DepositAccountDetailMapper depositAccountDetailMapper = Mappers.getMapper(DepositAccountDetailMapper.class);

  @Test
  void dtoToEntityTest() {
    DepositAccountDetail depositAccountDetail = DepositAccountDetail.builder()
        .balanceAmt(BigDecimal.valueOf(150000, 3))
        .lastPaidInCnt(5)
        .offeredRate(BigDecimal.valueOf(5.12345))
        .withdrawableAmt(BigDecimal.valueOf(15000, 3))
        .build();

    DepositAccountDetailEntity depositAccountDetailEntity = depositAccountDetailMapper
        .dtoToEntity(depositAccountDetail);

    assertAll(
        () -> assertEquals(depositAccountDetail.getBalanceAmt(), depositAccountDetailEntity.getBalanceAmt()),
        () -> assertEquals(depositAccountDetail.getCurrencyCode(), depositAccountDetailEntity.getCurrencyCode()),
        () -> assertEquals(depositAccountDetail.getLastPaidInCnt(), depositAccountDetailEntity.getLastPaidInCnt()),
        () -> assertEquals(depositAccountDetail.getOfferedRate(), depositAccountDetailEntity.getOfferedRate()),
        () -> assertEquals(depositAccountDetail.getWithdrawableAmt(), depositAccountDetailEntity.getWithdrawableAmt())
    );
  }
}
