package com.banksalad.collectmydata.bank.depoist;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.cloud.contract.wiremock.WireMockSpring;
import org.springframework.http.HttpStatus;

import com.banksalad.collectmydata.bank.common.db.entity.AccountSummaryEntity;
import com.banksalad.collectmydata.bank.common.db.entity.DepositAccountTransactionEntity;
import com.banksalad.collectmydata.bank.common.db.entity.mapper.AccountSummaryMapper;
import com.banksalad.collectmydata.bank.common.db.repository.AccountSummaryRepository;
import com.banksalad.collectmydata.bank.common.db.repository.DepositAccountTransactionRepository;
import com.banksalad.collectmydata.bank.common.dto.AccountSummary;
import com.banksalad.collectmydata.bank.depoist.dto.DepositAccountTransaction;
import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.util.DateUtil;
import com.github.tomakehurst.wiremock.WireMockServer;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.entity.ContentType;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
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

@Slf4j
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@DisplayName("수신계좌 거래내역 조회 테스트")
class DepositAccountTransactionServiceImplTest {

  private static final Long BANKSALAD_USER_ID = 1L;
  private static final String ORGANIZATION_ID = "woori_bank";
  private static final String ORGANIZATION_HOST = "http://localhost";
  private static final LocalDateTime TRANSACTION_SYNCED_AT = LocalDateTime.of(2020, 03, 27, 0, 0, 0);

  @Autowired
  private DepositAccountTransactionService depositAccountTransactionService;

  @Autowired
  private DepositAccountTransactionRepository depositAccountTransactionRepository;

  @Autowired
  private AccountSummaryRepository accountSummaryRepository;

  private AccountSummaryMapper accountSummaryMapper = Mappers.getMapper(AccountSummaryMapper.class);

  public static WireMockServer wiremock = new WireMockServer(WireMockSpring.options().dynamicPort());

  @BeforeEach
  public void setupClass() {
    wiremock.start();
  }

  @AfterEach
  public void after() {
    wiremock.resetAll();
    accountSummaryRepository.deleteAll();
    depositAccountTransactionRepository.deleteAll();
  }

  @AfterAll
  public static void clean() {
    wiremock.shutdown();
  }

  @Test
  @DisplayName("총 2개 계좌에 대해 거래내역 동기화 중 1개만 성공 하는 경우") // TODO jayden-lee displayname 변경 필요ㅠ
  public void step_01_listDepositAccountTransactions_single_page_success() throws Exception {

    /* transaction mock server */
    setupServerDepositAccountTransactionsSinglePage();

    /* save mock account summaries */
    accountSummaryRepository.saveAll(getAccountSummaryEntities());

    List<AccountSummary> accountSummaries = accountSummaryRepository
        .findByBanksaladUserIdAndOrganizationIdAndIsConsent(BANKSALAD_USER_ID, ORGANIZATION_ID, true)
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
    List<DepositAccountTransaction> depositAccountTransactions = depositAccountTransactionService
        .listDepositAccountTransactions(executionContext, accountSummaries);

    Assertions.assertThat(depositAccountTransactions.size()).isEqualTo(4);

    List<DepositAccountTransactionEntity> depositAccountTransactionEntities = depositAccountTransactionRepository
        .findAll();

    Assertions.assertThat(depositAccountTransactionEntities.size()).isEqualTo(4);

    /* assertions transactionSyncedAt */
    AccountSummaryEntity successAccountSummaryEntity = accountSummaryRepository
        .findByBanksaladUserIdAndOrganizationIdAndAccountNumAndSeqnoAndIsForeignDeposit(BANKSALAD_USER_ID,
            ORGANIZATION_ID, "1234567890", "a123", false);

    Assertions.assertThat(successAccountSummaryEntity.getTransactionSyncedAt()).isEqualTo(currentSyncStartedAt);

    AccountSummaryEntity failAccountSummaryEntity = accountSummaryRepository
        .findByBanksaladUserIdAndOrganizationIdAndAccountNumAndSeqnoAndIsForeignDeposit(BANKSALAD_USER_ID,
            ORGANIZATION_ID, "414312341242", "ba123", false);

    Assertions.assertThat(failAccountSummaryEntity.getTransactionSyncedAt()).isEqualTo(TRANSACTION_SYNCED_AT);
  }

  private void setupServerDepositAccountTransactionsSinglePage() {

    // accountNum: 1234567890, dateRange: 20200901 ~ 20201130, dataSize: 2
    wiremock.stubFor(post(urlMatching("/accounts/deposit/transactions"))
        .withRequestBody(equalToJson(readText("classpath:mock/bank/request/BA04_001_single_page_00.json")))
        .willReturn(
            aResponse()
                .withFixedDelay(1000)
                .withStatus(HttpStatus.OK.value())
                .withHeader("Content-Type", ContentType.APPLICATION_JSON.toString())
                .withBody(readText("classpath:mock/bank/response/BA04_001_single_page_00.json"))));

    // accountNum: 1234567890, dateRange: 20200901 ~ 20201130, dataSize: 0
    wiremock.stubFor(post(urlMatching("/accounts/deposit/transactions"))
        .withRequestBody(equalToJson(readText("classpath:mock/bank/request/BA04_001_single_page_01.json")))
        .willReturn(
            aResponse()
                .withFixedDelay(1000)
                .withStatus(HttpStatus.OK.value())
                .withHeader("Content-Type", ContentType.APPLICATION_JSON.toString())
                .withBody(readText("classpath:mock/bank/response/BA04_001_single_page_01.json"))));

    // accountNum: 1234567890, dateRange: 20200601 ~ 20200831, dataSize: 0
    wiremock.stubFor(post(urlMatching("/accounts/deposit/transactions"))
        .withRequestBody(equalToJson(readText("classpath:mock/bank/request/BA04_001_single_page_02.json")))
        .willReturn(
            aResponse()
                .withFixedDelay(1000)
                .withStatus(HttpStatus.OK.value())
                .withHeader("Content-Type", ContentType.APPLICATION_JSON.toString())
                .withBody(readText("classpath:mock/bank/response/BA04_001_single_page_01.json"))));

    // accountNum: 1234567890, dateRange: 20200327 ~ 20200531, dataSize: 0
    wiremock.stubFor(post(urlMatching("/accounts/deposit/transactions"))
        .withRequestBody(equalToJson(readText("classpath:mock/bank/request/BA04_001_single_page_03.json")))
        .willReturn(
            aResponse()
                .withFixedDelay(1000)
                .withStatus(HttpStatus.OK.value())
                .withHeader("Content-Type", ContentType.APPLICATION_JSON.toString())
                .withBody(readText("classpath:mock/bank/response/BA04_001_single_page_01.json"))));

    // accountNum: 414312341242, dateRange: 20201201 ~ 20210328, dataSize: 2
    wiremock.stubFor(post(urlMatching("/accounts/deposit/transactions"))
        .withRequestBody(equalToJson(readText("classpath:mock/bank/request/BA04_002_single_page_00.json")))
        .willReturn(
            aResponse()
                .withFixedDelay(1000)
                .withStatus(HttpStatus.OK.value())
                .withHeader("Content-Type", ContentType.APPLICATION_JSON.toString())
                .withBody(readText("classpath:mock/bank/response/BA04_002_single_page_00.json"))));
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
            .isForeignDeposit(false)
            .isConsent(true)
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
            .isForeignDeposit(false)
            .isConsent(true)
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
            .isForeignDeposit(false)
            .isConsent(false)
            .prodName("자유입출식 계좌")
            .build()
    );
  }
}
