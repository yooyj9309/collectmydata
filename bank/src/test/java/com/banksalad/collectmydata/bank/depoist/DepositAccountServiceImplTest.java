package com.banksalad.collectmydata.bank.depoist;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.cloud.contract.wiremock.WireMockSpring;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;

import com.banksalad.collectmydata.bank.common.db.entity.AccountSummaryEntity;
import com.banksalad.collectmydata.bank.common.db.entity.mapper.AccountSummaryMapper;
import com.banksalad.collectmydata.bank.common.db.repository.AccountSummaryRepository;
import com.banksalad.collectmydata.bank.common.dto.AccountSummary;
import com.banksalad.collectmydata.bank.depoist.dto.DepositAccountBasic;
import com.banksalad.collectmydata.bank.depoist.dto.DepositAccountDetail;
import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.util.DateUtil;
import com.github.tomakehurst.wiremock.WireMockServer;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.entity.ContentType;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.banksalad.collectmydata.bank.testutil.FileUtil.readText;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;

@Slf4j
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@DisplayName("수신계좌 서비스 테스트")
@Transactional
class DepositAccountServiceImplTest {

  private static final Long BANKSALAD_USER_ID = 1L;
  private static final String ORGANIZATION_ID = "woori_bank";
  private static final String ORGANIZATION_HOST = "http://localhost";

  @Autowired
  private DepositAccountService depositAccountService;

  @Autowired
  private AccountSummaryRepository accountSummaryRepository;

  private AccountSummaryMapper accountSummaryMapper = Mappers.getMapper(AccountSummaryMapper.class);

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

    List<AccountSummary> accountSummaries = accountSummaryRepository
        .findByBanksaladUserIdAndOrganizationIdAndIsConsent(BANKSALAD_USER_ID, ORGANIZATION_ID, true)
        .stream()
        .map(accountSummaryMapper::entityToDto)
        .collect(Collectors.toList());

    /* execution context */
    ExecutionContext executionContext = ExecutionContext.builder()
        .banksaladUserId(BANKSALAD_USER_ID)
        .organizationId(ORGANIZATION_ID)
        .accessToken("test")
        .organizationHost(ORGANIZATION_HOST + ":" + wiremock.port())
        .executionRequestId(UUID.randomUUID().toString())
        .syncStartedAt(LocalDateTime.now(DateUtil.KST_ZONE_ID))
        .build();

    List<DepositAccountBasic> depositAccountBasics = depositAccountService
        .listDepositAccountBasics(executionContext, accountSummaries);

    Assertions.assertThat(depositAccountBasics.size()).isEqualTo(1);
  }

  @Test
  @DisplayName("수신계좌 추가정보 조회")
  public void step_02_listDepositAccountDetails_success() throws Exception {
    /* deposit account detail mock server */
    setupServerDepositAccountDetail();

    /* save mock account summaries */
    List<AccountSummaryEntity> accountSummaryEntities = getAccountSummaryEntities();
    accountSummaryRepository.saveAll(accountSummaryEntities);

    List<AccountSummary> accountSummaries = accountSummaryRepository
        .findByBanksaladUserIdAndOrganizationIdAndIsConsent(BANKSALAD_USER_ID, ORGANIZATION_ID, true)
        .stream()
        .map(accountSummaryMapper::entityToDto)
        .collect(Collectors.toList());

    /* execution context */
    ExecutionContext executionContext = ExecutionContext.builder()
        .banksaladUserId(BANKSALAD_USER_ID)
        .organizationId(ORGANIZATION_ID)
        .accessToken("test")
        .organizationHost(ORGANIZATION_HOST + ":" + wiremock.port())
        .executionRequestId(UUID.randomUUID().toString())
        .syncStartedAt(LocalDateTime.now(DateUtil.KST_ZONE_ID))
        .build();

    List<DepositAccountDetail> depositAccountDetails = depositAccountService
        .listDepositAccountDetails(executionContext, accountSummaries);

    Assertions.assertThat(depositAccountDetails.size()).isEqualTo(2);
  }

  private void setupServerDepositAccountBasic() throws Exception {
    wiremock.stubFor(post(urlMatching("/accounts/deposit/basic"))
        .withRequestBody(equalToJson(readText("classpath:mock/bank/request/BA02_001_single_page_00.json")))
        .willReturn(
            aResponse()
                .withFixedDelay(1000)
                .withStatus(HttpStatus.OK.value())
                .withHeader("Content-Type", ContentType.APPLICATION_JSON.toString())
                .withBody(readText("classpath:mock/bank/response/BA02_001_single_page_00.json"))));
  }

  private void setupServerDepositAccountDetail() throws Exception {
    wiremock.stubFor(post(urlMatching("/accounts/deposit/detail"))
        .withRequestBody(equalToJson(readText("classpath:mock/bank/request/BA03_002_single_page_00.json")))
        .willReturn(
            aResponse()
                .withFixedDelay(1000)
                .withStatus(HttpStatus.OK.value())
                .withHeader("Content-Type", ContentType.APPLICATION_JSON.toString())
                .withBody(readText("classpath:mock/bank/response/BA03_002_single_page_00.json"))));
  }

  private List<AccountSummaryEntity> getAccountSummaryEntities() {
    return List.of(
        AccountSummaryEntity.builder()
            .banksaladUserId(BANKSALAD_USER_ID)
            .organizationId(ORGANIZATION_ID)
            .syncedAt(LocalDateTime.now(DateUtil.KST_ZONE_ID))
            .accountNum("100246541123")
            .accountStatus("01")
            .accountType("1001")
            .basicSearchTimestamp(0L)
            .detailSearchTimestamp(0L)
            .transactionFromDate(null)
            .isForeignDeposit(false)
            .isConsent(true)
            .prodName("자유입출식 계좌")
            .seqno("a123")
            .build(),
        AccountSummaryEntity.builder()
            .banksaladUserId(BANKSALAD_USER_ID)
            .organizationId(ORGANIZATION_ID)
            .syncedAt(LocalDateTime.now(DateUtil.KST_ZONE_ID))
            .accountNum("234246541143")
            .accountStatus("01")
            .accountType("1001")
            .basicSearchTimestamp(0L)
            .detailSearchTimestamp(0L)
            .transactionFromDate(null)
            .isForeignDeposit(false)
            .isConsent(false)
            .prodName("자유입출식 계좌")
            .build()
    );
  }
}
