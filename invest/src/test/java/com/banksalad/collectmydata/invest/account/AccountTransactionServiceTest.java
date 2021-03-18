package com.banksalad.collectmydata.invest.account;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.WireMockSpring;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.util.DateUtil;
import com.banksalad.collectmydata.finance.api.transaction.TransactionApiService;
import com.banksalad.collectmydata.finance.api.transaction.TransactionRequestHelper;
import com.banksalad.collectmydata.finance.api.transaction.TransactionResponseHelper;
import com.banksalad.collectmydata.invest.account.dto.AccountTransaction;
import com.banksalad.collectmydata.invest.account.dto.ListAccountTransactionsRequest;
import com.banksalad.collectmydata.invest.collect.Executions;
import com.banksalad.collectmydata.invest.common.db.entity.AccountSummaryEntity;
import com.banksalad.collectmydata.invest.common.db.repository.AccountSummaryRepository;
import com.banksalad.collectmydata.invest.summary.dto.AccountSummary;
import com.github.tomakehurst.wiremock.WireMockServer;
import org.apache.http.entity.ContentType;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static com.banksalad.collectmydata.invest.util.FileUtil.readText;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class AccountTransactionServiceTest {

  @Autowired
  private TransactionApiService<AccountSummary, ListAccountTransactionsRequest, AccountTransaction> accountTransactionApiService;
  @Autowired
  private TransactionRequestHelper<AccountSummary, ListAccountTransactionsRequest> accountTransactionRequestHelper;
  @Autowired
  private TransactionResponseHelper<AccountSummary, AccountTransaction> accountTransactionResponseHelper;

  @Autowired
  AccountSummaryRepository accountSummaryRepository;

  private static WireMockServer wireMockServer;

  private static final Long BANKSALAD_USER_ID = 1L;
  private static final String ORGANIZATION_ID = "nh_securities";
  private static final String ORGANIZATION_HOST = "http://localhost";
  private static final String ACCESS_TOKEN = "accessToken";
  private static final String ORGANIZATION_CODE = "020";

  @BeforeAll
  static void setup() {
    wireMockServer = new WireMockServer(WireMockSpring.options().dynamicPort());
    wireMockServer.start();
    setupMockServer();
  }

  @AfterAll
  static void clean() {
    wireMockServer.shutdown();
  }

  @Test
  @Transactional
  @DisplayName("6.4.3 계좌 거래내역 조회 성공 테스트")
  void transactionApiServiceTest() {
    accountSummaryRepository.saveAll(getAccountSummaryEntities());

    ExecutionContext executionContext = ExecutionContext.builder()
        .syncRequestId(UUID.randomUUID().toString())
        .banksaladUserId(BANKSALAD_USER_ID)
        .organizationId(ORGANIZATION_ID)
        .organizationCode(ORGANIZATION_CODE)
        .accessToken(ACCESS_TOKEN)
        .organizationHost(ORGANIZATION_HOST + ":" + wireMockServer.port())
        .executionRequestId(UUID.randomUUID().toString())
        .syncStartedAt(LocalDateTime.of(2021, 3, 1, 0, 0, 0))
        .build();

    List<AccountTransaction> accountTransactions = accountTransactionApiService
        .listTransactions(executionContext, Executions.finance_invest_account_transactions,
            accountTransactionRequestHelper, accountTransactionResponseHelper);

    assertEquals(2, accountTransactions.size());
  }

  private static void setupMockServer() {
    wireMockServer.stubFor(post(urlMatching("/accounts/transactions"))
        .withRequestBody(equalToJson(readText("classpath:mock/request/IV03_001_single_page_00.json")))
        .willReturn(
            aResponse()
                .withFixedDelay(0)
                .withStatus(HttpStatus.OK.value())
                .withHeader("Content-Type", ContentType.APPLICATION_JSON.toString())
                .withBody(readText("classpath:mock/response/IV03_001_single_page_00.json"))));
  }

  private List<AccountSummaryEntity> getAccountSummaryEntities() {
    return List.of(
        AccountSummaryEntity.builder()
            .syncedAt(LocalDateTime.now(DateUtil.KST_ZONE_ID))
            .banksaladUserId(BANKSALAD_USER_ID)
            .organizationId(ORGANIZATION_ID)
            .accountNum("1234567890")
            .consent(true)
            .accountName("종합매매 증권계좌")
            .accountStatus("201")
            .accountType("101")
            .basicSearchTimestamp(0L)
            .transactionSyncedAt(LocalDateTime.of(2021, 1, 1, 0,  0, 0))
            .productSearchTimestamp(0L)
            .build()
    );
  }
}
