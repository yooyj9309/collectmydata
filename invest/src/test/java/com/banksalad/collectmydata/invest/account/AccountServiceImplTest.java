package com.banksalad.collectmydata.invest.account;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.contract.wiremock.WireMockSpring;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.organization.Organization;
import com.banksalad.collectmydata.common.util.DateUtil;
import com.banksalad.collectmydata.finance.api.accountinfo.AccountInfoRequestHelper;
import com.banksalad.collectmydata.finance.api.accountinfo.AccountInfoResponseHelper;
import com.banksalad.collectmydata.finance.api.accountinfo.AccountInfoService;
import com.banksalad.collectmydata.invest.account.dto.AccountBasic;
import com.banksalad.collectmydata.invest.account.dto.GetAccountBasicRequest;
import com.banksalad.collectmydata.invest.collect.Executions;
import com.banksalad.collectmydata.invest.common.db.entity.AccountBasicEntity;
import com.banksalad.collectmydata.invest.common.db.entity.AccountSummaryEntity;
import com.banksalad.collectmydata.invest.common.db.entity.mapper.AccountSummaryMapper;
import com.banksalad.collectmydata.invest.common.db.repository.AccountBasicRepository;
import com.banksalad.collectmydata.invest.common.db.repository.AccountSummaryRepository;
import com.banksalad.collectmydata.invest.common.service.AccountSummaryService;
import com.banksalad.collectmydata.invest.summary.dto.AccountSummary;
import com.github.tomakehurst.wiremock.WireMockServer;
import org.apache.http.entity.ContentType;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.banksalad.collectmydata.invest.util.FileUtil.readText;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class AccountServiceImplTest {

  @Autowired
  private AccountInfoService<AccountSummary, GetAccountBasicRequest, AccountBasic> accountBasicService;
  @Autowired
  private AccountInfoRequestHelper<GetAccountBasicRequest, AccountSummary> accountInfoRequestHelper;
  @Autowired
  private AccountInfoResponseHelper<AccountSummary, AccountBasic> accountInfoResponseHelper;
  @Autowired
  private AccountBasicRepository accountBasicRepository;

  @MockBean
  AccountSummaryService accountSummaryService;

  private static WireMockServer wireMockServer;

  private static final Long BANKSALAD_USER_ID = 1L;
  private static final String ORGANIZATION_ID = "nh_securities";
  private static final String ORGANIZATION_HOST = "http://localhost";
  private static final String ACCESS_TOKEN = "accessToken";
  private static final String ORGANIZATION_CODE = "020";

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
  @DisplayName("6.4.2 계좌 기본정보 조회 성공 테스트")
  void listInvestAccountBasicsTest() {
    Mockito.when(accountSummaryService.listSummariesConsented(BANKSALAD_USER_ID, ORGANIZATION_ID))
        .thenReturn(List.of(
            AccountSummary.builder()
                .accountNum("1234567890")
                .consent(true)
                .accountName("종합매매 증권계좌")
                .accountStatus("201")
                .accountType("101")
                .build()
        ));

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

    List<AccountBasic> accountBasics = accountBasicService
        .listAccountInfos(executionContext, Executions.finance_invest_account_basic, accountInfoRequestHelper,
            accountInfoResponseHelper);

    Optional<AccountBasicEntity> accountBasicEntity = accountBasicRepository
        .findByBanksaladUserIdAndOrganizationIdAndAccountNum(BANKSALAD_USER_ID, ORGANIZATION_ID, "1234567890");

    assertEquals(1, accountBasics.size());
    assertTrue(accountBasicEntity.isPresent());
  }

  private static void setupMockServer() {
    wireMockServer.stubFor(post(urlMatching("/accounts/basic"))
        .withRequestBody(equalToJson(readText("classpath:mock/request/IV02_001_single_page_00.json")))
        .willReturn(
            aResponse()
                .withFixedDelay(0)
                .withStatus(HttpStatus.OK.value())
                .withHeader("Content-Type", ContentType.APPLICATION_JSON.toString())
                .withBody(readText("classpath:mock/response/IV02_001_single_page_00.json"))));
  }
}
