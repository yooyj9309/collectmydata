package com.banksalad.collectmydata.invest.common.db.entity.mapper;

import com.banksalad.collectmydata.finance.common.constant.FinanceConstant;
import com.banksalad.collectmydata.invest.common.db.entity.AccountProductEntity;
import com.banksalad.collectmydata.invest.common.db.entity.AccountProductHistoryEntity;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;

import static com.banksalad.collectmydata.finance.common.constant.FinanceConstant.ENTITY_EXCLUDE_FIELD;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class AccountProductHistoryMapperTest {

  private final AccountProductHistoryMapper accountProductHistoryMapper = Mappers.getMapper(AccountProductHistoryMapper.class);

  @Test
  void toHistoryEntityTest() {
    AccountProductEntity accountProductEntity = AccountProductEntity.builder()
        .banksaladUserId(1L)
        .organizationId("invest1")
        .accountNum("1111111111")
        .prodNo((short) 1)
        .prodCode("005930")
        .prodType("401")
        .prodTypeDetail("국내주식")
        .prodName("삼성전자")
        .purchaseAmt(BigDecimal.valueOf(11111.111))
        .holdingNum(100L)
        .availForSaleNum(100L)
        .evalAmt(BigDecimal.valueOf(22222.222))
        .issueDate("20210101")
        .paidInAmt(BigDecimal.valueOf(33333.333))
        .withdrawalAmt(BigDecimal.valueOf(44444.444))
        .lastPaidInDate("20210201")
        .rcvAmt(BigDecimal.valueOf(55555.555))
        .currencyCode(FinanceConstant.CURRENCY_KRW)
        .build();

    AccountProductHistoryEntity accountProductHistoryEntity = accountProductHistoryMapper
        .toHistoryEntity(accountProductEntity);

    assertThat(accountProductHistoryEntity).usingRecursiveComparison().ignoringFields(ENTITY_EXCLUDE_FIELD)
        .isEqualTo(accountProductEntity);
  }
}
