package com.banksalad.collectmydata.bank.summary;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.WireMockSpring;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;

import com.banksalad.collectmydata.bank.common.collect.Apis;
import com.banksalad.collectmydata.bank.common.collect.Executions;
import com.banksalad.collectmydata.bank.common.db.entity.AccountSummaryEntity;
import com.banksalad.collectmydata.bank.common.db.repository.AccountSummaryRepository;
import com.banksalad.collectmydata.bank.summary.dto.AccountSummary;
import com.banksalad.collectmydata.bank.summary.dto.ListAccountSummariesRequest;
import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.util.DateUtil;
import com.banksalad.collectmydata.finance.api.summary.SummaryRequestHelper;
import com.banksalad.collectmydata.finance.api.summary.SummaryResponseHelper;
import com.banksalad.collectmydata.finance.api.summary.SummaryService;
import com.banksalad.collectmydata.finance.common.db.entity.UserSyncStatusEntity;
import com.banksalad.collectmydata.finance.common.db.repository.UserSyncStatusRepository;
import com.banksalad.collectmydata.finance.common.exception.ResponseNotOkException;
import com.banksalad.collectmydata.finance.common.service.UserSyncStatusService;
import com.github.tomakehurst.wiremock.WireMockServer;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.entity.ContentType;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static com.banksalad.collectmydata.bank.testutil.FileUtil.readText;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("6.2.1 계좌 목록 조회 테스트")
@SpringBootTest
@Transactional
public class AccountSummaryServiceImplTest {

  private static final Long BANKSALAD_USER_ID = 1L;
  private static final String ORGANIZATION_ID = "woori_bank";

  @Autowired
  private SummaryService<ListAccountSummariesRequest, AccountSummary> summaryService;

  @Autowired
  private SummaryRequestHelper<ListAccountSummariesRequest> summaryRequestHelper;

  @Autowired
  private SummaryResponseHelper<AccountSummary> summaryResponseHelper;

  @Autowired
  private AccountSummaryRepository accountSummaryRepository;

  @Autowired
  private UserSyncStatusService userSyncStatusService;

  @Autowired
  private UserSyncStatusRepository userSyncStatusRepository;

  public static WireMockServer wiremock = new WireMockServer(WireMockSpring.options().dynamicPort());

  @BeforeEach
  public void setupClass() {
    wiremock.start();
  }

  @AfterEach
  public void after() {
    wiremock.resetAll();
  }

  @AfterAll
  public static void clean() {
    wiremock.shutdown();
  }

  @DisplayName("STEP-01: HTTP 응답 코드가 500인 경우에 ResponseNotOkException 예외 발생")
  @Test
  void step_01_getAccounts_httpStatusInternalServerError_throwResponseNotOkException() {
    // given
    userSyncStatusService.updateUserSyncStatus(BANKSALAD_USER_ID, ORGANIZATION_ID, Apis.finance_bank_summaries.getId(),
        LocalDateTime.now(DateUtil.UTC_ZONE_ID).minusDays(1), 100);

    setupMockServer(100, HttpStatus.INTERNAL_SERVER_ERROR, "mock/bank/response/BA01_001_error_response_00.json");

    ExecutionContext executionContext = getExecutionContext(wiremock.port());

    // when
    ResponseNotOkException responseNotOkException = assertThrows(ResponseNotOkException.class,
        () -> summaryService.listAccountSummaries(executionContext, Executions.finance_bank_summaries,
            summaryRequestHelper, summaryResponseHelper));

    // then
    assertThat(responseNotOkException.getStatusCode()).isEqualTo(500);
    assertThat(responseNotOkException.getResponseCode()).isEqualTo("50001");
    assertThat(responseNotOkException.getResponseMessage()).isEqualTo("시스템장애");

    long searchTimestamp = userSyncStatusService
        .getSearchTimestamp(BANKSALAD_USER_ID, ORGANIZATION_ID, Apis.finance_bank_summaries);

    assertThat(searchTimestamp).isEqualTo(100);
  }

  @DisplayName("STEP-02: 기존 0건 + 신규 3건, searchTimestamp 100으로 변경")
  @Test
  void step_02_getAccounts_newAccounts_success() throws ResponseNotOkException {
    // given
    setupMockServer(0, HttpStatus.OK, "mock/bank/response/BA01_002_single_page_00.json");

    ExecutionContext executionContext = getExecutionContext(wiremock.port());

    // when
    summaryService.listAccountSummaries(executionContext, Executions.finance_bank_summaries, summaryRequestHelper,
        summaryResponseHelper);

    // then
    List<AccountSummaryEntity> accountSummaryEntities = accountSummaryRepository
        .findByBanksaladUserIdAndOrganizationIdAndConsentIsTrue(BANKSALAD_USER_ID, ORGANIZATION_ID);

    assertThat(accountSummaryEntities.size()).isEqualTo(3);

    long searchTimestamp = userSyncStatusService
        .getSearchTimestamp(BANKSALAD_USER_ID, ORGANIZATION_ID, Apis.finance_bank_summaries);

    assertThat(searchTimestamp).isEqualTo(100);
  }

  @DisplayName("STEP-03: 기존 1건 + 신규 0건")
  @Test
  void step_03_getAccounts_emptyResponse_success() throws ResponseNotOkException {
    // given
    LocalDateTime beforeSyncedAt = LocalDateTime.now(DateUtil.UTC_ZONE_ID).minusDays(1);

    AccountSummaryEntity accountSummaryEntity = createDepositAccountSummary(beforeSyncedAt);
    accountSummaryRepository.save(accountSummaryEntity);

    userSyncStatusService.updateUserSyncStatus(BANKSALAD_USER_ID, ORGANIZATION_ID, Apis.finance_bank_summaries.getId(),
        beforeSyncedAt, 100);

    setupMockServer(100, HttpStatus.OK, "mock/bank/response/BA01_003_empty_response_00.json");

    ExecutionContext executionContext = getExecutionContext(wiremock.port());

    // when
    summaryService.listAccountSummaries(executionContext, Executions.finance_bank_summaries, summaryRequestHelper,
        summaryResponseHelper);

    // then
    List<AccountSummaryEntity> accountSummaryEntities = accountSummaryRepository
        .findByBanksaladUserIdAndOrganizationIdAndConsentIsTrue(BANKSALAD_USER_ID, ORGANIZATION_ID);

    assertThat(accountSummaryEntities.size()).isEqualTo(1);

    UserSyncStatusEntity userSyncStatusEntity = userSyncStatusRepository
        .findByBanksaladUserIdAndOrganizationIdAndApiId(BANKSALAD_USER_ID, ORGANIZATION_ID,
            Apis.finance_bank_summaries.getId())
        .orElseThrow(() -> new RuntimeException("UserSyncStatus does not exist"));

    assertThat(userSyncStatusEntity.getSearchTimestamp()).isEqualTo(100);
    assertThat(userSyncStatusEntity.getSyncedAt()).isAfter(beforeSyncedAt);
  }

  @DisplayName("STEP-04: 기존 1건 + 신규 1건")
  @Test
  void step_04_getAccounts_newAccounts_success() throws ResponseNotOkException {
    // given
    LocalDateTime beforeSyncedAt = LocalDateTime.now(DateUtil.UTC_ZONE_ID).minusDays(1);

    AccountSummaryEntity accountSummaryEntity = createDepositAccountSummary(beforeSyncedAt);
    accountSummaryRepository.save(accountSummaryEntity);

    userSyncStatusService.updateUserSyncStatus(BANKSALAD_USER_ID, ORGANIZATION_ID, Apis.finance_bank_summaries.getId(),
        beforeSyncedAt, 100);

    setupMockServer(100, HttpStatus.OK, "mock/bank/response/BA01_004_single_page_00.json");

    ExecutionContext executionContext = getExecutionContext(wiremock.port());

    // when
    summaryService.listAccountSummaries(executionContext, Executions.finance_bank_summaries, summaryRequestHelper,
        summaryResponseHelper);

    // then
    List<AccountSummaryEntity> accountSummaryEntities = accountSummaryRepository
        .findByBanksaladUserIdAndOrganizationIdAndConsentIsTrue(BANKSALAD_USER_ID, ORGANIZATION_ID);

    assertThat(accountSummaryEntities.size()).isEqualTo(2);

    UserSyncStatusEntity userSyncStatusEntity = userSyncStatusRepository
        .findByBanksaladUserIdAndOrganizationIdAndApiId(BANKSALAD_USER_ID, ORGANIZATION_ID,
            Apis.finance_bank_summaries.getId())
        .orElseThrow(() -> new RuntimeException("UserSyncStatus does not exist"));

    assertThat(userSyncStatusEntity.getSearchTimestamp()).isEqualTo(200);
    assertThat(userSyncStatusEntity.getSyncedAt()).isAfter(beforeSyncedAt);
  }

  @DisplayName("STEP-05: 기존 1건 + 동일 1건")
  @Test
  void step_05_getAccounts_sameAccount_success() throws ResponseNotOkException {
    // given
    LocalDateTime beforeSyncedAt = LocalDateTime.now(DateUtil.UTC_ZONE_ID).minusDays(1);

    AccountSummaryEntity beforeAccountSummaryEntity = createDepositAccountSummary(beforeSyncedAt);
    accountSummaryRepository.save(beforeAccountSummaryEntity);

    userSyncStatusService.updateUserSyncStatus(BANKSALAD_USER_ID, ORGANIZATION_ID, Apis.finance_bank_summaries.getId(),
        beforeSyncedAt, 100);

    setupMockServer(100, HttpStatus.OK, "mock/bank/response/BA01_005_single_page_00.json");

    ExecutionContext executionContext = getExecutionContext(wiremock.port());

    // when
    summaryService.listAccountSummaries(executionContext, Executions.finance_bank_summaries, summaryRequestHelper,
        summaryResponseHelper);

    // then
    List<AccountSummaryEntity> accountSummaryEntities = accountSummaryRepository
        .findByBanksaladUserIdAndOrganizationIdAndConsentIsTrue(BANKSALAD_USER_ID, ORGANIZATION_ID);

    assertThat(accountSummaryEntities.size()).isEqualTo(1);

    AccountSummaryEntity accountSummaryEntity = accountSummaryEntities.get(0);

    assertThat(accountSummaryEntity.getAccountNum()).isEqualTo("123123123");
    assertThat(accountSummaryEntity.getSeqno()).isEqualTo("a10230");
    assertThat(accountSummaryEntity.getSyncedAt()).isAfter(beforeSyncedAt);

    UserSyncStatusEntity userSyncStatusEntity = userSyncStatusRepository
        .findByBanksaladUserIdAndOrganizationIdAndApiId(BANKSALAD_USER_ID, ORGANIZATION_ID,
            Apis.finance_bank_summaries.getId())
        .orElseThrow(() -> new RuntimeException("UserSyncStatus does not exist"));

    assertThat(userSyncStatusEntity.getSearchTimestamp()).isEqualTo(100);
    assertThat(userSyncStatusEntity.getSyncedAt()).isAfter(beforeSyncedAt);
  }

  @DisplayName("STEP-06: 기존 1건 + 변경 1건")
  @Test
  void step_06_getAccounts_dirtyAccount_success() throws ResponseNotOkException {
    // given
    LocalDateTime beforeSyncedAt = LocalDateTime.now(DateUtil.UTC_ZONE_ID).minusDays(1);

    AccountSummaryEntity beforeAccountSummaryEntity = createDepositAccountSummary(beforeSyncedAt);
    accountSummaryRepository.save(beforeAccountSummaryEntity);

    userSyncStatusService.updateUserSyncStatus(BANKSALAD_USER_ID, ORGANIZATION_ID, Apis.finance_bank_summaries.getId(),
        beforeSyncedAt, 100);

    setupMockServer(100, HttpStatus.OK, "mock/bank/response/BA01_006_single_page_00.json");

    ExecutionContext executionContext = getExecutionContext(wiremock.port());

    // when
    summaryService.listAccountSummaries(executionContext, Executions.finance_bank_summaries, summaryRequestHelper,
        summaryResponseHelper);

    // then
    List<AccountSummaryEntity> accountSummaryEntities = accountSummaryRepository
        .findByBanksaladUserIdAndOrganizationIdAndConsentIsTrue(BANKSALAD_USER_ID, ORGANIZATION_ID);

    assertThat(accountSummaryEntities.size()).isEqualTo(1);

    AccountSummaryEntity accountSummaryEntity = accountSummaryEntities.get(0);

    assertThat(accountSummaryEntity.getAccountNum()).isEqualTo("123123123");
    assertThat(accountSummaryEntity.getSeqno()).isEqualTo("a10230");
    assertThat(accountSummaryEntity.getAccountStatus()).isEqualTo("02");
    assertThat(accountSummaryEntity.getSyncedAt()).isAfter(beforeSyncedAt);

    UserSyncStatusEntity userSyncStatusEntity = userSyncStatusRepository
        .findByBanksaladUserIdAndOrganizationIdAndApiId(BANKSALAD_USER_ID, ORGANIZATION_ID,
            Apis.finance_bank_summaries.getId())
        .orElseThrow(() -> new RuntimeException("UserSyncStatus does not exist"));

    assertThat(userSyncStatusEntity.getSearchTimestamp()).isEqualTo(200);
    assertThat(userSyncStatusEntity.getSyncedAt()).isAfter(beforeSyncedAt);
  }

  @DisplayName("STEP-07: 기존 0건 + 3개의 페이지네이션 요청으로 4건 저장")
  @Test
  void step_07_getAccounts_multiPage_success() throws ResponseNotOkException {
    // given
    setupMockPaginationServer(0, HttpStatus.OK, null, "mock/bank/response/BA01_007_multi_page_01.json");
    setupMockPaginationServer(0, HttpStatus.OK, "02", "mock/bank/response/BA01_007_multi_page_02.json");
    setupMockPaginationServer(0, HttpStatus.OK, "03", "mock/bank/response/BA01_007_multi_page_03.json");

    ExecutionContext executionContext = getExecutionContext(wiremock.port());

    // when
    summaryService.listAccountSummaries(executionContext, Executions.finance_bank_summaries, summaryRequestHelper,
        summaryResponseHelper);

    // then
    List<AccountSummaryEntity> accountSummaryEntities = accountSummaryRepository
        .findByBanksaladUserIdAndOrganizationIdAndConsentIsTrue(BANKSALAD_USER_ID, ORGANIZATION_ID);

    assertThat(accountSummaryEntities.size()).isEqualTo(4);

    UserSyncStatusEntity userSyncStatusEntity = userSyncStatusRepository
        .findByBanksaladUserIdAndOrganizationIdAndApiId(BANKSALAD_USER_ID, ORGANIZATION_ID,
            Apis.finance_bank_summaries.getId())
        .orElseThrow(() -> new RuntimeException("UserSyncStatus does not exist"));

    assertThat(userSyncStatusEntity.getSearchTimestamp()).isEqualTo(1000);
  }

  @DisplayName("STEP-08: 마지막 페이지네이션 요청에 대한 응답이 HTTP 응답 코드가 500인 경우에 ResponseNotOkException 예외 발생")
  @Test
  void step_08_getAccounts_multiPage_httpStatusInternalServerError_throwResponseNotOkException() {
    // given
    LocalDateTime beforeSyncedAt = LocalDateTime.now(DateUtil.UTC_ZONE_ID).minusDays(1);
    userSyncStatusService.updateUserSyncStatus(BANKSALAD_USER_ID, ORGANIZATION_ID, Apis.finance_bank_summaries.getId(),
        beforeSyncedAt, 0);

    setupMockPaginationServer(0, HttpStatus.OK, null, "mock/bank/response/BA01_008_multi_page_01.json");
    setupMockPaginationServer(0, HttpStatus.INTERNAL_SERVER_ERROR, "02",
        "mock/bank/response/BA01_008_multi_page_02.json");

    ExecutionContext executionContext = getExecutionContext(wiremock.port());

    // when
    ResponseNotOkException responseNotOkException = assertThrows(ResponseNotOkException.class,
        () -> summaryService.listAccountSummaries(executionContext, Executions.finance_bank_summaries,
            summaryRequestHelper, summaryResponseHelper));

    // then
    assertThat(responseNotOkException.getStatusCode()).isEqualTo(500);
    assertThat(responseNotOkException.getResponseCode()).isEqualTo("50001");
    assertThat(responseNotOkException.getResponseMessage()).isEqualTo("시스템장애");

    List<AccountSummaryEntity> accountSummaryEntities = accountSummaryRepository
        .findByBanksaladUserIdAndOrganizationIdAndConsentIsTrue(BANKSALAD_USER_ID, ORGANIZATION_ID);

    assertThat(accountSummaryEntities.size()).isEqualTo(1);

    UserSyncStatusEntity userSyncStatusEntity = userSyncStatusRepository
        .findByBanksaladUserIdAndOrganizationIdAndApiId(BANKSALAD_USER_ID, ORGANIZATION_ID,
            Apis.finance_bank_summaries.getId())
        .orElseThrow(() -> new RuntimeException("UserSyncStatus does not exist"));

    assertThat(userSyncStatusEntity.getSearchTimestamp()).isEqualTo(0);
    assertThat(userSyncStatusEntity.getSyncedAt()).isEqualTo(beforeSyncedAt);
  }

  private AccountSummaryEntity createDepositAccountSummary(LocalDateTime syncedAt) {
    return AccountSummaryEntity.builder()
        .organizationId(ORGANIZATION_ID)
        .banksaladUserId(BANKSALAD_USER_ID)
        .accountNum("123123123")
        .seqno("a10230")
        .consent(true)
        .foreignDeposit(false)
        .prodName("자유입출금")
        .accountType("1001")
        .accountStatus("01")
        .syncedAt(syncedAt)
        .consentId("consentId")
        .build();
  }

  private ExecutionContext getExecutionContext(int port) {
    return ExecutionContext.builder()
        .banksaladUserId(BANKSALAD_USER_ID)
        .organizationId(ORGANIZATION_ID)
        .syncRequestId(UUID.randomUUID().toString())
        .executionRequestId(UUID.randomUUID().toString())
        .accessToken("test")
        .organizationCode("020")
        .organizationHost("http://localhost:" + port)
        .syncStartedAt(LocalDateTime.now(DateUtil.UTC_ZONE_ID))
        .consentId("consentId")
        .build();
  }

  private void setupMockServer(int searchTimestamp, HttpStatus httpStatus, String fileInClassPath) {
    wiremock.stubFor(get(urlMatching("/accounts.*"))
        .withQueryParam("org_code", equalTo("020"))
        .withQueryParam("search_timestamp", equalTo(String.valueOf(searchTimestamp)))
        .withQueryParam("limit", equalTo("500"))
        .willReturn(
            aResponse()
                .withStatus(httpStatus.value())
                .withHeader("Content-Type", ContentType.APPLICATION_JSON.toString())
                .withBody(readText("classpath:" + fileInClassPath))));
  }

  private void setupMockPaginationServer(int searchTimestamp, HttpStatus httpStatus, String nextPage,
      String fileInClassPath) {

    if (StringUtils.isEmpty(nextPage)) {
      setupMockServer(searchTimestamp, httpStatus, fileInClassPath);
      return;
    }

    wiremock.stubFor(get(urlMatching("/accounts.*"))
        .withQueryParam("org_code", equalTo("020"))
        .withQueryParam("search_timestamp", equalTo(String.valueOf(searchTimestamp)))
        .withQueryParam("limit", equalTo("500"))
        .withQueryParam("next_page", equalTo(nextPage))
        .willReturn(
            aResponse()
                .withStatus(httpStatus.value())
                .withHeader("Content-Type", ContentType.APPLICATION_JSON.toString())
                .withBody(readText("classpath:" + fileInClassPath))));
  }
}
