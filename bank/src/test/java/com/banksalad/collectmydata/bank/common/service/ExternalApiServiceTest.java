package com.banksalad.collectmydata.bank.common.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.WireMockSpring;
import org.springframework.http.HttpStatus;

import com.banksalad.collectmydata.bank.summary.dto.AccountSummary;
import com.banksalad.collectmydata.bank.summary.dto.ListAccountSummariesResponse;
import com.banksalad.collectmydata.bank.invest.dto.GetInvestAccountBasicResponse;
import com.banksalad.collectmydata.bank.invest.dto.GetInvestAccountDetailResponse;
import com.banksalad.collectmydata.bank.invest.dto.InvestAccountBasic;
import com.banksalad.collectmydata.bank.invest.dto.InvestAccountDetail;
import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.organization.Organization;
import com.banksalad.collectmydata.common.util.DateUtil;
import com.github.tomakehurst.wiremock.WireMockServer;
import javax.transaction.Transactional;
import org.apache.http.entity.ContentType;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static com.banksalad.collectmydata.bank.testutil.FileUtil.readText;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

@Disabled
@SpringBootTest
@Transactional
@DisplayName("ExternalApiService Test")
class ExternalApiServiceTest {

  private static final String ORGANIZATION_ID = "organization_id";
  private static final String ORGANIZATION_CODE = "020";
  private static final String ORGANIZATION_HOST = "localhost";
  private static final Long BANKSALAD_USER_ID = 1L;
  private static final String ACCESS_TOKEN = "abc.def.ghi";

  private static WireMockServer wireMockServer;

  @Autowired
  private ExternalApiService externalApiService;

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
  @DisplayName("6.2.1 계좌 목록 조회")
  public void givenExecutionContextAndOrganization_whenGetAccounts_thenEquals() {
    setupAccountSummaryMockServer();
    // Given
    ExecutionContext executionContext = getExecutionContext();
    Organization organization = getOrganization();

    // When
    ListAccountSummariesResponse actualAccountResponse = externalApiService
        .listAccountSummaries(executionContext, organization.getOrganizationCode(), 0L);

    // Then
    assertEquals(4, actualAccountResponse.getAccountCnt());
  }

  @Test
  @DisplayName("6.2.5 투자상품계좌 기본정보 조회")
  public void givenExecutionContextAndOrganizationAndAccount_whenGetAccountBasic_thenEquals() {
    setupInvestAccountMockServer();
    // given
    ExecutionContext executionContext = getExecutionContext();
    Organization organization = getOrganization();
    GetInvestAccountBasicResponse expectedInvestAccountBasicResponse = GetInvestAccountBasicResponse.builder()
        .investAccountBasic(InvestAccountBasic.builder()
            .standardFundCode("standard_101")
            .paidInType("01")
            .issueDate("20200101")
            .expDate("20201231").build())
        .build();

    AccountSummary accountSummary = AccountSummary.builder()
        .accountNum("1234567890")
        .accountType("2100")
        .foreignDeposit(false)
        .accountStatus("01")
        .consent(true)
        .seqno("1")
        .prodName("뱅샐 투자 상품")
        .build();

    // when
    GetInvestAccountBasicResponse actualInvestAccountBasicResponse = externalApiService
        .getInvestAccountBasic(executionContext, accountSummary, organization, 0L);

    // then
    assertThat(actualInvestAccountBasicResponse).usingRecursiveComparison()
        .isEqualTo(expectedInvestAccountBasicResponse);
  }


  @Test
  @DisplayName("6.2.6 투자 상품 계좌 추가 정보 조회")
  void givenExecutionContextAndOrganizationAndAccount_whenGetAccountDetail_thenEquals() {
    setupInvestAccountMockServer();
    // Given
    ExecutionContext executionContext = getExecutionContext();
    Organization organization = getOrganization();
    GetInvestAccountDetailResponse expectedInvestAccountDetailResponse = GetInvestAccountDetailResponse.builder()
        .investAccountDetail(InvestAccountDetail.builder()
            .currencyCode("KRW")
            .balanceAmt(new BigDecimal("1928393.123"))
            .evalAmt(new BigDecimal("12345.678"))
            .invPrincipal(new BigDecimal("123456789123456.123"))
            .fundNum(new BigDecimal("12342.12"))
            .build())
        .build();

    AccountSummary accountSummary = AccountSummary.builder()
        .accountNum("1234567890")
        .accountType("2100")
        .foreignDeposit(false)
        .accountStatus("01")
        .consent(true)
        .seqno("1")
        .prodName("뱅샐 투자 상품")
        .build();

    // when
    GetInvestAccountDetailResponse actualInvestAccountDetailResponse = externalApiService
        .getInvestAccountDetail(executionContext, accountSummary, organization, 0L);

    // then
    assertThat(actualInvestAccountDetailResponse).usingRecursiveComparison()
        .isEqualTo(expectedInvestAccountDetailResponse);
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

  private Organization getOrganization() {
    return Organization.builder()
        .organizationId(ORGANIZATION_ID)
        .organizationCode(ORGANIZATION_CODE)
        .build();
  }

  private static void setupAccountSummaryMockServer() {
    // 계좌목록조회 page 01
    wireMockServer.stubFor(get(urlMatching("/accounts.*"))
        .withQueryParam("org_code", equalTo("020"))
        .withQueryParam("search_timestamp", equalTo("0"))
        .withQueryParam("limit", equalTo("500"))
        .willReturn(
            aResponse()
                .withFixedDelay(1000)
                .withStatus(HttpStatus.OK.value())
                .withHeader("Content-Type", ContentType.APPLICATION_JSON.toString())
                .withBody(readText("classpath:mock/bank/response/BA01_001_single_page_01.json"))));

    // 계좌목록조회 page 02
    wireMockServer.stubFor(get(urlMatching("/accounts.*"))
        .withQueryParam("org_code", equalTo("020"))
        .withQueryParam("search_timestamp", equalTo("0"))
        .withQueryParam("limit", equalTo("500"))
        .withQueryParam("next_page", equalTo("02"))
        .willReturn(
            aResponse()
                .withFixedDelay(1000)
                .withStatus(HttpStatus.OK.value())
                .withHeader("Content-Type", ContentType.APPLICATION_JSON.toString())
                .withBody(readText("classpath:mock/bank/response/BA01_001_single_page_02.json"))));

    // 계좌목록조회 page 03
    wireMockServer.stubFor(get(urlMatching("/accounts.*"))
        .withQueryParam("org_code", equalTo("020"))
        .withQueryParam("search_timestamp", equalTo("0"))
        .withQueryParam("limit", equalTo("500"))
        .withQueryParam("next_page", equalTo("03"))
        .willReturn(
            aResponse()
                .withFixedDelay(1000)
                .withStatus(HttpStatus.OK.value())
                .withHeader("Content-Type", ContentType.APPLICATION_JSON.toString())
                .withBody(readText("classpath:mock/bank/response/BA01_001_single_page_03.json"))));
  }

  private static void setupInvestAccountMockServer() {
    // 6.2.5 투자상품계좌 기본정보 조회
    wireMockServer.stubFor(post(urlMatching("/accounts/invest/basic"))
        .withRequestBody(
            equalToJson(readText("classpath:mock/bank/request/BA05_001_single_page_00.json"))
        )
        .willReturn(
            aResponse()
                .withFixedDelay(1000)
                .withStatus(HttpStatus.OK.value())
                .withHeader("Content-Type", ContentType.APPLICATION_JSON.toString())
                .withBody(readText("classpath:mock/bank/response/BA05_001_single_page_00.json"))));

    // 6.2.6 투자 상품계좌 추가정보 조회
    wireMockServer.stubFor(post(urlMatching("/accounts/invest/detail"))
        .withRequestBody(
            equalToJson(readText("classpath:mock/bank/request/BA06_001_single_page_00.json")))
        .willReturn(
            aResponse()
                .withFixedDelay(1000)
                .withStatus(HttpStatus.OK.value())
                .withHeader("Content-Type", ContentType.APPLICATION_JSON.toString())
                .withBody(readText("classpath:mock/bank/response/BA06_001_single_page_00.json"))));

  }
}
