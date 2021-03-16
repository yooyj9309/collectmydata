package com.banksalad.collectmydata.insu.loan;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.WireMockSpring;
import org.springframework.http.HttpStatus;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.util.DateUtil;
import com.banksalad.collectmydata.common.util.NumberUtil;
import com.banksalad.collectmydata.insu.collect.Apis;
import com.banksalad.collectmydata.insu.common.db.entity.LoanBasicEntity;
import com.banksalad.collectmydata.insu.common.db.entity.LoanBasicHistoryEntity;
import com.banksalad.collectmydata.insu.common.db.entity.LoanSummaryEntity;
import com.banksalad.collectmydata.insu.common.db.repository.LoanBasicHistoryRepository;
import com.banksalad.collectmydata.insu.common.db.repository.LoanBasicRepository;
import com.banksalad.collectmydata.insu.common.db.repository.LoanDetailHistoryRepository;
import com.banksalad.collectmydata.insu.common.db.repository.LoanDetailRepository;
import com.banksalad.collectmydata.insu.common.db.repository.LoanSummaryRepository;
import com.banksalad.collectmydata.insu.summary.dto.LoanSummary;
import com.banksalad.collectmydata.insu.loan.dto.LoanBasic;
import com.banksalad.collectmydata.insu.loan.dto.LoanDetail;
import com.banksalad.collectmydata.insu.loan.service.LoanService;
import com.github.tomakehurst.wiremock.WireMockServer;
import org.apache.http.entity.ContentType;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static com.banksalad.collectmydata.insu.common.util.FileUtil.readText;
import static com.banksalad.collectmydata.insu.common.util.TestHelper.ACCESS_TOKEN;
import static com.banksalad.collectmydata.insu.common.util.TestHelper.ACCOUNT_NUM;
import static com.banksalad.collectmydata.insu.common.util.TestHelper.BANKSALAD_USER_ID;
import static com.banksalad.collectmydata.insu.common.util.TestHelper.CURRENCY_CODE;
import static com.banksalad.collectmydata.insu.common.util.TestHelper.ENTITY_IGNORE_FIELD;
import static com.banksalad.collectmydata.insu.common.util.TestHelper.ORGANIZATION_CODE;
import static com.banksalad.collectmydata.insu.common.util.TestHelper.ORGANIZATION_HOST;
import static com.banksalad.collectmydata.insu.common.util.TestHelper.ORGANIZATION_ID;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class LoanServiceTest {

  private static WireMockServer wireMockServer;

  @Autowired
  private LoanSummaryRepository loanSummaryRepository;

  @Autowired
  private LoanBasicRepository loanBasicRepository;

  @Autowired
  private LoanBasicHistoryRepository loanBasicHistoryRepository;

  @Autowired
  private LoanDetailRepository loanDetailRepository;

  @Autowired
  private LoanDetailHistoryRepository loanDetailHistoryRepository;

//  @Autowired
//  private UserSyncStatusRepository userSyncStatusRepository;

  @Autowired
  private LoanService loanService;

  @BeforeAll
  static void setup() {
    wireMockServer = new WireMockServer(WireMockSpring.options().dynamicPort());
    wireMockServer.start();
    setupMockServer();
  }

  @AfterEach
  public void afterEach() {
    loanSummaryRepository.deleteAll();
//    userSyncStatusRepository.deleteAll();
    loanBasicRepository.deleteAll();
    loanBasicHistoryRepository.deleteAll();
    loanDetailRepository.deleteAll();
    loanDetailHistoryRepository.deleteAll();
  }

  @AfterAll
  static void tearDown() {
    wireMockServer.shutdown();
  }

  private static void setupMockServer() {
    // 6.5.9 대출상품 기본정보 조회
    wireMockServer.stubFor(post(urlMatching("/loans/basic"))
        .withRequestBody(
            equalToJson(readText("classpath:mock/request/IS12_001_single_page_00.json")))
        .willReturn(
            aResponse()
                .withFixedDelay(1000)
                .withStatus(HttpStatus.OK.value())
                .withHeader("Content-Type", ContentType.APPLICATION_JSON.toString())
                .withBody(readText("classpath:mock/response/IS12_001_single_page_00.json"))));

    // 6.5.10 대출상품 추가정보 조회
    wireMockServer.stubFor(post(urlMatching("/loans/detail"))
        .withRequestBody(
            equalToJson(readText("classpath:mock/request/IS13_001_single_page_00.json")))
        .willReturn(
            aResponse()
                .withFixedDelay(1000)
                .withStatus(HttpStatus.OK.value())
                .withHeader("Content-Type", ContentType.APPLICATION_JSON.toString())
                .withBody(readText("classpath:mock/response/IS13_001_single_page_00.json"))));
  }

  @Test
  @DisplayName("6.5.9 (1) 대출상품 추가정보 조회: 신규 케이스")
  public void listLoanBasics_succeed_1_of_1() {
    ExecutionContext executionContext = getExecutionContext();
    List<LoanSummary> summaries = Arrays.asList(
        LoanSummary.builder()
            .accountNum(ACCOUNT_NUM)
            .build()
    );
    saveLoanSummaryEntity();

    List<LoanBasic> loanBasics = loanService.listLoanBasics(executionContext, ORGANIZATION_CODE, summaries);
    List<LoanBasicEntity> loanBasicEntities = loanBasicRepository.findAll();
    List<LoanBasicHistoryEntity> loanBasicHistoryEntities = loanBasicHistoryRepository.findAll();

    assertEquals(1, loanBasics.size());
    assertEquals(1, loanBasicEntities.size());
    assertEquals(1, loanBasicHistoryEntities.size());

    assertThat(loanBasics.get(0)).usingRecursiveComparison()
        .isEqualTo(
            LoanBasic.builder()
                .accountNum(ACCOUNT_NUM)
                .loanStartDate("20210305")
                .loanExpDate("20300506")
                .repayMethod("03")
                .insuNum("123456789")
                .build()
        );

    assertThat(loanBasics.get(0)).usingRecursiveComparison()
        .ignoringFields(ENTITY_IGNORE_FIELD)
        .isEqualTo(
            LoanBasicEntity.builder()
                .banksaladUserId(BANKSALAD_USER_ID)
                .organizationId(ORGANIZATION_ID)
                .accountNum(ACCOUNT_NUM)
                .loanStartDate("20210305")
                .loanExpDate("20300506")
                .repayMethod("03")
                .insuNum("123456789")
                .build()
        );

    assertThat(loanBasicHistoryEntities.get(0)).usingRecursiveComparison()
        .ignoringFields(ENTITY_IGNORE_FIELD)
        .isEqualTo(
            LoanBasicHistoryEntity.builder()
                .banksaladUserId(BANKSALAD_USER_ID)
                .organizationId(ORGANIZATION_ID)
                .accountNum(ACCOUNT_NUM)
                .loanStartDate("20210305")
                .loanExpDate("20300506")
                .repayMethod("03")
                .insuNum("123456789")
                .build()
        );

//    UserSyncStatusEntity userSyncStatusEntity = userSyncStatusRepository
//        .findByBanksaladUserIdAndOrganizationIdAndApiId(BANKSALAD_USER_ID, ORGANIZATION_ID,
//            Apis.insurance_get_loan_basic.getId()).get();
//    assertEquals(executionContext.getSyncStartedAt(), userSyncStatusEntity.getSyncedAt());
  }

  @Test
  @DisplayName("6.5.10 (1) 대출상품 추가정보 조회: 신규 케이스")
  public void listLoanDetails_succeed_1_of_1() {
    // Given
    ExecutionContext executionContext = getExecutionContext();
    List<LoanSummary> summaries = Arrays.asList(
        LoanSummary.builder()
            .accountNum(ACCOUNT_NUM)
            .build()
    );
    saveLoanSummaryEntity();

    LoanDetail expectedLoanDetail = LoanDetail.builder()
        .accountNum(ACCOUNT_NUM)
        .currencyCode(CURRENCY_CODE)
        .balanceAmt(NumberUtil.bigDecimalOf(125.075, 3))
        .loanPrincipal(NumberUtil.bigDecimalOf(10000.000, 3))
        .nextRepayDate("20210325")
        .build();

    // When
    List<LoanDetail> actualLoanDetails = loanService.listLoanDetails(executionContext, ORGANIZATION_CODE, summaries);

    // Then
    assertThat(loanSummaryRepository.count()).isEqualTo(1);
    assertThat(loanDetailRepository.count()).isEqualTo(1);
    assertThat(loanDetailHistoryRepository.count()).isEqualTo(1);
    assertThat(loanDetailRepository.findAll().get(0)).usingRecursiveComparison()
        .ignoringFields("id", "createdAt", "updatedAt")
        .isEqualTo(loanDetailHistoryRepository.findAll().get(0));
    assertThat(actualLoanDetails.get(0)).usingRecursiveComparison().isEqualTo(expectedLoanDetail);
    long actualDetailSearchTimestamp = loanSummaryRepository.findByBanksaladUserIdAndOrganizationIdAndAccountNum(
        BANKSALAD_USER_ID, ORGANIZATION_ID, ACCOUNT_NUM).get().getDetailSearchTimestamp();
    assertThat(actualDetailSearchTimestamp).isEqualTo(1000L);
//    assertThat(userSyncStatusRepository.count()).isEqualTo(1);
//    LocalDateTime actualSyncedAt = userSyncStatusRepository.findByBanksaladUserIdAndOrganizationIdAndApiId(
//        BANKSALAD_USER_ID, ORGANIZATION_ID, Apis.insurance_get_loan_detail.getId()).get().getSyncedAt();
//    assertThat(actualSyncedAt)
//        .isCloseTo(executionContext.getSyncStartedAt(), Assertions.within(1, ChronoUnit.MILLIS));
  }

  @Test
  @DisplayName("6.5.10 (2) 대출상품 추가정보 조회: 신규 케이스")
  public void listLoanDetails_succeed_1_of_2() {
    // Given
    ExecutionContext executionContext = getExecutionContext();
    List<LoanSummary> summaries = Arrays.asList(
        LoanSummary.builder()
            .accountNum("wrong")
            .build(),
        LoanSummary.builder()
            .accountNum(ACCOUNT_NUM)
            .build()
    );
    saveLoanSummaryEntity();

    LoanDetail expectedLoanDetail = LoanDetail.builder()
        .accountNum(ACCOUNT_NUM)
        .currencyCode(CURRENCY_CODE)
        .balanceAmt(NumberUtil.bigDecimalOf(125.075, 3))
        .loanPrincipal(NumberUtil.bigDecimalOf(10000.000, 3))
        .nextRepayDate("20210325")
        .build();

    // When
    List<LoanDetail> actualLoanDetails = loanService.listLoanDetails(executionContext, ORGANIZATION_CODE, summaries);

    // Then
    assertThat(loanSummaryRepository.count()).isEqualTo(1);
    assertThat(loanDetailRepository.count()).isEqualTo(1);
    assertThat(loanDetailHistoryRepository.count()).isEqualTo(1);
    assertThat(loanDetailRepository.findAll().get(0)).usingRecursiveComparison()
        .ignoringFields("id", "createdAt", "updatedAt")
        .isEqualTo(loanDetailHistoryRepository.findAll().get(0));
    assertThat(actualLoanDetails.get(0)).usingRecursiveComparison().isEqualTo(expectedLoanDetail);
    long actualDetailSearchTimestamp = loanSummaryRepository.findByBanksaladUserIdAndOrganizationIdAndAccountNum(
        BANKSALAD_USER_ID, ORGANIZATION_ID, ACCOUNT_NUM).get().getDetailSearchTimestamp();
    assertThat(actualDetailSearchTimestamp).isEqualTo(1000L);
//    assertThat(userSyncStatusRepository.count()).isEqualTo(0);
//    LocalDateTime actualSyncedAt = userSyncStatusRepository.findByBanksaladUserIdAndOrganizationIdAndApiId(
//        BANKSALAD_USER_ID, ORGANIZATION_ID, Apis.insurance_get_loan_detail.getId()).get().getSyncedAt();
//    Assertions.assertThat(actualSyncedAt).isEqualTo(executionContext.getSyncStartedAt());
  }

  private void saveLoanSummaryEntity() {
    loanSummaryRepository.save(LoanSummaryEntity.builder()
        .syncedAt(LocalDateTime.now(DateUtil.UTC_ZONE_ID))
        .banksaladUserId(BANKSALAD_USER_ID)
        .organizationId(ORGANIZATION_ID)
        .accountNum(ACCOUNT_NUM)
        .consent(true)
        .prodName("좋은 보험대출")
        .accountType("3400")
        .accountStatus("01")
        .detailSearchTimestamp(0L)
        .build());
  }

  private ExecutionContext getExecutionContext() {
    return ExecutionContext.builder()
        .organizationHost("http://" + ORGANIZATION_HOST + ":" + wireMockServer.port())
        .accessToken(ACCESS_TOKEN)
        .banksaladUserId(BANKSALAD_USER_ID)
        .organizationId(ORGANIZATION_ID)
        .executionRequestId(UUID.randomUUID().toString())
        .syncStartedAt(LocalDateTime.now(DateUtil.UTC_ZONE_ID))
        .build();
  }
}
