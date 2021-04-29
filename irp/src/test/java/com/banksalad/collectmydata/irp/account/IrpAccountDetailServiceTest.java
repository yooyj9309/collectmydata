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
import com.banksalad.collectmydata.irp.common.db.entity.IrpAccountDetailEntity;
import com.banksalad.collectmydata.irp.common.db.entity.IrpAccountDetailHistoryEntity;
import com.banksalad.collectmydata.irp.common.db.entity.IrpAccountSummaryEntity;
import com.banksalad.collectmydata.irp.common.db.repository.IrpAccountDetailHistoryRepository;
import com.banksalad.collectmydata.irp.common.db.repository.IrpAccountDetailRepository;
import com.banksalad.collectmydata.irp.common.db.repository.IrpAccountSummaryRepository;
import com.banksalad.collectmydata.irp.common.mapper.IrpAccountDetailHistoryMapper;
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
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.CONSENT_ID;
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.ORGANIZATION_CODE;
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
@DisplayName("6.1.5 개인형 IRP 계좌 추가정보 조회")
@Disabled("이전 비즈니스 로직 검증을 위한 테스트 케이스")
class IrpAccountDetailServiceTest {

  private static final Long BANKSALAD_USER_ID = 1L;
  private static final String ORGANIZATION_ID = "organizationId";
  private static final String ORGANIZATION_HOST = "http://localhost";
  private static final long SEARCH_TIMESTAMP = 100L;
  private static final long DETAIL_SEARCH_TIMESTAMP = 1000L;

  public static WireMockServer wiremock = new WireMockServer(WireMockSpring.options().dynamicPort());

  private final UserSyncStatusRepository userSyncStatusRepository;

  private final IrpAccountService irpAccountService;

  private final IrpAccountSummaryRepository accountSummaryRepository;

  private final IrpAccountDetailRepository accountDetailRepository;

  private final IrpAccountDetailHistoryRepository accountDetailHistoryRepository;

  private final IrpAccountDetailHistoryMapper accountDetailHistoryMapper = Mappers
      .getMapper(IrpAccountDetailHistoryMapper.class);

  private IrpAccountSummaryEntity accountSummaryEntity1;
  private IrpAccountDetailEntity expectedAccountDetailEntity;

  private ExecutionContext executionContext;
  private LocalDateTime requestSyncedAt;
  private LocalDateTime previousSyncedAt;

  @AfterAll
  public static void clean() {
    wiremock.shutdown();
  }

  @BeforeEach
  public void setupClass() {

    requestSyncedAt = LocalDateTime.now(DateUtil.UTC_ZONE_ID);
    previousSyncedAt = requestSyncedAt.minusDays(1);

    accountSummaryEntity1 = IrpAccountSummaryEntity.builder()
        .consentId(CONSENT_ID)
        .syncRequestId(SYNC_REQUEST_ID)
        .syncedAt(previousSyncedAt)
        .banksaladUserId(BANKSALAD_USER_ID)
        .organizationId(ORGANIZATION_ID)
        .accountNum("100246541123")
        .seqno("a123")
        .accountStatus("01")
        .basicSearchTimestamp(0L)
        .detailSearchTimestamp(DETAIL_SEARCH_TIMESTAMP)
        .transactionSyncedAt(null)
        .isConsent(true)
        .prodName("개인형 IRP 계좌1")
        .build();

    wiremock.start();

    expectedAccountDetailEntity = IrpAccountDetailEntity.builder()
        .consentId(CONSENT_ID)
        .syncRequestId(SYNC_REQUEST_ID)
        .syncedAt(requestSyncedAt)
        .banksaladUserId(BANKSALAD_USER_ID)
        .organizationId(ORGANIZATION_ID)
        .accountNum("100246541123")
        .seqno("a123")
        .irpDetailNo((short) 0)
        .irpName("irp개별운용상품1")
        .irpType("01")
        .evalAmt(new BigDecimal("10.123"))
        .invPrincipal(new BigDecimal("5000.456"))
        .fundNum(5)
        .openDate("20200228")
        .expDate("20211230")
        .intRate(new BigDecimal("14.3"))
        .build();

    executionContext = ExecutionContext.builder()
        .consentId(CONSENT_ID)
        .syncRequestId(SYNC_REQUEST_ID)
        .banksaladUserId(BANKSALAD_USER_ID)
        .organizationId(ORGANIZATION_ID)
        .accessToken("test")
        .organizationHost(ORGANIZATION_HOST + ":" + wiremock.port())
        .organizationCode(ORGANIZATION_CODE)
        .executionRequestId(UUID.randomUUID().toString())
        .syncStartedAt(requestSyncedAt)
        .build();
  }

  @AfterEach
  public void after() {
    wiremock.resetAll();
  }

  @Test
  @DisplayName("1. 존재하지 않는 계좌번호")
  void listIrpAccountDetails_notExistedAccount() {

    /* api mock server */
    setupMockServer(HttpStatus.NOT_FOUND, "IR03_001_single_page_00.json");

    saveUserSyncStatus();

    /* save mock account summaries */
    accountSummaryRepository.save(accountSummaryEntity1);

    irpAccountService.listIrpAccountDetails(executionContext);
    assertEquals(0, accountDetailRepository.count());

    assertUserSyncStatusSyncedAt();
    assertUserSyncStatusSearchTimestamp(0);

    assertEquals(1, accountSummaryRepository.count());
    assertEquals(0, accountDetailRepository.count());
    assertEquals(0, accountDetailHistoryRepository.count());

    List<IrpAccountSummaryEntity> accountSummaryEntities = accountSummaryRepository.findAll();
    IrpAccountSummaryEntity accountSummaryEntity = accountSummaryEntities.get(0);
    assertEquals(previousSyncedAt, accountSummaryEntity.getSyncedAt());
    assertEquals(DETAIL_SEARCH_TIMESTAMP, accountSummaryEntity.getDetailSearchTimestamp());
    assertEquals("40402", accountSummaryEntity.getDetailResponseCode());
  }

  @Test
  @DisplayName("2. 해지된 계좌 번호")
  void listIrpAccountDetails_canceledAccount() {

    /* api mock server */
    setupMockServer(HttpStatus.NOT_FOUND, "IR03_002_single_page_00.json");

    saveUserSyncStatus();

    /* save mock account summaries */
    accountSummaryRepository.save(accountSummaryEntity1);

    irpAccountService.listIrpAccountDetails(executionContext);
    assertEquals(0, accountDetailRepository.count());

    assertUserSyncStatusSyncedAt();
    assertUserSyncStatusSearchTimestamp(0);

    assertEquals(1, accountSummaryRepository.count());
    assertEquals(0, accountDetailRepository.count());
    assertEquals(0, accountDetailHistoryRepository.count());

    List<IrpAccountSummaryEntity> accountSummaryEntities = accountSummaryRepository.findAll();
    IrpAccountSummaryEntity accountSummaryEntity = accountSummaryEntities.get(0);
    assertEquals(previousSyncedAt, accountSummaryEntity.getSyncedAt());
    assertEquals(DETAIL_SEARCH_TIMESTAMP, accountSummaryEntity.getDetailSearchTimestamp());
    assertEquals("40404", accountSummaryEntity.getDetailResponseCode());
  }

  @Test
  @DisplayName("3. 기존 0건 + 추가 3건")
  void listIrpAccountDetails_addTwoDetails() {

    /* api mock server */
    setupMockServer(HttpStatus.OK, "IR03_003_multi_page_00.json", "IR03_003_multi_page_01.json");

    /* save mock account summaries */
    accountSummaryEntity1.setDetailSearchTimestamp(0L);
    accountSummaryRepository.save(accountSummaryEntity1);

    irpAccountService.listIrpAccountDetails(executionContext);

    assertEquals(1, accountSummaryRepository.count());
    assertEquals(3, accountDetailRepository.count());
    assertEquals(3, accountDetailHistoryRepository.count());

    assertUserSyncStatusSyncedAt();
    assertUserSyncStatusSearchTimestamp(0); // upsert성공시 0으로 timestamp를 update

    assertAccountDetailEntitySyncedAt(accountDetailRepository.findAll(), accountDetailHistoryRepository.findAll());

    List<IrpAccountSummaryEntity> accountSummaryEntities = accountSummaryRepository.findAll();
    IrpAccountSummaryEntity accountSummaryEntity = accountSummaryEntities.get(0);
    assertEquals(DETAIL_SEARCH_TIMESTAMP, accountSummaryEntity.getDetailSearchTimestamp()); // 최초 : 1000, 2page : 2000
    assertNull(accountSummaryEntity.getDetailResponseCode());
  }

  @Test
  @DisplayName("4. 기존 1건 + 추가 1건")
  void listIrpAccountDetails_ZeroAddOneAccount() {

    /* api mock server */
    setupMockServer(HttpStatus.OK, "IR03_004_multi_page_00.json", "IR03_004_multi_page_01.json");

    saveUserSyncStatus();

    /* save mock account summaries */
    accountSummaryEntity1.setDetailSearchTimestamp(DETAIL_SEARCH_TIMESTAMP);
    accountSummaryRepository.save(accountSummaryEntity1);

    /* save mock account details and history */
    saveAccountDetailsAndHistorySource();

    irpAccountService.listIrpAccountDetails(executionContext);

    assertEquals(1, accountSummaryRepository.count());
    assertEquals(2, accountDetailRepository.count());
    assertEquals(3, accountDetailHistoryRepository.count());  // previous(init) + api insert(previous) + api insert(new)

    assertUserSyncStatusSyncedAt();
    assertUserSyncStatusSearchTimestamp(0); // upsert성공시 0으로 timestamp를 update

    List<IrpAccountSummaryEntity> accountSummaryEntities = accountSummaryRepository.findAll();
    IrpAccountSummaryEntity accountSummaryEntity = accountSummaryEntities.get(0);

    assertEquals(DETAIL_SEARCH_TIMESTAMP * 2, accountSummaryEntity.getDetailSearchTimestamp());
    assertNull(accountSummaryEntity.getDetailResponseCode());

    List<IrpAccountDetailEntity> accountDetailEntities = accountDetailRepository.findAll();
    List<IrpAccountDetailHistoryEntity> accountDetailHistoryEntities = accountDetailHistoryRepository.findAll();

    assertAccountDetailEntitySyncedAt(accountDetailEntities, accountDetailHistoryEntities);

    BigDecimal notModifiedAccumAmt = new BigDecimal("10.123");
    BigDecimal modifiedAccumAmt = notModifiedAccumAmt.add(new BigDecimal("10"));
    assertAccountDetailEntity(accountDetailEntities.get(0), notModifiedAccumAmt);
    assertAccountDetailEntity(accountDetailEntities.get(1), modifiedAccumAmt);

    // previous(init)
    assertAccountDetailHistoryEntity(accountDetailHistoryEntities.get(0), notModifiedAccumAmt);
    // api insert(기존 init)
    assertAccountDetailHistoryEntity(accountDetailHistoryEntities.get(1), notModifiedAccumAmt);
    // api insert(추가)
    assertAccountDetailHistoryEntity(accountDetailHistoryEntities.get(2), modifiedAccumAmt);
  }

  @Test
  @DisplayName("5. 기존 1건 + 동일 1건")
  void listIrpAccountDetails_SameOneAccount() {

    /* api mock server */
    setupMockServer(HttpStatus.OK, "IR03_005_single_page_00.json");

    saveUserSyncStatus();

    /* save mock account summaries */
    accountSummaryEntity1.setDetailSearchTimestamp(DETAIL_SEARCH_TIMESTAMP);
    accountSummaryRepository.save(accountSummaryEntity1);

    /* save mock account details and history */
    saveAccountDetailsAndHistorySource();

    irpAccountService.listIrpAccountDetails(executionContext);

    assertEquals(1, accountSummaryRepository.count());
    assertEquals(1, accountDetailRepository.count());
    assertEquals(2, accountDetailHistoryRepository.count());

    assertUserSyncStatusSyncedAt();
    assertUserSyncStatusSearchTimestamp(0); // upsert성공시 0으로 timestamp를 update

    List<IrpAccountSummaryEntity> accountSummaryEntities = accountSummaryRepository.findAll();
    IrpAccountSummaryEntity accountSummaryEntity = accountSummaryEntities.get(0);

    assertEquals(DETAIL_SEARCH_TIMESTAMP, accountSummaryEntity.getDetailSearchTimestamp());
    assertNull(accountSummaryEntity.getDetailResponseCode());

    assertAccountDetailEntitySyncedAt(accountDetailRepository.findAll(), accountDetailHistoryRepository.findAll());
  }

  @Test
  @DisplayName("6. 기존 1건 + 변경 1건")
  void listIrpAccountDetails_ModifiedOneAccount() {

    /* api mock server */
    setupMockServer(HttpStatus.OK, "IR03_006_single_page_00.json");

    saveUserSyncStatus();

    /* save mock account summaries */
    accountSummaryEntity1.setDetailSearchTimestamp(DETAIL_SEARCH_TIMESTAMP);
    accountSummaryRepository.save(accountSummaryEntity1);

    /* save mock account details and history */
    saveAccountDetailsAndHistorySource();

    irpAccountService.listIrpAccountDetails(executionContext);

    assertEquals(1, accountSummaryRepository.count());
    assertEquals(1, accountDetailRepository.count());
    assertEquals(2, accountDetailHistoryRepository.count());

    assertUserSyncStatusSyncedAt();
    assertUserSyncStatusSearchTimestamp(0); // upsert성공시 0으로 timestamp를 update

    List<IrpAccountSummaryEntity> accountSummaryEntities = accountSummaryRepository.findAll();
    IrpAccountSummaryEntity accountSummaryEntity = accountSummaryEntities.get(0);

    assertEquals(DETAIL_SEARCH_TIMESTAMP * 2, accountSummaryEntity.getDetailSearchTimestamp());
    assertNull(accountSummaryEntity.getDetailResponseCode());

    List<IrpAccountDetailEntity> accountDetailEntitiesEntities = accountDetailRepository.findAll();
    List<IrpAccountDetailHistoryEntity> accountDetailHistoryEntities = accountDetailHistoryRepository.findAll();

    assertAccountDetailEntitySyncedAt(accountDetailEntitiesEntities, accountDetailHistoryEntities);

    BigDecimal notModifiedAccumAmt = new BigDecimal("10.123");
    BigDecimal modifiedAccumAmt = notModifiedAccumAmt.add(new BigDecimal("10"));
    assertAccountDetailEntity(accountDetailEntitiesEntities.get(0), modifiedAccumAmt);

    assertAccountDetailHistoryEntity(accountDetailHistoryEntities.get(0),
        notModifiedAccumAmt); // not modified previous source
    assertAccountDetailHistoryEntity(accountDetailHistoryEntities.get(1), modifiedAccumAmt); // modified
  }

  @Test
  @DisplayName("7. 기존 0건 + 추가 2건 + 실패 1건")
  void listIrpAccountDetails_addTwoDetails_oneFailure() {

    /* api mock server */
    setupMockServer(HttpStatus.OK, "IR03_007_multi_page_00.json");
    setupMockServer(HttpStatus.FORBIDDEN, "IR03_007_multi_page_01.json");

    /* save mock account summaries */
    accountSummaryEntity1.setDetailSearchTimestamp(0L);
    accountSummaryRepository.save(accountSummaryEntity1);

    irpAccountService.listIrpAccountDetails(executionContext);

    assertEquals(1, accountSummaryRepository.count());
    assertEquals(2, accountDetailRepository.count());
    assertEquals(2, accountDetailHistoryRepository.count());

    assertUserSyncStatusSyncedAt();
    assertUserSyncStatusSearchTimestamp(0); // upsert성공시 0으로 timestamp를 update

    assertAccountDetailEntitySyncedAt(accountDetailRepository.findAll(), accountDetailHistoryRepository.findAll());

    List<IrpAccountSummaryEntity> accountSummaryEntities = accountSummaryRepository.findAll();
    IrpAccountSummaryEntity accountSummaryEntity = accountSummaryEntities.get(0);
    assertEquals(DETAIL_SEARCH_TIMESTAMP, accountSummaryEntity.getDetailSearchTimestamp()); // 최초 : 1000, 2page : 2000
    assertEquals("40305", accountSummaryEntity.getDetailResponseCode());
  }

//  @Test
//  @Disabled("UP_TO_DATE에 대한 명세 확인 필요 및 추가 0건에 대해 처리 미고려")
//  @DisplayName("8. 기존 1건 + 추가 0건")
//  void listIrpAccountDetails_NoAddition() {
//
//    /* api mock server */
//    setupMockServer(HttpStatus.OK, "IR02_006_single_page_00.json");
//
//    saveUserSyncStatus();
//
//    /* save mock account summaries */
//    accountSummaryEntity1.setDetailSearchTimestamp(DETAIL_SEARCH_TIMESTAMP);  // previous selection
//    accountSummaryRepository.save(accountSummaryEntity1);
//
//    /* save mock account details and history */
//    saveAccountDetailsAndHistorySource();
//
//    irpAccountService.listIrpAccountDetails(executionContext);
//
//    assertEquals(1, accountSummaryRepository.count());
//    assertEquals(1, accountDetailRepository.count());
//    assertEquals(1, accountDetailHistoryRepository.count());
//
//    assertUserSyncStatusSyncedAt();
//    assertUserSyncStatusSearchTimestamp(300); // 변경이 없으므로 이전 값 그대로
//
//    assertAccountDetailEntitySyncedAt(accountDetailRepository.findAll(), accountDetailHistoryRepository.findAll());
//
//    List<IrpAccountSummaryEntity> accountSummaryEntities = accountSummaryRepository.findAll();
//    IrpAccountSummaryEntity accountSummaryEntity = accountSummaryEntities.get(0);
//
//    assertEquals(DETAIL_SEARCH_TIMESTAMP, accountSummaryEntity.getDetailSearchTimestamp());
//    assertNull(accountSummaryEntity.getDetailResponseCode());
//  }

  private void setupMockServer(HttpStatus httpStatus, String... fileNames) {

    Arrays.stream(fileNames).forEach(fileName ->

        wiremock.stubFor(post(urlMatching("/irps/detail"))
            .withRequestBody(equalToJson(readText("classpath:mock/irp/request/" + fileName)))
            .willReturn(
                aResponse()
                    .withStatus(httpStatus.value())
                    .withHeader("Content-Type", ContentType.APPLICATION_JSON.toString())
                    .withBody(readText("classpath:mock/irp/response/" + fileName)))));
  }

  private void saveAccountDetailsAndHistorySource() {

    accountDetailRepository.save(expectedAccountDetailEntity);
    IrpAccountDetailHistoryEntity accountDetailHistoryEntity = accountDetailHistoryMapper
        .toHistoryEntity(expectedAccountDetailEntity);
    accountDetailHistoryRepository.save(accountDetailHistoryEntity);
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
            executionContext.getOrganizationId(), Executions.irp_get_detail.getApi().getId()).orElse(null);
  }

  private void assertAccountDetailEntity(IrpAccountDetailEntity accountDetailEntity,
      BigDecimal notModifiedAccumAmt) {
    assertEquals(notModifiedAccumAmt, accountDetailEntity.getEvalAmt());
  }

  private void assertAccountDetailHistoryEntity(IrpAccountDetailHistoryEntity accountDetailHistoryEntity,
      BigDecimal notModifiedAccumAmt) {
    assertEquals(notModifiedAccumAmt, accountDetailHistoryEntity.getEvalAmt());
  }

  private void assertAccountDetailEntitySyncedAt(List<IrpAccountDetailEntity> accountDetailEntities,
      List<IrpAccountDetailHistoryEntity> accountDetailHistoryEntities) {

    accountDetailEntities
        .forEach(irpAccountDetailEntity -> assertEquals(irpAccountDetailEntity.getSyncedAt(), requestSyncedAt));

    accountDetailHistoryEntities
        .forEach(irpAccountDetailHistoryEntity -> assertEquals(irpAccountDetailHistoryEntity.getSyncedAt(),
            requestSyncedAt));
  }

  private void saveUserSyncStatus() {
    userSyncStatusRepository.save(
        UserSyncStatusEntity.builder()
            .syncedAt(LocalDateTime.now())
            .searchTimestamp(SEARCH_TIMESTAMP)
            .banksaladUserId(BANKSALAD_USER_ID)
            .organizationId(ORGANIZATION_ID)
            .apiId(Apis.irp_get_detail.getId())
            .build()
    );
  }
}
