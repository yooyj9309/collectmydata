package com.banksalad.collectmydata.capital.account;

import com.banksalad.collectmydata.capital.account.dto.AccountBasic;
import com.banksalad.collectmydata.capital.account.dto.GetAccountBasicRequest;
import com.banksalad.collectmydata.capital.collect.Executions;
import com.banksalad.collectmydata.capital.common.db.entity.AccountBasicEntity;
import com.banksalad.collectmydata.capital.common.db.entity.AccountBasicHistoryEntity;
import com.banksalad.collectmydata.capital.common.db.entity.AccountSummaryEntity;
import com.banksalad.collectmydata.capital.common.db.repository.AccountBasicHistoryRepository;
import com.banksalad.collectmydata.capital.common.db.repository.AccountBasicRepository;
import com.banksalad.collectmydata.capital.common.db.repository.AccountSummaryRepository;
import com.banksalad.collectmydata.capital.summary.dto.AccountSummary;
import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.finance.api.accountinfo.AccountInfoRequestHelper;
import com.banksalad.collectmydata.finance.api.accountinfo.AccountInfoResponseHelper;
import com.banksalad.collectmydata.finance.api.accountinfo.AccountInfoService;
import com.banksalad.collectmydata.finance.common.db.entity.UserSyncStatusEntity;
import com.banksalad.collectmydata.finance.common.db.repository.UserSyncStatusRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.WireMockSpring;
import org.springframework.http.HttpStatus;

import com.github.tomakehurst.wiremock.WireMockServer;
import javax.transaction.Transactional;
import org.apache.http.entity.ContentType;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static com.banksalad.collectmydata.capital.common.TestHelper.ACCOUNT_NUM;
import static com.banksalad.collectmydata.capital.common.TestHelper.ACCOUNT_STATUS;
import static com.banksalad.collectmydata.capital.common.TestHelper.ACCOUNT_TYPE;
import static com.banksalad.collectmydata.capital.common.TestHelper.BANKSALAD_USER_ID;
import static com.banksalad.collectmydata.capital.common.TestHelper.ENTITY_IGNORE_FIELD;
import static com.banksalad.collectmydata.capital.common.TestHelper.EXP_DATE;
import static com.banksalad.collectmydata.capital.common.TestHelper.HOLDER_NAME;
import static com.banksalad.collectmydata.capital.common.TestHelper.ISSUE_DATE;
import static com.banksalad.collectmydata.capital.common.TestHelper.LAST_OFFERED_RATE;
import static com.banksalad.collectmydata.capital.common.TestHelper.ORGANIZATION_ID;
import static com.banksalad.collectmydata.capital.common.TestHelper.PRODUCT_NAME;
import static com.banksalad.collectmydata.capital.common.TestHelper.REPAY_ACCOUNT_NUM;
import static com.banksalad.collectmydata.capital.common.TestHelper.REPAY_DATE;
import static com.banksalad.collectmydata.capital.common.TestHelper.REPAY_METHOD;
import static com.banksalad.collectmydata.capital.common.TestHelper.REPAY_ORG_CODE;
import static com.banksalad.collectmydata.capital.common.TestHelper.SEQNO1;
import static com.banksalad.collectmydata.capital.common.TestHelper.getExecutionContext;
import static com.banksalad.collectmydata.capital.util.FileUtil.readText;
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.RSP_CODE_SUCCESS;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static java.lang.Boolean.TRUE;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DisplayName("6.7.2 대출상품계좌 기본정보 조회")
@Transactional
@SpringBootTest
public class AccountBasicServiceTest {

  @Autowired
  private AccountBasicRepository accountBasicRepository;

  @Autowired
  private AccountBasicHistoryRepository accountBasicHistoryRepository;

  @Autowired
  private AccountSummaryRepository accountSummaryRepository;

  @Autowired
  private UserSyncStatusRepository userSyncStatusRepository;

  @Autowired
  private AccountInfoService<AccountSummary, GetAccountBasicRequest, AccountBasic> accountBasicService;

  @Autowired
  private AccountInfoRequestHelper<GetAccountBasicRequest, AccountSummary> accountBasicRequestHelper;

  @Autowired
  private AccountInfoResponseHelper<AccountSummary, AccountBasic> accountBasicResponseHelper;

  private static WireMockServer wireMockServer;

  private final LocalDateTime SYNCED_AT = LocalDateTime.of(2021, 01, 01, 0, 0);

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
  void getAccountBasic_failure_01() {
    // given
    setupMockServer1();
    ExecutionContext executionContext = getExecutionContext(wireMockServer.port());

    AccountSummaryEntity accountSummaryEntity = getAccountSummaryEntity(1000L);
    accountSummaryRepository.save(accountSummaryEntity);

    // when
    accountBasicService
        .listAccountInfos(executionContext, Executions.capital_get_account_basic, accountBasicRequestHelper,
            accountBasicResponseHelper);

    // then
    /* check #1 : userSyncStatusEntity - syncedAt */
    UserSyncStatusEntity userSyncStatusEntity = userSyncStatusRepository
        .findByBanksaladUserIdAndOrganizationIdAndApiId(BANKSALAD_USER_ID, ORGANIZATION_ID,
            Executions.capital_get_account_basic.getApi().getId())
        .orElseGet(() -> UserSyncStatusEntity.builder().build());
    assertThat(userSyncStatusEntity.getSyncedAt())
        .isEqualTo(executionContext.getSyncStartedAt());

    /* check #2 : accountSummary - syncedAt, responseCode, searchTimestamp */
    assertThat(accountSummaryRepository.findAll().get(0).getSyncedAt()).isEqualTo(SYNCED_AT);
    assertThat(accountSummaryRepository.findAll().get(0).getBasicResponseCode()).isEqualTo("40404");
    assertThat(accountSummaryRepository.findAll().get(0).getBasicSearchTimestamp()).isEqualTo(1000L);

    /* check #3 : accountBasicEntity */
    assertThat(accountBasicRepository.findAll().isEmpty()).isTrue();

    /* check #4 : accountBasicHistoryEntity */
    assertThat(accountBasicHistoryRepository.findAll().isEmpty()).isTrue();
  }

  @Test
  @DisplayName("#2 기존 0건 + 신규 1건")
  void getAccountBasic_success_01() {
    // given
    setupMockServer2();
    ExecutionContext executionContext = getExecutionContext(wireMockServer.port());

    AccountSummaryEntity accountSummaryEntity = getAccountSummaryEntity(0L);
    accountSummaryRepository.save(accountSummaryEntity);

    // when
    accountBasicService
        .listAccountInfos(executionContext, Executions.capital_get_account_basic, accountBasicRequestHelper,
            accountBasicResponseHelper);

    // then
    /* check #1 : userSyncStatusEntity - syncedAt */
    UserSyncStatusEntity userSyncStatusEntity = userSyncStatusRepository
        .findByBanksaladUserIdAndOrganizationIdAndApiId(BANKSALAD_USER_ID, ORGANIZATION_ID,
            Executions.capital_get_account_basic.getApi().getId())
        .orElseGet(() -> UserSyncStatusEntity.builder().build());
    assertThat(userSyncStatusEntity.getSyncedAt())
        .isEqualTo(executionContext.getSyncStartedAt());

    /* check #2 : accountSummaryEntity - searchTimestamp */
    assertThat(accountSummaryRepository.findAll().size()).isEqualTo(1);
    assertThat(accountSummaryRepository.findAll().get(0).getBasicSearchTimestamp())
        .isEqualTo(1000);

    /* check #3 : accountBasicEntity */
    assertThat(accountBasicRepository.findAll().size()).isEqualTo(1);
    assertThat(accountBasicRepository.findAll()).usingRecursiveComparison().ignoringFields(ENTITY_IGNORE_FIELD)
        .isEqualTo(List.of(getAccountBasicEntity()));

    /* check #4 : accountBasicEntity and accountBasicHistoryEntity */
    assertThat(accountBasicRepository.findAll()).usingRecursiveComparison().ignoringFields(ENTITY_IGNORE_FIELD)
        .isEqualTo(accountBasicHistoryRepository.findAll());
  }

  @Test
  @DisplayName("#3 기존 1건 + 신규 1건")
  void getAccountBasic_success_02() {
    // given
    setupMockServer3();
    ExecutionContext executionContext = getExecutionContext(wireMockServer.port());

    accountSummaryRepository.saveAll(List.of(
        getAccountSummaryEntity(1000L),
        getAccountSummaryEntitySecond(0L))
    );
    accountBasicRepository.save(getAccountBasicEntity());
    accountBasicHistoryRepository.save(getAccountBasicHistoryEntity());

    // when
    accountBasicService
        .listAccountInfos(executionContext, Executions.capital_get_account_basic, accountBasicRequestHelper,
            accountBasicResponseHelper);

    // then
    /* check #1 : userSyncStatusEntity - syncedAt */
    UserSyncStatusEntity userSyncStatusEntity = userSyncStatusRepository
        .findByBanksaladUserIdAndOrganizationIdAndApiId(BANKSALAD_USER_ID, ORGANIZATION_ID,
            Executions.capital_get_account_basic.getApi().getId())
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
    assertThat(accountBasicRepository.findAll().size()).isEqualTo(2);
    assertThat(accountBasicRepository.findAll()).usingRecursiveComparison().ignoringFields(ENTITY_IGNORE_FIELD)
        .isEqualTo(List.of(
            getAccountBasicEntity(),
            AccountBasicEntity.builder()
                .syncedAt(SYNCED_AT)
                .banksaladUserId(BANKSALAD_USER_ID)
                .organizationId(ORGANIZATION_ID)
                .accountNum(ACCOUNT_NUM + "2")
                .seqno(SEQNO1)
                .holderName(HOLDER_NAME)
                .issueDate(ISSUE_DATE)
                .expDate(EXP_DATE)
                .lastOfferedRate(LAST_OFFERED_RATE)
                .repayDate(REPAY_DATE)
                .repayMethod(REPAY_METHOD)
                .repayOrgCode(REPAY_ORG_CODE)
                .repayAccountNum(REPAY_ACCOUNT_NUM)
                .build())
        );
    assertThat(accountBasicRepository.findAll().get(1).getSyncedAt()).isEqualTo(executionContext.getSyncStartedAt());

    /* check #4 : accountBasicEntity and accountBasicHistoryEntity */
    assertThat(accountBasicRepository.findAll()).usingRecursiveComparison().ignoringFields(ENTITY_IGNORE_FIELD)
        .isEqualTo(accountBasicHistoryRepository.findAll());
  }

  @Test
  @DisplayName("#4 기존 1건 + 동일 1건")
  void getAccountBasic_success_03() {
    // given
    setupMockServer4();
    ExecutionContext executionContext = getExecutionContext(wireMockServer.port());

    accountSummaryRepository.save(getAccountSummaryEntity(1000L));
    accountBasicRepository.save(getAccountBasicEntity());
    accountBasicHistoryRepository.save(getAccountBasicHistoryEntity());

    // when
    accountBasicService
        .listAccountInfos(executionContext, Executions.capital_get_account_basic, accountBasicRequestHelper,
            accountBasicResponseHelper);

    // then
    /* check #1 : userSyncStatusEntity - syncedAt */
    UserSyncStatusEntity userSyncStatusEntity = userSyncStatusRepository
        .findByBanksaladUserIdAndOrganizationIdAndApiId(BANKSALAD_USER_ID, ORGANIZATION_ID,
            Executions.capital_get_account_basic.getApi().getId())
        .orElseGet(() -> UserSyncStatusEntity.builder().build());
    assertThat(userSyncStatusEntity.getSyncedAt())
        .isEqualTo(executionContext.getSyncStartedAt());

    /* check #2 : accountSummaryEntity */
    assertThat(accountSummaryRepository.findAll().size()).isEqualTo(1);
    assertThat(accountSummaryRepository.findAll().get(0)).usingRecursiveComparison().ignoringFields(ENTITY_IGNORE_FIELD)
        .isEqualTo(getAccountSummaryEntity(1000L, RSP_CODE_SUCCESS));

    /* check #3 : accountBasicEntity */
    assertThat(accountBasicRepository.findAll().size()).isEqualTo(1);
    assertThat(accountBasicRepository.findAll()).usingRecursiveComparison().ignoringFields(ENTITY_IGNORE_FIELD)
        .isEqualTo(List.of(getAccountBasicEntity()));

    /* check #4 : accountBasicEntity and accountBasicHistoryEntity */
    assertThat(accountBasicRepository.findAll()).usingRecursiveComparison().ignoringFields(ENTITY_IGNORE_FIELD)
        .isEqualTo(accountBasicHistoryRepository.findAll());
  }

  @Test
  @DisplayName("#5 기존 1건 + 변경 1건")
  void getAccountBasic_success_04() {
    // given
    setupMockServer5();
    ExecutionContext executionContext = getExecutionContext(wireMockServer.port());

    accountSummaryRepository.save(getAccountSummaryEntity(1000L));
    accountBasicRepository.save(getAccountBasicEntity());
    accountBasicHistoryRepository.save(getAccountBasicHistoryEntity());

    // when
    accountBasicService
        .listAccountInfos(executionContext, Executions.capital_get_account_basic, accountBasicRequestHelper,
            accountBasicResponseHelper);

    // then
    /* check #1 : userSyncStatusEntity - syncedAt */
    UserSyncStatusEntity userSyncStatusEntity = userSyncStatusRepository
        .findByBanksaladUserIdAndOrganizationIdAndApiId(BANKSALAD_USER_ID, ORGANIZATION_ID,
            Executions.capital_get_account_basic.getApi().getId())
        .orElseGet(() -> UserSyncStatusEntity.builder().build());
    assertThat(userSyncStatusEntity.getSyncedAt())
        .isEqualTo(executionContext.getSyncStartedAt());

    /* check #2 : accountSummaryEntity */
    assertThat(accountSummaryRepository.findAll().size()).isEqualTo(1);
    assertThat(accountSummaryRepository.findAll().get(0)).usingRecursiveComparison().ignoringFields(ENTITY_IGNORE_FIELD)
        .isEqualTo(getAccountSummaryEntity(2000L, RSP_CODE_SUCCESS));

    /* check #3 : accountBasicEntity - syncedAt, holderName */
    assertThat(accountBasicRepository.findAll().size()).isEqualTo(1);
    assertThat(accountBasicRepository.findAll().get(0).getSyncedAt()).isEqualTo(executionContext.getSyncStartedAt());
    assertThat(accountBasicRepository.findAll()).usingRecursiveComparison().ignoringFields(ENTITY_IGNORE_FIELD)
        .isEqualTo(List.of(
            AccountBasicEntity.builder()
                .syncedAt(SYNCED_AT)
                .banksaladUserId(BANKSALAD_USER_ID)
                .organizationId(ORGANIZATION_ID)
                .accountNum(ACCOUNT_NUM)
                .seqno(SEQNO1)
                .holderName(HOLDER_NAME + "2")
                .issueDate(ISSUE_DATE)
                .expDate(EXP_DATE)
                .lastOfferedRate(LAST_OFFERED_RATE)
                .repayDate(REPAY_DATE)
                .repayMethod(REPAY_METHOD)
                .repayOrgCode(REPAY_ORG_CODE)
                .repayAccountNum(REPAY_ACCOUNT_NUM)
                .build()
        ));

    /* check #4 : accountBasicEntity and accountBasicHistoryEntity - holderName, syncedAt */
    assertThat(accountBasicHistoryRepository.findAll().size()).isEqualTo(2);
    assertThat(accountBasicHistoryRepository.findAll().get(0).getHolderName()).isEqualTo(HOLDER_NAME);
    assertThat(accountBasicHistoryRepository.findAll().get(0).getSyncedAt()).isEqualTo(SYNCED_AT);
    assertThat(accountBasicHistoryRepository.findAll().get(1).getHolderName()).isEqualTo(HOLDER_NAME + "2");
    assertThat(accountBasicHistoryRepository.findAll().get(1).getSyncedAt())
        .isEqualTo(executionContext.getSyncStartedAt());
  }

  private AccountSummaryEntity getAccountSummaryEntity(Long basicSearchTimestamp) {
    return AccountSummaryEntity.builder()
        .syncedAt(SYNCED_AT)
        .banksaladUserId(BANKSALAD_USER_ID)
        .organizationId(ORGANIZATION_ID)
        .accountNum(ACCOUNT_NUM)
        .seqno(SEQNO1)
        .isConsent(TRUE)
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
        .seqno(SEQNO1)
        .isConsent(TRUE)
        .prodName(PRODUCT_NAME)
        .accountType(ACCOUNT_TYPE)
        .accountStatus(ACCOUNT_STATUS)
        .basicSearchTimestamp(basicSearchTimestamp)
        .build();
  }

  private AccountSummaryEntity getAccountSummaryEntity(Long basicSearchTimestamp, String responseCode) {
    return AccountSummaryEntity.builder()
        .syncedAt(SYNCED_AT)
        .banksaladUserId(BANKSALAD_USER_ID)
        .organizationId(ORGANIZATION_ID)
        .accountNum(ACCOUNT_NUM)
        .seqno(SEQNO1)
        .isConsent(TRUE)
        .prodName(PRODUCT_NAME)
        .accountType(ACCOUNT_TYPE)
        .accountStatus(ACCOUNT_STATUS)
        .basicSearchTimestamp(basicSearchTimestamp)
        .basicResponseCode(responseCode)
        .build();
  }

  private AccountBasicEntity getAccountBasicEntity() {
    return AccountBasicEntity.builder()
        .syncedAt(SYNCED_AT)
        .banksaladUserId(BANKSALAD_USER_ID)
        .organizationId(ORGANIZATION_ID)
        .accountNum(ACCOUNT_NUM)
        .seqno(SEQNO1)
        .holderName(HOLDER_NAME)
        .issueDate(ISSUE_DATE)
        .expDate(EXP_DATE)
        .lastOfferedRate(LAST_OFFERED_RATE)
        .repayDate(REPAY_DATE)
        .repayMethod(REPAY_METHOD)
        .repayOrgCode(REPAY_ORG_CODE)
        .repayAccountNum(REPAY_ACCOUNT_NUM)
        .build();
  }

  private AccountBasicHistoryEntity getAccountBasicHistoryEntity() {
    return AccountBasicHistoryEntity.builder()
        .syncedAt(SYNCED_AT)
        .banksaladUserId(BANKSALAD_USER_ID)
        .organizationId(ORGANIZATION_ID)
        .accountNum(ACCOUNT_NUM)
        .seqno(SEQNO1)
        .holderName(HOLDER_NAME)
        .issueDate(ISSUE_DATE)
        .expDate(EXP_DATE)
        .lastOfferedRate(LAST_OFFERED_RATE)
        .repayDate(REPAY_DATE)
        .repayMethod(REPAY_METHOD)
        .repayOrgCode(REPAY_ORG_CODE)
        .repayAccountNum(REPAY_ACCOUNT_NUM)
        .build();
  }

  private static void setupMockServer1() {
    // #1 : 존재하지 않거나 해지된 계좌 번호건 : 기존 0건 + 실패 1건
    wireMockServer.stubFor(post(urlMatching("/loans/basic"))
        .withRequestBody(
            equalToJson(readText("classpath:mock/request/CP02_001_single_page_01.json")))
        .willReturn(
            aResponse()
                .withStatus(HttpStatus.NOT_FOUND.value())
                .withHeader("Content-Type", ContentType.APPLICATION_JSON.toString())
                .withBody(readText("classpath:mock/response/CP02_001_single_page_01.json"))));
  }

  private static void setupMockServer2() {
    // #2 : 기존 0건 + 신규 1건
    wireMockServer.stubFor(post(urlMatching("/loans/basic"))
        .withRequestBody(
            equalToJson(readText("classpath:mock/request/CP02_002_single_page_01.json")))
        .willReturn(
            aResponse()
                .withStatus(HttpStatus.OK.value())
                .withHeader("Content-Type", ContentType.APPLICATION_JSON.toString())
                .withBody(readText("classpath:mock/response/CP02_002_single_page_01.json"))));
  }

  private static void setupMockServer3() {
    // #3 : 기존 1건 + 신규 1건
    wireMockServer.stubFor(post(urlMatching("/loans/basic"))
        .withRequestBody(
            equalToJson(readText("classpath:mock/request/CP02_003_single_page_01.json")))
        .willReturn(
            aResponse()
                .withStatus(HttpStatus.OK.value())
                .withHeader("Content-Type", ContentType.APPLICATION_JSON.toString())
                .withBody(readText("classpath:mock/response/CP02_003_single_page_01.json"))));

    wireMockServer.stubFor(post(urlMatching("/loans/basic"))
        .withRequestBody(
            equalToJson(readText("classpath:mock/request/CP02_003_single_page_02.json")))
        .willReturn(
            aResponse()
                .withStatus(HttpStatus.OK.value())
                .withHeader("Content-Type", ContentType.APPLICATION_JSON.toString())
                .withBody(readText("classpath:mock/response/CP02_003_single_page_02.json"))));
  }

  private static void setupMockServer4() {
    // #4 : 기존 1건 + 동일 1건
    wireMockServer.stubFor(post(urlMatching("/loans/basic"))
        .withRequestBody(
            equalToJson(readText("classpath:mock/request/CP02_004_single_page_01.json")))
        .willReturn(
            aResponse()
                .withStatus(HttpStatus.OK.value())
                .withHeader("Content-Type", ContentType.APPLICATION_JSON.toString())
                .withBody(readText("classpath:mock/response/CP02_004_single_page_01.json"))));
  }

  private static void setupMockServer5() {
    // #5 : 기존 1건 + 변경 1건
    wireMockServer.stubFor(post(urlMatching("/loans/basic"))
        .withRequestBody(
            equalToJson(readText("classpath:mock/request/CP02_005_single_page_01.json")))
        .willReturn(
            aResponse()
                .withStatus(HttpStatus.OK.value())
                .withHeader("Content-Type", ContentType.APPLICATION_JSON.toString())
                .withBody(readText("classpath:mock/response/CP02_005_single_page_01.json"))));
  }
}
