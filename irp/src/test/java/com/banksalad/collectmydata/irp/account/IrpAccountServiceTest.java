package com.banksalad.collectmydata.irp.account;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.WireMockSpring;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.util.DateUtil;
import com.banksalad.collectmydata.finance.common.db.entity.UserSyncStatusEntity;
import com.banksalad.collectmydata.finance.common.db.repository.UserSyncStatusRepository;
import com.banksalad.collectmydata.irp.collect.Apis;
import com.banksalad.collectmydata.irp.collect.Executions;
import com.banksalad.collectmydata.irp.common.db.entity.IrpAccountBasicEntity;
import com.banksalad.collectmydata.irp.common.db.entity.IrpAccountBasicHistoryEntity;
import com.banksalad.collectmydata.irp.common.db.entity.IrpAccountSummaryEntity;
import com.banksalad.collectmydata.irp.common.db.repository.IrpAccountBasicHistoryRepository;
import com.banksalad.collectmydata.irp.common.db.repository.IrpAccountBasicRepository;
import com.banksalad.collectmydata.irp.common.db.repository.IrpAccountSummaryRepository;
import com.banksalad.collectmydata.irp.common.mapper.IrpAccountBasicHistoryMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import lombok.RequiredArgsConstructor;
import org.apache.http.entity.ContentType;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.CONSENT_ID;
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.SYNC_REQUEST_ID;
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
@DisplayName("6.1.4 개인형 IRP 계좌 기본정보 조회")
@Disabled("이전 비즈니스 로직 검증을 위한 테스트 케이스")
class IrpAccountServiceTest {

  private static final Long BANKSALAD_USER_ID = 1L;
  private static final String ORGANIZATION_ID = "organizationId";
  private static final String ORGANIZATION_HOST = "http://localhost";
  private static final long SEARCH_TIMESTAMP = 100L;
  private static final long BASIC_SEARCH_TIMESTAMP = 1000L;

  public static WireMockServer wiremock = new WireMockServer(WireMockSpring.options().dynamicPort());

  private final UserSyncStatusRepository userSyncStatusRepository;

  private final IrpAccountService irpAccountService;

  private final IrpAccountSummaryRepository accountSummaryRepository;

  private final IrpAccountBasicRepository accountBasicRepository;

  private final IrpAccountBasicHistoryRepository accountBasicHistoryRepository;

  private final IrpAccountBasicHistoryMapper accountBasicHistoryMapper = Mappers
      .getMapper(IrpAccountBasicHistoryMapper.class);

  private IrpAccountSummaryEntity accountSummaryEntity1;
  private IrpAccountSummaryEntity accountSummaryEntity2;
  private IrpAccountBasicEntity expectedAccountBasicEntity;

  private ExecutionContext executionContext;
  private LocalDateTime now;

  @AfterAll
  public static void clean() {
    wiremock.shutdown();
  }

  @BeforeEach
  public void setupClass() {

    now = LocalDateTime.now(DateUtil.UTC_ZONE_ID);

    accountSummaryEntity1 = IrpAccountSummaryEntity.builder()
        .consentId(CONSENT_ID)
        .syncRequestId(SYNC_REQUEST_ID)
        .syncedAt(now)
        .banksaladUserId(BANKSALAD_USER_ID)
        .organizationId(ORGANIZATION_ID)
        .accountNum("100246541123")
        .accountStatus("01")
        .basicSearchTimestamp(BASIC_SEARCH_TIMESTAMP)
        .detailSearchTimestamp(0L)
        .transactionSyncedAt(null)
        .isConsent(true)
        .prodName("개인형 IRP 계좌1")
        .seqno("a123")
        .build();
    accountSummaryEntity2 = IrpAccountSummaryEntity.builder()
        .consentId(CONSENT_ID)
        .syncRequestId(SYNC_REQUEST_ID)
        .syncedAt(now)
        .banksaladUserId(BANKSALAD_USER_ID)
        .organizationId(ORGANIZATION_ID)
        .accountNum("234246541143")
        .accountStatus("01")
        .basicSearchTimestamp(0L)
        .detailSearchTimestamp(0L)
        .transactionSyncedAt(null)
        .isConsent(true)
        .prodName("개인형 IRP 계좌2")
        .seqno("a124")
        .build();

    wiremock.start();

    expectedAccountBasicEntity = IrpAccountBasicEntity.builder()
        .consentId(CONSENT_ID)
        .syncRequestId(SYNC_REQUEST_ID)
        .syncedAt(now)
        .banksaladUserId(BANKSALAD_USER_ID)
        .organizationId(ORGANIZATION_ID)
        .accountNum("100246541123")
        .seqno("a123")
        .accumAmt(new BigDecimal("10.123"))
        .evalAmt(new BigDecimal("11.123"))
        .employerAmt(new BigDecimal("12.123"))
        .employeeAmt(new BigDecimal("13.123"))
        .issueDate("20200204")
        .firstDepositDate("20200204")
        .build();

    executionContext = ExecutionContext.builder()
        .consentId(CONSENT_ID)
        .syncRequestId(SYNC_REQUEST_ID)
        .banksaladUserId(BANKSALAD_USER_ID)
        .organizationId(ORGANIZATION_ID)
        .accessToken("test")
        .organizationHost(ORGANIZATION_HOST + ":" + wiremock.port())
        .organizationCode("020")
        .executionRequestId(UUID.randomUUID().toString())
        .syncStartedAt(now)
        .build();
  }

  @AfterEach
  public void after() {
    wiremock.resetAll();
  }

  @Test
  @DisplayName("1. 존재하지 않는 계좌번호")
  void getIrpAccountBasics_notExistedAccount() {

    /* api mock server */
    setupMockServer(HttpStatus.NOT_FOUND, "IR02_001_single_page_00.json");

    saveUserSyncStatus();

    /* save mock account summaries */
    accountSummaryRepository.save(accountSummaryEntity1);

    irpAccountService.getIrpAccountBasics(executionContext);
    assertEquals(0, accountBasicRepository.count());

    assertUserSyncStatusSyncedAt();
    assertUserSyncStatusSearchTimestamp(0);

    assertEquals(1, accountSummaryRepository.count());
    assertEquals(0, accountBasicRepository.count());
    assertEquals(0, accountBasicHistoryRepository.count());

    List<IrpAccountSummaryEntity> accountSummaryEntities = accountSummaryRepository.findAll();
    IrpAccountSummaryEntity accountSummaryEntity = accountSummaryEntities.get(0);
    assertEquals(executionContext.getSyncStartedAt(), accountSummaryEntity.getSyncedAt());
    assertEquals(BASIC_SEARCH_TIMESTAMP, accountSummaryEntity.getBasicSearchTimestamp());
    assertEquals("40402", accountSummaryEntity.getBasicResponseCode());
  }

  @Test
  @DisplayName("2. 기존 0건 + 추가 2건")
  void getIrpAccountBasics_addTwoBasics() {

    /* api mock server */
    setupMockServer(HttpStatus.OK, "IR02_002_single_page_00.json");
    setupMockServer(HttpStatus.OK, "IR02_002_single_page_01.json");

    accountSummaryEntity1.setBasicSearchTimestamp(0L);
    accountSummaryEntity2.setBasicSearchTimestamp(0L);
    List<IrpAccountSummaryEntity> mockAccountSummaryEntities = List
        .of(accountSummaryEntity1, accountSummaryEntity2);

    /* save mock account summaries */
    accountSummaryRepository.saveAll(mockAccountSummaryEntities);

    irpAccountService.getIrpAccountBasics(executionContext);

    assertEquals(2, accountSummaryRepository.count());
    assertEquals(2, accountBasicRepository.count());
    assertEquals(2, accountBasicHistoryRepository.count());

    assertUserSyncStatusSyncedAt();
    assertUserSyncStatusSearchTimestamp(0); // upsert성공시 0으로 timestamp를 update

    List<IrpAccountBasicEntity> accountBasicEntities = accountBasicRepository.findAll();
    accountBasicEntities
        .forEach(irpAccountBasicEntity -> assertEquals(irpAccountBasicEntity.getSyncedAt(), now));

    List<IrpAccountSummaryEntity> accountSummaryEntities = accountSummaryRepository.findAll();
    for (int i = 0; i < accountSummaryEntities.size(); i++) {

      IrpAccountSummaryEntity accountSummaryEntity = accountSummaryEntities.get(i);
      assertEquals(BASIC_SEARCH_TIMESTAMP * (i + 1), accountSummaryEntity.getBasicSearchTimestamp());
      assertNull(accountSummaryEntity.getBasicResponseCode());
    }
  }

  @Test
  @DisplayName("3. 기존 1건 + 추가 1건")
  void getIrpAccountBasics_ZeroAddOneAccount() {

    /* api mock server */
    setupMockServer(HttpStatus.OK, "IR02_003_single_page_00.json");
    setupMockServer(HttpStatus.OK, "IR02_003_single_page_01.json");

    saveUserSyncStatus();

    /* save mock account summaries */
    accountSummaryEntity1.setBasicSearchTimestamp(BASIC_SEARCH_TIMESTAMP);  // previous selection
    accountSummaryEntity2.setBasicSearchTimestamp(0L);  // not selection
    List<IrpAccountSummaryEntity> mockAccountSummaryEntities = List
        .of(accountSummaryEntity1, accountSummaryEntity2);
    accountSummaryRepository.saveAll(mockAccountSummaryEntities);

    /* save mock account basics and history */
    saveAccountBasicsAndHistorySource();

    irpAccountService.getIrpAccountBasics(executionContext);

    assertEquals(2, accountSummaryRepository.count());
    assertEquals(2, accountBasicRepository.count());
    assertEquals(2, accountBasicHistoryRepository.count());

    assertUserSyncStatusSyncedAt();
    assertUserSyncStatusSearchTimestamp(0); // upsert성공시 0으로 timestamp를 update

    List<IrpAccountSummaryEntity> accountSummaryEntities = accountSummaryRepository.findAll();

    for (IrpAccountSummaryEntity accountSummaryEntity : accountSummaryEntities) {

//    기본 정보를 이전에 조회한 적이 없는 경우는 mock response의 searchTimestamp를 2000으로 설정
      long expectedSearchTimestamp = accountSummaryEntity.getBasicSearchTimestamp();
      if (expectedSearchTimestamp != BASIC_SEARCH_TIMESTAMP) {
        expectedSearchTimestamp = BASIC_SEARCH_TIMESTAMP * 2;
      }

      assertEquals(expectedSearchTimestamp, accountSummaryEntity.getBasicSearchTimestamp());
      assertNull(accountSummaryEntity.getBasicResponseCode());
    }

    List<IrpAccountBasicEntity> accountBasicEntities = accountBasicRepository.findAll();
    List<IrpAccountBasicHistoryEntity> accountBasicHistoryEntities = accountBasicHistoryRepository.findAll();

    assertAccountBasicEntitySyncedAt(accountBasicEntities, accountBasicHistoryEntities);

    BigDecimal notModifiedAccumAmt = new BigDecimal("10.123");
    BigDecimal modifiedAccumAmt = notModifiedAccumAmt.add(new BigDecimal("10"));
    assertAccountBasicEntity(accountBasicEntities.get(0), notModifiedAccumAmt);
    assertAccountBasicEntity(accountBasicEntities.get(1), modifiedAccumAmt);

    assertAccountBasicHistoryEntity(accountBasicHistoryEntities.get(0), notModifiedAccumAmt);
    assertAccountBasicHistoryEntity(accountBasicHistoryEntities.get(1), modifiedAccumAmt);
  }

  @Test
  @DisplayName("4. 기존 1건 + 동일 1건")
  void getIrpAccountBasics_SameOneAccount() {

    /* api mock server */
    setupMockServer(HttpStatus.OK, "IR02_004_single_page_00.json");

    saveUserSyncStatus();

    /* save mock account summaries */
    accountSummaryEntity1.setBasicSearchTimestamp(BASIC_SEARCH_TIMESTAMP);  // previous selection
    accountSummaryRepository.save(accountSummaryEntity1);

    /* save mock account basics and history */
    saveAccountBasicsAndHistorySource();

    irpAccountService.getIrpAccountBasics(executionContext);

    assertEquals(1, accountSummaryRepository.count());
    assertEquals(1, accountBasicRepository.count());
    assertEquals(1, accountBasicHistoryRepository.count());

    assertUserSyncStatusSyncedAt();
    assertUserSyncStatusSearchTimestamp(0); // upsert성공시 0으로 timestamp를 update

    List<IrpAccountSummaryEntity> accountSummaryEntities = accountSummaryRepository.findAll();
    IrpAccountSummaryEntity accountSummaryEntity = accountSummaryEntities.get(0);

    assertEquals(BASIC_SEARCH_TIMESTAMP, accountSummaryEntity.getBasicSearchTimestamp());
    assertNull(accountSummaryEntity.getBasicResponseCode());

    assertAccountBasicEntitySyncedAt(accountBasicRepository.findAll(), accountBasicHistoryRepository.findAll());
  }

  @Test
  @DisplayName("5. 기존 1건 + 변경 1건")
  void getIrpAccountBasics_ModifiedOneAccount() {

    /* api mock server */
    setupMockServer(HttpStatus.OK, "IR02_005_single_page_00.json");

    saveUserSyncStatus();

    /* save mock account summaries */
    accountSummaryEntity1.setBasicSearchTimestamp(BASIC_SEARCH_TIMESTAMP);  // previous selection
    accountSummaryRepository.save(accountSummaryEntity1);

    /* save mock account basics and history */
    saveAccountBasicsAndHistorySource();

    irpAccountService.getIrpAccountBasics(executionContext);

    assertEquals(1, accountSummaryRepository.count());
    assertEquals(1, accountBasicRepository.count());
    assertEquals(2, accountBasicHistoryRepository.count());

    assertUserSyncStatusSyncedAt();
    assertUserSyncStatusSearchTimestamp(0); // upsert성공시 0으로 timestamp를 update

    List<IrpAccountSummaryEntity> accountSummaryEntities = accountSummaryRepository.findAll();
    IrpAccountSummaryEntity accountSummaryEntity = accountSummaryEntities.get(0);

    assertEquals(BASIC_SEARCH_TIMESTAMP * 2, accountSummaryEntity.getBasicSearchTimestamp());
    assertNull(accountSummaryEntity.getBasicResponseCode());

    List<IrpAccountBasicEntity> accountBasicEntities = accountBasicRepository.findAll();
    List<IrpAccountBasicHistoryEntity> accountBasicHistoryEntities = accountBasicHistoryRepository.findAll();

    assertAccountBasicEntitySyncedAt(accountBasicEntities, accountBasicHistoryEntities);

    BigDecimal notModifiedAccumAmt = new BigDecimal("10.123");
    BigDecimal modifiedAccumAmt = notModifiedAccumAmt.add(new BigDecimal("10"));
    assertAccountBasicEntity(accountBasicEntities.get(0), modifiedAccumAmt);

    assertAccountBasicHistoryEntity(accountBasicHistoryEntities.get(0),
        notModifiedAccumAmt); // not modified previous source
    assertAccountBasicHistoryEntity(accountBasicHistoryEntities.get(1), modifiedAccumAmt); // modified
  }

//  @Test
//  @Disabled("UP_TO_DATE에 대한 명세 확인 필요 및 추가 0건에 대해 처리 미고려")
//  @DisplayName("6. 기존 1건 + 추가 0건")
//  void getIrpAccountBasics_NoAddition() {
//
//    /* api mock server */
//    setupMockServer(HttpStatus.OK, "IR02_006_single_page_00.json");
//
//    saveUserSyncStatus();
//
//    /* save mock account summaries */
//    accountSummaryEntity1.setBasicSearchTimestamp(BASIC_SEARCH_TIMESTAMP);  // previous selection
//    accountSummaryRepository.save(accountSummaryEntity1);
//
//    /* save mock account basics and history */
//    saveAccountBasicsAndHistorySource();
//
//    irpAccountService.getIrpAccountBasics(executionContext);
//
//    assertEquals(1, accountSummaryRepository.count());
//    assertEquals(1, accountBasicRepository.count());
//    assertEquals(1, accountBasicHistoryRepository.count());
//
//    assertUserSyncStatusSyncedAt();
//    assertUserSyncStatusSearchTimestamp(300); // 변경이 없으므로 이전 값 그대로
//
//    assertAccountBasicEntitySyncedAt(accountBasicRepository.findAll(), accountBasicHistoryRepository.findAll());
//
//    List<IrpAccountSummaryEntity> accountSummaryEntities = accountSummaryRepository.findAll();
//    IrpAccountSummaryEntity accountSummaryEntity = accountSummaryEntities.get(0);
//
//    assertEquals(BASIC_SEARCH_TIMESTAMP, accountSummaryEntity.getBasicSearchTimestamp());
//    assertNull(accountSummaryEntity.getBasicResponseCode());
//  }

  private void setupMockServer(HttpStatus httpStatus, String fileName) {
    wiremock.stubFor(post(urlMatching("/irps/basic"))
        .withRequestBody(equalToJson(readText("classpath:mock/irp/request/" + fileName)))
        .willReturn(
            aResponse()
                .withStatus(httpStatus.value())
                .withHeader("Content-Type", ContentType.APPLICATION_JSON.toString())
                .withBody(readText("classpath:mock/irp/response/" + fileName))));
  }

  private void saveAccountBasicsAndHistorySource() {

    accountBasicRepository.save(expectedAccountBasicEntity);
    IrpAccountBasicHistoryEntity accountBasicHistoryEntity = accountBasicHistoryMapper
        .toHistoryEntity(expectedAccountBasicEntity);
    accountBasicHistoryRepository.save(accountBasicHistoryEntity);
  }

  private UserSyncStatusEntity assertUserSyncStatusSearchTimestamp(long searchTimestamp) {

    UserSyncStatusEntity userSyncStatusEntity = getUserSyncStatusEntity(executionContext);
    assertEquals(searchTimestamp, userSyncStatusEntity.getSearchTimestamp());

    return userSyncStatusEntity;
  }

  private UserSyncStatusEntity assertUserSyncStatusSyncedAt() {

    UserSyncStatusEntity userSyncStatusEntity = getUserSyncStatusEntity(executionContext);
    assertThat(userSyncStatusEntity.getSyncedAt()).isEqualTo(executionContext.getSyncStartedAt());

    return userSyncStatusEntity;
  }

  private UserSyncStatusEntity getUserSyncStatusEntity(ExecutionContext executionContext) {

    return userSyncStatusRepository
        .findByBanksaladUserIdAndOrganizationIdAndApiId(executionContext.getBanksaladUserId(),
            executionContext.getOrganizationId(), Executions.irp_get_basic.getApi().getId()).orElse(null);
  }

  private void assertAccountBasicEntity(IrpAccountBasicEntity accountBasicEntity,
      BigDecimal notModifiedAccumAmt) {
    assertEquals(notModifiedAccumAmt, accountBasicEntity.getAccumAmt());
  }

  private void assertAccountBasicHistoryEntity(IrpAccountBasicHistoryEntity accountBasicHistoryEntity,
      BigDecimal notModifiedAccumAmt) {
    assertEquals(notModifiedAccumAmt, accountBasicHistoryEntity.getAccumAmt());
  }

  private void assertAccountBasicEntitySyncedAt(List<IrpAccountBasicEntity> accountBasicEntities,
      List<IrpAccountBasicHistoryEntity> accountBasicHistoryEntities) {

    accountBasicEntities
        .forEach(irpAccountBasicEntity -> assertEquals(irpAccountBasicEntity.getSyncedAt(), now));

    accountBasicHistoryEntities
        .forEach(irpAccountBasicHistoryEntity -> assertEquals(irpAccountBasicHistoryEntity.getSyncedAt(), now));
  }

  private void saveUserSyncStatus() {
    userSyncStatusRepository.save(
        UserSyncStatusEntity.builder()
            .syncedAt(LocalDateTime.now())
            .searchTimestamp(SEARCH_TIMESTAMP)
            .banksaladUserId(BANKSALAD_USER_ID)
            .organizationId(ORGANIZATION_ID)
            .apiId(Apis.irp_get_basic.getId())
            .build()
    );
  }
}

