package com.banksalad.collectmydata.referencebank.deposit;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.util.DateUtil;
import com.banksalad.collectmydata.finance.api.summary.SummaryRequestHelper;
import com.banksalad.collectmydata.finance.api.summary.SummaryResponseHelper;
import com.banksalad.collectmydata.finance.api.summary.SummaryService;
import com.banksalad.collectmydata.referencebank.collect.Executions;
import com.banksalad.collectmydata.referencebank.common.db.entity.AccountSummaryEntity;
import com.banksalad.collectmydata.referencebank.common.db.repository.AccountSummaryRepository;
import com.banksalad.collectmydata.referencebank.summary.dto.AccountSummary;
import com.banksalad.collectmydata.referencebank.summary.dto.ListAccountSummariesRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.cloud.contract.wiremock.WireMockSpring;
import org.springframework.http.HttpStatus;

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
import java.util.Optional;
import java.util.UUID;

import static com.banksalad.collectmydata.referencebank.testutil.FileUtil.readText;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@DisplayName("수신계좌 서비스 테스트")
class DepositAccountSummaryServiceTest {

  private static final int FIXED_DELAY = 0;

  private static final Long BANKSALAD_USER_ID = 1L;
  private static final String ORGANIZATION_ID = "woori_bank";
  private static final String ORGANIZATION_HOST = "http://localhost";

  @Autowired
  private SummaryService<ListAccountSummariesRequest, AccountSummary> accountSummaryService;
  @Autowired
  private SummaryRequestHelper<ListAccountSummariesRequest> bankSummaryRequestHelper;
  @Autowired
  private SummaryResponseHelper<AccountSummary> bankSummaryResponseHelper;
  @Autowired
  private AccountSummaryRepository accountSummaryRepository;

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

  private ExecutionContext initExecutionContext() {
    return ExecutionContext.builder()
        .consentId("consentId1")
        .syncRequestId("syncRequestId1")
        .banksaladUserId(BANKSALAD_USER_ID)
        .organizationId(ORGANIZATION_ID)
        .executionRequestId(UUID.randomUUID().toString())
        .organizationCode("020")
        .organizationHost(ORGANIZATION_HOST + ":" + wiremock.port())
        .accessToken("test")
        .syncStartedAt(LocalDateTime.now(DateUtil.UTC_ZONE_ID))
        .build();
  }

  @Test
  @DisplayName("수신계좌목록조회_싱글페이지")
  public void step_01_listDepositAccountSummaries_singlepage_success() throws Exception {
    /* deposit account summaries mock server */
    setupServerDepositAccountSummariesSingle();
    ExecutionContext executionContext = initExecutionContext();

    accountSummaryService.listAccountSummaries(
        executionContext, Executions.finance_bank_summaries, bankSummaryRequestHelper, bankSummaryResponseHelper);

    Optional<AccountSummaryEntity> accountSummaryEntity = accountSummaryRepository
        .findByBanksaladUserIdAndOrganizationIdAndAccountNumAndSeqno(BANKSALAD_USER_ID, ORGANIZATION_ID, "1234567890",
            "1");

    Assertions.assertThat(accountSummaryEntity.isPresent()).isTrue();
    assertEquals("consentId1", accountSummaryEntity.get().getConsentId());
    assertEquals("syncRequestId1", accountSummaryEntity.get().getSyncRequestId());
  }

  @Test
  @DisplayName("수신계좌목록조회_멀티페이지")
  public void step_02_listDepositAccountSummaries_multipage_success() throws Exception {
    /* deposit account summaries mock server */
    setupServerDepositAccountSummariesMulti();
    ExecutionContext executionContext = initExecutionContext();

    accountSummaryService.listAccountSummaries(
        executionContext, Executions.finance_bank_summaries, bankSummaryRequestHelper, bankSummaryResponseHelper);

    List<AccountSummaryEntity> accountSummaryEntities = accountSummaryRepository.findAll();

    Assertions.assertThat(accountSummaryEntities.size()).isEqualTo(4);
  }

  private void setupServerDepositAccountSummariesSingle() throws Exception {
    wiremock.stubFor(get(urlMatching("/accounts.*"))
        .withQueryParam("org_code", equalTo("020"))
        .withQueryParam("search_timestamp", equalTo("0"))
        .withQueryParam("limit", equalTo("500"))
        .willReturn(
            aResponse()
                .withStatus(HttpStatus.OK.value())
                .withHeader("Content-Type", ContentType.APPLICATION_JSON.toString())
                .withBody(readText("classpath:mock/bank/response/BA01_001_summary_single_page_00.json"))));
  }

  private void setupServerDepositAccountSummariesMulti() throws Exception {
    wiremock.stubFor(get(urlMatching("/accounts.*"))
        .withQueryParam("org_code", equalTo("020"))
        .withQueryParam("search_timestamp", equalTo("0"))
        .withQueryParam("limit", equalTo("500"))
        .willReturn(
            aResponse()
                .withStatus(HttpStatus.OK.value())
                .withHeader("Content-Type", ContentType.APPLICATION_JSON.toString())
                .withBody(readText("classpath:mock/bank/response/BA01_002_summary_multi_page_01.json"))));

    wiremock.stubFor(get(urlMatching("/accounts.*"))
        .withQueryParam("org_code", equalTo("020"))
        .withQueryParam("search_timestamp", equalTo("0"))
        .withQueryParam("next_page", equalTo("02"))
        .withQueryParam("limit", equalTo("500"))
        .willReturn(
            aResponse()
                .withStatus(HttpStatus.OK.value())
                .withHeader("Content-Type", ContentType.APPLICATION_JSON.toString())
                .withBody(readText("classpath:mock/bank/response/BA01_002_summary_multi_page_02.json"))));

    wiremock.stubFor(get(urlMatching("/accounts.*"))
        .withQueryParam("org_code", equalTo("020"))
        .withQueryParam("search_timestamp", equalTo("0"))
        .withQueryParam("next_page", equalTo("03"))
        .withQueryParam("limit", equalTo("500"))
        .willReturn(
            aResponse()
                .withStatus(HttpStatus.OK.value())
                .withHeader("Content-Type", ContentType.APPLICATION_JSON.toString())
                .withBody(readText("classpath:mock/bank/response/BA01_002_summary_multi_page_03.json"))));
  }
}
