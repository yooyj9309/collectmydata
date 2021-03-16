package com.banksalad.collectmydata.telecom.summary;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.util.DateUtil;
import com.banksalad.collectmydata.finance.api.summary.SummaryService;
import com.banksalad.collectmydata.finance.common.db.repository.UserSyncStatusRepository;
import com.banksalad.collectmydata.finance.common.service.UserSyncStatusService;
import com.banksalad.collectmydata.telecom.collect.Apis;
import com.banksalad.collectmydata.telecom.collect.Executions;
import com.banksalad.collectmydata.telecom.common.db.entity.TelecomSummaryEntity;
import com.banksalad.collectmydata.telecom.common.db.repository.TelecomSummaryRepository;
import com.banksalad.collectmydata.telecom.common.mapper.TelecomSummaryMapper;
import com.banksalad.collectmydata.telecom.summary.dto.ListTelecomSummariesRequest;
import com.banksalad.collectmydata.telecom.summary.dto.TelecomSummary;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.WireMockSpring;
import org.springframework.http.HttpStatus;

import com.github.tomakehurst.wiremock.WireMockServer;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.entity.ContentType;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.util.UUID;

import static com.banksalad.collectmydata.telecom.util.FileUtil.readText;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@TestMethodOrder(MethodOrderer.MethodName.class)
@SpringBootTest
public class TelecomSummaryServiceTest {

  private static final int FIXED_DELAY = 500;
  private static final String ACCESS_TOKEN = "xxx.yyy.zzz";
  private static final Long BANKSALAD_USER_ID = 1L;
  private static final String ORGANIZATION_ID = "x-telecom";
  private static final String ORGANIZATION_CODE = "020";
  private static final String ORGANIZATION_HOST = "http://localhost";

  private static final WireMockServer wiremock = new WireMockServer(WireMockSpring.options().dynamicPort());
  private final TelecomSummaryMapper telecomSummaryMapper = Mappers.getMapper(TelecomSummaryMapper.class);

  @Autowired
  private TelecomSummaryRequestHelper telecomSummaryRequestHelper;
  @Autowired
  private TelecomSummaryResponseHelper telecomSummaryResponseHelper;
  @Autowired
  private SummaryService<ListTelecomSummariesRequest, TelecomSummary> telecomSummaryService;
  @Autowired
  private TelecomSummaryRepository telecomSummaryRepository;
  @Autowired
  private UserSyncStatusRepository userSyncStatusRepository;
  @Autowired
  private UserSyncStatusService userSyncStatusService;

  @AfterAll
  public static void teardown() {
    wiremock.shutdown();
  }

  @BeforeEach
  public void setup() {
    wiremock.start();
  }

  @AfterEach
  public void after() {
    wiremock.resetAll();
  }

  @Test
  @DisplayName("6.9.1 (1) 통신 계약 목록 조회: 첫번째 조회")
  public void tc01_001_listTelecomSummaries_single_01() throws Exception {
    // Given
    final String MGMT_ID = "1234567890";
    final long SEARCH_TIMSTAMP = 0;
    stubForListSummary(SEARCH_TIMSTAMP, "01");
    ExecutionContext executionContext = initExecutionContext();

    // When
    telecomSummaryService
        .listAccountSummaries(executionContext, Executions.finance_telecom_summaries, telecomSummaryRequestHelper,
            telecomSummaryResponseHelper);
    TelecomSummaryEntity telecomSummaryEntity = telecomSummaryRepository
        .findByBanksaladUserIdAndOrganizationIdAndMgmtId(BANKSALAD_USER_ID, ORGANIZATION_ID, MGMT_ID)
        .orElse(null);
    TelecomSummary actualTelecomSummary = telecomSummaryMapper.entityToDto(telecomSummaryEntity);

    // Then
    assertThat(telecomSummaryRepository.count()).isEqualTo(1);
    TelecomSummary expectedTelecomSummary = TelecomSummary.builder()
        .mgmtId(MGMT_ID)
        .consent(true)
        .telecomNum("01012345678")
        .type("01")
        .status("01")
        .build();
    assertThat(actualTelecomSummary).usingRecursiveComparison().ignoringFields().isEqualTo(expectedTelecomSummary);
    assertThat(userSyncStatusService.getSearchTimestamp(BANKSALAD_USER_ID, ORGANIZATION_ID,
        Apis.finance_telecom_summaries)).isEqualTo(100);
  }

  @Test
  @DisplayName("6.9.1 (2) 통신 계약 목록 조회: 두번째 조회")
  public void tc01_001_listTelecomSummaries_single_02() throws Exception {
    // Given
    final String MGMT_ID = "1234567892";
    final long SEARCH_TIMSTAMP = 100;
    stubForListSummary(SEARCH_TIMSTAMP, "02");
    ExecutionContext executionContext = initExecutionContext();

    // When
    telecomSummaryService
        .listAccountSummaries(executionContext, Executions.finance_telecom_summaries, telecomSummaryRequestHelper,
            telecomSummaryResponseHelper);
    TelecomSummaryEntity telecomSummaryEntity = telecomSummaryRepository
        .findByBanksaladUserIdAndOrganizationIdAndMgmtId(BANKSALAD_USER_ID, ORGANIZATION_ID, MGMT_ID)
        .orElse(null);
    TelecomSummary actualTelecomSummary = telecomSummaryMapper.entityToDto(telecomSummaryEntity);

    // Then
    assertThat(telecomSummaryRepository.count()).isEqualTo(3);
    TelecomSummary expectedTelecomSummary = TelecomSummary.builder()
        .mgmtId(MGMT_ID)
        .consent(true)
        .telecomNum("77701234567")
        .type("03")
        .status("02")
        .build();
    assertThat(actualTelecomSummary).usingRecursiveComparison().ignoringFields().isEqualTo(expectedTelecomSummary);
    assertThat(userSyncStatusService.getSearchTimestamp(BANKSALAD_USER_ID, ORGANIZATION_ID,
        Apis.finance_telecom_summaries)).isEqualTo(200);

  }

  private ExecutionContext initExecutionContext() {
    return ExecutionContext.builder()
        .syncRequestId(UUID.randomUUID().toString())
        .banksaladUserId(BANKSALAD_USER_ID)
        .organizationId(ORGANIZATION_ID)
        .executionRequestId(UUID.randomUUID().toString())
        .organizationCode(ORGANIZATION_CODE)
        .organizationHost(ORGANIZATION_HOST + ":" + wiremock.port())
        .accessToken(ACCESS_TOKEN)
        .syncStartedAt(LocalDateTime.now(DateUtil.UTC_ZONE_ID))
        .build();
  }

  private void stubForListSummary(long searchTimestamp, String seq) {
    wiremock.stubFor(get(urlMatching("/telecoms.*"))
        .withQueryParam("org_code", equalTo(ORGANIZATION_CODE))
        .withQueryParam("search_timestamp", equalTo(String.valueOf(searchTimestamp)))
        .willReturn(
            aResponse()
                .withFixedDelay(FIXED_DELAY)
                .withStatus(HttpStatus.OK.value())
                .withHeader("Content-Type", ContentType.APPLICATION_JSON.toString())
                .withBody(readText("classpath:mock/response/TC01_001_summary_single_page_" + seq + ".json"))));
  }
}
