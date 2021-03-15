package com.banksalad.collectmydata.referencebank.deposit;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.util.DateUtil;
import com.banksalad.collectmydata.finance.api.accountinfo.AccountInfoRequestHelper;
import com.banksalad.collectmydata.finance.api.accountinfo.AccountInfoResponseHelper;
import com.banksalad.collectmydata.finance.api.accountinfo.AccountInfoService;
import com.banksalad.collectmydata.referencebank.collect.Executions;
import com.banksalad.collectmydata.referencebank.common.db.entity.DepositAccountDetailEntity;
import com.banksalad.collectmydata.referencebank.common.db.repository.DepositAccountDetailRepository;
import com.banksalad.collectmydata.referencebank.common.enums.BankAccountType;
import com.banksalad.collectmydata.referencebank.common.service.AccountSummaryService;
import com.banksalad.collectmydata.referencebank.deposit.dto.DepositAccountDetail;
import com.banksalad.collectmydata.referencebank.deposit.dto.GetDepositAccountDetailRequest;
import com.banksalad.collectmydata.referencebank.summaries.dto.AccountSummary;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
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
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.banksalad.collectmydata.referencebank.testutil.FileUtil.readText;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;

@Slf4j
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@DisplayName("수신계좌 서비스 테스트")
class DepositAccountDetailServiceTest {

  private static final int FIXED_DELAY = 0;

  private static final Long BANKSALAD_USER_ID = 1L;
  private static final String ORGANIZATION_ID = "woori_bank";
  private static final String ORGANIZATION_HOST = "http://localhost";

  @Autowired
  private AccountInfoService<AccountSummary, GetDepositAccountDetailRequest, List<DepositAccountDetail>> depositAccountDetailApiService;
  @Autowired
  private AccountInfoRequestHelper<GetDepositAccountDetailRequest, AccountSummary> depositAccountDetailInfoRequestHelper;
  @Autowired
  private AccountInfoResponseHelper<AccountSummary, List<DepositAccountDetail>> depositAccountInfoDetailResponseHelper;
  @Autowired
  private DepositAccountDetailRepository depositAccountDetailRepository;

  @MockBean
  AccountSummaryService accountSummaryService;

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
        .syncRequestId(UUID.randomUUID().toString())
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
  @DisplayName("수신계좌추가정보 조회")
  public void step_03_listDepositAccountDetail_success() throws Exception {
    /* deposit account detail mock server */
    setupServerDepositAccountDetail();
    ExecutionContext executionContext = initExecutionContext();

    Mockito.when(accountSummaryService.listSummariesConsented(BANKSALAD_USER_ID, ORGANIZATION_ID, BankAccountType.DEPOSIT))
        .thenReturn(List.of(
            AccountSummary.builder()
                .accountNum("1234567890")
                .seqno("01")
                .accountType(BankAccountType.depositAccountTypeCodes.get(0))
                .foreignDeposit(false)
                .consent(true)
                .prodName("뱅크샐러드 대박 적금")
                .build()
        ));

    List<List<DepositAccountDetail>> depositAccountDetails = depositAccountDetailApiService
        .listAccountInfos(executionContext, Executions.finance_bank_deposit_account_detail, depositAccountDetailInfoRequestHelper,
            depositAccountInfoDetailResponseHelper);

    Optional<DepositAccountDetailEntity> depositAccountDetailEntityKrw = depositAccountDetailRepository
        .findByBanksaladUserIdAndOrganizationIdAndAccountNumAndSeqnoAndCurrencyCode(
            BANKSALAD_USER_ID, ORGANIZATION_ID, "1234567890", "01", "KRW");

    Optional<DepositAccountDetailEntity> depositAccountDetailEntityUsd = depositAccountDetailRepository
        .findByBanksaladUserIdAndOrganizationIdAndAccountNumAndSeqnoAndCurrencyCode(
            BANKSALAD_USER_ID, ORGANIZATION_ID, "1234567890", "01", "USD");

    Assertions.assertThat(depositAccountDetails.size()).isEqualTo(1);
    Assertions.assertThat(depositAccountDetails.get(0).size()).isEqualTo(2);
    Assertions.assertThat(depositAccountDetailEntityKrw.isPresent()).isTrue();
    Assertions.assertThat(depositAccountDetailEntityUsd.isPresent()).isTrue();
  }

  private void setupServerDepositAccountDetail() throws Exception {
    wiremock.stubFor(post(urlMatching("/accounts/deposit/detail"))
        .withRequestBody(equalToJson(readText("classpath:mock/bank/request/BA03_001_detail_00.json")))
        .willReturn(
            aResponse()
                .withFixedDelay(FIXED_DELAY)
                .withStatus(HttpStatus.OK.value())
                .withHeader("Content-Type", ContentType.APPLICATION_JSON.toString())
                .withBody(readText("classpath:mock/bank/response/BA03_001_detail_00.json"))));
  }
}
