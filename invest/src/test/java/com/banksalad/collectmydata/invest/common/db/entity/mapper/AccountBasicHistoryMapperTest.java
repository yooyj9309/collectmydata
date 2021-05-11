package com.banksalad.collectmydata.invest.common.db.entity.mapper;

import com.banksalad.collectmydata.finance.common.constant.FinanceConstant;
import com.banksalad.collectmydata.invest.common.db.entity.AccountBasicEntity;
import com.banksalad.collectmydata.invest.common.db.entity.AccountBasicHistoryEntity;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;

import static com.banksalad.collectmydata.finance.common.constant.FinanceConstant.ENTITY_EXCLUDE_FIELD;
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.BANKSALAD_USER_ID;
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.CONSENT_ID;
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.ORGANIZATION_ID;
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.SYNC_REQUEST_ID;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class AccountBasicHistoryMapperTest {

  private final AccountBasicHistoryMapper accountBasicHistoryMapper = Mappers.getMapper(AccountBasicHistoryMapper.class);

  @Test
  void toHistoryEntityTest() {
    AccountBasicEntity accountBasicEntity = AccountBasicEntity.builder()
        .banksaladUserId(BANKSALAD_USER_ID)
        .organizationId(ORGANIZATION_ID)
        .accountNum("1111111111")
        .issueDate("20210101")
        .taxBenefits(true)
        .withholdingsAmt(BigDecimal.valueOf(11111.111))
        .creditLoanAmt(BigDecimal.valueOf(222222.222))
        .mortgageAmt(BigDecimal.valueOf(333333.333))
        .currencyCode(FinanceConstant.CURRENCY_KRW)
        .consentId(CONSENT_ID)
        .syncRequestId(SYNC_REQUEST_ID)
        .build();

    accountBasicEntity.setCreatedBy(String.valueOf(BANKSALAD_USER_ID));
    accountBasicEntity.setUpdatedBy(String.valueOf(BANKSALAD_USER_ID));

    AccountBasicHistoryEntity accountBasicHistoryEntity = accountBasicHistoryMapper
        .toHistoryEntity(accountBasicEntity, AccountBasicHistoryEntity.builder().build());

    assertThat(accountBasicHistoryEntity).usingRecursiveComparison().ignoringFields(ENTITY_EXCLUDE_FIELD)
        .isEqualTo(accountBasicEntity);
  }
}
