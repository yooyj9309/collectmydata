package com.banksalad.collectmydata.bank.invest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.contract.wiremock.WireMockSpring;
import org.springframework.http.HttpStatus;

import com.banksalad.collectmydata.bank.common.collect.Executions;
import com.banksalad.collectmydata.bank.common.db.entity.InvestAccountDetailEntity;
import com.banksalad.collectmydata.bank.common.db.repository.InvestAccountDetailRepository;
import com.banksalad.collectmydata.bank.common.enums.BankAccountType;
import com.banksalad.collectmydata.bank.common.service.AccountSummaryService;
import com.banksalad.collectmydata.bank.invest.dto.GetInvestAccountDetailRequest;
import com.banksalad.collectmydata.bank.invest.dto.InvestAccountDetail;
import com.banksalad.collectmydata.bank.summary.dto.AccountSummary;
import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.util.DateUtil;
import com.banksalad.collectmydata.finance.api.accountinfo.AccountInfoRequestHelper;
import com.banksalad.collectmydata.finance.api.accountinfo.AccountInfoResponseHelper;
import com.banksalad.collectmydata.finance.api.accountinfo.AccountInfoService;
import com.github.tomakehurst.wiremock.WireMockServer;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.entity.ContentType;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.banksalad.collectmydata.bank.testutil.FileUtil.readText;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@DisplayName("투자계좌 추가 정보 서비스 테스트")
class InvestAccountDetailServiceTest {

  public static final WireMockServer wiremock = new WireMockServer(WireMockSpring.options().dynamicPort());

  private static final Long BANKSALAD_USER_ID = 1L;
  private static final String ORGANIZATION_ID = "woori_bank";
  private static final String ORGANIZATION_HOST = "http://localhost";

  @Autowired
  private AccountInfoService<AccountSummary, GetInvestAccountDetailRequest, InvestAccountDetail> investAccountDetailApiService;

  @Autowired
  private AccountInfoRequestHelper<GetInvestAccountDetailRequest, AccountSummary> investAccountDetailInfoRequestHelper;

  @Autowired
  private AccountInfoResponseHelper<AccountSummary, InvestAccountDetail> investAccountInfoDetailResponseHelper;

  @MockBean
  private AccountSummaryService accountSummaryService;

  @Autowired
  private InvestAccountDetailRepository investAccountDetailRepository;

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
        .banksaladUserId(BANKSALAD_USER_ID)
        .organizationId(ORGANIZATION_ID)
        .syncRequestId(UUID.randomUUID().toString())
        .executionRequestId(UUID.randomUUID().toString())
        .accessToken("test")
        .organizationCode("020")
        .organizationHost(ORGANIZATION_HOST + ":" + wiremock.port())
        .executionRequestId(UUID.randomUUID().toString())
        .syncStartedAt(LocalDateTime.now(DateUtil.UTC_ZONE_ID))
        .build();
  }

  @Test
  @DisplayName("투자계좌 추가 정보 조회")
  public void step_01_listInvestAccountDetails_success() throws Exception {
    /* invest account detail mock server */
    setupServerInvestAccountDetail();

    /* execution context */
    ExecutionContext executionContext = initExecutionContext();

    Mockito
        .when(accountSummaryService.listSummariesConsented(BANKSALAD_USER_ID, ORGANIZATION_ID, BankAccountType.INVEST))
        .thenReturn(getAccountSummaries());

    List<InvestAccountDetail> investAccountDetails = investAccountDetailApiService.listAccountInfos(
        executionContext,
        Executions.finance_bank_invest_account_detail,
        investAccountDetailInfoRequestHelper,
        investAccountInfoDetailResponseHelper
    );

    Optional<InvestAccountDetailEntity> investAccountDetailEntity = investAccountDetailRepository
        .findByBanksaladUserIdAndOrganizationIdAndAccountNumAndSeqnoAndCurrencyCode(
            BANKSALAD_USER_ID,
            ORGANIZATION_ID,
            "1234567890",
            "1",
            "KRW"
        );

    assertThat(investAccountDetails.size()).isEqualTo(1);
    assertThat(investAccountDetailEntity.isPresent()).isTrue();
  }

  private void setupServerInvestAccountDetail() throws Exception {
    wiremock.stubFor(post(urlMatching("/accounts/invest/detail"))
        .withRequestBody(equalToJson(readText("classpath:mock/bank/request/BA06_001_single_page_00.json")))
        .willReturn(
            aResponse()
                .withStatus(HttpStatus.OK.value())
                .withHeader("Content-Type", ContentType.APPLICATION_JSON.toString())
                .withBody(readText("classpath:mock/bank/response/BA06_001_single_page_00.json"))));
  }

  private List<AccountSummary> getAccountSummaries() {
    return List.of(
        AccountSummary.builder()
            .accountNum("1234567890")
            .accountStatus("01")
            .accountType("2001")
            .foreignDeposit(false)
            .consent(true)
            .prodName("자유입출식 계좌")
            .seqno("1")
            .build()
    );
  }
}
