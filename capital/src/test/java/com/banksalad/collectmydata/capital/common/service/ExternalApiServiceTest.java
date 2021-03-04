package com.banksalad.collectmydata.capital.common.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.WireMockSpring;
import org.springframework.http.HttpStatus;

import com.banksalad.collectmydata.capital.common.dto.AccountSummary;
import com.banksalad.collectmydata.capital.common.dto.AccountSummaryResponse;
import com.banksalad.collectmydata.capital.common.dto.Organization;
import com.banksalad.collectmydata.capital.loan.dto.LoanAccountBasicResponse;
import com.banksalad.collectmydata.capital.loan.dto.LoanAccountDetailResponse;
import com.banksalad.collectmydata.capital.loan.dto.LoanAccountTransactionResponse;
import com.banksalad.collectmydata.capital.oplease.dto.OperatingLeaseBasicResponse;
import com.banksalad.collectmydata.capital.oplease.dto.OperatingLeaseTransaction;
import com.banksalad.collectmydata.capital.oplease.dto.OperatingLeaseTransactionResponse;
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
import static com.banksalad.collectmydata.capital.common.TestHelper.respondLoanAccountTransactionResponseWithOnePage;
import static com.banksalad.collectmydata.capital.common.TestHelper.respondLoanAccountTransactionResponseWithTwoPages;
import static com.banksalad.collectmydata.capital.util.FileUtil.readText;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static java.lang.Boolean.TRUE;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

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
  @DisplayName("6.7.1 계좌 목록 조회")
  public void givenExecutionContextAndOrganization_whenGetAccounts_thenEquals() {
    // Given
    ExecutionContext executionContext = getExecutionContext();
    Organization organization = getOrganization();
    AccountSummaryResponse expectedAccountSummaryResponse = getAccountResponse();

    // When
    AccountSummaryResponse actualAccountSummaryResponse = externalApiService
        .getAccounts(executionContext, organization.getOrganizationCode(), 0l);

    // Then
    assertEquals(2, actualAccountSummaryResponse.getAccountCnt());
    assertThat(actualAccountSummaryResponse.getAccountSummaries()).usingRecursiveComparison()
        .isEqualTo(expectedAccountSummaryResponse.getAccountSummaries());
  }

  @Test
  @DisplayName("6.7.2 대출상품계좌 기본정보 조회")
  public void givenExecutionContextAndOrganizationAndAccount_whenGetAccountBasic_thenEquals() {
    // given
    ExecutionContext executionContext = getExecutionContext();
    Organization organization = getOrganization();
    AccountSummary accountSummary = getAccount();

    // when
    LoanAccountBasicResponse loanAccountBasicResponse = externalApiService
        .getAccountBasic(executionContext, organization, accountSummary);

    // then
    assertThat(loanAccountBasicResponse).usingRecursiveComparison().isEqualTo(
        LoanAccountBasicResponse.builder()
            .rspCode("000")
            .rspMsg("rep_msg")
            .searchTimestamp(1000)
            .holderName("대출차주명")
            .issueDate("20210210")
            .expDate("20221231")
            .lastOfferedRate(BigDecimal.valueOf(2.117))
            .repayDate("03")
            .repayMethod("01")
            .repayOrgCode("B01")
            .repayAccountNum("11022212345")
            .build()
    );
  }

  @Test
  @DisplayName("6.7.3 대출 상품 계좌 추가 정보 조회")
  void givenExecutionContextAndOrganizationAndAccount_whenGetAccountDetail_thenEquals() {
    // Given
    ExecutionContext executionContext = getExecutionContext();
    Organization organization = getOrganization();
    AccountSummary accountSummary = getAccount();
    LoanAccountDetailResponse expectedLoanAccountDetailResponse = getAccountDetailResponse();

    // When
    LoanAccountDetailResponse actualLoanAccountDetailResponse = externalApiService
        .getAccountDetail(executionContext, organization, accountSummary);

    // Then
    assertThat(actualLoanAccountDetailResponse).usingRecursiveComparison().isEqualTo(expectedLoanAccountDetailResponse);
  }

  @Test
  @DisplayName("6.7.4 대출상품계좌 거래내역 조회: 2개 페이지 결합하기 (seqno 없는 경우)")
  public void givenExecutionContextAndRequest_whenGetTransactions_thenTwoPages() {
    // Given
    final ExecutionContext executionContext = getExecutionContext();
    final Organization organization = getOrganization();
    final AccountSummary accountSummary = getAccount();
    final Long bankSaladUserId = executionContext.getBanksaladUserId();
    final String organizationId = organization.getOrganizationId();
    LoanAccountTransactionResponse expectedLoanAccountTransactionResponse = respondLoanAccountTransactionResponseWithTwoPages();
    expectedLoanAccountTransactionResponse.getTransList().forEach(loanAccountTransaction -> {
          loanAccountTransaction.setAccountNum(accountSummary.getAccountNum());
          loanAccountTransaction.setSeqno(accountSummary.getSeqno());
        }
    );

    // When
    LoanAccountTransactionResponse actualLoanAccountTransactionResponse = externalApiService
        .getAccountTransactions(executionContext, organization, accountSummary);

    // Then
    assertEquals(3, actualLoanAccountTransactionResponse.getTransCnt());
    assertEquals(3, actualLoanAccountTransactionResponse.getTransList().size());
    assertThat(actualLoanAccountTransactionResponse).usingRecursiveComparison()
        .isEqualTo(expectedLoanAccountTransactionResponse);
  }

  @Test
  @DisplayName("6.7.5 운용 리스 기본 정보 조회")
  void givenExecutionContextAndOrganizationAndAccount_whenGetLeaseBasic_thenEquals() {
    // Given
    ExecutionContext executionContext = getExecutionContext();
    Organization organization = getOrganization();
    AccountSummary accountSummary = getAccount();
    OperatingLeaseBasicResponse expectedLeaseBasicResponse = getOperatingLeaseBasicResponse();

    // When
    OperatingLeaseBasicResponse actualLeaseBasicResponseResponse = externalApiService
        .getOperatingLeaseBasic(executionContext, organization, accountSummary);

    // Then
    assertThat(actualLeaseBasicResponseResponse).usingRecursiveComparison().isEqualTo(expectedLeaseBasicResponse);
  }

  @Test
  @DisplayName("6.7.6 운용리스 거래내역 조회 : 여러 페이지 조회 - 2개의 페이지 결합")
  public void givenExecutionContextAndOrganizationAndAccount_whenGetOperatingLeaseTransactions_thenEquals() {
    // given
    ExecutionContext executionContext = getExecutionContext();
    Organization organization = getOrganization();
    AccountSummary accountSummary = getAccount();

    // when
    OperatingLeaseTransactionResponse operatingLeaseTransactionResponse = externalApiService
        .getOperatingLeaseTransactions(executionContext, organization, accountSummary);

    // then
    assertThat(operatingLeaseTransactionResponse).usingRecursiveComparison().isEqualTo(
        OperatingLeaseTransactionResponse.builder()
            .rspCode("00000")
            .rspMsg("rsp_msg")
            .nextPage(null)
            .transCnt(3)
            .transList(List.of(
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

  private AccountSummaryResponse getAccountResponse() {
    return AccountSummaryResponse.builder()
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

  private LoanAccountDetailResponse getAccountDetailResponse() {
    return LoanAccountDetailResponse.builder()
        .rspCode("000")
        .rspMsg("rsp_msg")
        .searchTimestamp(0L)
        .balanceAmount(BigDecimal.valueOf(30000))
        .loanPrincipal(BigDecimal.valueOf(20000))
        .nextRepayDate(LocalDate.of(2020, 11, 14).format(DateTimeFormatter.ofPattern("yyyyMMdd")))
        .build();
  }

  private OperatingLeaseBasicResponse getOperatingLeaseBasicResponse() {
    return OperatingLeaseBasicResponse.builder()
        .rspCode("00000")
        .rspMsg("rep_msg")
        .searchTimestamp(1000L)
        .holderName("김뱅셀")
        .issueDate("20210210")
        .expDate("20221231")
        .repayDate("03")
        .repayMethod("01")
        .repayOrgCode("B01")
        .repayAccountNum("11022212345")
        .nextRepayDate("20211114")
        .build();
  }

  private static void setupMockServer() {
    // 6.7.1 계좌목록 조회
    wireMockServer.stubFor(get(urlMatching("/loans.*"))
        .withQueryParam("org_code", equalTo(ORGANIZATION_CODE))
        .withQueryParam("search_timestamp", equalTo("0"))
        .willReturn(
            aResponse()
                .withFixedDelay(1000)
                .withStatus(HttpStatus.OK.value())
                .withHeader("Content-Type", ContentType.APPLICATION_JSON.toString())
                .withBody(readText("classpath:mock/response/CP01_001.json"))));

    // 6.7.2 대출상품계좌 기본정보 조회
    wireMockServer.stubFor(post(urlMatching("/loans/basic"))
        .willReturn(
            aResponse()
                .withFixedDelay(1000)
                .withStatus(HttpStatus.OK.value())
                .withHeader("Content-Type", ContentType.APPLICATION_JSON.toString())
                .withBody(readText("classpath:mock/response/CP02_001.json"))));

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

    // 6.7.4 대출상품계좌 거래내역 조회: 첫번째 페이지 (next_page를 요청에 설정하지 않음)
    wireMockServer.stubFor(post(urlMatching("/loans/transactions"))
        .withRequestBody(
            equalToJson(readText("classpath:mock/request/CP04_001.json")))
        .willReturn(
            aResponse()
                .withFixedDelay(500)
                .withStatus(HttpStatus.OK.value())
                .withHeader("Content-Type", ContentType.APPLICATION_JSON.toString())
                .withBody(readText("classpath:mock/response/CP04_002.json"))));

    // 6.7.4 대출상품계좌 거래내역 조회: 두번째 페이지 (응답에 next_page가 없음)
    wireMockServer.stubFor(post(urlMatching("/loans/transactions"))
        .withRequestBody(
            equalToJson(readText("classpath:mock/request/CP04_002.json")))
        .willReturn(
            aResponse()
                .withFixedDelay(500)
                .withStatus(HttpStatus.OK.value())
                .withHeader("Content-Type", ContentType.APPLICATION_JSON.toString())
                .withBody(readText("classpath:mock/response/CP04_003.json"))));

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
