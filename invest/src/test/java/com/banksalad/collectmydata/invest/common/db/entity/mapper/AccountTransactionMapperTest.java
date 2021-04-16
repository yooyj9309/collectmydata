package com.banksalad.collectmydata.invest.common.db.entity.mapper;

import com.banksalad.collectmydata.finance.common.constant.FinanceConstant;
import com.banksalad.collectmydata.invest.account.dto.AccountTransaction;
import com.banksalad.collectmydata.invest.common.db.entity.AccountTransactionEntity;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class AccountTransactionMapperTest {

  private final AccountTransactionMapper accountTransactionMapper = Mappers.getMapper(AccountTransactionMapper.class);

  @Test
  void dtoToEntityTest() {
    AccountTransaction accountTransaction = AccountTransaction.builder()
        .prodCode("401")
        .transDtime("20210101102000")
        .prodName("주식")
        .transType("301")
        .transTypeDetail("입금")
        .transNum(100L)
        .baseAmt(BigDecimal.valueOf(11111.1111))
        .transAmt(BigDecimal.valueOf(22222.222))
        .settleAmt(BigDecimal.valueOf(33333.333))
        .balanceAmt(BigDecimal.valueOf(44444.444))
        .currencyCode(FinanceConstant.CURRENCY_KRW)
        .build();

    AccountTransactionEntity accountTransactionEntity = accountTransactionMapper.dtoToEntity(accountTransaction);

    assertThat(accountTransactionEntity).usingRecursiveComparison().ignoringExpectedNullFields()
        .isEqualTo(accountTransaction);
  }
}
