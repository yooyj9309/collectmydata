package com.banksalad.collectmydata.capital.account;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.WireMockSpring;
import org.springframework.http.HttpStatus;

import com.banksalad.collectmydata.capital.account.dto.AccountTransaction;
import com.banksalad.collectmydata.capital.account.dto.ListAccountTransactionsRequest;
import com.banksalad.collectmydata.capital.collect.Executions;
import com.banksalad.collectmydata.capital.common.TestHelper;
import com.banksalad.collectmydata.capital.common.db.entity.AccountSummaryEntity;
import com.banksalad.collectmydata.capital.common.db.entity.AccountTransactionEntity;
import com.banksalad.collectmydata.capital.common.db.entity.AccountTransactionInterestEntity;
import com.banksalad.collectmydata.capital.common.db.repository.AccountSummaryRepository;
import com.banksalad.collectmydata.capital.common.db.repository.AccountTransactionInterestRepository;
import com.banksalad.collectmydata.capital.common.db.repository.AccountTransactionRepository;
import com.banksalad.collectmydata.capital.summary.dto.AccountSummary;
import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.finance.api.transaction.TransactionApiService;
import com.banksalad.collectmydata.finance.api.transaction.TransactionRequestHelper;
import com.banksalad.collectmydata.finance.api.transaction.TransactionResponseHelper;
import com.github.tomakehurst.wiremock.WireMockServer;
import javax.transaction.Transactional;
import org.apache.http.entity.ContentType;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static com.banksalad.collectmydata.capital.common.TestHelper.ACCOUNT_NUM;
import static com.banksalad.collectmydata.capital.common.TestHelper.BANKSALAD_USER_ID;
import static com.banksalad.collectmydata.capital.common.TestHelper.ENTITY_IGNORE_FIELD;
import static com.banksalad.collectmydata.capital.common.TestHelper.ORGANIZATION_ID;
import static com.banksalad.collectmydata.capital.common.TestHelper.SEQNO1;
import static com.banksalad.collectmydata.capital.util.FileUtil.readText;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class AccountTransactionServiceTest {

  private static WireMockServer wireMockServer;

  @Autowired
  private AccountSummaryRepository accountSummaryRepository;

  @Autowired
  private AccountTransactionRepository accountTransactionRepository;

  @Autowired
  private AccountTransactionInterestRepository accountTransactionInterestRepository;

  @Autowired
  private TransactionApiService<AccountSummary, ListAccountTransactionsRequest, AccountTransaction> accountTransactionService;

  @Autowired
  private TransactionRequestHelper<AccountSummary, ListAccountTransactionsRequest> requestHelper;

  @Autowired
  private TransactionResponseHelper<AccountSummary, AccountTransaction> responseHelper;


  @AfterEach
  void cleanBefore() {
    accountSummaryRepository.deleteAll();
    accountTransactionRepository.deleteAll();
    accountTransactionInterestRepository.deleteAll();
  }

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

  private static void setupMockServer() {
    // 6.7.6 운용리스 거래내역 조회 :
    String[] fileNames = {
        "CP04_001_single_page_00.json",
        "CP04_002_single_page_00.json",
        "CP04_003_multi_page_00.json",
        "CP04_003_multi_page_01.json"
    };
    for (String fileName : fileNames) {
      wireMockServer.stubFor(post(urlMatching("/loans/transactions"))
          .withRequestBody(
              equalToJson(readText("classpath:mock/request/" + fileName)))
          .willReturn(
              aResponse()
                  .withFixedDelay(500)
                  .withStatus(HttpStatus.OK.value())
                  .withHeader("Content-Type", ContentType.APPLICATION_JSON.toString())
                  .withBody(readText("classpath:mock/response/" + fileName))));
    }
  }

  @Test
  @Transactional
  @DisplayName("6.7.4 거래내역 조회 단일페이지 성공케이스 - 데이터가 없는경우 ")
  public void listTransaction_single1_success() {
    ExecutionContext context = TestHelper.getExecutionContext(
        wireMockServer.port(),
        LocalDateTime.of(2021, 01, 02, 10, 00)
    );
    saveAccountSummary(LocalDateTime.of(2021, 01, 01, 10, 00));

    List<AccountTransaction> accountTransactions = accountTransactionService
        .listTransactions(context, Executions.capital_get_account_transactions, requestHelper, responseHelper);

    List<AccountTransactionEntity> accountTransactionEntities = accountTransactionRepository.findAll();
    List<AccountTransactionInterestEntity> accountTransactionInterestEntities = accountTransactionInterestRepository
        .findAll();
    assertEquals(0, accountTransactions.size());
    assertEquals(0, accountTransactionEntities.size());
    assertEquals(0, accountTransactionInterestEntities.size());
  }

  @Test
  @Transactional
  @DisplayName("6.7.4 거래내역 조회 단일페이지 성공케이스 - 데이터 있는 경우")
  public void listTransaction_single2_success() {
    ExecutionContext context = TestHelper.getExecutionContext(
        wireMockServer.port(),
        LocalDateTime.of(2021, 03, 01, 10, 00)
    );
    saveAccountSummary(LocalDateTime.of(2021, 02, 01, 10, 00));

    List<AccountTransaction> accountTransactions = accountTransactionService
        .listTransactions(context, Executions.capital_get_account_transactions, requestHelper, responseHelper);
    List<AccountTransactionEntity> accountTransactionEntities = accountTransactionRepository.findAll();
    List<AccountTransactionInterestEntity> accountTransactionInterestEntities = accountTransactionInterestRepository
        .findAll();

    assertEquals(2, accountTransactions.size());
    assertEquals(2, accountTransactionEntities.size());
    assertEquals(3, accountTransactionInterestEntities.size());

    assertThat(accountTransactionEntities.get(0)).usingRecursiveComparison()
        .ignoringFields(ENTITY_IGNORE_FIELD)
        .isEqualTo(
            AccountTransactionEntity.builder()
                .transactionYearMonth(202101)
                .banksaladUserId(BANKSALAD_USER_ID)
                .organizationId(ORGANIZATION_ID)
                .accountNum(ACCOUNT_NUM)
                .seqno(SEQNO1)
                .uniqueTransNo("6fae7b0455d3ad49ced012942bd051a6ac631fdb53839f5d29f61d40c334e370")
                .transDtime("20210121103000")
                .transNo("trans#2")
                .transType("03")
                .transAmt(new BigDecimal("1000.300"))
                .balanceAmt(new BigDecimal("18000.700"))
                .principalAmt(new BigDecimal("20000.000"))
                .intAmt(new BigDecimal("100.000"))
                .build()
        );

    assertThat(accountTransactionInterestEntities.get(0)).usingRecursiveComparison()
        .ignoringFields(ENTITY_IGNORE_FIELD)
        .isEqualTo(
            AccountTransactionInterestEntity.builder()
                .transactionYearMonth(202101)
                .banksaladUserId(BANKSALAD_USER_ID)
                .organizationId(ORGANIZATION_ID)
                .accountNum(ACCOUNT_NUM)
                .seqno(SEQNO1)
                .uniqueTransNo("6fae7b0455d3ad49ced012942bd051a6ac631fdb53839f5d29f61d40c334e370")
                .intNo(1)
                .intStartDate("20201201")
                .intEndDate("20201231")
                .intRate(new BigDecimal("4.125"))
                .intType("02")
                .build()
        );
  }

  @Test
  @Transactional
  @DisplayName("6.7.4 거래내역 조회 멀티페이지")
  public void listTransaction_multi_page_success() {
    ExecutionContext context = TestHelper.getExecutionContext(
        wireMockServer.port(),
        LocalDateTime.of(2021, 04, 01, 10, 00)
    );
    saveAccountSummary(LocalDateTime.of(2021, 03, 01, 10, 00));

    List<AccountTransaction> accountTransactions = accountTransactionService
        .listTransactions(context, Executions.capital_get_account_transactions, requestHelper, responseHelper);
    List<AccountTransactionEntity> accountTransactionEntities = accountTransactionRepository.findAll();
    List<AccountTransactionInterestEntity> accountTransactionInterestEntities = accountTransactionInterestRepository
        .findAll();

    assertEquals(3, accountTransactions.size());
    assertEquals(3, accountTransactionEntities.size());
    assertEquals(3, accountTransactionInterestEntities.size());

    assertThat(accountTransactionEntities.get(0)).usingRecursiveComparison()
        .ignoringFields(ENTITY_IGNORE_FIELD)
        .isEqualTo(
            AccountTransactionEntity.builder()
                .transactionYearMonth(202101)
                .banksaladUserId(BANKSALAD_USER_ID)
                .organizationId(ORGANIZATION_ID)
                .accountNum(ACCOUNT_NUM)
                .seqno(SEQNO1)
                .uniqueTransNo("c71b4b5922141cf6b6eae3159e789b1e30835bf3030acad793da490e109cc28d")
                .transDtime("20210121103000")
                .transNo("trans#2")
                .transType("03")
                .transAmt(new BigDecimal("1000.3"))
                .balanceAmt(new BigDecimal("18000.7"))
                .principalAmt(new BigDecimal("20000.0"))
                .intAmt(new BigDecimal("100.3"))
                .build()
        );

    assertThat(accountTransactionInterestEntities.get(0)).usingRecursiveComparison()
        .ignoringFields(ENTITY_IGNORE_FIELD)
        .isEqualTo(
            AccountTransactionInterestEntity.builder()
                .transactionYearMonth(202101)
                .banksaladUserId(BANKSALAD_USER_ID)
                .organizationId(ORGANIZATION_ID)
                .accountNum(ACCOUNT_NUM)
                .seqno(SEQNO1)
                .uniqueTransNo("c71b4b5922141cf6b6eae3159e789b1e30835bf3030acad793da490e109cc28d")
                .intNo(1)
                .intStartDate("20201201")
                .intEndDate("20201231")
                .intRate(new BigDecimal("4.125"))
                .intType("02")
                .build()
        );
  }

  public void saveAccountSummary(LocalDateTime fromDateTime) {
    accountSummaryRepository.save(
        AccountSummaryEntity.builder()
            .syncedAt(LocalDateTime.now())
            .banksaladUserId(BANKSALAD_USER_ID)
            .organizationId(ORGANIZATION_ID)
            .accountNum(ACCOUNT_NUM)
            .seqno(SEQNO1)
            .isConsent(true)
            .prodName("")
            .accountType("")
            .accountStatus("")
            .transactionSyncedAt(fromDateTime)
            .build()
    );
  }
}
