package com.banksalad.collectmydata.invest.common.db.entity.mapper;

import com.banksalad.collectmydata.invest.common.db.entity.AccountSummaryEntity;
import com.banksalad.collectmydata.invest.summary.dto.AccountSummary;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class AccountSummaryMapperTest {

  private final AccountSummaryMapper accountSummaryMapper = Mappers.getMapper(AccountSummaryMapper.class);

  @Test
  void mergeTest() {
    AccountSummary accountSummary = AccountSummary.builder()
        .accountNum("1111111111")
        .consent(true)
        .accountName("증권계좌1")
        .accountType("101")
        .accountStatus("201")
        .build();

    AccountSummaryEntity accountSummaryEntity = AccountSummaryEntity.builder().build();

    accountSummaryMapper.merge(accountSummary, accountSummaryEntity);

    assertThat(accountSummaryEntity).usingRecursiveComparison().ignoringExpectedNullFields().isEqualTo(accountSummary);
  }

  @Test
  void entityToDtoTest() {
    AccountSummaryEntity accountSummaryEntity = AccountSummaryEntity.builder()
        .id(1L)
        .banksaladUserId(1L)
        .organizationId("invest1")
        .accountNum("1111111111")
        .consent(true)
        .accountName("증권계좌1")
        .accountType("101")
        .accountStatus("201")
        .build();

    AccountSummary accountSummary = accountSummaryMapper.entityToDto(accountSummaryEntity);

    assertThat(accountSummary).usingRecursiveComparison().isEqualTo(accountSummaryEntity);
  }
}
