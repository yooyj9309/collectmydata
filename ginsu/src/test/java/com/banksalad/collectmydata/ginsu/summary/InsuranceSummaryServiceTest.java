package com.banksalad.collectmydata.ginsu.summary;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.WireMockSpring;
import org.springframework.http.HttpStatus;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.util.DateUtil;
import com.banksalad.collectmydata.finance.api.summary.SummaryRequestHelper;
import com.banksalad.collectmydata.finance.api.summary.SummaryResponseHelper;
import com.banksalad.collectmydata.finance.api.summary.SummaryService;
import com.banksalad.collectmydata.ginsu.collect.Executions;
import com.banksalad.collectmydata.ginsu.common.service.InsuranceSummaryService;
import com.banksalad.collectmydata.ginsu.summary.dto.InsuranceSummary;
import com.banksalad.collectmydata.ginsu.summary.dto.ListInsuranceSummariesRequest;
import com.github.tomakehurst.wiremock.WireMockServer;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.entity.ContentType;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static com.banksalad.collectmydata.ginsu.util.FileUtil.readText;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringBootTest
@DisplayName("보증 보험 목록 테스트")
public class InsuranceSummaryServiceTest {

  private static final WireMockServer wiremock = new WireMockServer(WireMockSpring.options().dynamicPort());

  private static final Long BANKSALAD_USER_ID = 1L;
  private static final String ORGANIZATION_ID = "gin_su";
  private static final String ORGANIZATION_HOST = "http://localhost";

  @Autowired
  private SummaryService<ListInsuranceSummariesRequest, InsuranceSummary> summaryService;

  @Autowired
  private SummaryRequestHelper<ListInsuranceSummariesRequest> summaryRequestHelper;

  @Autowired
  private SummaryResponseHelper<InsuranceSummary> summaryResponseHelper;

  @Autowired
  private InsuranceSummaryService insuranceSummaryService;

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
  public void step_01_getGinsu_single_page_success() throws Exception {
    setupServerGinsuPage();

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

    summaryService.listAccountSummaries(executionContext, Executions.finance_ginsu_summaries, summaryRequestHelper,
        summaryResponseHelper);

    List<InsuranceSummary> ginsuSummaries = insuranceSummaryService
        .listSummariesConsented(BANKSALAD_USER_ID, ORGANIZATION_ID);

    assertThat(ginsuSummaries.size()).isEqualTo(2);
  }

  private void setupServerGinsuPage() throws Exception {
    // 보증 보험 목록조회
    wiremock.stubFor(get(urlMatching("/insurances.*"))
        .withQueryParam("org_code", equalTo("020"))
        .withQueryParam("search_timestamp", equalTo("0"))
        .willReturn(
            aResponse()
                .withStatus(HttpStatus.OK.value())
                .withHeader("Content-Type", ContentType.APPLICATION_JSON.toString())
                .withBody(readText("classpath:mock/response/GI01_001_single_page_00.json"))));
  }
}
