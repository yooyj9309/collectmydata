package com.banksalad.collectmydata.irp.account;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.WireMockSpring;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.crypto.HashUtil;
import com.banksalad.collectmydata.finance.api.transaction.TransactionApiService;
import com.banksalad.collectmydata.finance.api.transaction.TransactionRequestHelper;
import com.banksalad.collectmydata.finance.api.transaction.TransactionResponseHelper;
import com.banksalad.collectmydata.finance.common.db.entity.UserSyncStatusEntity;
import com.banksalad.collectmydata.finance.common.db.repository.UserSyncStatusRepository;
import com.banksalad.collectmydata.irp.collect.Apis;
import com.banksalad.collectmydata.irp.collect.Executions;
import com.banksalad.collectmydata.irp.common.db.entity.IrpAccountSummaryEntity;
import com.banksalad.collectmydata.irp.common.db.entity.IrpAccountTransactionEntity;
import com.banksalad.collectmydata.irp.common.db.repository.IrpAccountSummaryRepository;
import com.banksalad.collectmydata.irp.common.db.repository.IrpAccountTransactionRepository;
import com.banksalad.collectmydata.irp.common.dto.IrpAccountSummary;
import com.banksalad.collectmydata.irp.common.dto.IrpAccountTransaction;
import com.banksalad.collectmydata.irp.common.dto.IrpAccountTransactionRequest;
import com.github.tomakehurst.wiremock.WireMockServer;
import lombok.RequiredArgsConstructor;
import org.apache.http.entity.ContentType;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static com.banksalad.collectmydata.irp.util.FileUtil.readText;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
@RequiredArgsConstructor
@DisplayName("6.1.6 개인형 IRP 계좌 거래내역 조회")
@Disabled("이전 비즈니스 로직 검증을 위한 테스트 케이스")
class IrpAccountTransactionServiceTest {

  private static final Long BANKSALAD_USER_ID = 1L;
  private static final String ORGANIZATION_ID = "organizationId";
  private static final String ORGANIZATION_HOST = "http://localhost";
  private static final long SEARCH_TIMESTAMP = 100L;

  public static WireMockServer wiremock = new WireMockServer(WireMockSpring.options().dynamicPort());

  private UserSyncStatusRepository userSyncStatusRepository;

  private IrpAccountSummaryRepository accountSummaryRepository;

  private TransactionApiService<IrpAccountSummary, IrpAccountTransactionRequest, IrpAccountTransaction> accountTransactionApiService;

  private TransactionRequestHelper<IrpAccountSummary, IrpAccountTransactionRequest> accountTransactionRequestHelper;

  private TransactionResponseHelper<IrpAccountSummary, IrpAccountTransaction> accountTransactionResponseHelper;

  private IrpAccountTransactionRepository accountTransactionRepository;

  private IrpAccountSummaryEntity accountSummaryEntity;
  private IrpAccountTransactionEntity expectedAccountTransactionEntity;

  private ExecutionContext executionContext;
  private LocalDateTime previousTransactionSyncedAt;
  private LocalDateTime requestTransactionSyncedAt;

  @AfterAll
  public static void clean() {
    wiremock.shutdown();
  }

  @BeforeEach
  public void setupClass() {

    previousTransactionSyncedAt = LocalDateTime.of(2021, 4, 11, 0, 0, 0);
    requestTransactionSyncedAt = LocalDateTime.of(2021, 4, 12, 0, 0, 0);

    accountSummaryEntity = IrpAccountSummaryEntity.builder()
        .banksaladUserId(BANKSALAD_USER_ID)
        .organizationId(ORGANIZATION_ID)
        .syncedAt(previousTransactionSyncedAt)
        .accountNum("100246541123")
        .accountStatus("01")
        .basicSearchTimestamp(0L)
        .detailSearchTimestamp(0L)
        .transactionSyncedAt(previousTransactionSyncedAt)
        .isConsent(true)
        .prodName("상품명1")
        .seqno("a123")
        .build();

    wiremock.start();

    expectedAccountTransactionEntity = IrpAccountTransactionEntity
        .builder()
        .syncedAt(requestTransactionSyncedAt)
        .banksaladUserId(BANKSALAD_USER_ID)
        .organizationId(ORGANIZATION_ID)
        .accountNum("100246541123")
        .seqno("a123")
        .transactionYearMonth(202104)
        .transDtime("20210402102000")
        .transAmt(new BigDecimal(10000))
        .transType("01").build();
    expectedAccountTransactionEntity.setUniqueTransNo(generateUniqueTransNo(expectedAccountTransactionEntity));

    executionContext = ExecutionContext.builder()
        .syncRequestId(UUID.randomUUID().toString())
        .banksaladUserId(BANKSALAD_USER_ID)
        .organizationId(ORGANIZATION_ID)
        .executionRequestId(UUID.randomUUID().toString())
        .organizationCode("020")
        .organizationHost(ORGANIZATION_HOST + ":" + wiremock.port())
        .accessToken("test")
        .syncStartedAt(requestTransactionSyncedAt)
        .build();
  }

  @AfterEach
  public void after() {
    wiremock.resetAll();
  }

  private String generateUniqueTransNo(IrpAccountTransactionEntity irpAccountTransactionEntity) {

//  TODO: 거래내역 아래 필드만으로 구분 불가(?)
    String transDtime = irpAccountTransactionEntity.getTransDtime();  // 거래일시
    String transType = irpAccountTransactionEntity.getTransType();    // 거래유형(01:입금, 02:지급)
    String transAmt = irpAccountTransactionEntity.getTransAmt().toString(); // 거래금액

    return HashUtil.hashCat(transDtime, transType, transAmt);
  }

  @Test
  @DisplayName("1. 존재하지 않는 계좌번호")
  void listTransactions_notExistedAccount() {

    /* api mock server */
    setupMockServer(HttpStatus.NOT_FOUND, "IR04_001_single_page_00.json");

    saveUserSyncStatus();

    /* save mock account summaries */
    accountSummaryRepository.save(accountSummaryEntity);

    accountTransactionApiService
        .listTransactions(executionContext, Executions.irp_get_transactions,
            accountTransactionRequestHelper, accountTransactionResponseHelper);

    assertEquals(0, accountTransactionRepository.count());

    assertUserSyncStatusSyncedAt();
    assertUserSyncStatusSearchTimestamp(0);

    assertEquals(1, accountSummaryRepository.count());
    assertEquals(0, accountTransactionRepository.count());

    List<IrpAccountSummaryEntity> accountSummaryEntities = accountSummaryRepository.findAll();
    IrpAccountSummaryEntity accountSummaryEntity = accountSummaryEntities.get(0);
    assertEquals(previousTransactionSyncedAt, accountSummaryEntity.getSyncedAt());
    assertEquals(previousTransactionSyncedAt, accountSummaryEntity.getTransactionSyncedAt());
    assertEquals("40402", accountSummaryEntity.getTransactionResponseCode());
  }

  @Test
  @DisplayName("2. 너무 많은 요청")
  void listTransactions_TooManyRequests() {

    /* api mock server */
    setupMockServer(HttpStatus.TOO_MANY_REQUESTS, "IR04_002_single_page_00.json");

    saveUserSyncStatus();

    /* save mock account summaries */
    accountSummaryRepository.save(accountSummaryEntity);

    accountTransactionApiService
        .listTransactions(executionContext, Executions.irp_get_transactions,
            accountTransactionRequestHelper, accountTransactionResponseHelper);
    assertEquals(0, accountTransactionRepository.count());

    assertUserSyncStatusSyncedAt();
    assertUserSyncStatusSearchTimestamp(0);

    assertEquals(1, accountSummaryRepository.count());
    assertEquals(0, accountTransactionRepository.count());

    List<IrpAccountSummaryEntity> accountSummaryEntities = accountSummaryRepository.findAll();
    IrpAccountSummaryEntity accountSummaryEntity = accountSummaryEntities.get(0);
    assertEquals(previousTransactionSyncedAt, accountSummaryEntity.getSyncedAt());
    assertEquals(previousTransactionSyncedAt, accountSummaryEntity.getTransactionSyncedAt());
    assertEquals("42901", accountSummaryEntity.getTransactionResponseCode());
  }

  @Test
  @DisplayName("3. 기존 0건 + 추가 3건")
  void listTransactions_addThreeTransactions() {

    /* api mock server */
    setupMockServer(HttpStatus.OK, "IR04_003_multi_page_00.json", "IR04_003_multi_page_01.json");

    /* save mock account summaries */
    accountSummaryRepository.save(accountSummaryEntity);
    accountSummaryEntity.setTransactionSyncedAt(null);

    accountTransactionApiService.listTransactions(executionContext, Executions.irp_get_transactions,
        accountTransactionRequestHelper, accountTransactionResponseHelper);

    assertEquals(1, accountSummaryRepository.count());
    assertEquals(3, accountTransactionRepository.count());

    assertUserSyncStatusSyncedAt();
    assertUserSyncStatusSearchTimestamp(0); // upsert성공시 0으로 timestamp를 update

    List<IrpAccountSummaryEntity> accountSummaryEntities = accountSummaryRepository.findAll();
    IrpAccountSummaryEntity accountSummaryEntity = accountSummaryEntities.get(0);
    assertEquals(previousTransactionSyncedAt, accountSummaryEntity.getSyncedAt());
    assertEquals(requestTransactionSyncedAt, accountSummaryEntity.getTransactionSyncedAt());
    assertNull(accountSummaryEntity.getTransactionResponseCode());

    accountTransactionRepository.findAll()
        .forEach(accountTransactionEntity -> assertEquals(requestTransactionSyncedAt,
            accountTransactionEntity.getSyncedAt()));
  }

  @Test
  @DisplayName("4. 기존 1건 + 추가 1건")
  void listTransactions_ZeroAddOneTransaction() {

    /* api mock server */
    setupMockServer(HttpStatus.OK, "IR04_004_single_page_00.json");

    saveUserSyncStatus();

    /* save mock account summaries */
    accountSummaryRepository.save(accountSummaryEntity);

    /* save mock account transactions */
    IrpAccountTransactionEntity expectedModifiedAccountTransactionEntity = IrpAccountTransactionEntity
        .builder()
        .syncedAt(requestTransactionSyncedAt)
        .banksaladUserId(BANKSALAD_USER_ID)
        .organizationId(ORGANIZATION_ID)
        .accountNum("100246541123")
        .seqno("a123")
        .transactionYearMonth(202104)
        .transDtime("20210402202000")
        .transAmt(new BigDecimal(20000))
        .transType("02").build();
    expectedModifiedAccountTransactionEntity
        .setUniqueTransNo(generateUniqueTransNo(expectedModifiedAccountTransactionEntity));
    accountTransactionRepository.save(expectedAccountTransactionEntity);

    expectedAccountTransactionEntity.setSyncedAt(previousTransactionSyncedAt);
    accountTransactionRepository.save(expectedModifiedAccountTransactionEntity);

    accountTransactionApiService.listTransactions(executionContext, Executions.irp_get_transactions,
        accountTransactionRequestHelper, accountTransactionResponseHelper);

    assertEquals(1, accountSummaryRepository.count());
    assertEquals(2, accountTransactionRepository.count());

    assertUserSyncStatusSyncedAt();
    assertUserSyncStatusSearchTimestamp(0); // upsert성공시 0으로 timestamp를 update

    List<IrpAccountSummaryEntity> accountSummaryEntities = accountSummaryRepository.findAll();
    IrpAccountSummaryEntity accountSummaryEntity = accountSummaryEntities.get(0);

    assertEquals(previousTransactionSyncedAt, accountSummaryEntity.getSyncedAt());
    assertEquals(requestTransactionSyncedAt, accountSummaryEntity.getTransactionSyncedAt());
    assertNull(accountSummaryEntity.getTransactionResponseCode());

    List<IrpAccountTransactionEntity> accountTransactionEntities = accountTransactionRepository.findAll();

    BigDecimal notModifiedTransAmt = new BigDecimal("10000");
    BigDecimal modifiedTransAmt = notModifiedTransAmt.multiply(new BigDecimal("2"));

    IrpAccountTransactionEntity notModifiedTransactionEntity = accountTransactionEntities.get(0);
    assertAccountTransactionEntity(notModifiedTransactionEntity, notModifiedTransAmt);
    assertEquals(previousTransactionSyncedAt, notModifiedTransactionEntity.getSyncedAt());

    IrpAccountTransactionEntity modifiedAccountTransactionEntity = accountTransactionEntities.get(1);
    assertAccountTransactionEntity(modifiedAccountTransactionEntity, modifiedTransAmt);
    assertEquals(requestTransactionSyncedAt, modifiedAccountTransactionEntity.getSyncedAt());
  }

  @Test
  @DisplayName("5. 기존 1건 + 동일 1건")
  void listTransactions_SameOneTransaction() {

    /* api mock server */
    setupMockServer(HttpStatus.OK, "IR04_005_single_page_00.json");

    saveUserSyncStatus();

    /* save mock account summaries */
    accountSummaryRepository.save(accountSummaryEntity);

    /* save mock account transactions */
    expectedAccountTransactionEntity.setSyncedAt(previousTransactionSyncedAt);
    accountTransactionRepository.save(expectedAccountTransactionEntity);

    accountTransactionApiService.listTransactions(executionContext, Executions.irp_get_transactions,
        accountTransactionRequestHelper, accountTransactionResponseHelper);

    assertEquals(1, accountSummaryRepository.count());
    assertEquals(1, accountTransactionRepository.count());

    assertUserSyncStatusSyncedAt();
    assertUserSyncStatusSearchTimestamp(0); // upsert성공시 0으로 timestamp를 update

    List<IrpAccountSummaryEntity> accountSummaryEntities = accountSummaryRepository.findAll();
    IrpAccountSummaryEntity accountSummaryEntity = accountSummaryEntities.get(0);

    assertEquals(requestTransactionSyncedAt, accountSummaryEntity.getTransactionSyncedAt());
    assertNull(accountSummaryEntity.getTransactionResponseCode());

    List<IrpAccountTransactionEntity> accountTransactionEntities = accountTransactionRepository.findAll();

    BigDecimal notModifiedTransAmt = new BigDecimal("10000");
    IrpAccountTransactionEntity notModifiedTransactionEntity = accountTransactionEntities.get(0);
    assertAccountTransactionEntity(notModifiedTransactionEntity, notModifiedTransAmt);
    assertEquals(previousTransactionSyncedAt, notModifiedTransactionEntity.getSyncedAt());
  }

  @Test
  @DisplayName("6. 기존 0건 + 추가 2건 + 실패 1건")
  void listTransactions_addTwoTransactions_oneFailure() {

    /* api mock server */
    setupMockServer(HttpStatus.OK, "IR04_006_multi_page_00.json");
    setupMockServer(HttpStatus.FORBIDDEN, "IR04_006_multi_page_01.json");

    /* save mock account summaries */
    accountSummaryRepository.save(accountSummaryEntity);
    accountSummaryEntity.setTransactionSyncedAt(null);  // null : fromDate : -5year

    accountTransactionApiService.listTransactions(executionContext, Executions.irp_get_transactions,
        accountTransactionRequestHelper, accountTransactionResponseHelper);

    assertEquals(1, accountSummaryRepository.count());
    assertEquals(2, accountTransactionRepository.count());

    assertUserSyncStatusSyncedAt();
    assertUserSyncStatusSearchTimestamp(0); // upsert성공시 0으로 timestamp를 update

    List<IrpAccountSummaryEntity> accountSummaryEntities = accountSummaryRepository.findAll();
    IrpAccountSummaryEntity accountSummaryEntity = accountSummaryEntities.get(0);
    assertNull(accountSummaryEntity.getTransactionSyncedAt());
    assertEquals("40305", accountSummaryEntity.getTransactionResponseCode());

    accountTransactionRepository.findAll()
        .forEach(accountTransactionEntity -> assertEquals(requestTransactionSyncedAt,
            accountTransactionEntity.getSyncedAt()));
  }

//  @Test
//  @Disabled("UP_TO_DATE에 대한 명세 확인 필요 및 추가 0건에 대해 처리 미고려")
//  @DisplayName("7. 기존 1건 + 추가 0건")
//  void listTransactions_NoAddition() {
//
//    /* api mock server */
//    setupMockServer(HttpStatus.OK, "IR02_006_single_page_00.json");
//
//    saveUserSyncStatus();
//
//    /* save mock account summaries */
//    accountSummaryRepository.save(accountSummaryEntity);
//
//    /* save mock account transactions */
//    accountTransactionRepository.save(expectedAccountTransactionEntity);
//
//    accountTransactionApiService.listTransactions(executionContext, Executions.irp_get_transactions,
//        accountTransactionRequestHelper, accountTransactionResponseHelper);
//
//    assertEquals(1, accountSummaryRepository.count());
//    assertEquals(1, accountTransactionRepository.count());
//
//    assertUserSyncStatusSyncedAt();
//    assertUserSyncStatusSearchTimestamp(300); // 변경이 없으므로 이전 값 그대로
//
//    List<IrpAccountSummaryEntity> accountSummaryEntities = accountSummaryRepository.findAll();
//    IrpAccountSummaryEntity accountSummaryEntity = accountSummaryEntities.get(0);
//
//    assertEquals(previousTransactionSyncedAt, accountSummaryEntity.getTransactionSyncedAt());
//    assertNull(accountSummaryEntity.getTransactionResponseCode());
//  }

  private void setupMockServer(HttpStatus httpStatus, String... fileNames) {

    Arrays.stream(fileNames).forEach(fileName ->

        wiremock.stubFor(post(urlMatching("/irps/transactions"))
            .withRequestBody(equalToJson(readText("classpath:mock/irp/request/" + fileName)))
            .willReturn(
                aResponse()
                    .withStatus(httpStatus.value())
                    .withHeader("Content-Type", ContentType.APPLICATION_JSON.toString())
                    .withBody(readText("classpath:mock/irp/response/" + fileName)))));
  }

  private void assertUserSyncStatusSearchTimestamp(long searchTimestamp) {

    UserSyncStatusEntity userSyncStatusEntity = getUserSyncStatusEntity(executionContext);
    assertEquals(searchTimestamp, userSyncStatusEntity.getSearchTimestamp());
  }

  private void assertUserSyncStatusSyncedAt() {

    UserSyncStatusEntity userSyncStatusEntity = getUserSyncStatusEntity(executionContext);
    assertThat(userSyncStatusEntity.getSyncedAt()).isEqualTo(executionContext.getSyncStartedAt());
  }

  private UserSyncStatusEntity getUserSyncStatusEntity(ExecutionContext executionContext) {

    return userSyncStatusRepository
        .findByBanksaladUserIdAndOrganizationIdAndApiId(executionContext.getBanksaladUserId(),
            executionContext.getOrganizationId(), Executions.irp_get_transactions.getApi().getId()).orElse(null);
  }

  private void assertAccountTransactionEntity(IrpAccountTransactionEntity accountTransactionEntity,
      BigDecimal notModifiedTransAmt) {
    assertEquals(notModifiedTransAmt, accountTransactionEntity.getTransAmt());
  }

  private void saveUserSyncStatus() {
    userSyncStatusRepository.save(
        UserSyncStatusEntity.builder()
            .syncedAt(LocalDateTime.now())
            .searchTimestamp(SEARCH_TIMESTAMP)
            .banksaladUserId(BANKSALAD_USER_ID)
            .organizationId(ORGANIZATION_ID)
            .apiId(Apis.irp_get_transactions.getId())
            .build()
    );
  }
}
