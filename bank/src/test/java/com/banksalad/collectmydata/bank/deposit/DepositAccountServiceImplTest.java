package com.banksalad.collectmydata.bank.deposit;

import com.banksalad.collectmydata.bank.common.collect.Executions;
import com.banksalad.collectmydata.bank.common.db.entity.AccountSummaryEntity;
import com.banksalad.collectmydata.bank.common.db.entity.DepositAccountDetailEntity;
import com.banksalad.collectmydata.bank.common.db.repository.AccountSummaryRepository;
import com.banksalad.collectmydata.bank.common.db.repository.DepositAccountDetailRepository;
import com.banksalad.collectmydata.bank.deposit.dto.DepositAccountBasic;
import com.banksalad.collectmydata.bank.deposit.dto.DepositAccountDetail;
import com.banksalad.collectmydata.bank.deposit.dto.GetDepositAccountBasicRequest;
import com.banksalad.collectmydata.bank.deposit.dto.GetDepositAccountDetailRequest;
import com.banksalad.collectmydata.bank.summary.dto.AccountSummary;
import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.util.DateUtil;
import com.banksalad.collectmydata.finance.api.accountinfo.AccountInfoRequestHelper;
import com.banksalad.collectmydata.finance.api.accountinfo.AccountInfoResponseHelper;
import com.banksalad.collectmydata.finance.api.accountinfo.AccountInfoService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.WireMockSpring;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;

import com.github.tomakehurst.wiremock.WireMockServer;
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
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;

@SpringBootTest
@DisplayName("수신계좌 서비스 테스트")
@Transactional
class DepositAccountServiceImplTest {

  private static final Long BANKSALAD_USER_ID = 1L;
  private static final String ORGANIZATION_ID = "woori_bank";
  private static final String ORGANIZATION_HOST = "http://localhost";

  @Autowired
  private AccountInfoService<AccountSummary, GetDepositAccountBasicRequest, DepositAccountBasic> depositAccountBasicApiService;

  @Autowired
  private AccountInfoRequestHelper<GetDepositAccountBasicRequest, AccountSummary> depositAccountBasicInfoRequestHelper;

  @Autowired
  private AccountInfoResponseHelper<AccountSummary, DepositAccountBasic> depositAccountBasicInfoResponseHelper;

  @Autowired
  private AccountInfoService<AccountSummary, GetDepositAccountDetailRequest, List<DepositAccountDetail>> depositAccountDetailApiService;

  @Autowired
  private AccountInfoRequestHelper<GetDepositAccountDetailRequest, AccountSummary> depositAccountDetailInfoRequestHelper;

  @Autowired
  private AccountInfoResponseHelper<AccountSummary, List<DepositAccountDetail>> depositAccountDetailInfoResponseHelper;

  @Autowired
  private AccountSummaryRepository accountSummaryRepository;

  @Autowired
  private DepositAccountDetailRepository depositAccountDetailRepository;

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
  @DisplayName("수신계좌 기본정보 조회")
  public void step_01_listDepositAccountBasics_success() throws Exception {
    /* deposit account basic mock server */
    setupServerDepositAccountBasic();

    /* save mock account summaries */
    List<AccountSummaryEntity> accountSummaryEntities = getAccountSummaryEntities();
    accountSummaryRepository.saveAll(accountSummaryEntities);

    /* execution context */
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

    // TODO : check testcase code
    depositAccountBasicApiService.listAccountInfos(executionContext,
        Executions.finance_bank_deposit_account_basic, depositAccountBasicInfoRequestHelper,
        depositAccountBasicInfoResponseHelper);

//    Assertions.assertThat(depositAccountBasics.size()).isEqualTo(1);
  }

  @Test
  @DisplayName("수신계좌 추가정보 조회")
  public void step_02_listDepositAccountDetails_success() throws Exception {
    /* deposit account detail mock server */
    setupServerDepositAccountDetail();

    /* save mock account summaries */
    List<AccountSummaryEntity> accountSummaryEntities = getAccountSummaryEntities();
    accountSummaryRepository.saveAll(accountSummaryEntities);

    /* execution context */
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

    depositAccountDetailApiService.listAccountInfos(executionContext,
        Executions.finance_bank_deposit_account_detail, depositAccountDetailInfoRequestHelper,
        depositAccountDetailInfoResponseHelper);

    List<DepositAccountDetailEntity> depositAccountDetailEntities = depositAccountDetailRepository
        .findByBanksaladUserIdAndOrganizationId(BANKSALAD_USER_ID, ORGANIZATION_ID);

    Assertions.assertThat(depositAccountDetailEntities.size()).isEqualTo(2);
  }

  private void setupServerDepositAccountBasic() throws Exception {
    wiremock.stubFor(post(urlMatching("/accounts/deposit/basic"))
        .withRequestBody(equalToJson(readText("classpath:mock/bank/request/BA02_001_single_page_00.json")))
        .willReturn(
            aResponse()
                .withStatus(HttpStatus.OK.value())
                .withHeader("Content-Type", ContentType.APPLICATION_JSON.toString())
                .withBody(readText("classpath:mock/bank/response/BA02_001_single_page_00.json"))));
  }

  private void setupServerDepositAccountDetail() throws Exception {
    wiremock.stubFor(post(urlMatching("/accounts/deposit/detail"))
        .withRequestBody(equalToJson(readText("classpath:mock/bank/request/BA03_002_single_page_00.json")))
        .willReturn(
            aResponse()
                .withStatus(HttpStatus.OK.value())
                .withHeader("Content-Type", ContentType.APPLICATION_JSON.toString())
                .withBody(readText("classpath:mock/bank/response/BA03_002_single_page_00.json"))));
  }

  private List<AccountSummaryEntity> getAccountSummaryEntities() {
    return List.of(
        AccountSummaryEntity.builder()
            .banksaladUserId(BANKSALAD_USER_ID)
            .organizationId(ORGANIZATION_ID)
            .syncedAt(LocalDateTime.now(DateUtil.UTC_ZONE_ID))
            .accountNum("100246541123")
            .accountStatus("01")
            .accountType("1001")
            .basicSearchTimestamp(0L)
            .detailSearchTimestamp(0L)
            .transactionSyncedAt(null)
            .foreignDeposit(false)
            .consent(true)
            .prodName("자유입출식 계좌")
            .seqno("a123")
            .build(),
        AccountSummaryEntity.builder()
            .banksaladUserId(BANKSALAD_USER_ID)
            .organizationId(ORGANIZATION_ID)
            .syncedAt(LocalDateTime.now(DateUtil.UTC_ZONE_ID))
            .accountNum("234246541143")
            .accountStatus("01")
            .accountType("1001")
            .basicSearchTimestamp(0L)
            .detailSearchTimestamp(0L)
            .transactionSyncedAt(null)
            .foreignDeposit(false)
            .consent(false)
            .prodName("자유입출식 계좌")
            .build()
    );
  }
}
