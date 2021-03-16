package com.banksalad.collectmydata.bank.common.db.entity.mapper;

import com.banksalad.collectmydata.bank.common.db.entity.AccountSummaryEntity;
import com.banksalad.collectmydata.bank.summary.dto.AccountSummary;
import com.banksalad.collectmydata.common.util.ObjectComparator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("계좌 목록 매퍼 테스트")
class AccountSummaryMapperTest {

  private AccountSummaryMapper accountSummaryMapper = Mappers.getMapper(AccountSummaryMapper.class);

  @Test
  public void mergeTest() {
    AccountSummaryEntity expectedAccountSummaryEntity = AccountSummaryEntity.builder()
        .accountNum("123456789")
        .consent(true)
        .seqno("ab1234")
        .foreignDeposit(false)
        .prodName("자유입출금")
        .accountType("1001")
        .accountStatus("01")
        .basicSearchTimestamp(1000L)
        .detailSearchTimestamp(1000L)
        .transactionSyncedAt(LocalDateTime.of(2021, 3, 3, 0, 0))
        .build();

    AccountSummary accountSummary = AccountSummary.builder()
        .accountNum("123456789")
        .consent(true)
        .seqno("ab1234")
        .foreignDeposit(false)
        .prodName("자유입출금")
        .accountType("1001")
        .accountStatus("01")
        .basicSearchTimestamp(1000)
        .detailSearchTimestamp(1000)
        .transactionSyncedAt(LocalDateTime.of(2021, 3, 3, 0, 0))
        .build();

    AccountSummaryEntity actualAccountSummaryEntity = AccountSummaryEntity.builder()
        .build();

    accountSummaryMapper.merge(accountSummary, actualAccountSummaryEntity);

    assertThat(ObjectComparator.isSame(actualAccountSummaryEntity, expectedAccountSummaryEntity)).isEqualTo(true);
  }

  @Test
  public void entityToDtoTest() {
    AccountSummary expectedAccountSummary = AccountSummary.builder()
        .accountNum("123456789")
        .consent(true)
        .seqno("ab1234")
        .foreignDeposit(false)
        .prodName("자유입출금")
        .accountType("1001")
        .accountStatus("01")
        .basicSearchTimestamp(1000)
        .detailSearchTimestamp(1000)
        .transactionSyncedAt(LocalDateTime.of(2021, 3, 3, 0, 0))
        .build();

    AccountSummaryEntity accountSummaryEntity = AccountSummaryEntity.builder()
        .accountNum("123456789")
        .consent(true)
        .seqno("ab1234")
        .foreignDeposit(false)
        .prodName("자유입출금")
        .accountType("1001")
        .accountStatus("01")
        .basicSearchTimestamp(1000L)
        .detailSearchTimestamp(1000L)
        .transactionSyncedAt(LocalDateTime.of(2021, 3, 3, 0, 0))
        .build();

    AccountSummary actualAccountSummary = accountSummaryMapper.entityToDto(accountSummaryEntity);

    assertThat(ObjectComparator.isSame(actualAccountSummary, expectedAccountSummary)).isEqualTo(true);
  }
}
