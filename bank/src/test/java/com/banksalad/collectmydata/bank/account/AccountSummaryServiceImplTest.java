package com.banksalad.collectmydata.bank.account;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.WireMockSpring;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;

import com.banksalad.collectmydata.bank.common.collect.Executions;
import com.banksalad.collectmydata.bank.common.enums.BankAccountType;
import com.banksalad.collectmydata.bank.common.service.AccountSummaryService;
import com.banksalad.collectmydata.bank.summary.dto.AccountSummary;
import com.banksalad.collectmydata.bank.summary.dto.ListAccountSummariesRequest;
import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.util.DateUtil;
import com.banksalad.collectmydata.finance.api.summary.SummaryRequestHelper;
import com.banksalad.collectmydata.finance.api.summary.SummaryResponseHelper;
import com.banksalad.collectmydata.finance.api.summary.SummaryService;
import com.github.tomakehurst.wiremock.WireMockServer;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.entity.ContentType;
import org.assertj.core.api.Assertions;
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

@Slf4j
@SpringBootTest
@DisplayName("계좌 목록 서비스 테스트")
@Transactional
public class AccountSummaryServiceImplTest {

  private static final Long BANKSALAD_USER_ID = 1L;
  private static final String ORGANIZATION_ID = "woori_bank";
  private static final String ORGANIZATION_HOST = "http://localhost";

  private static final int API_RESPONSE_FIXED_DELAY = 0;

  @Autowired
  private SummaryService<ListAccountSummariesRequest, AccountSummary> summaryService;

  @Autowired
  private SummaryRequestHelper<ListAccountSummariesRequest> summaryRequestHelper;

  @Autowired
  private SummaryResponseHelper<AccountSummary> summaryResponseHelper;

  @Autowired
  private AccountSummaryService accountSummaryService;

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

  @Test
  @DisplayName("계좌목록조회: 단일페이지")
  public void step_01_getAccounts_single_page_success() throws Exception {
    setupServerAccountsSinglePage();

    ExecutionContext executionContext = ExecutionContext.builder()
        .banksaladUserId(BANKSALAD_USER_ID)
        .organizationId(ORGANIZATION_ID)
        .syncRequestId(UUID.randomUUID().toString())
        .executionRequestId(UUID.randomUUID().toString())
        .accessToken("test")
        .organizationCode("020")
        .organizationHost(ORGANIZATION_HOST + ":" + wiremock.port())
        .syncStartedAt(LocalDateTime.now(DateUtil.UTC_ZONE_ID))
        .build();

    summaryService.listAccountSummaries(executionContext, Executions.finance_bank_summaries,
        summaryRequestHelper, summaryResponseHelper);

    List<AccountSummary> depositAccountSummaries = accountSummaryService.listSummariesConsented(BANKSALAD_USER_ID,
        ORGANIZATION_ID, BankAccountType.DEPOSIT);

    List<AccountSummary> loanAccountSummaries = accountSummaryService.listSummariesConsented(BANKSALAD_USER_ID,
        ORGANIZATION_ID, BankAccountType.LOAN);

    List<AccountSummary> investAccountSummaries = accountSummaryService.listSummariesConsented(BANKSALAD_USER_ID,
        ORGANIZATION_ID, BankAccountType.INVEST);

    Assertions.assertThat(depositAccountSummaries.size()).isEqualTo(1);
    Assertions.assertThat(loanAccountSummaries.size()).isEqualTo(0);
    Assertions.assertThat(investAccountSummaries.size()).isEqualTo(0);
  }

  @Test
  @DisplayName("계좌목록조회: 복수페이지")
  public void step_02_getAccounts_multi_page_success() throws Exception {
    setupServerAccountsMultiPage();

    ExecutionContext executionContext = ExecutionContext.builder()
        .banksaladUserId(BANKSALAD_USER_ID)
        .organizationId(ORGANIZATION_ID)
        .syncRequestId(UUID.randomUUID().toString())
        .executionRequestId(UUID.randomUUID().toString())
        .accessToken("test")
        .organizationCode("020")
        .organizationHost(ORGANIZATION_HOST + ":" + wiremock.port())
        .syncStartedAt(LocalDateTime.now(DateUtil.UTC_ZONE_ID))
        .build();

    summaryService.listAccountSummaries(executionContext, Executions.finance_bank_summaries,
        summaryRequestHelper, summaryResponseHelper);

    List<AccountSummary> depositAccountSummaries = accountSummaryService.listSummariesConsented(BANKSALAD_USER_ID,
        ORGANIZATION_ID, BankAccountType.DEPOSIT);

    List<AccountSummary> loanAccountSummaries = accountSummaryService.listSummariesConsented(BANKSALAD_USER_ID,
        ORGANIZATION_ID, BankAccountType.LOAN);

    List<AccountSummary> investAccountSummaries = accountSummaryService.listSummariesConsented(BANKSALAD_USER_ID,
        ORGANIZATION_ID, BankAccountType.INVEST);

    Assertions.assertThat(depositAccountSummaries.size()).isEqualTo(4);
    Assertions.assertThat(loanAccountSummaries.size()).isEqualTo(0);
    Assertions.assertThat(investAccountSummaries.size()).isEqualTo(0);
  }

  private void setupServerAccountsSinglePage() throws Exception {
    // 계좌목록조회 page 01
    wiremock.stubFor(get(urlMatching("/accounts.*"))
        .withQueryParam("org_code", equalTo("020"))
        .withQueryParam("search_timestamp", equalTo("0"))
        .withQueryParam("limit", equalTo("500"))
        .willReturn(
            aResponse()
                .withStatus(HttpStatus.OK.value())
                .withHeader("Content-Type", ContentType.APPLICATION_JSON.toString())
                .withBody(readText("classpath:mock/bank/response/BA01_001_single_page_00.json"))));
  }

  private void setupServerAccountsMultiPage() throws Exception {
    // 계좌목록조회 page 01
    wiremock.stubFor(get(urlMatching("/accounts.*"))
        .withQueryParam("org_code", equalTo("020"))
        .withQueryParam("search_timestamp", equalTo("0"))
        .withQueryParam("limit", equalTo("500"))
        .willReturn(
            aResponse()
                .withStatus(HttpStatus.OK.value())
                .withHeader("Content-Type", ContentType.APPLICATION_JSON.toString())
                .withBody(readText("classpath:mock/bank/response/BA01_001_single_page_01.json"))));

    // 계좌목록조회 page 02
    wiremock.stubFor(get(urlMatching("/accounts.*"))
        .withQueryParam("org_code", equalTo("020"))
        .withQueryParam("search_timestamp", equalTo("0"))
        .withQueryParam("limit", equalTo("500"))
        .withQueryParam("next_page", equalTo("02"))
        .willReturn(
            aResponse()
                .withStatus(HttpStatus.OK.value())
                .withHeader("Content-Type", ContentType.APPLICATION_JSON.toString())
                .withBody(readText("classpath:mock/bank/response/BA01_001_single_page_02.json"))));

    // 계좌목록조회 page 03
    wiremock.stubFor(get(urlMatching("/accounts.*"))
        .withQueryParam("org_code", equalTo("020"))
        .withQueryParam("search_timestamp", equalTo("0"))
        .withQueryParam("limit", equalTo("500"))
        .withQueryParam("next_page", equalTo("03"))
        .willReturn(
            aResponse()
                .withStatus(HttpStatus.OK.value())
                .withHeader("Content-Type", ContentType.APPLICATION_JSON.toString())
                .withBody(readText("classpath:mock/bank/response/BA01_001_single_page_03.json"))));
  }
}
