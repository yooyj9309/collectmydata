package com.banksalad.collectmydata.invest.publishment.account;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.banksalad.collectmydata.finance.common.constant.FinanceConstant;
import com.banksalad.collectmydata.invest.common.db.entity.AccountSummaryEntity;
import com.banksalad.collectmydata.invest.common.db.entity.AccountTransactionEntity;
import com.banksalad.collectmydata.invest.common.db.repository.AccountSummaryRepository;
import com.banksalad.collectmydata.invest.common.db.repository.AccountTransactionRepository;
import com.banksalad.collectmydata.invest.publishment.account.dto.AccountTransactionResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.BANKSALAD_USER_ID;
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.OLD_SYNCED_AT;
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.ORGANIZATION_ID;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@DisplayName("계좌 거래내역정보 Publish 서비스 테스트")
class AccountTransactionPublishServiceTest {

  @Autowired
  private AccountSummaryRepository accountSummaryRepository;

  @Autowired
  private AccountTransactionRepository accountTransactionRepository;

  @Autowired
  private AccountTransactionPublishService accountTransactionPublishService;

  private static final String ACCOUNT_NUM1 = "1111111111";

  @Test
  @DisplayName("계좌 거래내역 조회 성공 테스트")
  void getAccountTransactionResponsesTest() {
    // given
    accountSummaryRepository.save(getAccountSummaryEntity());
    accountTransactionRepository.saveAll(getAccountTransactionEntities());

    // when
    List<AccountTransactionResponse> accountTransactionResponses = accountTransactionPublishService
        .getAccountTransactionResponses(BANKSALAD_USER_ID, ORGANIZATION_ID, ACCOUNT_NUM1,
            LocalDateTime.now().minusDays(1L), 500);

    // then
    assertThat(accountTransactionResponses).usingRecursiveComparison().isEqualTo(accountTransactionRepository.findAll());
  }

  private AccountSummaryEntity getAccountSummaryEntity() {
    return AccountSummaryEntity.builder()
            .syncedAt(OLD_SYNCED_AT)
            .banksaladUserId(BANKSALAD_USER_ID)
            .organizationId(ORGANIZATION_ID)
            .accountNum(ACCOUNT_NUM1)
            .consent(true)
            .accountName("증권계좌1")
            .accountType("101")
            .accountStatus("201")
            .transactionResponseCode("00000")
            .build();
  }

  private List<AccountTransactionEntity> getAccountTransactionEntities() {
    return List.of(
        AccountTransactionEntity.builder()
            .transactionYearMonth(202101)
            .syncedAt(OLD_SYNCED_AT)
            .banksaladUserId(BANKSALAD_USER_ID)
            .organizationId(ORGANIZATION_ID)
            .accountNum(ACCOUNT_NUM1)
            .uniqueTransNo("66e96aece4de23a3961350d7f6463e7fdaebc823d554473c18e6b316c4dc0ff4")
            .prodCode("401")
            .transDtime("20210101000000")
            .prodName("주식1")
            .transType("301")
            .transTypeDetail("입금")
            .transNum(100L)
            .baseAmt(new BigDecimal("10000.0000"))
            .transAmt(new BigDecimal("20000.000"))
            .settleAmt(new BigDecimal("30000.000"))
            .balanceAmt(new BigDecimal("40000.000"))
            .currencyCode(FinanceConstant.CURRENCY_KRW)
            .build(),
        AccountTransactionEntity.builder()
            .transactionYearMonth(202101)
            .syncedAt(OLD_SYNCED_AT)
            .banksaladUserId(BANKSALAD_USER_ID)
            .organizationId(ORGANIZATION_ID)
            .accountNum(ACCOUNT_NUM1)
            .uniqueTransNo("2ee9fd91cb7ddb1cb947d3c2412007bea497e4bbdebf5e6258b15a41ea06acd9")
            .prodCode("401")
            .transDtime("20210102000000")
            .prodName("주식2")
            .transType("301")
            .transTypeDetail("입금")
            .transNum(200L)
            .baseAmt(new BigDecimal("10000.0000"))
            .transAmt(new BigDecimal("20000.000"))
            .settleAmt(new BigDecimal("30000.000"))
            .balanceAmt(new BigDecimal("40000.000"))
            .currencyCode(FinanceConstant.CURRENCY_KRW)
            .build()
    );
  }
}
