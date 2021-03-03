package com.banksalad.collectmydata.capital.oplease;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.WireMockSpring;
import org.springframework.http.HttpStatus;

import com.banksalad.collectmydata.capital.common.db.entity.AccountListEntity;
import com.banksalad.collectmydata.capital.common.db.entity.OperatingLeaseEntity;
import com.banksalad.collectmydata.capital.common.db.entity.OperatingLeaseHistoryEntity;
import com.banksalad.collectmydata.capital.common.db.entity.UserSyncStatusEntity;
import com.banksalad.collectmydata.capital.common.db.repository.AccountListRepository;
import com.banksalad.collectmydata.capital.common.db.repository.OperatingLeaseHistoryRepository;
import com.banksalad.collectmydata.capital.common.db.repository.OperatingLeaseRepository;
import com.banksalad.collectmydata.capital.common.db.repository.UserSyncStatusRepository;
import com.banksalad.collectmydata.capital.common.dto.AccountSummary;
import com.banksalad.collectmydata.capital.common.dto.Organization;
import com.banksalad.collectmydata.capital.oplease.dto.OperatingLease;
import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.github.tomakehurst.wiremock.WireMockServer;
import org.apache.http.entity.ContentType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static com.banksalad.collectmydata.capital.util.FileUtil.readText;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
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
  private AccountListRepository accountListRepository;

  @Autowired
  private UserSyncStatusRepository userSyncStatusRepository;

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
    accountListRepository.deleteAll();
    userSyncStatusRepository.deleteAll();
  }

  @Test
  @DisplayName("운용리스 기본정보 조회 서비스 로직 성공케이스")
  public void getOperatingLeaseBasic_firstInflow() {

    LocalDateTime firstTime = LocalDateTime.now();
    ExecutionContext context = generateExecutionContext(firstTime);

    Organization organization = Organization.builder()
        .organizationCode("10041004").build();

    AccountSummary accountSummary = AccountSummary.builder()
        .accountNum(accountNum)
        .seqno(1)
        .build();

    accountListRepository.save(
        AccountListEntity.builder()
            .syncedAt(LocalDateTime.now())
            .banksaladUserId(banksaladUserId)
            .organizationId(organizationId)
            .accountNum(accountNum)
            .seqno(1)
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

  private ExecutionContext generateExecutionContext(LocalDateTime now) {
    return ExecutionContext.builder()
        .organizationId(organizationId)
        .banksaladUserId(banksaladUserId)
        .syncStartedAt(now)
        .accessToken("accessToken")
        .organizationHost("http://localhost:" + wireMockServer.port())
        .build();
  }

  private void validateResult(LocalDateTime firstTime, LocalDateTime recentTime,
      List<OperatingLeaseEntity> operatingLeaseEntities,
      List<OperatingLeaseHistoryEntity> operatingLeaseHistoryEntities, List<OperatingLease> operatingLeases) {
    assertEquals(1, operatingLeaseEntities.size());
    assertThat(operatingLeaseEntities.get(0)).usingRecursiveComparison()
        .ignoringFields("operatingLeaseId", "createdAt", "updatedAt")
        .isEqualTo(
            OperatingLeaseEntity.builder()
                .syncedAt(firstTime)
                .banksaladUserId(banksaladUserId)
                .organizationId(organizationId)
                .accountNum(accountNum)
                .seqno(1)
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
        .ignoringFields("operatingLeaseHistoryId", "createdAt", "updatedAt")
        .isEqualTo(
            OperatingLeaseHistoryEntity.builder()
                .syncedAt(firstTime)
                .banksaladUserId(banksaladUserId)
                .organizationId(organizationId)
                .accountNum(accountNum)
                .seqno(1)
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
                .seqno(1)
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

    List<UserSyncStatusEntity> userSyncStatusEntities = userSyncStatusRepository.findAll();
    assertEquals(1, userSyncStatusEntities.size());
    assertEquals(recentTime, userSyncStatusEntities.get(0).getSyncedAt());
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
  }
}
