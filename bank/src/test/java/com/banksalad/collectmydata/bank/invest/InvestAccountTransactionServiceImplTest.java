package com.banksalad.collectmydata.bank.invest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.cloud.contract.wiremock.WireMockSpring;
import org.springframework.http.HttpStatus;

import com.banksalad.collectmydata.bank.common.db.entity.AccountSummaryEntity;
import com.banksalad.collectmydata.bank.common.db.entity.InvestAccountTransactionEntity;
import com.banksalad.collectmydata.bank.common.db.repository.AccountSummaryRepository;
import com.banksalad.collectmydata.bank.common.db.repository.InvestAccountTransactionRepository;
import com.banksalad.collectmydata.bank.common.mapper.AccountSummaryMapper;
import com.banksalad.collectmydata.bank.invest.dto.InvestAccountTransaction;
import com.banksalad.collectmydata.bank.summary.dto.AccountSummary;
import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.util.DateUtil;
import com.github.tomakehurst.wiremock.WireMockServer;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.entity.ContentType;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.banksalad.collectmydata.bank.testutil.FileUtil.readText;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;

@Disabled
@Slf4j
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@DisplayName("투자 계좌 거래내역 조회 테스트")
public class InvestAccountTransactionServiceImplTest {

  private static final Long BANKSALAD_USER_ID = 1L;
  private static final String ORGANIZATION_ID = "woori_bank";
  private static final String ORGANIZATION_HOST = "http://localhost";
  private static final LocalDateTime TRANSACTION_SYNCED_AT = LocalDateTime.of(2020, 03, 27, 0, 0, 0);
  private static final WireMockServer wiremock = new WireMockServer(WireMockSpring.options().dynamicPort());

  private final AccountSummaryMapper accountSummaryMapper = Mappers.getMapper(AccountSummaryMapper.class);

  @Autowired
  private InvestAccountTransactionService investAccountTransactionService;

  @Autowired
  private InvestAccountTransactionRepository investAccountTransactionRepository;

  @Autowired
  private AccountSummaryRepository accountSummaryRepository;


  @BeforeEach
  public void setupClass() {
    wiremock.start();
  }

  @AfterEach
  public void after() {
    wiremock.resetAll();
    accountSummaryRepository.deleteAll();
    investAccountTransactionRepository.deleteAll();
  }

  @AfterAll
  public static void clean() {
    wiremock.shutdown();
  }

  @Test
  @DisplayName("총 2개 계좌에 대해 거래내역 동기화 중 1개만 성공 하는 경우")
  public void step_01_listInvestAccountTransactions_single_page_success() throws Exception {

    /* transaction mock server */
    setupServerInvestAccountTransactionsSinglePage();

    /* save mock account summaries */
    accountSummaryRepository.saveAll(getAccountSummaryEntities());

    List<AccountSummary> accountSummaries = accountSummaryRepository
        .findByBanksaladUserIdAndOrganizationIdAndConsent(BANKSALAD_USER_ID, ORGANIZATION_ID, true)
        .stream()
        .map(accountSummaryMapper::entityToDto)
        .collect(Collectors.toList());

    /* execution context */
    LocalDateTime currentSyncStartedAt = LocalDateTime.of(2021, 03, 28, 0, 0, 0);
    ExecutionContext executionContext = ExecutionContext.builder()
        .banksaladUserId(BANKSALAD_USER_ID)
        .organizationId(ORGANIZATION_ID)
        .accessToken("test")
        .organizationHost(ORGANIZATION_HOST + ":" + wiremock.port())
        .executionRequestId(UUID.randomUUID().toString())
        .syncStartedAt(currentSyncStartedAt)
        .build();

    /* assertions transaction size */
    List<InvestAccountTransaction> investAccountTransactions = investAccountTransactionService
        .listInvestAccountTransactions(executionContext, accountSummaries);

    Assertions.assertThat(investAccountTransactions.size()).isEqualTo(4);

    List<InvestAccountTransactionEntity> investAccountTransactionEntities = investAccountTransactionRepository
        .findAll();

    Assertions.assertThat(investAccountTransactionEntities.size()).isEqualTo(4);

    /* assertions transactionSyncedAt */
    AccountSummaryEntity successAccountSummaryEntity = accountSummaryRepository
        .findByBanksaladUserIdAndOrganizationIdAndAccountNumAndSeqno(BANKSALAD_USER_ID, ORGANIZATION_ID,
            "1234567890", "a123").get();

    Assertions.assertThat(successAccountSummaryEntity.getTransactionSyncedAt()).isEqualTo(currentSyncStartedAt);

    AccountSummaryEntity failAccountSummaryEntity = accountSummaryRepository
        .findByBanksaladUserIdAndOrganizationIdAndAccountNumAndSeqno(BANKSALAD_USER_ID, ORGANIZATION_ID,
            "414312341242", "ba123").get();

    Assertions.assertThat(failAccountSummaryEntity.getTransactionSyncedAt()).isEqualTo(TRANSACTION_SYNCED_AT);
  }


  private void setupServerInvestAccountTransactionsSinglePage() {

    // accountNum: 1234567890, dateRange: 20200901 ~ 20201130, dataSize: 2
    wiremock.stubFor(post(urlMatching("/accounts/invest/transactions"))
        .withRequestBody(equalToJson(readText("classpath:mock/bank/request/BA07_001_single_page_00.json")))
        .willReturn(
            aResponse()
                .withStatus(HttpStatus.OK.value())
                .withHeader("Content-Type", ContentType.APPLICATION_JSON.toString())
                .withBody(readText("classpath:mock/bank/response/BA07_001_single_page_00.json"))));

    // accountNum: 1234567890, dateRange: 20200901 ~ 20201130, dataSize: 0
    wiremock.stubFor(post(urlMatching("/accounts/invest/transactions"))
        .withRequestBody(equalToJson(readText("classpath:mock/bank/request/BA07_001_single_page_01.json")))
        .willReturn(
            aResponse()
                .withStatus(HttpStatus.OK.value())
                .withHeader("Content-Type", ContentType.APPLICATION_JSON.toString())
                .withBody(readText("classpath:mock/bank/response/BA07_001_single_page_01.json"))));

    // accountNum: 1234567890, dateRange: 20200601 ~ 20200831, dataSize: 0
    wiremock.stubFor(post(urlMatching("/accounts/invest/transactions"))
        .withRequestBody(equalToJson(readText("classpath:mock/bank/request/BA07_001_single_page_02.json")))
        .willReturn(
            aResponse()
                .withStatus(HttpStatus.OK.value())
                .withHeader("Content-Type", ContentType.APPLICATION_JSON.toString())
                .withBody(readText("classpath:mock/bank/response/BA07_001_single_page_01.json"))));

    // accountNum: 1234567890, dateRange: 20200327 ~ 20200531, dataSize: 0
    wiremock.stubFor(post(urlMatching("/accounts/invest/transactions"))
        .withRequestBody(equalToJson(readText("classpath:mock/bank/request/BA07_001_single_page_03.json")))
        .willReturn(
            aResponse()
                .withStatus(HttpStatus.OK.value())
                .withHeader("Content-Type", ContentType.APPLICATION_JSON.toString())
                .withBody(readText("classpath:mock/bank/response/BA07_001_single_page_01.json"))));

    // accountNum: 414312341242, dateRange: 20201201 ~ 20210328, dataSize: 2
    wiremock.stubFor(post(urlMatching("/accounts/invest/transactions"))
        .withRequestBody(equalToJson(readText("classpath:mock/bank/request/BA07_002_single_page_00.json")))
        .willReturn(
            aResponse()
                .withStatus(HttpStatus.OK.value())
                .withHeader("Content-Type", ContentType.APPLICATION_JSON.toString())
                .withBody(readText("classpath:mock/bank/response/BA07_002_single_page_00.json"))));
  }

  private List<AccountSummaryEntity> getAccountSummaryEntities() {
    return List.of(
        AccountSummaryEntity.builder()
            .banksaladUserId(BANKSALAD_USER_ID)
            .organizationId(ORGANIZATION_ID)
            .syncedAt(LocalDateTime.now(DateUtil.KST_ZONE_ID))
            .accountNum("1234567890")
            .accountStatus("01")
            .accountType("1001")
            .basicSearchTimestamp(0L)
            .detailSearchTimestamp(0L)
            .transactionSyncedAt(LocalDateTime.of(2020, 03, 27, 0, 0, 0))
            .foreignDeposit(false)
            .consent(true)
            .prodName("자유입출식 계좌")
            .seqno("a123")
            .build(),
        AccountSummaryEntity.builder()
            .banksaladUserId(BANKSALAD_USER_ID)
            .organizationId(ORGANIZATION_ID)
            .syncedAt(LocalDateTime.now(DateUtil.KST_ZONE_ID))
            .accountNum("414312341242")
            .accountStatus("01")
            .accountType("1001")
            .basicSearchTimestamp(0L)
            .detailSearchTimestamp(0L)
            .transactionSyncedAt(TRANSACTION_SYNCED_AT)
            .foreignDeposit(false)
            .consent(true)
            .prodName("자유입출식 계좌")
            .seqno("ba123")
            .build(),
        AccountSummaryEntity.builder()
            .banksaladUserId(BANKSALAD_USER_ID)
            .organizationId(ORGANIZATION_ID)
            .syncedAt(LocalDateTime.now(DateUtil.KST_ZONE_ID))
            .accountNum("234246541143")
            .accountStatus("01")
            .accountType("1001")
            .basicSearchTimestamp(0L)
            .detailSearchTimestamp(0L)
            .transactionSyncedAt(TRANSACTION_SYNCED_AT)
            .foreignDeposit(false)
            .consent(false)
            .prodName("자유입출식 계좌")
            .build()
    );
  }
}
