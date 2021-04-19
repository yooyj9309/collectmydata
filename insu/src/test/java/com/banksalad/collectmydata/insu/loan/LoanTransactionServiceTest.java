package com.banksalad.collectmydata.insu.loan;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.util.DateUtil;
import com.banksalad.collectmydata.finance.api.transaction.TransactionApiService;
import com.banksalad.collectmydata.finance.api.transaction.TransactionRequestHelper;
import com.banksalad.collectmydata.finance.api.transaction.TransactionResponseHelper;
import com.banksalad.collectmydata.insu.collect.Executions;
import com.banksalad.collectmydata.insu.common.db.entity.LoanSummaryEntity;
import com.banksalad.collectmydata.insu.common.db.entity.LoanTransactionEntity;
import com.banksalad.collectmydata.insu.common.db.entity.LoanTransactionInterestEntity;
import com.banksalad.collectmydata.insu.common.db.repository.LoanSummaryRepository;
import com.banksalad.collectmydata.insu.common.db.repository.LoanTransactionInterestRepository;
import com.banksalad.collectmydata.insu.common.db.repository.LoanTransactionRepository;
import com.banksalad.collectmydata.insu.common.util.TestHelper;
import com.banksalad.collectmydata.insu.loan.dto.ListLoanTransactionRequest;
import com.banksalad.collectmydata.insu.loan.dto.LoanTransaction;
import com.banksalad.collectmydata.insu.summary.dto.LoanSummary;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.WireMockSpring;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.apache.http.entity.ContentType;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static com.banksalad.collectmydata.insu.common.util.FileUtil.readText;
import static com.banksalad.collectmydata.insu.common.util.TestHelper.ACCOUNT_NUM;
import static com.banksalad.collectmydata.insu.common.util.TestHelper.BANKSALAD_USER_ID;
import static com.banksalad.collectmydata.insu.common.util.TestHelper.ENTITY_IGNORE_FIELD;
import static com.banksalad.collectmydata.insu.common.util.TestHelper.ORGANIZATION_ID;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class LoanTransactionServiceTest {

  @Autowired
  private TransactionApiService<LoanSummary, ListLoanTransactionRequest, LoanTransaction> transactionApiService;
  @Autowired
  private TransactionRequestHelper<LoanSummary, ListLoanTransactionRequest> requestHelper;
  @Autowired
  private TransactionResponseHelper<LoanSummary, LoanTransaction> responseHelper;
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
  @Transactional
  @DisplayName("6.5.11 대출 거래내역 성공케이스 :db에 없던 데이터가 추가된경우.")
  void listLoanTransactions_success() {
    // Given
    ExecutionContext executionContext = TestHelper.getExecutionContext(
        wireMockServer.port(),
        LocalDateTime.of(2021, 03, 16, 10, 00)
    );
    saveLoanSummaryEntity(LocalDateTime.of(2021, 03, 15, 10, 00));

    // When
    transactionApiService
        .listTransactions(executionContext, Executions.insurance_get_loan_transactions, requestHelper, responseHelper);

    List<LoanTransactionEntity> loanTransactionEntities = loanTransactionRepository.findAll();
    List<LoanTransactionInterestEntity> loanTransactionInterestEntities = loanTransactionInterestRepository.findAll();

    // Then
    assertEquals(2, loanTransactionEntities.size());
    assertEquals(3, loanTransactionInterestEntities.size());

    // TODO : compare with db
//    assertThat(loanTransactions.get(0)).usingRecursiveComparison()
//        .isEqualTo(
//            LoanTransaction.builder()
//                .transNo("trans#2")
//                .transDtime("20210121103000")
//                .currencyCode("KRW")
//                .loanPaidAmt(new BigDecimal("1000.312"))
//                .intPaidAmt(new BigDecimal("18000.712"))
//                .intCnt(2)
//                .intList(
//                    List.of(
//                        LoanTransactionInterest.builder()
//                            .intStartDate("20201201")
//                            .intEndDate("20201231")
//                            .intRate(new BigDecimal("4.112"))
//                            .intType("02")
//                            .build(),
//                        LoanTransactionInterest.builder()
//                            .intStartDate("20201201")
//                            .intEndDate("20201231")
//                            .intRate(new BigDecimal("3.012"))
//                            .intType("01")
//                            .build()
//                    )
//                )
//                .build()
//        );

    assertThat(loanTransactionEntities.get(0)).usingRecursiveComparison()
        .ignoringFields(ENTITY_IGNORE_FIELD)
        .isEqualTo(
            LoanTransactionEntity.builder()
                .transactionYearMonth(202101)
                .banksaladUserId(BANKSALAD_USER_ID)
                .organizationId(ORGANIZATION_ID)
                .accountNum(ACCOUNT_NUM)
                .transNo("trans#2")
                .transDtime("20210121103000")
                .accountType("3400")
                .currencyCode("KRW")
                .loanPaidAmt(new BigDecimal("1000.312"))
                .intPaidAmt(new BigDecimal("18000.712"))
                .build()
        );

    assertThat(loanTransactionInterestEntities.get(0)).usingRecursiveComparison()
        .ignoringFields(ENTITY_IGNORE_FIELD)
        .isEqualTo(
            LoanTransactionInterestEntity.builder()
                .transactionYearMonth(202101)
                .banksaladUserId(BANKSALAD_USER_ID)
                .organizationId(ORGANIZATION_ID)
                .accountNum(ACCOUNT_NUM)
                .transDtime("20210121103000")
                .transNo("trans#2")
                .intNo(1)
                .intStartDate("20201201")
                .intEndDate("20201231")
                .intRate(new BigDecimal("4.112"))
                .intType("02")
                .build()
        );
  }

  private void saveLoanSummaryEntity(LocalDateTime transactionSyncedAt) {
    loanSummaryRepository.save(LoanSummaryEntity.builder()
        .syncedAt(LocalDateTime.now(DateUtil.UTC_ZONE_ID))
        .banksaladUserId(BANKSALAD_USER_ID)
        .organizationId(ORGANIZATION_ID)
        .accountNum(ACCOUNT_NUM)
        .consent(true)
        .prodName("좋은 보험대출")
        .accountType("3400")
        .accountStatus("01")
        .transactionSyncedAt(transactionSyncedAt)
        .build());
  }

  private static void setupMockServer() {
    wireMockServer.stubFor(post(urlMatching("/loans/transactions"))
        .withRequestBody(
            equalToJson(readText("classpath:mock/request/IS14_001_single_page_00.json")))
        .willReturn(
            aResponse()
                .withStatus(HttpStatus.OK.value())
                .withHeader("Content-Type", ContentType.APPLICATION_JSON.toString())
                .withBody(readText("classpath:mock/response/IS14_001_single_page_00.json"))));
  }
}
