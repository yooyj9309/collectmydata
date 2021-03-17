package com.banksalad.collectmydata.bank.common.mapper;

import com.banksalad.collectmydata.bank.common.db.entity.DepositAccountBasicEntity;
import com.banksalad.collectmydata.bank.deposit.dto.DepositAccountBasic;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("수신계좌 기본정보 매퍼 테스트")
class DepositAccountBasicMapperTest {

  private DepositAccountBasicMapper depositAccountBasicMapper = Mappers.getMapper(DepositAccountBasicMapper.class);

  @Test
  void dtoToEntityTest() {
    DepositAccountBasic depositAccountBasic = DepositAccountBasic.builder()
        .savingMethod("01")
        .holderName("예금주명")
        .issueDate("20210303")
        .commitAmt(BigDecimal.valueOf(180000, 3))
        .monthlyPaidInAmt(BigDecimal.valueOf(30000, 3))
        .build();

    DepositAccountBasicEntity depositAccountBasicEntity = depositAccountBasicMapper.dtoToEntity(depositAccountBasic);

    assertAll(
        () -> assertEquals(depositAccountBasic.getSavingMethod(), depositAccountBasicEntity.getSavingMethod()),
        () -> assertEquals(depositAccountBasic.getHolderName(), depositAccountBasicEntity.getHolderName()),
        () -> assertEquals(depositAccountBasic.getIssueDate(), depositAccountBasicEntity.getIssueDate()),
        () -> assertEquals(depositAccountBasic.getExpDate(), depositAccountBasicEntity.getExpDate()),
        () -> assertEquals(depositAccountBasic.getCurrencyCode(), depositAccountBasicEntity.getCurrencyCode()),
        () -> assertEquals(depositAccountBasic.getCommitAmt(), depositAccountBasicEntity.getCommitAmt()),
        () -> assertEquals(depositAccountBasic.getMonthlyPaidInAmt(), depositAccountBasicEntity.getMonthlyPaidInAmt())
    );
  }
}
