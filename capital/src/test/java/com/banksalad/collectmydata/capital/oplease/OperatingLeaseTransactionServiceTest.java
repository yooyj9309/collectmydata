package com.banksalad.collectmydata.capital.oplease;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.contract.wiremock.WireMockSpring;
import org.springframework.http.HttpStatus;

import com.banksalad.collectmydata.capital.collect.Executions;
import com.banksalad.collectmydata.capital.common.service.AccountSummaryService;
import com.banksalad.collectmydata.capital.oplease.dto.ListOperatingLeaseTransactionsRequest;
import com.banksalad.collectmydata.capital.oplease.dto.OperatingLeaseTransaction;
import com.banksalad.collectmydata.capital.summary.dto.AccountSummary;
import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.util.DateUtil;
import com.banksalad.collectmydata.finance.api.transaction.TransactionApiService;
import com.banksalad.collectmydata.finance.api.transaction.TransactionRequestHelper;
import com.banksalad.collectmydata.finance.api.transaction.TransactionResponseHelper;
import com.github.tomakehurst.wiremock.WireMockServer;
import org.apache.http.entity.ContentType;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static com.banksalad.collectmydata.capital.common.TestHelper.*;
import static com.banksalad.collectmydata.capital.util.FileUtil.readText;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static java.lang.Boolean.TRUE;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@DisplayName("통신 거래내역 서비스 테스트")
class OperatingLeaseTransactionServiceTest {

  @Autowired
  private TransactionApiService<AccountSummary, ListOperatingLeaseTransactionsRequest, OperatingLeaseTransaction> operatingLeaseTransactionApiService;

  @Autowired
  private TransactionRequestHelper<AccountSummary, ListOperatingLeaseTransactionsRequest> operatingLeaseTransactionRequestHelper;

  @Autowired
  private TransactionResponseHelper<AccountSummary, OperatingLeaseTransaction> operatingLeaseTransactionResponseHelper;

  @MockBean
  private AccountSummaryService accountSummaryService;

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
  @DisplayName("6.7.6 운용리스 거래내역 조회 성공")
  void operatingLeaseTransaction_transactionService_listTransactions_test() {
    // given
    LocalDateTime timeOnlyForTest = LocalDateTime.of(2021, 4, 1, 1, 0, 0);
    ExecutionContext executionContext = getExecutionContext(wireMockServer.port(), timeOnlyForTest);

    when(accountSummaryService.listSummariesConsented(BANKSALAD_USER_ID, ORGANIZATION_ID, true))
        .thenReturn(List.of(
            AccountSummary.builder()
                .accountNum(ACCOUNT_NUM)
                .isConsent(TRUE)
                .seqno(SEQNO1)
                .prodName(PRODUCT_NAME)
                .accountType(ACCOUNT_TYPE)
                .accountStatus(ACCOUNT_STATUS)
                .operatingLeaseTransactionSyncedAt(LocalDateTime.of(2021, 3, 1, 1, 0, 0))
                .build()
        ));

    // when
    List<OperatingLeaseTransaction> operatingLeaseTransactions = operatingLeaseTransactionApiService
        .listTransactions(executionContext, Executions.capital_get_operating_lease_transactions,
            operatingLeaseTransactionRequestHelper, operatingLeaseTransactionResponseHelper);

    // then
    assertThat(operatingLeaseTransactions).usingRecursiveComparison().isEqualTo(
        List.of(
            OperatingLeaseTransaction.builder()
                .transDtime("20210301010100")
                .transNo("1")
                .transType("01")
                .transAmt(BigDecimal.valueOf(100.001))
                .build(),
            OperatingLeaseTransaction.builder()
                .transDtime("20210302020100")
                .transNo("1")
                .transType("01")
                .transAmt(BigDecimal.valueOf(100.001))
                .build()
        )
    );
  }

  private static void setupMockServer() {
    // 6.7.6 운용리스 거래내역 조회
    wireMockServer.stubFor(post(urlMatching("/loans/oplease/transactions"))
        .withRequestBody(
            equalToJson(readText("classpath:mock/request/CP06_003.json")))
        .willReturn(
            aResponse()
                .withFixedDelay(500)
                .withStatus(HttpStatus.OK.value())
                .withHeader("Content-Type", ContentType.APPLICATION_JSON.toString())
                .withBody(readText("classpath:mock/response/CP06_003.json"))));
  }
}
