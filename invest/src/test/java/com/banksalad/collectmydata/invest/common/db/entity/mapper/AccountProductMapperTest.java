package com.banksalad.collectmydata.invest.common.db.entity.mapper;

import com.banksalad.collectmydata.finance.common.constant.FinanceConstant;
import com.banksalad.collectmydata.invest.account.dto.AccountProduct;
import com.banksalad.collectmydata.invest.common.db.entity.AccountProductEntity;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;

import static com.banksalad.collectmydata.finance.common.constant.FinanceConstant.ENTITY_EXCLUDE_FIELD;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class AccountProductMapperTest {

  private final AccountProductMapper accountProductMapper = Mappers.getMapper(AccountProductMapper.class);
  
  @Test
  void entityToDtoTest() {
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

    AccountProduct accountProduct = accountProductMapper.entityToDto(accountProductEntity);

    assertThat(accountProduct).usingRecursiveComparison().ignoringFields(ENTITY_EXCLUDE_FIELD)
        .isEqualTo(accountProductEntity);
  }

  @Test
  void dtoToEntityTest() {
    AccountProduct accountProduct = AccountProduct.builder()
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

    AccountProductEntity accountProductEntity = accountProductMapper.dtoToEntity(accountProduct);

    assertThat(accountProductEntity).usingRecursiveComparison().ignoringExpectedNullFields().isEqualTo(accountProduct);
  }
}
