package com.banksalad.collectmydata.capital.common.db.entity.mapper;

import org.springframework.boot.test.context.SpringBootTest;

import com.banksalad.collectmydata.capital.common.db.entity.AccountTransactionEntity;
import com.banksalad.collectmydata.capital.common.mapper.AccountTransactionInterestMapper;
import com.banksalad.collectmydata.capital.common.mapper.AccountTransactionMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static com.banksalad.collectmydata.capital.common.TestHelper.BANKSALAD_USER_ID;
import static com.banksalad.collectmydata.capital.common.TestHelper.ORGANIZATION_ID;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class AccountTransactionEntityMapperTest {

  private final AccountTransactionMapper accountTransactionMapper = Mappers.getMapper(AccountTransactionMapper.class);
  private final AccountTransactionInterestMapper accountTransactionInterestMapper = Mappers
      .getMapper(AccountTransactionInterestMapper.class);


  @Test
  @DisplayName("AccountTransactionEntity to AccountTransactionEntity 병합 테스트")
  public void AccountTransactionInterestMapper_mergeTest() {
    LocalDateTime now = LocalDateTime.now();

    AccountTransactionEntity sourceEntity = AccountTransactionEntity.builder()
        .accountNum("43214321432143221")
        .seqno("2")
        .uniqueTransNo("33")
        .transDtime("20210316121212")
        .transNo("33")
        .transType("04")
        .transAmt(new BigDecimal("222.111"))
        .balanceAmt(new BigDecimal("333.222"))
        .principalAmt(new BigDecimal("444.333"))
        .intAmt(new BigDecimal("555.444"))
        .build();

    AccountTransactionEntity targetEntity = assembleTargetEntity(now);
    accountTransactionMapper.merge(sourceEntity, targetEntity);

    assertThat(targetEntity).usingRecursiveComparison()
        .isEqualTo(
            AccountTransactionEntity.builder()
                .id(1l)
                .transactionYearMonth(202106)
                .syncedAt(now)
                .banksaladUserId(BANKSALAD_USER_ID)
                .organizationId(ORGANIZATION_ID)
                .accountNum("43214321432143221")
                .seqno("2")
                .uniqueTransNo("33")
                .transDtime("20210316121212")
                .transNo("33")
                .transType("04")
                .transAmt(new BigDecimal("222.111"))
                .balanceAmt(new BigDecimal("333.222"))
                .principalAmt(new BigDecimal("444.333"))
                .intAmt(new BigDecimal("555.444"))
                .build()
        );
  }


  private AccountTransactionEntity assembleTargetEntity(LocalDateTime now) {
    return AccountTransactionEntity.builder()
        .id(1l)
        .transactionYearMonth(202106)
        .syncedAt(now)
        .banksaladUserId(BANKSALAD_USER_ID)
        .organizationId(ORGANIZATION_ID)
        .accountNum("1234123412341234")
        .seqno("1")
        .uniqueTransNo("33")
        .transDtime("20210316121212")
        .transNo("33")
        .transType("03")
        .transAmt(new BigDecimal("111.111"))
        .balanceAmt(new BigDecimal("222.222"))
        .principalAmt(new BigDecimal("333.333"))
        .intAmt(new BigDecimal("444.444"))
        .build();
  }
}
