package com.banksalad.collectmydata.insu.loan;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.WireMockSpring;
import org.springframework.http.HttpStatus;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.organization.Organization;
import com.banksalad.collectmydata.insu.common.db.entity.LoanSummaryEntity;
import com.banksalad.collectmydata.insu.common.db.entity.LoanTransactionEntity;
import com.banksalad.collectmydata.insu.common.db.repository.LoanSummaryRepository;
import com.banksalad.collectmydata.insu.common.db.repository.LoanTransactionInterestRepository;
import com.banksalad.collectmydata.insu.common.db.repository.LoanTransactionRepository;
import com.banksalad.collectmydata.insu.common.dto.LoanSummary;
import com.banksalad.collectmydata.insu.common.util.TestHelper;
import com.banksalad.collectmydata.insu.loan.dto.LoanTransaction;
import com.banksalad.collectmydata.insu.loan.dto.LoanTransactionInterest;
import com.banksalad.collectmydata.insu.loan.service.LoanTransactionServiceImpl;
import com.github.tomakehurst.wiremock.WireMockServer;
import org.apache.http.entity.ContentType;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;

import static com.banksalad.collectmydata.insu.common.util.FileUtil.readText;
import static com.banksalad.collectmydata.insu.common.util.TestHelper.BANKSALAD_USER_ID;
import static com.banksalad.collectmydata.insu.common.util.TestHelper.INDUSTRY;
import static com.banksalad.collectmydata.insu.common.util.TestHelper.ORGANIZATION_CODE;
import static com.banksalad.collectmydata.insu.common.util.TestHelper.ORGANIZATION_HOST;
import static com.banksalad.collectmydata.insu.common.util.TestHelper.ORGANIZATION_ID;
import static com.banksalad.collectmydata.insu.common.util.TestHelper.SECTOR;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class LoanTransactionServiceTest {

  @Autowired
  private LoanTransactionServiceImpl loanTransactionService;

  @Autowired
  private LoanSummaryRepository loanSummaryRepository;

  @Autowired
  private LoanTransactionRepository loanTransactionRepository;

  @Autowired
  private LoanTransactionInterestRepository loanTransactionInterestRepository;

  private static WireMockServer wireMockServer;

  @BeforeAll
  static void setup() {
    wireMockServer = new WireMockServer(WireMockSpring.options().dynamicPort());
    wireMockServer.start();
    setupMockServer();
  }

  @AfterAll
  static void tearDown() {
    wireMockServer.shutdown();
  }

  @Test
  @DisplayName("6.5.11 Data Provider API Response : DB 에 없는 거래내역 01, DB 데이터와 동일한 거래내역 02")
  void givenLoanTransactions_whenListLoanTransactions_ThenInsertLonTransactionAndInterest() {
    // Given
    ExecutionContext executionContext = TestHelper.getExecutionContext(wireMockServer.port());
    Organization organization = getOrganization();
    LoanSummary loanSummary = getLoanSummary();

    LoanSummaryEntity loanSummaryEntity = LoanSummaryEntity.builder()
        .syncedAt(LocalDateTime.now())
        .banksaladUserId(BANKSALAD_USER_ID)
        .organizationId(ORGANIZATION_ID)
        .accountNum(loanSummary.getAccountNum())
        .consent(loanSummary.isConsent())
        .prodName(loanSummary.getProdName())
        .accountType(loanSummary.getAccountType())
        .accountStatus(loanSummary.getAccountStatus())
        .build();
    loanSummaryRepository.save(loanSummaryEntity);

    LoanTransaction loanTransaction = getLoanTransaction();
    LoanTransactionEntity loanTransactionEntity = LoanTransactionEntity.builder()
        .transactionYearMonth(Integer.parseInt(loanTransaction.getTransDtime().substring(0, 6)))
        .syncedAt(LocalDateTime.now().minusDays(1))
        .banksaladUserId(executionContext.getBanksaladUserId())
        .organizationId(executionContext.getOrganizationId())
        .accountNum(loanSummary.getAccountNum())
        .transDtime(loanTransaction.getTransDtime())
        .transNo(loanTransaction.getTransNo())
        .accountType(loanSummary.getAccountType())
        .currencyCode(loanTransaction.getCurrencyCode())
        .loanPaidAmt(loanTransaction.getLoanPaidAmt())
        .intPaidAmt(loanTransaction.getIntPaidAmt())
        .build();
    loanTransactionRepository.save(loanTransactionEntity);

    // When
    loanTransactionService.listLoanTransactions(executionContext, organization, Collections.singletonList(loanSummary));

    // Then
    assertEquals(2, loanTransactionRepository.count());
    assertEquals(2, loanTransactionInterestRepository.count());
  }

  private Organization getOrganization() {
    return Organization.builder()
        .sector(SECTOR)
        .industry(INDUSTRY)
        .organizationId(ORGANIZATION_ID)
        .organizationCode(ORGANIZATION_CODE)
        .hostUrl(ORGANIZATION_HOST)
        .build();
  }

  private LoanSummary getLoanSummary() {
    return LoanSummary.builder()
        .prodName("보금자리론")
        .accountNum("123456789")
        .consent(true)
        .accountType("3245")
        .accountStatus("01")
        .transactionSyncedAt(LocalDateTime.now().minusDays(1))
        .build();
  }

  private LoanTransaction getLoanTransaction() {
    return LoanTransaction.builder()
        .transNo("trans#1")
        .transDtime("20210121093000")
        .currencyCode("KRW")
        .loanPaidAmt(BigDecimal.valueOf(1000.312))
        .intPaidAmt(BigDecimal.valueOf(18000.712))
        .intCnt(1)
        .intList(Collections.singletonList(
            LoanTransactionInterest.builder()
                .intStartDate("20201201")
                .intEndDate("20201231")
                .intRate(BigDecimal.valueOf(3.012))
                .intType("99")
                .build()
        ))
        .build();
  }

  private static void setupMockServer() {
    wireMockServer.stubFor(post(urlMatching("/loans/transactions"))
        .withRequestBody(
            equalToJson(readText("classpath:mock/request/IS14_001_single_page_00.json")))
        .willReturn(
            aResponse()
                .withFixedDelay(1000)
                .withStatus(HttpStatus.OK.value())
                .withHeader("Content-Type", ContentType.APPLICATION_JSON.toString())
                .withBody(readText("classpath:mock/response/IS14_001_single_page_00.json"))));
  }
}
