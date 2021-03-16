package com.banksalad.collectmydata.capital.oplease;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.WireMockSpring;
import org.springframework.http.HttpStatus;

import com.banksalad.collectmydata.capital.common.db.entity.AccountSummaryEntity;
import com.banksalad.collectmydata.capital.common.db.entity.OperatingLeaseEntity;
import com.banksalad.collectmydata.capital.common.db.entity.OperatingLeaseHistoryEntity;
import com.banksalad.collectmydata.capital.common.db.entity.OperatingLeaseTransactionEntity;
import com.banksalad.collectmydata.capital.common.db.repository.AccountSummaryRepository;
import com.banksalad.collectmydata.capital.common.db.repository.OperatingLeaseHistoryRepository;
import com.banksalad.collectmydata.capital.common.db.repository.OperatingLeaseRepository;
import com.banksalad.collectmydata.capital.common.db.repository.OperatingLeaseTransactionRepository;
import com.banksalad.collectmydata.capital.common.dto.Organization;
import com.banksalad.collectmydata.capital.oplease.dto.OperatingLease;
import com.banksalad.collectmydata.capital.oplease.dto.OperatingLeaseTransaction;
import com.banksalad.collectmydata.capital.summary.dto.AccountSummary;
import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.util.DateUtil;
import com.banksalad.collectmydata.finance.common.db.repository.UserSyncStatusRepository;
import com.github.tomakehurst.wiremock.WireMockServer;
import org.apache.http.entity.ContentType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

import static com.banksalad.collectmydata.capital.common.TestHelper.ACCESS_TOKEN;
import static com.banksalad.collectmydata.capital.common.TestHelper.ACCOUNT_NUMBER;
import static com.banksalad.collectmydata.capital.common.TestHelper.ACCOUNT_STATUS;
import static com.banksalad.collectmydata.capital.common.TestHelper.ACCOUNT_TYPE;
import static com.banksalad.collectmydata.capital.common.TestHelper.BANKSALAD_USER_ID;
import static com.banksalad.collectmydata.capital.common.TestHelper.INDUSTRY;
import static com.banksalad.collectmydata.capital.common.TestHelper.ORGANIZATION_CODE;
import static com.banksalad.collectmydata.capital.common.TestHelper.ORGANIZATION_HOST;
import static com.banksalad.collectmydata.capital.common.TestHelper.ORGANIZATION_ID;
import static com.banksalad.collectmydata.capital.common.TestHelper.PRODUCT_NAME;
import static com.banksalad.collectmydata.capital.common.TestHelper.SECTOR;
import static com.banksalad.collectmydata.capital.common.TestHelper.SEQNO1;
import static com.banksalad.collectmydata.capital.common.TestHelper.TRANS_AMT;
import static com.banksalad.collectmydata.capital.common.TestHelper.TRANS_NO;
import static com.banksalad.collectmydata.capital.common.TestHelper.TRANS_TYPE;
import static com.banksalad.collectmydata.capital.util.FileUtil.readText;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static java.lang.Boolean.TRUE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@DisplayName("OperatingLeaseService Test")
public class OperatingLeaseServiceTest {

  @Autowired
  private OperatingLeaseService operatingLeaseService;

  @Autowired
  private OperatingLeaseRepository operatingLeaseRepository;

  @Autowired
  private OperatingLeaseHistoryRepository operatingLeaseHistoryRepository;

  @Autowired
  private AccountSummaryRepository accountSummaryRepository;

  @Autowired
  private UserSyncStatusRepository userSyncStatusRepository;

  @Autowired
  private OperatingLeaseTransactionRepository operatingLeaseTransactionRepository;

  private long banksaladUserId = 1L;
  private String organizationId = "shinhancard";
  private String accountNum = "1234567890";

  private static WireMockServer wireMockServer;

  @BeforeAll
  static void setup() {
    wireMockServer = new WireMockServer(WireMockSpring.options().dynamicPort());
    wireMockServer.start();
    setupMockServer();
  }

  @AfterEach
  private void after() {
    operatingLeaseRepository.deleteAll();
    operatingLeaseHistoryRepository.deleteAll();
    accountSummaryRepository.deleteAll();
    userSyncStatusRepository.deleteAll();
  }

  @Test
  @DisplayName("6.7.5 운용리스 기본정보 조회 서비스 로직 성공케이스")
  public void getOperatingLeaseBasic_firstInflow() {

    LocalDateTime firstTime = LocalDateTime.now();
    ExecutionContext context = generateExecutionContext(firstTime);

    Organization organization = Organization.builder()
        .organizationCode("10041004").build();

    AccountSummary accountSummary = AccountSummary.builder()
        .accountNum(accountNum)
        .seqno("1")
        .build();

    accountSummaryRepository.save(
        AccountSummaryEntity.builder()
            .syncedAt(LocalDateTime.now())
            .banksaladUserId(banksaladUserId)
            .organizationId(organizationId)
            .accountNum(accountNum)
            .seqno("1")
            .isConsent(true)
            .prodName("prodName")
            .accountType("")
            .accountStatus("")
            .build()
    );

    List<AccountSummary> accountSummaries = List.of(accountSummary);
    List<OperatingLease> operatingLeases = operatingLeaseService
        .listOperatingLeases(context, organization, accountSummaries);

    List<OperatingLeaseEntity> operatingLeaseEntities = operatingLeaseRepository.findAll();
    List<OperatingLeaseHistoryEntity> operatingLeaseHistoryEntities = operatingLeaseHistoryRepository.findAll();
    validateResult(firstTime, firstTime, operatingLeaseEntities, operatingLeaseHistoryEntities, operatingLeases);

    // 재조회시 히스토리 중첩여부 테스트
    LocalDateTime recentTime = LocalDateTime.now();
    context = generateExecutionContext(recentTime);
    operatingLeases = operatingLeaseService.listOperatingLeases(context, organization, accountSummaries);

    operatingLeaseEntities = operatingLeaseRepository.findAll();
    operatingLeaseHistoryEntities = operatingLeaseHistoryRepository.findAll();
    validateResult(firstTime, recentTime, operatingLeaseEntities, operatingLeaseHistoryEntities, operatingLeases);
  }

  @Test
  @DisplayName("6.7.6 운용리스 거래내역 조회 성공")
  void givenRequest_whenListOperatingLeaseTransactions_thenSuccess() {
    // given
    ExecutionContext executionContext = getExecutionContext();
    Organization organization = getOrganization();
    AccountSummary accountSummary = getAccountSummary();
    saveAccountSummaryEntity();

    // when
    List<OperatingLeaseTransaction> operatingLeaseTransactions = operatingLeaseService
        .listOperatingLeaseTransactions(executionContext, organization, List.of(accountSummary));

    // then
    assertThat(operatingLeaseTransactions).usingRecursiveComparison().isEqualTo(
        List.of(
            OperatingLeaseTransaction.builder()
                .transDtime("20210301010100")
                .transNo("1")
                .transType("01")
                .transAmt(BigDecimal.valueOf(100.001))
                .build(),
            OperatingLeaseTransaction.builder()
                .transDtime("20210302020100")
                .transNo("1")
                .transType("01")
                .transAmt(BigDecimal.valueOf(100.001))
                .build()
        )
    );
    assertThat(operatingLeaseTransactionRepository.findAll().size()).isEqualTo(operatingLeaseTransactions.size());
    assertThat(operatingLeaseTransactionRepository.findAll().get(0)).usingRecursiveComparison().isEqualTo(
        operatingLeaseTransactionRepository
            .findByBanksaladUserIdAndOrganizationIdAndAccountNumAndSeqnoAndTransDtimeAndTransNoAndTransactionYearMonth(
                executionContext.getBanksaladUserId(),
                executionContext.getOrganizationId(),
                accountSummary.getAccountNum(),
                accountSummary.getSeqno(),
                operatingLeaseTransactions.get(0).getTransDtime(),
                operatingLeaseTransactions.get(0).getTransNo(),
                generateTransactionYearMonth(operatingLeaseTransactions.get(0)))
            .orElse(OperatingLeaseTransactionEntity.builder().build())
    );
  }

  private void validateResult(LocalDateTime firstTime, LocalDateTime recentTime,
      List<OperatingLeaseEntity> operatingLeaseEntities,
      List<OperatingLeaseHistoryEntity> operatingLeaseHistoryEntities, List<OperatingLease> operatingLeases) {
    assertEquals(1, operatingLeaseEntities.size());
    assertThat(operatingLeaseEntities.get(0)).usingRecursiveComparison()
        .ignoringFields("id", "createdAt", "updatedAt", "syncedAt")
        .isEqualTo(
            OperatingLeaseEntity.builder()
                .syncedAt(firstTime)
                .banksaladUserId(banksaladUserId)
                .organizationId(organizationId)
                .accountNum(accountNum)
                .seqno("1")
                .holderName("김뱅셀")
                .issueDate(LocalDate.parse("20210210", DateTimeFormatter.ofPattern("yyyyMMdd")))
                .expDate(LocalDate.parse("20221231", DateTimeFormatter.ofPattern("yyyyMMdd")))
                .repayDate("03")
                .repayMethod("01")
                .repayOrgCode("B01")
                .repayAccountNum("11022212345")
                .nextRepayDate(LocalDate.parse("20211114", DateTimeFormatter.ofPattern("yyyyMMdd")))
                .build()
        );

    assertEquals(1, operatingLeaseHistoryEntities.size());
    assertThat(operatingLeaseHistoryEntities.get(0)).usingRecursiveComparison()
        .ignoringFields("id", "createdAt", "updatedAt", "syncedAt")
        .isEqualTo(
            OperatingLeaseHistoryEntity.builder()
                .syncedAt(firstTime)
                .banksaladUserId(banksaladUserId)
                .organizationId(organizationId)
                .accountNum(accountNum)
                .seqno("1")
                .holderName("김뱅셀")
                .issueDate(LocalDate.parse("20210210", DateTimeFormatter.ofPattern("yyyyMMdd")))
                .expDate(LocalDate.parse("20221231", DateTimeFormatter.ofPattern("yyyyMMdd")))
                .repayDate("03")
                .repayMethod("01")
                .repayOrgCode("B01")
                .repayAccountNum("11022212345")
                .nextRepayDate(LocalDate.parse("20211114", DateTimeFormatter.ofPattern("yyyyMMdd")))
                .build()
        );

    assertEquals(1, operatingLeases.size());
    assertThat(operatingLeases.get(0)).usingRecursiveComparison()
        .isEqualTo(
            OperatingLease.builder()
                .accountNum(accountNum)
                .seqno("1")
                .holderName("김뱅셀")
                .issueDate("20210210")
                .expDate("20221231")
                .repayDate("03")
                .repayMethod("01")
                .repayOrgCode("B01")
                .repayAccountNum("11022212345")
                .nextRepayDate("20211114")
                .build()
        );
  }

  private List<OperatingLeaseTransaction> getOperatingLeaseTransactions(LocalDate fromDate) {
    LocalDateTime firstTransDtime = DateUtil.toLocalDateTime(DateUtil.toDateString(fromDate), "010100");
    LocalDateTime secondTransDtime = DateUtil.toLocalDateTime(DateUtil.toDateString(fromDate), "020100");
    OperatingLeaseTransaction firstOperatingLeaseTransaction = OperatingLeaseTransaction.builder()
        .transDtime(DateUtil.toDateString(firstTransDtime))
        .transNo(TRANS_NO)
        .transType(TRANS_TYPE)
        .transAmt(TRANS_AMT)
        .build();
    OperatingLeaseTransaction secondOperatingLeaseTransaction = OperatingLeaseTransaction.builder()
        .transDtime(DateUtil.toDateString(secondTransDtime))
        .transNo(TRANS_NO)
        .transType(TRANS_TYPE)
        .transAmt(TRANS_AMT)
        .build();
    List<OperatingLeaseTransaction> operatingLeaseTransactions = List
        .of(firstOperatingLeaseTransaction, secondOperatingLeaseTransaction);

    return operatingLeaseTransactions;
  }

  /* test helper */
  private void saveAccountSummaryEntity() {
    accountSummaryRepository.save(
        AccountSummaryEntity.builder()
            .syncedAt(LocalDateTime.now())
            .banksaladUserId(BANKSALAD_USER_ID)
            .organizationId(ORGANIZATION_ID)
            .accountNum(ACCOUNT_NUMBER)
            .seqno(SEQNO1)
            .isConsent(true)
            .prodName("prodName")
            .accountType("")
            .accountStatus("")
            .build()
    );
  }

  private AccountSummary getAccountSummary() {
    LocalDateTime givenTransactionSyncedAt = LocalDateTime.of(2021, 3, 1, 1, 0, 0);
    return AccountSummary.builder()
        .accountNum(ACCOUNT_NUMBER)
        .isConsent(TRUE)
        .seqno(SEQNO1)
        .prodName(PRODUCT_NAME)
        .accountType(ACCOUNT_TYPE)
        .accountStatus(ACCOUNT_STATUS)
        .operatingLeaseTransactionSyncedAt(givenTransactionSyncedAt)
        .build();
  }

  private ExecutionContext getExecutionContext() {
    LocalDateTime currentSyncStartedAt = LocalDateTime.of(2021, 4, 1, 1, 0, 0);
    return ExecutionContext.builder()
        .organizationHost("http://localhost:" + wireMockServer.port())
        .accessToken(ACCESS_TOKEN)
        .banksaladUserId(BANKSALAD_USER_ID)
        .organizationId(ORGANIZATION_ID)
        .executionRequestId(UUID.randomUUID().toString())
        .syncStartedAt(currentSyncStartedAt)
        .build();
  }

  private Organization getOrganization() {
    return Organization.builder()
        .sector(SECTOR)
        .industry(INDUSTRY)
        .organizationId(ORGANIZATION_ID)
        .organizationCode(ORGANIZATION_CODE)
        .domain(ORGANIZATION_HOST)
        .build();
  }

  private ExecutionContext generateExecutionContext(LocalDateTime now) {
    return ExecutionContext.builder()
        .organizationId(organizationId)
        .banksaladUserId(banksaladUserId)
        .syncStartedAt(now)
        .accessToken("accessToken")
        .organizationHost("http://localhost:" + wireMockServer.port())
        .build();
  }

  private int generateTransactionYearMonth(OperatingLeaseTransaction operatingLeaseTransaction) {
    String transDtime = operatingLeaseTransaction.getTransDtime();
    String yearMonthString = transDtime.substring(0, 6);

    return Integer.valueOf(yearMonthString);
  }

  private static void setupMockServer() {
    // 6.7.5 운용리스 기본정보 조회
    wireMockServer.stubFor(post(urlMatching("/loans/oplease/basic"))
        .withRequestBody(
            equalToJson(readText("classpath:mock/request/CP05_001.json")))
        .willReturn(
            aResponse()
                .withFixedDelay(1000)
                .withStatus(HttpStatus.OK.value())
                .withHeader("Content-Type", ContentType.APPLICATION_JSON.toString())
                .withBody(readText("classpath:mock/response/CP05_001.json"))));

    wireMockServer.stubFor(post(urlMatching("/loans/oplease/basic"))
        .withRequestBody(
            equalToJson(readText("classpath:mock/request/CP05_002.json")))
        .willReturn(
            aResponse()
                .withFixedDelay(1000)
                .withStatus(HttpStatus.OK.value())
                .withHeader("Content-Type", ContentType.APPLICATION_JSON.toString())
                .withBody(readText("classpath:mock/response/CP05_001.json"))));

    // 6.7.6 운용리스 거래내역 조회
    wireMockServer.stubFor(post(urlMatching("/loans/oplease/transactions"))
        .withRequestBody(
            equalToJson(readText("classpath:mock/request/CP06_003.json")))
        .willReturn(
            aResponse()
                .withFixedDelay(500)
                .withStatus(HttpStatus.OK.value())
                .withHeader("Content-Type", ContentType.APPLICATION_JSON.toString())
                .withBody(readText("classpath:mock/response/CP06_003.json"))));
  }
}
