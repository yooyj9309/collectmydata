package com.banksalad.collectmydata.bank.invest;

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
import com.banksalad.collectmydata.bank.depoist.dto.DepositAccountDetail;
import com.banksalad.collectmydata.bank.invest.dto.InvestAccountBasic;
import com.banksalad.collectmydata.bank.invest.dto.InvestAccountDetail;
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
import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@DisplayName("투자계좌 서비스 테스트")
@Transactional
class InvestAccountServiceImplTest {

  private static final Long BANKSALAD_USER_ID = 1L;
  private static final String ORGANIZATION_ID = "woori_bank";
  private static final String ORGANIZATION_HOST = "http://localhost";

  @Autowired
  private InvestAccountService investAccountService;

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
  @DisplayName("투자계좌 기본정보 조회")
  public void step_01_listInvestAccountBasics_success() throws Exception {
    /* invest account basic mock server */
    setupServerInvestAccountBasic();

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

    List<InvestAccountBasic> investAccountBasics = investAccountService
        .listInvestAccountBasics(executionContext, accountSummaries);

    assertThat(investAccountBasics.size()).isEqualTo(1);
  }

  @Test
  @DisplayName("투자계좌 추가정보 조회")
  public void step_02_listDepositAccountDetails_success() throws Exception {
    /* invest account detail mock server */
    setupServerInvestAccountDetail();

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
        .syncStartedAt(LocalDateTime.now(DateUtil.UTC_ZONE_ID))
        .build();

    List<InvestAccountDetail> investAccountDetails = investAccountService
        .listInvestAccountDetails(executionContext, accountSummaries);

    Assertions.assertThat(investAccountDetails.size()).isEqualTo(1);
  }

  private void setupServerInvestAccountBasic() throws Exception {
    wiremock.stubFor(post(urlMatching("/accounts/invest/basic"))
        .withRequestBody(equalToJson(readText("classpath:mock/bank/request/BA05_001_single_page_00.json")))
        .willReturn(
            aResponse()
                .withFixedDelay(1000)
                .withStatus(HttpStatus.OK.value())
                .withHeader("Content-Type", ContentType.APPLICATION_JSON.toString())
                .withBody(readText("classpath:mock/bank/response/BA05_001_single_page_00.json"))));
  }

  private void setupServerInvestAccountDetail() throws Exception {
    wiremock.stubFor(post(urlMatching("/accounts/invest/detail"))
        .withRequestBody(equalToJson(readText("classpath:mock/bank/request/BA06_001_single_page_00.json")))
        .willReturn(
            aResponse()
                .withFixedDelay(1000)
                .withStatus(HttpStatus.OK.value())
                .withHeader("Content-Type", ContentType.APPLICATION_JSON.toString())
                .withBody(readText("classpath:mock/bank/response/BA06_001_single_page_00.json"))));
  }

  private List<AccountSummaryEntity> getAccountSummaryEntities() {
    return List.of(
        AccountSummaryEntity.builder()
            .banksaladUserId(BANKSALAD_USER_ID)
            .organizationId(ORGANIZATION_ID)
            .syncedAt(LocalDateTime.now(DateUtil.KST_ZONE_ID))
            .accountNum("1234567890")
            .accountStatus("01")
            .accountType("2001")
            .basicSearchTimestamp(0L)
            .detailSearchTimestamp(0L)
            .transactionFromDate(null)
            .isForeignDeposit(false)
            .isConsent(true)
            .prodName("자유입출식 계좌")
            .seqno("1")
            .build(),
        AccountSummaryEntity.builder()
            .banksaladUserId(BANKSALAD_USER_ID)
            .organizationId(ORGANIZATION_ID)
            .syncedAt(LocalDateTime.now(DateUtil.KST_ZONE_ID))
            .accountNum("234246541143")
            .accountStatus("01")
            .accountType("2001")
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