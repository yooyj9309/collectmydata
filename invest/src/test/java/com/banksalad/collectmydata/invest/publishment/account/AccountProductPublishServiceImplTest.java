package com.banksalad.collectmydata.invest.publishment.account;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.banksalad.collectmydata.finance.common.constant.FinanceConstant;
import com.banksalad.collectmydata.invest.common.db.entity.AccountProductEntity;
import com.banksalad.collectmydata.invest.common.db.entity.AccountSummaryEntity;
import com.banksalad.collectmydata.invest.common.db.repository.AccountProductRepository;
import com.banksalad.collectmydata.invest.common.db.repository.AccountSummaryRepository;
import com.banksalad.collectmydata.invest.publishment.account.dto.AccountProductResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.BANKSALAD_USER_ID;
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.CONSENT_ID;
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.OLD_SYNCED_AT;
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.ORGANIZATION_ID;
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.SYNC_REQUEST_ID;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@DisplayName("계좌 상품정보 Publish 서비스 테스트")
class AccountProductPublishServiceImplTest {

  @Autowired
  private AccountSummaryRepository accountSummaryRepository;

  @Autowired
  private AccountProductRepository accountProductRepository;

  @Autowired
  private AccountProductPublishService accountProductPublishService;

  @Test
  @DisplayName("계좌 상품정보 조회 성공 테스트")
  void getAccountProductResponsesTest() {
    // given
    accountSummaryRepository.saveAll(getAccountSummaryEntities());
    accountProductRepository.saveAll(getAccountProductEntities());

    // when
    List<AccountProductResponse> accountProductResponses = accountProductPublishService
        .getAccountProductResponses(BANKSALAD_USER_ID, ORGANIZATION_ID);

    // then
    assertThat(accountProductResponses).usingRecursiveComparison().isEqualTo(accountProductRepository.findAll());
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
            .productResponseCode("00000")
            .consentId(CONSENT_ID)
            .syncRequestId(SYNC_REQUEST_ID)
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
            .productResponseCode("00000")
            .consentId(CONSENT_ID)
            .syncRequestId(SYNC_REQUEST_ID)
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
            .productResponseCode("40305")
            .consentId(CONSENT_ID)
            .syncRequestId(SYNC_REQUEST_ID)
            .build()
    );
  }

  private List<AccountProductEntity> getAccountProductEntities() {
    return List.of(
        AccountProductEntity.builder()
            .syncedAt(OLD_SYNCED_AT)
            .banksaladUserId(BANKSALAD_USER_ID)
            .organizationId(ORGANIZATION_ID)
            .accountNum("1111111111")
            .prodNo((short) 1)
            .prodCode("005930")
            .prodType("401")
            .prodTypeDetail("국내주식")
            .prodName("삼성전자")
            .purchaseAmt(new BigDecimal( "10000.000"))
            .holdingNum(100L)
            .availForSaleNum(100L)
            .evalAmt(new BigDecimal("20000.000"))
            .issueDate("20210101")
            .paidInAmt(new BigDecimal("30000.000"))
            .withdrawalAmt(new BigDecimal("40000.000"))
            .lastPaidInDate("20210201")
            .rcvAmt(new BigDecimal("50000.000"))
            .currencyCode(FinanceConstant.CURRENCY_KRW)
            .consentId(CONSENT_ID)
            .syncRequestId(SYNC_REQUEST_ID)
            .build(),
        AccountProductEntity.builder()
            .syncedAt(OLD_SYNCED_AT)
            .banksaladUserId(BANKSALAD_USER_ID)
            .organizationId(ORGANIZATION_ID)
            .accountNum("1111111111")
            .prodNo((short) 2)
            .prodCode("AAPL")
            .prodType("402")
            .prodTypeDetail("해외주식")
            .prodName("애플")
            .purchaseAmt(new BigDecimal( "111.111"))
            .holdingNum(100L)
            .availForSaleNum(100L)
            .evalAmt(new BigDecimal("222.222"))
            .issueDate("20210101")
            .paidInAmt(new BigDecimal("333.333"))
            .withdrawalAmt(new BigDecimal("444.444"))
            .lastPaidInDate("20210201")
            .rcvAmt(new BigDecimal("555.555"))
            .currencyCode("USD")
            .consentId(CONSENT_ID)
            .syncRequestId(SYNC_REQUEST_ID)
            .build()
    );
  }
}
