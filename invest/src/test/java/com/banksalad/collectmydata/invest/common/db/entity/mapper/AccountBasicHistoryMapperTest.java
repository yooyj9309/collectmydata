package com.banksalad.collectmydata.invest.common.db.entity.mapper;

import com.banksalad.collectmydata.finance.common.constant.FinanceConstant;
import com.banksalad.collectmydata.invest.common.db.entity.AccountBasicEntity;
import com.banksalad.collectmydata.invest.common.db.entity.AccountBasicHistoryEntity;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;

import static com.banksalad.collectmydata.finance.common.constant.FinanceConstant.ENTITY_EXCLUDE_FIELD;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class AccountBasicHistoryMapperTest {

  private final AccountBasicHistoryMapper accountBasicHistoryMapper = Mappers.getMapper(AccountBasicHistoryMapper.class);

  @Test
  void toHistoryEntityTest() {
    AccountBasicEntity accountBasicEntity = AccountBasicEntity.builder()
        .banksaladUserId(1L)
        .organizationId("invest1")
        .accountNum("1111111111")
        .issueDate("20210101")
        .taxBenefits(true)
        .withholdingsAmt(BigDecimal.valueOf(11111.111))
        .creditLoanAmt(BigDecimal.valueOf(222222.222))
        .mortgageAmt(BigDecimal.valueOf(333333.333))
        .currencyCode(FinanceConstant.CURRENCY_KRW)
        .build();

    AccountBasicHistoryEntity accountBasicHistoryEntity = accountBasicHistoryMapper
        .toHistoryEntity(accountBasicEntity);

    assertThat(accountBasicHistoryEntity).usingRecursiveComparison().ignoringFields(ENTITY_EXCLUDE_FIELD)
        .isEqualTo(accountBasicEntity);
  }
}
