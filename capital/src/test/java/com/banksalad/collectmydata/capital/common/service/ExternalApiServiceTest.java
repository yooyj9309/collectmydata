package com.banksalad.collectmydata.capital.common.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.WireMockSpring;
import org.springframework.http.HttpStatus;

import com.banksalad.collectmydata.capital.account.dto.AccountDetailResponse;
import com.banksalad.collectmydata.capital.common.dto.Organization;
import com.banksalad.collectmydata.capital.oplease.dto.OperatingLeaseTransaction;
import com.banksalad.collectmydata.capital.oplease.dto.OperatingLeaseTransactionResponse;
import com.banksalad.collectmydata.capital.summary.dto.AccountSummary;
import com.banksalad.collectmydata.capital.summary.dto.ListAccountSummariesResponse;
import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.util.DateUtil;
import com.github.tomakehurst.wiremock.WireMockServer;
import javax.transaction.Transactional;
import org.apache.http.entity.ContentType;
import org.junit.jupiter.api.AfterAll;
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
import static com.banksalad.collectmydata.capital.common.TestHelper.ACCOUNT_NUM;
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
import static com.banksalad.collectmydata.capital.common.TestHelper.SEQNO2;
import static com.banksalad.collectmydata.capital.util.FileUtil.readText;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static java.lang.Boolean.TRUE;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Deprecated
@SpringBootTest
@Transactional
@DisplayName("ExternalApiService Test")
class ExternalApiServiceTest {


  private static WireMockServer wireMockServer;

  @Autowired
  private ExternalApiService externalApiService;

  @BeforeAll
  static void setup() {
    wireMockServer = new WireMockServer(WireMockSpring.options().dynamicPort());
    wireMockServer.start();
    setupMockServer();
  }

  @AfterAll
  static void tearDown() {
    wireMockServer.shutdown();
  }

  @Test
  @DisplayName("6.7.3 대출 상품 계좌 추가 정보 조회")
  void givenExecutionContextAndOrganizationAndAccount_whenGetAccountDetail_thenEquals() {
    // Given
    ExecutionContext executionContext = getExecutionContext();
    Organization organization = getOrganization();
    AccountSummary accountSummary = getAccount();
    AccountDetailResponse expectedAccountDetailResponse = getAccountDetailResponse();

    // When
    AccountDetailResponse actualAccountDetailResponse = externalApiService
        .getAccountDetail(executionContext, organization, accountSummary);

    // Then
    assertThat(actualAccountDetailResponse).usingRecursiveComparison().isEqualTo(expectedAccountDetailResponse);
  }


  @Test
  @DisplayName("6.7.6 운용리스 거래내역 조회 : 여러 페이지 조회 - 2개의 페이지 결합")
  public void givenExecutionContextAndOrganizationAndAccount_whenGetOperatingLeaseTransactions_thenEquals() {
    // given
    ExecutionContext executionContext = getExecutionContext();
    Organization organization = getOrganization();
    AccountSummary accountSummary = getAccount();
    LocalDate fromDate = LocalDate.of(2021, 01, 21);
    LocalDate toDate = LocalDate.of(2021, 01, 22);

    // when
    OperatingLeaseTransactionResponse operatingLeaseTransactionResponse = externalApiService
        .listOperatingLeaseTransactions(executionContext, organization, accountSummary, fromDate, toDate);

    // then
    assertThat(operatingLeaseTransactionResponse).usingRecursiveComparison().isEqualTo(
        OperatingLeaseTransactionResponse.builder()
            .rspCode("00000")
            .rspMsg("rsp_msg")
            .nextPage(null)
            .transCnt(3)
            .operatingLeaseTransactions(List.of(
                OperatingLeaseTransaction.builder()
                    .transDtime("20210217093000")
                    .transNo("trans#1")
                    .transType("03")
                    .transAmt(BigDecimal.valueOf(1000.3))
                    .build(),
                OperatingLeaseTransaction.builder()
                    .transDtime("20210217093000")
                    .transNo("trans#2")
                    .transType("01")
                    .transAmt(BigDecimal.valueOf(0.0))
                    .build(),
                OperatingLeaseTransaction.builder()
                    .transDtime("20210217093000")
                    .transNo("trans#3")
                    .transType("02")
                    .transAmt(BigDecimal.valueOf(999.1))
                    .build()
            ))
            .build()
    );
  }

  /**
   * Helper methods
   */
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

  private Organization getOrganization() {
    return Organization.builder()
        .sector(SECTOR)
        .industry(INDUSTRY)
        .organizationId(ORGANIZATION_ID)
        .organizationCode(ORGANIZATION_CODE)
        .domain(ORGANIZATION_HOST)
        .build();
  }

  private AccountSummary getAccount() {
    return AccountSummary.builder()
        .accountNum(ACCOUNT_NUM)
        .isConsent(TRUE)
        .seqno(SEQNO1)
        .prodName(PRODUCT_NAME)
        .accountType(ACCOUNT_TYPE)
        .accountStatus(ACCOUNT_STATUS)
        .build();
  }

  private ListAccountSummariesResponse getAccountResponse() {
    return ListAccountSummariesResponse.builder()
        .rspCode("200")
        .rspMsg("success")
        .searchTimestamp(0L)
        .regDate("20201114")
        .accountCnt(2)
        .accountSummaries(List.of(
            AccountSummary.builder()
                .accountNum("1234123412341234")
                .isConsent(true)
                .seqno(SEQNO1)
                .prodName("상품명1")
                .accountType("3100")
                .accountStatus("01")
                .build(),
            AccountSummary.builder()
                .accountNum("5678567856785678")
                .isConsent(true)
                .seqno(SEQNO2)
                .prodName("상품명2")
                .accountType("3210")
                .accountStatus("03")
                .build()
        ))
        .build();
  }

  private AccountDetailResponse getAccountDetailResponse() {
    return AccountDetailResponse.builder()
        .rspCode("000")
        .rspMsg("rsp_msg")
        .searchTimestamp(0L)
        .balanceAmt(BigDecimal.valueOf(30000.123))
        .loanPrincipal(BigDecimal.valueOf(20000.456))
        .nextRepayDate(LocalDate.of(2020, 11, 14).format(DateTimeFormatter.ofPattern("yyyyMMdd")))
        .build();
  }

  private static void setupMockServer() {
    // 6.7.3 대출상품계좌 추가정보 조회
    wireMockServer.stubFor(post(urlMatching("/loans/detail"))
        .withRequestBody(
            equalToJson(readText("classpath:mock/request/CP03_001.json")))
        .willReturn(
            aResponse()
                .withFixedDelay(1000)
                .withStatus(HttpStatus.OK.value())
                .withHeader("Content-Type", ContentType.APPLICATION_JSON.toString())
                .withBody(readText("classpath:mock/response/CP03_001.json"))));

    // 6.7.6 운용리스 거래내역 조회 : 응답 페이지가 둘인 경우 - 첫번째 페이지(1/2) (요청 next_page : null, 응답 next_page : 2)
    wireMockServer.stubFor(post(urlMatching("/loans/oplease/transactions"))
        .withRequestBody(
            equalToJson(readText("classpath:mock/request/CP06_001.json")))
        .willReturn(
            aResponse()
                .withFixedDelay(500)
                .withStatus(HttpStatus.OK.value())
                .withHeader("Content-Type", ContentType.APPLICATION_JSON.toString())
                .withBody(readText("classpath:mock/response/CP06_001.json"))));

    // 6.7.6 운용리스 거래내역 조회 : 응답 페이지가 둘인 경우 - 두번째 페이지(2/2) (요청 next_page : 2, 응답 next_page : null)
    wireMockServer.stubFor(post(urlMatching("/loans/oplease/transactions"))
        .withRequestBody(
            equalToJson(readText("classpath:mock/request/CP06_002.json")))
        .willReturn(
            aResponse()
                .withFixedDelay(500)
                .withStatus(HttpStatus.OK.value())
                .withHeader("Content-Type", ContentType.APPLICATION_JSON.toString())
                .withBody(readText("classpath:mock/response/CP06_002.json"))));
  }
}
