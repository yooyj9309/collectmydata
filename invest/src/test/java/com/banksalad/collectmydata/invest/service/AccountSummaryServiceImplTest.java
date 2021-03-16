package com.banksalad.collectmydata.invest.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.WireMockSpring;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.util.DateUtil;
import com.banksalad.collectmydata.finance.api.summary.SummaryRequestHelper;
import com.banksalad.collectmydata.finance.api.summary.SummaryResponseHelper;
import com.banksalad.collectmydata.finance.api.summary.SummaryService;
import com.banksalad.collectmydata.finance.common.exception.ResponseNotOkException;
import com.banksalad.collectmydata.invest.collect.Executions;
import com.banksalad.collectmydata.invest.common.db.entity.AccountSummaryEntity;
import com.banksalad.collectmydata.invest.common.db.entity.OrganizationUserEntity;
import com.banksalad.collectmydata.invest.common.db.repository.AccountSummaryRepository;
import com.banksalad.collectmydata.invest.common.db.repository.OrganizationUserRepository;
import com.banksalad.collectmydata.invest.summary.dto.AccountSummary;
import com.banksalad.collectmydata.invest.summary.dto.ListAccountSummariesRequest;
import com.github.tomakehurst.wiremock.WireMockServer;
import org.apache.http.entity.ContentType;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static com.banksalad.collectmydata.invest.util.FileUtil.readText;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class AccountSummaryServiceImplTest {

  @Autowired
  private SummaryService<ListAccountSummariesRequest, AccountSummary> accountSummaryService;
  @Autowired
  private SummaryRequestHelper<ListAccountSummariesRequest> investSummaryRequestHelper;
  @Autowired
  private SummaryResponseHelper<AccountSummary> investSummaryResponseHelper;
  @Autowired
  private AccountSummaryRepository accountSummaryRepository;
  @Autowired
  private OrganizationUserRepository organizationUserRepository;

  static WireMockServer wireMockServer;
  private static final Long BANKSALAD_USER_ID = 1L;
  private static final String ORGANIZATION_ID = "nh_securities";
  private static final String ORGANIZATION_HOST = "http://localhost";
  private static final String ACCESS_TOKEN = "accessToken";
  private static final String ORGANIZATION_CODE = "organizationCode";

  @BeforeAll
  static void setup() {
    wireMockServer = new WireMockServer(WireMockSpring.options().dynamicPort());
    wireMockServer.start();
    setupMockServer();
  }

  @AfterAll
  static void clean() {
    wireMockServer.shutdown();
  }

  @Test
  @Transactional
  @DisplayName("6.4.1 계좌 목록 조회 성공 테스트")
  void listAccountSummariesTest() throws ResponseNotOkException {
    ExecutionContext executionContext = ExecutionContext.builder()
        .syncRequestId(UUID.randomUUID().toString())
        .banksaladUserId(BANKSALAD_USER_ID)
        .organizationId(ORGANIZATION_ID)
        .organizationCode(ORGANIZATION_CODE)
        .accessToken(ACCESS_TOKEN)
        .organizationHost(ORGANIZATION_HOST + ":" + wireMockServer.port())
        .executionRequestId(UUID.randomUUID().toString())
        .syncStartedAt(LocalDateTime.now(DateUtil.UTC_ZONE_ID))
        .build();

    accountSummaryService.listAccountSummaries(
        executionContext, Executions.finance_invest_accounts, investSummaryRequestHelper, investSummaryResponseHelper);

    Optional<AccountSummaryEntity> accountSummaryEntity = accountSummaryRepository
        .findByBanksaladUserIdAndOrganizationIdAndAccountNum(BANKSALAD_USER_ID, ORGANIZATION_ID, "1234567890");

    Optional<OrganizationUserEntity> organizationUserEntity = organizationUserRepository
        .findByBanksaladUserIdAndOrganizationId(BANKSALAD_USER_ID, ORGANIZATION_ID);

    assertTrue(accountSummaryEntity.isPresent());
    assertTrue(organizationUserEntity.isPresent());
  }

  private static void setupMockServer() {
    wireMockServer.stubFor(get(urlMatching("/accounts.*"))
        .withQueryParam("org_code", equalTo(ORGANIZATION_CODE))
        .withQueryParam("search_timestamp", equalTo("0"))
        .willReturn(
            aResponse()
                .withFixedDelay(0)
                .withStatus(HttpStatus.OK.value())
                .withHeader("Content-Type", ContentType.APPLICATION_JSON.toString())
                .withBody(readText("classpath:mock/response/IV01_001_single_page_00.json"))));
  }
}
