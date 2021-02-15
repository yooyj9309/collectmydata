package com.banksalad.collectmydata.capital.common.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.WireMockSpring;
import org.springframework.http.HttpStatus;

import com.banksalad.collectmydata.capital.account.dto.Account;
import com.banksalad.collectmydata.capital.account.dto.AccountBasicResponse;
import com.banksalad.collectmydata.capital.account.dto.AccountDetailResponse;
import com.banksalad.collectmydata.capital.account.dto.AccountResponse;
import com.banksalad.collectmydata.capital.common.dto.Organization;
import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.github.tomakehurst.wiremock.WireMockServer;
import org.apache.http.entity.ContentType;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

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
    AccountResponse expectedAccountResponse = getAccountResponse();

    // When
    AccountResponse actualAccountResponse = externalApiService.getAccounts(executionContext, organization);

    // Then
    assertEquals(2, actualAccountResponse.getAccountCnt());
    assertThat(actualAccountResponse.getAccountList()).usingRecursiveComparison()
        .isEqualTo(expectedAccountResponse.getAccountList());
  }

  @Test
  @DisplayName("6.7.2 대출상품계좌 기본정보 조회")
  public void givenExecutionContextAndOrganizationAndAccount_whenGetAccountBasic_thenEquals() {
    // given
    ExecutionContext executionContext = getExecutionContext();
    Organization organization = getOrganization();
    Account account = getAccount();

    // when
    AccountBasicResponse accountBasicResponse = externalApiService
        .getAccountBasic(executionContext, organization, account);

    // then
    assertThat(accountBasicResponse).usingRecursiveComparison().isEqualTo(
        AccountBasicResponse.builder()
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
    Account account = getAccount();
    AccountDetailResponse expectedAccountDetailResponse = getAccountDetailResponse();

    // When
    AccountDetailResponse actualAccountDetailResponse = externalApiService
        .getAccountDetail(executionContext, organization, account);

    // Then
    assertThat(actualAccountDetailResponse).usingRecursiveComparison().isEqualTo(expectedAccountDetailResponse);
  }

  // TODO : will be added - 6.7.4 대출상품계좌 거래내역 조회

  private ExecutionContext getExecutionContext() {
    return ExecutionContext.builder()
        .accessToken("accessToken")
        .organizationHost("http://localhost:" + wireMockServer.port())
        .build();
  }

  private Organization getOrganization() {
    return Organization.builder()
        .sector("finance")
        .industry("bank")
        .organizationId("shinhanbank")
        .organizationCode("020")
        .domain("123")
        .build();
  }

  private Account getAccount() {
    return Account.builder()
        .accountNum("1234")
        .isConsent(TRUE)
        .seqno(1)
        .prodName("대출도 재산 상품")
        .accountType("1234")
        .accountStatus("01")
        .build();
  }

  private AccountResponse getAccountResponse() {
    return AccountResponse.builder()
        .rspCode("200")
        .rspMsg("success")
        .searchTimestamp(0L)
        .regDate("20201114")
        .accountCnt(2)
        .accountList(List.of(
            Account.builder()
                .accountNum("1234123412341234")
                .isConsent(true)
                .seqno(1)
                .prodName("상품명1")
                .accountType("3100")
                .accountStatus("01")
                .build(),
            Account.builder()
                .accountNum("5678567856785678")
                .isConsent(true)
                .seqno(2)
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
        .balanceAmount(BigDecimal.valueOf(30000))
        .loanPrincipal(BigDecimal.valueOf(20000))
        .nextRepayDate(LocalDate.of(2020, 11, 14).format(DateTimeFormatter.ofPattern("yyyyMMdd")))
        .build();
  }

  private static void setupMockServer() {
    // 6.7.1 계좌목록 조회
    wireMockServer.stubFor(get(urlMatching("/loans.*"))
        .withQueryParam("org_code", equalTo("020"))
        .withQueryParam("search_timestamp", equalTo("0"))
        .willReturn(
            aResponse()
                .withFixedDelay(1000)
                .withStatus(HttpStatus.OK.value())
                .withHeader("Content-Type", ContentType.APPLICATION_JSON.toString())
                .withBody(readText("classpath:mock/CP01_001.json"))));

    // 6.7.2 대출상품계좌 기본정보 조회
    wireMockServer.stubFor(post(urlMatching("/loans/basic"))
        .willReturn(
            aResponse()
                .withFixedDelay(1000)
                .withStatus(HttpStatus.OK.value())
                .withHeader("Content-Type", ContentType.APPLICATION_JSON.toString())
                .withBody(readText("classpath:mock/CP02_001.json"))));

    // 6.7.3 대출상품계좌 추가정보 조회
    wireMockServer.stubFor(post(urlMatching("/loans/detail"))
        .withRequestBody(equalToJson(
            "{\"org_code\" : \"020\"," +
                "\"account_num\" : \"1234\"," +
                "\"seqno\" : 1," +
                "\"search_timestamp\" : 0}"))
        .willReturn(
            aResponse()
                .withFixedDelay(1000)
                .withStatus(HttpStatus.OK.value())
                .withHeader("Content-Type", ContentType.APPLICATION_JSON.toString())
                .withBody(readText("classpath:mock/CP03_001.json"))));

    // 6.7.4 대출상품계좌 거래내역 조회: 빈 페이지 조회 AND seqno 없는 경우
    wireMockServer.stubFor(post(urlMatching("/loans/transactions.*"))
        .withQueryParam("org_code", equalTo("loanX"))
        .withQueryParam("account_num", equalTo("10041004"))
//            .withQueryParam("seqno", equalTo(""))
        .withQueryParam("from_dtime", equalTo("20210121000000"))
        .withQueryParam("to_dtime", equalTo("20210122000000"))
//            .withQueryParam("next_page", equalTo(""))
        .withQueryParam("limit", equalTo("500"))
        .willReturn(
            aResponse()
                .withFixedDelay(500)
                .withStatus(HttpStatus.OK.value())
                .withHeader("Content-Type", ContentType.APPLICATION_JSON.toString())
                .withBody(readText("classpath:mock/CP04_001.json"))));

    // 6.7.4 대출상품계좌 거래내역 조회: 첫번째 페이지 조회 AND seqno 없는 경우
    wireMockServer.stubFor(post(urlMatching("/loans/transactions.*"))
        .withQueryParam("org_code", equalTo("loanX"))
        .withQueryParam("account_num", equalTo("10041004"))
//            .withQueryParam("seqno", equalTo(""))
        .withQueryParam("from_dtime", equalTo("20210121000000"))
        .withQueryParam("to_dtime", equalTo("20210122000000"))
//            .withQueryParam("next_page", equalTo(""))
        .withQueryParam("limit", equalTo("2"))
        .willReturn(
            aResponse()
                .withFixedDelay(500)
                .withStatus(HttpStatus.OK.value())
                .withHeader("Content-Type", ContentType.APPLICATION_JSON.toString())
                .withBody(readText("classpath:mock/CP04_002.json"))));

    // 6.7.4 대출상품계좌 거래내역 조회: 두번째 페이지 조회 AND seqno 없는 경우
    wireMockServer.stubFor(post(urlMatching("/loans/transactions.*"))
        .withQueryParam("org_code", equalTo("loanX"))
        .withQueryParam("account_num", equalTo("10041004"))
//            .withQueryParam("seqno", equalTo(""))
        .withQueryParam("from_dtime", equalTo("20210121000000"))
        .withQueryParam("to_dtime", equalTo("20210122000000"))
        .withQueryParam("next_page", equalTo("2"))
        .withQueryParam("limit", equalTo("2"))
        .willReturn(
            aResponse()
                .withFixedDelay(2)
                .withStatus(HttpStatus.OK.value())
                .withHeader("Content-Type", ContentType.APPLICATION_JSON.toString())
                .withBody(readText("classpath:mock/CP04_003.json"))));
  }
}
