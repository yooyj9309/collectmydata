package com.banksalad.collectmydata.irp.account;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.cloud.contract.wiremock.WireMockSpring;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.util.DateUtil;
import com.banksalad.collectmydata.finance.common.exception.ResponseNotOkException;
import com.banksalad.collectmydata.finance.common.service.UserSyncStatusService;
import com.banksalad.collectmydata.irp.TestConfig;
import com.banksalad.collectmydata.irp.collect.Apis;
import com.banksalad.collectmydata.irp.common.dto.IrpAccountSummary;
import com.github.tomakehurst.wiremock.WireMockServer;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.entity.ContentType;
import org.javers.core.Javers;
import org.javers.core.JaversBuilder;
import org.javers.core.diff.Diff;
import org.javers.core.diff.ListCompareAlgorithm;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static com.banksalad.collectmydata.irp.util.FileUtil.readText;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringBootTest(classes = TestConfig.class, webEnvironment = WebEnvironment.RANDOM_PORT)
@DisplayName("계좌 목록 조회 테스트")
@Transactional
public class IrpAccountSummaryServiceImplTest {

  private static final Long BANKSALAD_USER_ID = 1L;
  private static final String ORGANIZATION_ID = "organizationId";
  private static final String ORGANIZATION_HOST = "http://localhost";

  @Autowired
  private IrpAccountSummaryService irpAccountSummaryService;

  @Autowired
  private UserSyncStatusService userSyncStatusService;

  public static WireMockServer wiremock = new WireMockServer(WireMockSpring.options().dynamicPort());

  private List<IrpAccountSummary> expectedIrpAccountSummaries;

  @BeforeEach
  public void setupClass() {

    String accountNum = "1234123412341234";

    expectedIrpAccountSummaries = List.of(
        IrpAccountSummary.builder()
            .prodName("상품명1")
            .accountNum(accountNum)
            .seqno("1")
            .consent(true)
            .accountStatus("01").build(),
        IrpAccountSummary.builder()
            .prodName("상품명2")
            .accountNum(accountNum)
            .seqno("2")
            .consent(true)
            .accountStatus("01").build()
    );

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

  @DisplayName("계좌목록조회 - 단일")
  @Test
  void getAccountSummaries() throws ResponseNotOkException {

    /* transaction mock server */
    setupServerAccountsSinglePage();

    /* execution context */
    ExecutionContext executionContext = ExecutionContext.builder()
        .syncRequestId(UUID.randomUUID().toString())
        .banksaladUserId(BANKSALAD_USER_ID)
        .organizationId(ORGANIZATION_ID)
        .accessToken("test")
        .organizationHost(ORGANIZATION_HOST + ":" + wiremock.port())
        .organizationCode("020")
        .syncStartedAt(LocalDateTime.now(DateUtil.UTC_ZONE_ID))
        .build();

    List<IrpAccountSummary> actualIrpAccountSummaries = irpAccountSummaryService.listAccountSummaries(executionContext);

    assertThat(actualIrpAccountSummaries.get(0)).usingRecursiveComparison().ignoringFields().isEqualTo(expectedIrpAccountSummaries.get(0));
    assertThat(userSyncStatusService.getSearchTimestamp(BANKSALAD_USER_ID, ORGANIZATION_ID,
        Apis.irp_get_accounts)).isEqualTo(1000);
  }

  @DisplayName("계좌목록조회 - 목록")
  @Test
  void listAccountSummaries() throws ResponseNotOkException {

    /* transaction mock server */
    setupServerAccountsSinglePage();

    /* execution context */
    ExecutionContext executionContext = ExecutionContext.builder()
        .syncRequestId(UUID.randomUUID().toString())
        .banksaladUserId(BANKSALAD_USER_ID)
        .organizationId(ORGANIZATION_ID)
        .accessToken("test")
        .organizationHost(ORGANIZATION_HOST + ":" + wiremock.port())
        .organizationCode("020")
        .syncStartedAt(LocalDateTime.now(DateUtil.UTC_ZONE_ID))
        .build();

    List<IrpAccountSummary> actualIrpAccountSummaries = irpAccountSummaryService.listAccountSummaries(executionContext);

    Javers javers = JaversBuilder.javers()
        .withListCompareAlgorithm(ListCompareAlgorithm.LEVENSHTEIN_DISTANCE).build();
    Diff diff = javers.compareCollections(expectedIrpAccountSummaries, actualIrpAccountSummaries, IrpAccountSummary.class);

    assertThat(diff.getChanges().size()).isEqualTo(0);
    assertThat(userSyncStatusService.getSearchTimestamp(BANKSALAD_USER_ID, ORGANIZATION_ID,
        Apis.irp_get_accounts)).isEqualTo(1000);
  }

  private void setupServerAccountsSinglePage() {

    // 계좌목록조회 page 01
    wiremock.stubFor(get(urlMatching("/irps.*"))
        .withQueryParam("org_code", equalTo("020"))
        .withQueryParam("search_timestamp", equalTo("0"))
        .willReturn(
            aResponse()
                .withFixedDelay(50)
                .withStatus(HttpStatus.OK.value())
                .withHeader("Content-Type", ContentType.APPLICATION_JSON.toString())
                .withBody(readText("classpath:mock/irp/response/IR01_001_single_page_00.json"))));
  }
}
