package com.banksalad.collectmydata.bank.deposit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.WireMockSpring;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;

import com.banksalad.collectmydata.bank.common.collect.Executions;
import com.banksalad.collectmydata.bank.common.db.entity.AccountSummaryEntity;
import com.banksalad.collectmydata.bank.common.db.entity.DepositAccountBasicEntity;
import com.banksalad.collectmydata.bank.common.db.entity.DepositAccountBasicHistoryEntity;
import com.banksalad.collectmydata.bank.common.db.entity.DepositAccountDetailEntity;
import com.banksalad.collectmydata.bank.common.db.repository.AccountSummaryRepository;
import com.banksalad.collectmydata.bank.common.db.repository.DepositAccountBasicHistoryRepository;
import com.banksalad.collectmydata.bank.common.db.repository.DepositAccountBasicRepository;
import com.banksalad.collectmydata.bank.common.db.repository.DepositAccountDetailRepository;
import com.banksalad.collectmydata.bank.deposit.dto.DepositAccountBasic;
import com.banksalad.collectmydata.bank.deposit.dto.DepositAccountDetail;
import com.banksalad.collectmydata.bank.deposit.dto.GetDepositAccountBasicRequest;
import com.banksalad.collectmydata.bank.deposit.dto.GetDepositAccountDetailRequest;
import com.banksalad.collectmydata.bank.summary.dto.AccountSummary;
import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.util.DateUtil;
import com.banksalad.collectmydata.finance.api.accountinfo.AccountInfoRequestHelper;
import com.banksalad.collectmydata.finance.api.accountinfo.AccountInfoResponseHelper;
import com.banksalad.collectmydata.finance.api.accountinfo.AccountInfoService;
import com.banksalad.collectmydata.finance.common.db.entity.UserSyncStatusEntity;
import com.banksalad.collectmydata.finance.common.db.repository.UserSyncStatusRepository;
import com.github.tomakehurst.wiremock.WireMockServer;
import org.apache.http.entity.ContentType;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static com.banksalad.collectmydata.bank.testutil.FileUtil.readText;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static java.lang.Boolean.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@DisplayName("6.2.2 수신계좌 기본정보 조회")
@Transactional
class DepositAccountBasicServiceTest {

  @Autowired
  private AccountInfoService<AccountSummary, GetDepositAccountBasicRequest, DepositAccountBasic> depositAccountBasicService;

  @Autowired
  private AccountInfoRequestHelper<GetDepositAccountBasicRequest, AccountSummary> depositAccountBasicInfoRequestHelper;

  @Autowired
  private AccountInfoResponseHelper<AccountSummary, DepositAccountBasic> depositAccountBasicInfoResponseHelper;

  @Autowired
  private AccountSummaryRepository accountSummaryRepository;

  @Autowired
  private DepositAccountBasicRepository depositAccountBasicRepository;

  @Autowired
  private DepositAccountBasicHistoryRepository depositAccountBasicHistoryRepository;

  @Autowired
  private UserSyncStatusRepository userSyncStatusRepository;

  private static WireMockServer wireMockServer;

  private final LocalDateTime SYNCED_AT = LocalDateTime.of(2021, 01, 01, 0, 0);
  private static final Long BANKSALAD_USER_ID = 1L;
  private static final String ORGANIZATION_ID = "bank1";
  private static final String ORGANIZATION_CODE = "bank001";
  private static final String ORGANIZATION_HOST = "http://localhost";
  private static final String ACCESS_TOKEN = "xxx.yyy.zzz";

  private static final String ACCOUNT_NUM = "1234567890";
  private static final String SEQNO1 = "1";
  public static final String PRODUCT_NAME = "product_name";
  public static final String ACCOUNT_TYPE = "1002";
  public static final String ACCOUNT_STATUS = "01";

  private static final String[] ENTITY_IGNORE_FIELD = {"id", "syncedAt", "createdAt", "createdBy", "updatedAt",
      "updatedBy"};


  @BeforeAll
  static void setup() {
    wireMockServer = new WireMockServer(WireMockSpring.options().dynamicPort());
    wireMockServer.start();
  }

  @AfterAll
  static void tearDown() {
    wireMockServer.shutdown();
  }

  @Test
  @DisplayName("#1 존재하지 않거나 해지된 계좌 번호건 : 기존 0 건 + 실패 1건")
  void getDepositAccountBasic_failure_01() {
    // given
    setupMockServer1();
    ExecutionContext executionContext = getExecutionContext(wireMockServer.port());

    AccountSummaryEntity accountSummaryEntity = getAccountSummaryEntity(1000L);
    accountSummaryRepository.save(accountSummaryEntity);

    // when
    depositAccountBasicService.listAccountInfos(executionContext, Executions.finance_bank_deposit_account_basic,
        depositAccountBasicInfoRequestHelper, depositAccountBasicInfoResponseHelper);

    // then
    /* check #1 : userSyncStatusEntity - syncedAt */
    UserSyncStatusEntity userSyncStatusEntity = userSyncStatusRepository
        .findByBanksaladUserIdAndOrganizationIdAndApiId(BANKSALAD_USER_ID, ORGANIZATION_ID,
            Executions.finance_bank_deposit_account_basic.getApi().getId())
        .orElseGet(() -> UserSyncStatusEntity.builder().build());
    assertThat(userSyncStatusEntity.getSyncedAt())
        .isEqualTo(executionContext.getSyncStartedAt());

    /* check #2 : accountSummary - syncedAt, responseCode, searchTimestamp */
    assertThat(accountSummaryRepository.findAll().get(0).getSyncedAt()).isEqualTo(SYNCED_AT);
    assertThat(accountSummaryRepository.findAll().get(0).getBasicResponseCode()).isEqualTo("40404");
    assertThat(accountSummaryRepository.findAll().get(0).getBasicSearchTimestamp()).isEqualTo(1000L);

    /* check #3 : accountBasicEntity */
    assertThat(depositAccountBasicRepository.findAll().isEmpty()).isTrue();

    /* check #4 : accountBasicHistoryEntity */
    assertThat(depositAccountBasicHistoryRepository.findAll().isEmpty()).isTrue();
  }

  @Test
  @DisplayName("#2 기존 0건 + 신규 1건")
  void getDepositAccountBasic_success_01() {
    // given
    setupMockServer2();
    ExecutionContext executionContext = getExecutionContext(wireMockServer.port());

    AccountSummaryEntity accountSummaryEntity = getAccountSummaryEntity(0L);
    accountSummaryRepository.save(accountSummaryEntity);

    // when
    depositAccountBasicService.listAccountInfos(executionContext, Executions.finance_bank_deposit_account_basic,
        depositAccountBasicInfoRequestHelper, depositAccountBasicInfoResponseHelper);

    // then
    /* check #1 : userSyncStatusEntity - syncedAt */
    UserSyncStatusEntity userSyncStatusEntity = userSyncStatusRepository
        .findByBanksaladUserIdAndOrganizationIdAndApiId(BANKSALAD_USER_ID, ORGANIZATION_ID,
            Executions.finance_bank_deposit_account_basic.getApi().getId())
        .orElseGet(() -> UserSyncStatusEntity.builder().build());
    assertThat(userSyncStatusEntity.getSyncedAt())
        .isEqualTo(executionContext.getSyncStartedAt());

    /* check #2 : accountSummaryEntity - searchTimestamp */
    assertThat(accountSummaryRepository.findAll().size()).isEqualTo(1);
    assertThat(accountSummaryRepository.findAll().get(0).getBasicSearchTimestamp())
        .isEqualTo(1000);

    /* check #3 : depositAccountBasicEntity */
    assertThat(depositAccountBasicRepository.findAll().size()).isEqualTo(1);
    assertThat(depositAccountBasicRepository.findAll()).usingRecursiveComparison().ignoringFields(ENTITY_IGNORE_FIELD)
        .isEqualTo(List.of(getDepositAccountBasicEntity()));

    /* check #4 : depositAccountBasicEntity and depositAccountBasicHistoryEntity */
    assertThat(depositAccountBasicRepository.findAll()).usingRecursiveComparison().ignoringFields(ENTITY_IGNORE_FIELD)
        .isEqualTo(depositAccountBasicHistoryRepository.findAll());
  }

  @Test
  @DisplayName("#3 기존 1건 + 신규 1건")
  void getDepositAccountBasic_success_02() {
    // given
    setupMockServer3();
    ExecutionContext executionContext = getExecutionContext(wireMockServer.port());

    accountSummaryRepository.saveAll(List.of(
        getAccountSummaryEntity(1000L),
        getAccountSummaryEntitySecond(0L))
    );
    depositAccountBasicRepository.save(getDepositAccountBasicEntity());
    depositAccountBasicHistoryRepository.save(getDepositAccountBasicHistoryEntity());

    // when
    depositAccountBasicService.listAccountInfos(executionContext, Executions.finance_bank_deposit_account_basic,
        depositAccountBasicInfoRequestHelper, depositAccountBasicInfoResponseHelper);

    // then
    /* check #1 : userSyncStatusEntity - syncedAt */
    UserSyncStatusEntity userSyncStatusEntity = userSyncStatusRepository
        .findByBanksaladUserIdAndOrganizationIdAndApiId(BANKSALAD_USER_ID, ORGANIZATION_ID,
            Executions.finance_bank_deposit_account_basic.getApi().getId())
        .orElseGet(() -> UserSyncStatusEntity.builder().build());
    assertThat(userSyncStatusEntity.getSyncedAt())
        .isEqualTo(executionContext.getSyncStartedAt());

    /* check #2 : accountSummaryEntity - searchTimestamp */
    assertThat(accountSummaryRepository.findAll().size()).isEqualTo(2);
    assertThat(accountSummaryRepository.findAll().get(0).getBasicSearchTimestamp())
        .isEqualTo(1000);
    assertThat(accountSummaryRepository.findAll().get(1).getBasicSearchTimestamp())
        .isEqualTo(2000);

    /* check #3 : accountBasicEntity */
    assertThat(depositAccountBasicRepository.findAll().size()).isEqualTo(2);
    assertThat(depositAccountBasicRepository.findAll()).usingRecursiveComparison().ignoringFields(ENTITY_IGNORE_FIELD)
        .isEqualTo(List.of(
            getDepositAccountBasicEntity(),
            DepositAccountBasicEntity.builder()
                .syncedAt(SYNCED_AT)
                .banksaladUserId(BANKSALAD_USER_ID)
                .organizationId(ORGANIZATION_ID)
                .accountNum(ACCOUNT_NUM + "2")
                .seqno(SEQNO1)
                .currencyCode("KRW")
                .savingMethod("00")
                .holderName("김뱅샐")
                .issueDate("20210101")
                .expDate("20211231")
                .commitAmt(null)
                .monthlyPaidInAmt(null)
                .build())
        );
    assertThat(depositAccountBasicRepository.findAll().get(1).getSyncedAt())
        .isEqualTo(executionContext.getSyncStartedAt());

    /* check #4 : accountBasicEntity and accountBasicHistoryEntity */
    assertThat(depositAccountBasicRepository.findAll()).usingRecursiveComparison().ignoringFields(ENTITY_IGNORE_FIELD)
        .isEqualTo(depositAccountBasicHistoryRepository.findAll());
  }

  @Test
  @DisplayName("#4 기존 1건 + 동일 1건")
  void getDepositAccountBasic_success_03() {
    // given
    setupMockServer4();
    ExecutionContext executionContext = getExecutionContext(wireMockServer.port());

    accountSummaryRepository.save(getAccountSummaryEntity(1000L));
    depositAccountBasicRepository.save(getDepositAccountBasicEntity());
    depositAccountBasicHistoryRepository.save(getDepositAccountBasicHistoryEntity());

    // when
    depositAccountBasicService.listAccountInfos(executionContext, Executions.finance_bank_deposit_account_basic,
        depositAccountBasicInfoRequestHelper, depositAccountBasicInfoResponseHelper);

    // then
    /* check #1 : userSyncStatusEntity - syncedAt */
    UserSyncStatusEntity userSyncStatusEntity = userSyncStatusRepository
        .findByBanksaladUserIdAndOrganizationIdAndApiId(BANKSALAD_USER_ID, ORGANIZATION_ID,
            Executions.finance_bank_deposit_account_basic.getApi().getId())
        .orElseGet(() -> UserSyncStatusEntity.builder().build());
    assertThat(userSyncStatusEntity.getSyncedAt())
        .isEqualTo(executionContext.getSyncStartedAt());

    /* check #2 : accountSummaryEntity */
    assertThat(accountSummaryRepository.findAll().size()).isEqualTo(1);
    assertThat(accountSummaryRepository.findAll().get(0)).usingRecursiveComparison().ignoringFields(ENTITY_IGNORE_FIELD)
        .isEqualTo(getAccountSummaryEntity(1000L));

    /* check #3 : accountBasicEntity */
    assertThat(depositAccountBasicRepository.findAll().size()).isEqualTo(1);
    assertThat(depositAccountBasicRepository.findAll()).usingRecursiveComparison().ignoringFields(ENTITY_IGNORE_FIELD)
        .isEqualTo(List.of(getDepositAccountBasicEntity()));

    /* check #4 : accountBasicEntity and accountBasicHistoryEntity */
    assertThat(depositAccountBasicRepository.findAll()).usingRecursiveComparison().ignoringFields(ENTITY_IGNORE_FIELD)
        .isEqualTo(depositAccountBasicHistoryRepository.findAll());
  }

  @Test
  @DisplayName("#5 기존 1건 + 변경 1건")
  void getDepositAccountBasic_success_04() {
    // given
    setupMockServer5();
    ExecutionContext executionContext = getExecutionContext(wireMockServer.port());

    accountSummaryRepository.save(getAccountSummaryEntity(1000L));
    depositAccountBasicRepository.save(getDepositAccountBasicEntity());
    depositAccountBasicHistoryRepository.save(getDepositAccountBasicHistoryEntity());

    // when
    depositAccountBasicService.listAccountInfos(executionContext, Executions.finance_bank_deposit_account_basic,
        depositAccountBasicInfoRequestHelper, depositAccountBasicInfoResponseHelper);

    // then
    /* check #1 : userSyncStatusEntity - syncedAt */
    UserSyncStatusEntity userSyncStatusEntity = userSyncStatusRepository
        .findByBanksaladUserIdAndOrganizationIdAndApiId(BANKSALAD_USER_ID, ORGANIZATION_ID,
            Executions.finance_bank_deposit_account_basic.getApi().getId())
        .orElseGet(() -> UserSyncStatusEntity.builder().build());
    assertThat(userSyncStatusEntity.getSyncedAt())
        .isEqualTo(executionContext.getSyncStartedAt());

    /* check #2 : accountSummaryEntity */
    assertThat(accountSummaryRepository.findAll().size()).isEqualTo(1);
    assertThat(accountSummaryRepository.findAll().get(0)).usingRecursiveComparison().ignoringFields(ENTITY_IGNORE_FIELD)
        .isEqualTo(getAccountSummaryEntity(2000L));

    /* check #3 : accountBasicEntity - syncedAt, holderName */
    assertThat(depositAccountBasicRepository.findAll().size()).isEqualTo(1);
    assertThat(depositAccountBasicRepository.findAll().get(0).getSyncedAt()).isEqualTo(executionContext.getSyncStartedAt());
    assertThat(depositAccountBasicRepository.findAll()).usingRecursiveComparison().ignoringFields(ENTITY_IGNORE_FIELD)
        .isEqualTo(List.of(
            DepositAccountBasicEntity.builder()
                .syncedAt(SYNCED_AT)
                .banksaladUserId(BANKSALAD_USER_ID)
                .organizationId(ORGANIZATION_ID)
                .accountNum(ACCOUNT_NUM)
                .seqno(SEQNO1)
                .currencyCode("KRW")
                .savingMethod("00")
                .holderName("김뱅샐" + "2")
                .issueDate("20210101")
                .expDate("20211231")
                .commitAmt(null)
                .monthlyPaidInAmt(null)
                .build()
        ));

    /* check #4 : accountBasicEntity and accountBasicHistoryEntity - holderName, syncedAt */
    assertThat(depositAccountBasicHistoryRepository.findAll().size()).isEqualTo(2);
    assertThat(depositAccountBasicHistoryRepository.findAll().get(0).getHolderName()).isEqualTo("김뱅샐");
    assertThat(depositAccountBasicHistoryRepository.findAll().get(0).getSyncedAt()).isEqualTo(SYNCED_AT);
    assertThat(depositAccountBasicHistoryRepository.findAll().get(1).getHolderName()).isEqualTo("김뱅샐" + "2");
    assertThat(depositAccountBasicHistoryRepository.findAll().get(1).getSyncedAt())
        .isEqualTo(executionContext.getSyncStartedAt());
  }

  private static ExecutionContext getExecutionContext(int port) {
    return ExecutionContext.builder()
        .syncRequestId(UUID.randomUUID().toString())
        .banksaladUserId(BANKSALAD_USER_ID)
        .organizationId(ORGANIZATION_ID)
        .executionRequestId(UUID.randomUUID().toString())
        .organizationCode(ORGANIZATION_CODE)
        .organizationHost(ORGANIZATION_HOST + ":" + port)
        .accessToken(ACCESS_TOKEN)
        .syncStartedAt(LocalDateTime.now(DateUtil.UTC_ZONE_ID))
        .build();
  }

  private AccountSummaryEntity getAccountSummaryEntity(Long basicSearchTimestamp) {
    return AccountSummaryEntity.builder()
        .syncedAt(SYNCED_AT)
        .banksaladUserId(BANKSALAD_USER_ID)
        .organizationId(ORGANIZATION_ID)
        .accountNum(ACCOUNT_NUM)
        .consent(TRUE)
        .seqno(SEQNO1)
        .foreignDeposit(FALSE)
        .prodName(PRODUCT_NAME)
        .accountType(ACCOUNT_TYPE)
        .accountStatus(ACCOUNT_STATUS)
        .basicSearchTimestamp(basicSearchTimestamp)
        .build();
  }

  private AccountSummaryEntity getAccountSummaryEntitySecond(Long basicSearchTimestamp) {
    final String SECOND_ACCOUNT_NUM = ACCOUNT_NUM + "2";
    return AccountSummaryEntity.builder()
        .syncedAt(SYNCED_AT)
        .banksaladUserId(BANKSALAD_USER_ID)
        .organizationId(ORGANIZATION_ID)
        .accountNum(SECOND_ACCOUNT_NUM)
        .consent(TRUE)
        .seqno(SEQNO1)
        .foreignDeposit(FALSE)
        .prodName(PRODUCT_NAME)
        .accountType(ACCOUNT_TYPE)
        .accountStatus(ACCOUNT_STATUS)
        .basicSearchTimestamp(basicSearchTimestamp)
        .build();
  }

  private DepositAccountBasicEntity getDepositAccountBasicEntity() {
    return DepositAccountBasicEntity.builder()
        .syncedAt(SYNCED_AT)
        .banksaladUserId(BANKSALAD_USER_ID)
        .organizationId(ORGANIZATION_ID)
        .accountNum(ACCOUNT_NUM)
        .seqno(SEQNO1)
        .currencyCode("KRW")
        .savingMethod("00")
        .holderName("김뱅샐")
        .issueDate("20210101")
        .expDate("20211231")
        .commitAmt(null)
        .monthlyPaidInAmt(null)
        .build();
  }

  private DepositAccountBasicHistoryEntity getDepositAccountBasicHistoryEntity() {
    return DepositAccountBasicHistoryEntity.builder()
        .syncedAt(SYNCED_AT)
        .banksaladUserId(BANKSALAD_USER_ID)
        .organizationId(ORGANIZATION_ID)
        .accountNum(ACCOUNT_NUM)
        .seqno(SEQNO1)
        .currencyCode("KRW")
        .savingMethod("00")
        .holderName("김뱅샐")
        .issueDate("20210101")
        .expDate("20211231")
        .commitAmt(null)
        .monthlyPaidInAmt(null)
        .build();
  }

  private static void setupMockServer1() {
    // #1 : 존재하지 않거나 해지된 계좌 번호건 : 기존 0건 + 실패 1건
    wireMockServer.stubFor(post(urlMatching("/accounts/deposit/basic"))
        .withRequestBody(
            equalToJson(readText("classpath:mock/bank/request/BA02_001_single_page_01.json")))
        .willReturn(
            aResponse()
                .withStatus(HttpStatus.NOT_FOUND.value())
                .withHeader("Content-Type", ContentType.APPLICATION_JSON.toString())
                .withBody(readText("classpath:mock/bank/response/BA02_001_single_page_01.json"))));
  }

  private static void setupMockServer2() {
    // #2 : 기존 0건 + 신규 1건
    wireMockServer.stubFor(post(urlMatching("/accounts/deposit/basic"))
        .withRequestBody(
            equalToJson(readText("classpath:mock/bank/request/BA02_002_single_page_01.json")))
        .willReturn(
            aResponse()
                .withStatus(HttpStatus.OK.value())
                .withHeader("Content-Type", ContentType.APPLICATION_JSON.toString())
                .withBody(readText("classpath:mock/bank/response/BA02_002_single_page_01.json"))));
  }

  private static void setupMockServer3() {
    // #3 : 기존 1건 + 신규 1건
    wireMockServer.stubFor(post(urlMatching("/accounts/deposit/basic"))
        .withRequestBody(
            equalToJson(readText("classpath:mock/bank/request/BA02_003_single_page_01.json")))
        .willReturn(
            aResponse()
                .withStatus(HttpStatus.OK.value())
                .withHeader("Content-Type", ContentType.APPLICATION_JSON.toString())
                .withBody(readText("classpath:mock/bank/response/BA02_003_single_page_01.json"))));

    wireMockServer.stubFor(post(urlMatching("/accounts/deposit/basic"))
        .withRequestBody(
            equalToJson(readText("classpath:mock/bank/request/BA02_003_single_page_02.json")))
        .willReturn(
            aResponse()
                .withStatus(HttpStatus.OK.value())
                .withHeader("Content-Type", ContentType.APPLICATION_JSON.toString())
                .withBody(readText("classpath:mock/bank/response/BA02_003_single_page_02.json"))));
  }

  private static void setupMockServer4() {
    // #4 : 기존 1건 + 동일 1건
    wireMockServer.stubFor(post(urlMatching("/accounts/deposit/basic"))
        .withRequestBody(
            equalToJson(readText("classpath:mock/bank/request/BA02_004_single_page_01.json")))
        .willReturn(
            aResponse()
                .withStatus(HttpStatus.OK.value())
                .withHeader("Content-Type", ContentType.APPLICATION_JSON.toString())
                .withBody(readText("classpath:mock/bank/response/BA02_004_single_page_01.json"))));
  }

  private static void setupMockServer5() {
    // #5 : 기존 1건 + 변경 1건
    wireMockServer.stubFor(post(urlMatching("/accounts/deposit/basic"))
        .withRequestBody(
            equalToJson(readText("classpath:mock/bank/request/BA02_005_single_page_01.json")))
        .willReturn(
            aResponse()
                .withStatus(HttpStatus.OK.value())
                .withHeader("Content-Type", ContentType.APPLICATION_JSON.toString())
                .withBody(readText("classpath:mock/bank/response/BA02_005_single_page_01.json"))));
  }
}
