package com.banksalad.collectmydata.invest.publishment.account;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.banksalad.collectmydata.finance.common.constant.FinanceConstant;
import com.banksalad.collectmydata.invest.common.db.entity.AccountBasicEntity;
import com.banksalad.collectmydata.invest.common.db.entity.AccountSummaryEntity;
import com.banksalad.collectmydata.invest.common.db.repository.AccountBasicRepository;
import com.banksalad.collectmydata.invest.common.db.repository.AccountSummaryRepository;
import com.banksalad.collectmydata.invest.publishment.account.dto.AccountBasicResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.BANKSALAD_USER_ID;
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.OLD_SYNCED_AT;
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.ORGANIZATION_ID;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@DisplayName("계좌 기본정보 Publish 서비스 테스트")
class AccountBasicPublishServiceTest {

  @Autowired
  private AccountSummaryRepository accountSummaryRepository;

  @Autowired
  private AccountBasicRepository accountBasicRepository;

  @Autowired
  private AccountBasicPublishService accountBasicPublishService;

  @Test
  @DisplayName("계좌 기본정보 조회 성공 테스트")
  void getAccountBasicResponsesTest() {
    // given
    accountSummaryRepository.saveAll(getAccountSummaryEntities());
    accountBasicRepository.save(getAccountBasicEntity());

    // when
    List<AccountBasicResponse> accountBasicResponses = accountBasicPublishService
        .getAccountBasicResponses(BANKSALAD_USER_ID, ORGANIZATION_ID);

    // then
    assertThat(accountBasicResponses.size()).isEqualTo(1);
    assertThat(accountBasicResponses).usingRecursiveComparison().isEqualTo(accountBasicRepository.findAll());
  }

  private List<AccountSummaryEntity> getAccountSummaryEntities() {
    return List.of(
        AccountSummaryEntity.builder()
            .syncedAt(OLD_SYNCED_AT)
            .banksaladUserId(BANKSALAD_USER_ID)
            .organizationId(ORGANIZATION_ID)
            .accountNum("1111111111")
            .consent(true)
            .accountName("증권계좌1")
            .accountType("101")
            .accountStatus("201")
            .basicResponseCode("00000")
            .build(),
        AccountSummaryEntity.builder()
            .syncedAt(OLD_SYNCED_AT)
            .banksaladUserId(BANKSALAD_USER_ID)
            .organizationId(ORGANIZATION_ID)
            .accountNum("2222222222")
            .consent(false)
            .accountName("증권계좌2")
            .accountType("101")
            .accountStatus("201")
            .basicResponseCode("00000")
            .build(),
        AccountSummaryEntity.builder()
            .syncedAt(OLD_SYNCED_AT)
            .banksaladUserId(BANKSALAD_USER_ID)
            .organizationId(ORGANIZATION_ID)
            .accountNum("3333333333")
            .consent(true)
            .accountName("증권계좌3")
            .accountType("101")
            .accountStatus("201")
            .basicResponseCode("40305")
            .build()
    );
  }

  private AccountBasicEntity getAccountBasicEntity() {
    return AccountBasicEntity.builder()
        .syncedAt(OLD_SYNCED_AT)
        .banksaladUserId(BANKSALAD_USER_ID)
        .organizationId(ORGANIZATION_ID)
        .accountNum("1111111111")
        .issueDate("20210101")
        .taxBenefits(true)
        .withholdingsAmt(new BigDecimal("10000.000"))
        .creditLoanAmt(new BigDecimal("20000.000"))
        .mortgageAmt(new BigDecimal("30000.000"))
        .currencyCode(FinanceConstant.CURRENCY_KRW)
        .build();
  }
}
