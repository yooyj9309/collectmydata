package com.banksalad.collectmydata.bank.common.mapper;

import com.banksalad.collectmydata.bank.common.db.entity.DepositAccountTransactionEntity;
import com.banksalad.collectmydata.bank.deposit.dto.DepositAccountTransaction;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("수신계좌 거래내역 매퍼 테스트")
class DepositAccountTransactionMapperTest {

  private DepositAccountTransactionMapper depositAccountTransactionMapper = Mappers
      .getMapper(DepositAccountTransactionMapper.class);

  @Test
  void dtoToEntityTest() {

    DepositAccountTransaction depositAccountTransaction = DepositAccountTransaction.builder()
        .transDtime("20210303130000")
        .transType("03")
        .transClass("입금")
        .transAmt(BigDecimal.valueOf(150000000, 3))
        .balanceAmt(BigDecimal.valueOf(500000000, 3))
        .build();

    DepositAccountTransactionEntity depositAccountTransactionEntity = depositAccountTransactionMapper
        .dtoToEntity(depositAccountTransaction);

    assertAll(
        () -> assertEquals(depositAccountTransaction.getTransDtime(), depositAccountTransactionEntity.getTransDtime()),
        () -> assertEquals(depositAccountTransaction.getTransNo(), depositAccountTransactionEntity.getTransNo()),
        () -> assertEquals(depositAccountTransaction.getTransType(), depositAccountTransactionEntity.getTransType()),
        () -> assertEquals(depositAccountTransaction.getTransClass(), depositAccountTransactionEntity.getTransClass()),
        () -> assertEquals(depositAccountTransaction.getCurrencyCode(),
            depositAccountTransactionEntity.getCurrencyCode()),
        () -> assertEquals(depositAccountTransaction.getTransAmt(), depositAccountTransactionEntity.getTransAmt()),
        () -> assertEquals(depositAccountTransaction.getBalanceAmt(), depositAccountTransactionEntity.getBalanceAmt()),
        () -> assertEquals(depositAccountTransaction.getPaidInCnt(), depositAccountTransactionEntity.getPaidInCnt())
    );
  }
}
