package com.banksalad.collectmydata.telecom.telecom;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.contract.wiremock.WireMockSpring;
import org.springframework.http.HttpStatus;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.finance.api.transaction.TransactionApiService;
import com.banksalad.collectmydata.finance.api.transaction.TransactionRequestHelper;
import com.banksalad.collectmydata.finance.api.transaction.TransactionResponseHelper;
import com.banksalad.collectmydata.telecom.collect.Executions;
import com.banksalad.collectmydata.telecom.common.service.TelecomSummaryService;
import com.banksalad.collectmydata.telecom.summary.dto.TelecomSummary;
import com.banksalad.collectmydata.telecom.telecom.dto.ListTelecomTransactionsRequest;
import com.banksalad.collectmydata.telecom.telecom.dto.TelecomTransaction;
import com.github.tomakehurst.wiremock.WireMockServer;
import org.apache.http.entity.ContentType;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static com.banksalad.collectmydata.telecom.common.util.FileUtil.readText;
import static com.banksalad.collectmydata.telecom.common.util.TestHelper.*;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@DisplayName("통신 거래내역 서비스 테스트")
class TelecomTransactionServiceTest {

  @Autowired
  private TransactionApiService<TelecomSummary, ListTelecomTransactionsRequest, TelecomTransaction> telecomTransactionApiService;

  @Autowired
  private TransactionRequestHelper<TelecomSummary, ListTelecomTransactionsRequest> telecomTransactionRequestHelper;

  @Autowired
  private TransactionResponseHelper<TelecomSummary, TelecomTransaction> telecomTransactionResponseHelper;

  @MockBean
  private TelecomSummaryService telecomSummaryService;

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
  @DisplayName("6.9.3 통신 거래내역 조회")
  void telecomTransaction_transactionService_listTransactions_test() {
    // given
    ExecutionContext executionContext = getExecutionContext(wireMockServer.port());

    when(telecomSummaryService.listSummariesConsented(BANKSALAD_USER_ID, ORGANIZATION_ID))
        .thenReturn(List.of(
            TelecomSummary.builder()
                .mgmtId("1234567890")
                .consent(true)
                .telecomNum("01012345678")
                .type("01")
                .status("01")
                .transactionSyncedAt(LocalDateTime.of(2021, 2, 1, 0, 0))
                .paidTransactionSyncedAt(null)
                .build()
        ));

    // when
    List<TelecomTransaction> telecomTransactions = telecomTransactionApiService
        .listTransactions(executionContext, Executions.finance_telecom_transactions, telecomTransactionRequestHelper,
            telecomTransactionResponseHelper);

    // then
    assertThat(telecomTransactions).usingRecursiveComparison().isEqualTo(
        List.of(
            TelecomTransaction.builder()
                .transMonth("202102")
                .paidAmt(BigDecimal.valueOf(30000))
                .payMethod("01")
                .build(),
            TelecomTransaction.builder()
                .transMonth("202103")
                .paidAmt(BigDecimal.valueOf(45000))
                .payMethod("01")
                .build()
        ));
  }

  private static void setupMockServer() {
    // 6.9.3 통신 거래내역 조회
    wireMockServer.stubFor(post(urlMatching("/telecoms/transactions"))
        .withRequestBody(
            equalToJson(readText("classpath:mock/request/TC03_001_single_page_00.json")))
        .willReturn(
            aResponse()
                .withFixedDelay(0)
                .withStatus(HttpStatus.OK.value())
                .withHeader("Content-Type", ContentType.APPLICATION_JSON.toString())
                .withBody(readText("classpath:mock/response/TC03_001_single_page_00.json"))));
  }
}
