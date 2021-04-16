package com.banksalad.collectmydata.invest.common.db.entity.mapper;

import com.banksalad.collectmydata.finance.common.constant.FinanceConstant;
import com.banksalad.collectmydata.invest.account.dto.AccountBasic;
import com.banksalad.collectmydata.invest.common.db.entity.AccountBasicEntity;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class AccountBasicMapperTest {

  private final AccountBasicMapper accountBasicMapper = Mappers.getMapper(AccountBasicMapper.class);

  @Test
  void dtoToEntityTest() {
    AccountBasic accountBasic = AccountBasic.builder()
        .issueDate("20210101")
        .taxBenefits(true)
        .withholdingsAmt(BigDecimal.valueOf(11111.111))
        .creditLoanAmt(BigDecimal.valueOf(222222.222))
        .mortgageAmt(BigDecimal.valueOf(333333.333))
        .currencyCode(FinanceConstant.CURRENCY_KRW)
        .build();

    AccountBasicEntity accountBasicEntity = accountBasicMapper.dtoToEntity(accountBasic);

    assertThat(accountBasicEntity).usingRecursiveComparison().ignoringExpectedNullFields().isEqualTo(accountBasic);
  }
}
