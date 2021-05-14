package com.banksalad.collectmydata.bank.deposit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.WireMockSpring;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;

import com.banksalad.collectmydata.bank.common.collect.Executions;
import com.banksalad.collectmydata.bank.common.db.entity.AccountSummaryEntity;
import com.banksalad.collectmydata.bank.common.db.entity.DepositAccountDetailEntity;
import com.banksalad.collectmydata.bank.common.db.entity.DepositAccountDetailHistoryEntity;
import com.banksalad.collectmydata.bank.common.db.repository.AccountSummaryRepository;
import com.banksalad.collectmydata.bank.common.db.repository.DepositAccountDetailHistoryRepository;
import com.banksalad.collectmydata.bank.common.db.repository.DepositAccountDetailRepository;
import com.banksalad.collectmydata.bank.deposit.dto.DepositAccountDetail;
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
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static com.banksalad.collectmydata.bank.testutil.FileUtil.readText;
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.RSP_CODE_SUCCESS;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@DisplayName("6.2.3 수신계좌 추가정보 조회")
@Transactional
public class DepositAccountDetailServiceTest {

  @Autowired
  private AccountInfoService<AccountSummary, GetDepositAccountDetailRequest, List<DepositAccountDetail>> depositAccountDetailService;

  @Autowired
  private AccountInfoRequestHelper<GetDepositAccountDetailRequest, AccountSummary> depositAccountDetailInfoRequestHelper;

  @Autowired
  private AccountInfoResponseHelper<AccountSummary, List<DepositAccountDetail>> depositAccountDetailInfoResponseHelper;

  @Autowired
  private AccountSummaryRepository accountSummaryRepository;

  @Autowired
  private DepositAccountDetailRepository depositAccountDetailRepository;

  @Autowired
  private DepositAccountDetailHistoryRepository depositAccountDetailHistoryRepository;

  @Autowired
  private UserSyncStatusRepository userSyncStatusRepository;

  private static WireMockServer wireMockServer;

  private final LocalDateTime SYNCED_AT = LocalDateTime.of(2021, 01, 01, 0, 0);
  private static final Long BANKSALAD_USER_ID = 1L;
  private static final String ORGANIZATION_ID = "bank1";
  private static final String ORGANIZATION_CODE = "bank001";
  private static final String ORGANIZATION_HOST = "http://localhost";
  private static final String ACCESS_TOKEN = "xxx.yyy.zzz";
  private static final String CONSENT_ID = "consentId";
  private static final String SYNC_REQUEST_ID = UUID.randomUUID().toString();

  private static final String ACCOUNT_NUM = "1234567890";
  private static final String SEQNO1 = "1";
  public static final String PRODUCT_NAME = "product_name";
  public static final String ACCOUNT_TYPE = "1002";
  public static final String ACCOUNT_STATUS = "01";

  private static final String[] ENTITY_IGNORE_FIELD = {"id", "syncedAt", "createdAt", "createdBy", "updatedAt",
      "updatedBy", "consentId", "syncRequestId"};

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
  @DisplayName("#1 존재하지 않거나 해지된 계좌 번호건 : 기존 0건 + 실패 1건")
  void getDepositAccountDetail_failure_01() {
    // given
    setupMockServer1();
    ExecutionContext executionContext = getExecutionContext(wireMockServer.port());

    AccountSummaryEntity accountSummaryEntity = getAccountSummaryEntity(1000L);
    accountSummaryRepository.save(accountSummaryEntity);

    // when
    depositAccountDetailService.listAccountInfos(executionContext, Executions.finance_bank_deposit_account_detail,
        depositAccountDetailInfoRequestHelper, depositAccountDetailInfoResponseHelper);

    // then
    /* check #1 : userSyncStatusEntity - syncedAt */
    UserSyncStatusEntity userSyncStatusEntity = userSyncStatusRepository
        .findByBanksaladUserIdAndOrganizationIdAndApiId(BANKSALAD_USER_ID, ORGANIZATION_ID,
            Executions.finance_bank_deposit_account_detail.getApi().getId())
        .orElseGet(() -> UserSyncStatusEntity.builder().build());
    assertThat(userSyncStatusEntity.getSyncedAt())
        .isEqualTo(executionContext.getSyncStartedAt());

    /* check #2 : accountSummary - syncedAt, responseCode, searchTimestamp */
    assertThat(accountSummaryRepository.findAll().get(0).getSyncedAt()).isEqualTo(SYNCED_AT);
    assertThat(accountSummaryRepository.findAll().get(0).getDetailResponseCode()).isEqualTo("40404");
    assertThat(accountSummaryRepository.findAll().get(0).getDetailSearchTimestamp()).isEqualTo(1000L);

    /* check #3 : accountDetailEntity */
    assertThat(depositAccountDetailRepository.findAll().isEmpty()).isTrue();

    /* check #4 : accountDetailHistoryEntity */
    assertThat(depositAccountDetailHistoryRepository.findAll().isEmpty()).isTrue();
  }

  @Test
  @DisplayName("#2 기존 0건 + 신규 1건")
  void getDepositAccountDetail_success_01() {
    // given
    setupMockServer2();
    ExecutionContext executionContext = getExecutionContext(wireMockServer.port());

    AccountSummaryEntity accountSummaryEntity = getAccountSummaryEntity(0L);
    accountSummaryRepository.save(accountSummaryEntity);

    // when
    depositAccountDetailService.listAccountInfos(executionContext, Executions.finance_bank_deposit_account_detail,
        depositAccountDetailInfoRequestHelper, depositAccountDetailInfoResponseHelper);

    // then
    /* check #1 : userSyncStatusEntity - syncedAt */
    UserSyncStatusEntity userSyncStatusEntity = userSyncStatusRepository
        .findByBanksaladUserIdAndOrganizationIdAndApiId(BANKSALAD_USER_ID, ORGANIZATION_ID,
            Executions.finance_bank_deposit_account_detail.getApi().getId())
        .orElseGet(() -> UserSyncStatusEntity.builder().build());
    assertThat(userSyncStatusEntity.getSyncedAt())
        .isEqualTo(executionContext.getSyncStartedAt());

    /* check #2 : accountSummaryEntity - searchTimestamp */
    assertThat(accountSummaryRepository.findAll().size()).isEqualTo(1);
    assertThat(accountSummaryRepository.findAll().get(0).getDetailSearchTimestamp())
        .isEqualTo(1000);

    /* check #3 : accountDetailEntity */
    assertThat(depositAccountDetailRepository.findAll().size()).isEqualTo(1);
    assertThat(depositAccountDetailRepository.findAll()).usingRecursiveComparison().ignoringFields(ENTITY_IGNORE_FIELD)
        .isEqualTo(List.of(getDepositAccountDetailEntity()));

    /* check #4 : accountDetailEntity and accountDetailHistoryEntity */
    assertThat(depositAccountDetailRepository.findAll()).usingRecursiveComparison().ignoringFields(ENTITY_IGNORE_FIELD)
        .isEqualTo(depositAccountDetailHistoryRepository.findAll());
  }

  @Test
  @DisplayName("#3 기존 1건 + 신규 1건")
  void getDepositAccountDetail_success_02() {
    // given
    setupMockServer3();
    ExecutionContext executionContext = getExecutionContext(wireMockServer.port());

    accountSummaryRepository.saveAll(List.of(
        getAccountSummaryEntity(1000L),
        getAccountSummaryEntitySecond(0L))
    );
    depositAccountDetailRepository.save(getDepositAccountDetailEntity());
    depositAccountDetailHistoryRepository.save(getDepositAccountDetailHistoryEntity());

    // when
    depositAccountDetailService.listAccountInfos(executionContext, Executions.finance_bank_deposit_account_detail,
        depositAccountDetailInfoRequestHelper, depositAccountDetailInfoResponseHelper);

    // then
    /* check #1 : userSyncStatusEntity - syncedAt */
    UserSyncStatusEntity userSyncStatusEntity = userSyncStatusRepository
        .findByBanksaladUserIdAndOrganizationIdAndApiId(BANKSALAD_USER_ID, ORGANIZATION_ID,
            Executions.finance_bank_deposit_account_detail.getApi().getId())
        .orElseGet(() -> UserSyncStatusEntity.builder().build());
    assertThat(userSyncStatusEntity.getSyncedAt())
        .isEqualTo(executionContext.getSyncStartedAt());

    /* check #2 : accountSummaryEntity - searchTimestamp */
    assertThat(accountSummaryRepository.findAll().size()).isEqualTo(2);
    assertThat(accountSummaryRepository.findAll().get(0).getDetailSearchTimestamp())
        .isEqualTo(1000);
    assertThat(accountSummaryRepository.findAll().get(1).getDetailSearchTimestamp())
        .isEqualTo(2000);

    /* check #3 : accountDetailEntity */
    assertThat(depositAccountDetailRepository.findAll().size()).isEqualTo(2);
    assertThat(depositAccountDetailRepository.findAll()).usingRecursiveComparison().ignoringFields(ENTITY_IGNORE_FIELD)
        .isEqualTo(List.of(
            getDepositAccountDetailEntity(),
            DepositAccountDetailEntity.builder()
                .syncedAt(SYNCED_AT)
                .banksaladUserId(BANKSALAD_USER_ID)
                .organizationId(ORGANIZATION_ID)
                .accountNum(ACCOUNT_NUM + "2")
                .seqno(SEQNO1)
                .currencyCode("KRW")
                .balanceAmt(BigDecimal.valueOf(10000100, 3))
                .withdrawableAmt(BigDecimal.valueOf(2000200, 3))
                .offeredRate(BigDecimal.valueOf(3030000, 5))
                .lastPaidInCnt(null)
                .consentId(CONSENT_ID)
                .syncRequestId(SYNC_REQUEST_ID)
                .build())
        );
    assertThat(depositAccountDetailRepository.findAll().get(1).getSyncedAt())
        .isEqualTo(executionContext.getSyncStartedAt());

    /* check #4 : accountDetailEntity and accountDetailHistoryEntity */
    assertThat(depositAccountDetailRepository.findAll()).usingRecursiveComparison().ignoringFields(ENTITY_IGNORE_FIELD)
        .isEqualTo(depositAccountDetailHistoryRepository.findAll());
  }

  @Test
  @DisplayName("#4 기존 1건 + 동일 1건")
  void getDepositAccountDetail_success_03() {
    // given
    setupMockServer4();
    ExecutionContext executionContext = getExecutionContext(wireMockServer.port());

    accountSummaryRepository.save(getAccountSummaryEntity(1000L));
    depositAccountDetailRepository.save(getDepositAccountDetailEntity());
    depositAccountDetailHistoryRepository.save(getDepositAccountDetailHistoryEntity());

    // when
    depositAccountDetailService.listAccountInfos(executionContext, Executions.finance_bank_deposit_account_detail,
        depositAccountDetailInfoRequestHelper, depositAccountDetailInfoResponseHelper);

    // then
    /* check #1 : userSyncStatusEntity - syncedAt */
    UserSyncStatusEntity userSyncStatusEntity = userSyncStatusRepository
        .findByBanksaladUserIdAndOrganizationIdAndApiId(BANKSALAD_USER_ID, ORGANIZATION_ID,
            Executions.finance_bank_deposit_account_detail.getApi().getId())
        .orElseGet(() -> UserSyncStatusEntity.builder().build());
    assertThat(userSyncStatusEntity.getSyncedAt())
        .isEqualTo(executionContext.getSyncStartedAt());

    /* check #2 : accountSummaryEntity */
    assertThat(accountSummaryRepository.findAll().size()).isEqualTo(1);
    assertThat(accountSummaryRepository.findAll().get(0)).usingRecursiveComparison().ignoringFields(ENTITY_IGNORE_FIELD)
        .isEqualTo(getAccountSummaryEntity(1000L, RSP_CODE_SUCCESS));

    /* check #3 : accountDetailEntity */
    assertThat(depositAccountDetailRepository.findAll().size()).isEqualTo(1);
    assertThat(depositAccountDetailRepository.findAll()).usingRecursiveComparison().ignoringFields(ENTITY_IGNORE_FIELD)
        .isEqualTo(List.of(getDepositAccountDetailEntity()));

    /* check #4 : accountDetailEntity and accountDetailHistoryEntity */
    assertThat(depositAccountDetailRepository.findAll()).usingRecursiveComparison().ignoringFields(ENTITY_IGNORE_FIELD)
        .isEqualTo(depositAccountDetailHistoryRepository.findAll());
  }

  @Test
  @DisplayName("#5 기존 1건 + 변경 1건")
  void getDepositAccountDetail_success_04() {
    // given
    setupMockServer5();
    ExecutionContext executionContext = getExecutionContext(wireMockServer.port());

    accountSummaryRepository.save(getAccountSummaryEntity(1000L));
    depositAccountDetailRepository.save(getDepositAccountDetailEntity());
    depositAccountDetailHistoryRepository.save(getDepositAccountDetailHistoryEntity());

    // when
    depositAccountDetailService.listAccountInfos(executionContext, Executions.finance_bank_deposit_account_detail,
        depositAccountDetailInfoRequestHelper, depositAccountDetailInfoResponseHelper);

    // then
    /* check #1 : userSyncStatusEntity - syncedAt */
    UserSyncStatusEntity userSyncStatusEntity = userSyncStatusRepository
        .findByBanksaladUserIdAndOrganizationIdAndApiId(BANKSALAD_USER_ID, ORGANIZATION_ID,
            Executions.finance_bank_deposit_account_detail.getApi().getId())
        .orElseGet(() -> UserSyncStatusEntity.builder().build());
    assertThat(userSyncStatusEntity.getSyncedAt())
        .isEqualTo(executionContext.getSyncStartedAt());

    /* check #2 : accountSummaryEntity */
    assertThat(accountSummaryRepository.findAll().size()).isEqualTo(1);
    assertThat(accountSummaryRepository.findAll().get(0)).usingRecursiveComparison().ignoringFields(ENTITY_IGNORE_FIELD)
        .isEqualTo(getAccountSummaryEntity(2000L, RSP_CODE_SUCCESS));

    /* check #3 : accountDetailEntity - syncedAt, holderName */
    assertThat(depositAccountDetailRepository.findAll().size()).isEqualTo(1);
    assertThat(depositAccountDetailRepository.findAll().get(0).getSyncedAt())
        .isEqualTo(executionContext.getSyncStartedAt());
    assertThat(depositAccountDetailRepository.findAll()).usingRecursiveComparison().ignoringFields(ENTITY_IGNORE_FIELD)
        .isEqualTo(List.of(
            DepositAccountDetailEntity.builder()
                .syncedAt(SYNCED_AT)
                .banksaladUserId(BANKSALAD_USER_ID)
                .organizationId(ORGANIZATION_ID)
                .accountNum(ACCOUNT_NUM)
                .seqno(SEQNO1)
                .currencyCode("KRW")
                .balanceAmt(BigDecimal.valueOf(50000500, 3))
                .withdrawableAmt(BigDecimal.valueOf(2000200, 3))
                .offeredRate(BigDecimal.valueOf(3030000, 5))
                .lastPaidInCnt(null)
                .consentId(CONSENT_ID)
                .syncRequestId(SYNC_REQUEST_ID)
                .build()
        ));

    /* check #4 : accountDetailEntity and accountDetailHistoryEntity - holderName, syncedAt */
    assertThat(depositAccountDetailHistoryRepository.findAll().size()).isEqualTo(2);
    assertThat(depositAccountDetailHistoryRepository.findAll().get(0).getBalanceAmt())
        .isEqualTo(BigDecimal.valueOf(10000100, 3));
    assertThat(depositAccountDetailHistoryRepository.findAll().get(0).getSyncedAt()).isEqualTo(SYNCED_AT);
    assertThat(depositAccountDetailHistoryRepository.findAll().get(1).getBalanceAmt())
        .isEqualTo(BigDecimal.valueOf(50000500, 3));
    assertThat(depositAccountDetailHistoryRepository.findAll().get(1).getSyncedAt())
        .isEqualTo(executionContext.getSyncStartedAt());
  }

  private static ExecutionContext getExecutionContext(int port) {
    return ExecutionContext.builder()
        .consentId(CONSENT_ID)
        .syncRequestId(SYNC_REQUEST_ID)
        .banksaladUserId(BANKSALAD_USER_ID)
        .organizationId(ORGANIZATION_ID)
        .executionRequestId(UUID.randomUUID().toString())
        .organizationCode(ORGANIZATION_CODE)
        .organizationHost(ORGANIZATION_HOST + ":" + port)
        .accessToken(ACCESS_TOKEN)
        .syncStartedAt(LocalDateTime.now(DateUtil.UTC_ZONE_ID))
        .build();
  }

  private AccountSummaryEntity getAccountSummaryEntity(Long detailSearchTimestamp) {
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
        .detailSearchTimestamp(detailSearchTimestamp)
        .consentId(CONSENT_ID)
        .build();
  }

  private AccountSummaryEntity getAccountSummaryEntity(Long detailSearchTimestamp, String responseCode) {
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
        .detailSearchTimestamp(detailSearchTimestamp)
        .consentId(CONSENT_ID)
        .detailResponseCode(responseCode)
        .build();
  }

  private AccountSummaryEntity getAccountSummaryEntitySecond(Long detailSearchTimestamp) {
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
        .detailSearchTimestamp(detailSearchTimestamp)
        .consentId(CONSENT_ID)
        .build();
  }

  private DepositAccountDetailEntity getDepositAccountDetailEntity() {
    return DepositAccountDetailEntity.builder()
        .syncedAt(SYNCED_AT)
        .banksaladUserId(BANKSALAD_USER_ID)
        .organizationId(ORGANIZATION_ID)
        .accountNum(ACCOUNT_NUM)
        .seqno(SEQNO1)
        .currencyCode("KRW")
        .balanceAmt(BigDecimal.valueOf(10000100, 3))
        .withdrawableAmt(BigDecimal.valueOf(2000200, 3))
        .offeredRate(BigDecimal.valueOf(3030000, 5))
        .lastPaidInCnt(null)
        .consentId(CONSENT_ID)
        .syncRequestId(SYNC_REQUEST_ID)
        .build();
  }

  private DepositAccountDetailHistoryEntity getDepositAccountDetailHistoryEntity() {
    return DepositAccountDetailHistoryEntity.builder()
        .syncedAt(SYNCED_AT)
        .banksaladUserId(BANKSALAD_USER_ID)
        .organizationId(ORGANIZATION_ID)
        .accountNum(ACCOUNT_NUM)
        .seqno(SEQNO1)
        .currencyCode("KRW")
        .balanceAmt(BigDecimal.valueOf(10000100, 3))
        .withdrawableAmt(BigDecimal.valueOf(2000200, 3))
        .offeredRate(BigDecimal.valueOf(3030000, 5))
        .lastPaidInCnt(null)
        .consentId(CONSENT_ID)
        .syncRequestId(SYNC_REQUEST_ID)
        .build();
  }

  private static void setupMockServer1() {
    // #1 : 존재하지 않거나 해지된 계좌 번호건 : 기존 0건 + 실패 1건
    wireMockServer.stubFor(post(urlMatching("/accounts/deposit/detail"))
        .withRequestBody(
            equalToJson(readText("classpath:mock/bank/request/BA03_001_single_page_01.json")))
        .willReturn(
            aResponse()
                .withStatus(HttpStatus.NOT_FOUND.value())
                .withHeader("Content-Type", ContentType.APPLICATION_JSON.toString())
                .withBody(readText("classpath:mock/bank/response/BA03_001_single_page_01.json"))));
  }

  private static void setupMockServer2() {
    // #2 : 기존 0건 + 신규 1건
    wireMockServer.stubFor(post(urlMatching("/accounts/deposit/detail"))
        .withRequestBody(
            equalToJson(readText("classpath:mock/bank/request/BA03_002_single_page_01.json")))
        .willReturn(
            aResponse()
                .withStatus(HttpStatus.OK.value())
                .withHeader("Content-Type", ContentType.APPLICATION_JSON.toString())
                .withBody(readText("classpath:mock/bank/response/BA03_002_single_page_01.json"))));
  }

  private static void setupMockServer3() {
    // #3 : 기존 1건 + 신규 1건
    wireMockServer.stubFor(post(urlMatching("/accounts/deposit/detail"))
        .withRequestBody(
            equalToJson(readText("classpath:mock/bank/request/BA03_003_single_page_01.json")))
        .willReturn(
            aResponse()
                .withStatus(HttpStatus.OK.value())
                .withHeader("Content-Type", ContentType.APPLICATION_JSON.toString())
                .withBody(readText("classpath:mock/bank/response/BA03_003_single_page_01.json"))));

    wireMockServer.stubFor(post(urlMatching("/accounts/deposit/detail"))
        .withRequestBody(
            equalToJson(readText("classpath:mock/bank/request/BA03_003_single_page_02.json")))
        .willReturn(
            aResponse()
                .withStatus(HttpStatus.OK.value())
                .withHeader("Content-Type", ContentType.APPLICATION_JSON.toString())
                .withBody(readText("classpath:mock/bank/response/BA03_003_single_page_02.json"))));
  }

  private static void setupMockServer4() {
    // #4 : 기존 1건 + 동일 1건
    wireMockServer.stubFor(post(urlMatching("/accounts/deposit/detail"))
        .withRequestBody(
            equalToJson(readText("classpath:mock/bank/request/BA03_004_single_page_01.json")))
        .willReturn(
            aResponse()
                .withStatus(HttpStatus.OK.value())
                .withHeader("Content-Type", ContentType.APPLICATION_JSON.toString())
                .withBody(readText("classpath:mock/bank/response/BA03_004_single_page_01.json"))));
  }

  private static void setupMockServer5() {
    // #5 : 기존 1건 + 변경 1건
    wireMockServer.stubFor(post(urlMatching("/accounts/deposit/detail"))
        .withRequestBody(
            equalToJson(readText("classpath:mock/bank/request/BA03_005_single_page_01.json")))
        .willReturn(
            aResponse()
                .withStatus(HttpStatus.OK.value())
                .withHeader("Content-Type", ContentType.APPLICATION_JSON.toString())
                .withBody(readText("classpath:mock/bank/response/BA03_005_single_page_01.json"))));
  }
}
