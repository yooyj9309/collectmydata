package com.banksalad.collectmydata.insu.common.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.WireMockSpring;
import org.springframework.http.HttpStatus;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.insu.common.db.entity.LoanSummaryEntity;
import com.banksalad.collectmydata.insu.common.db.repository.LoanSummaryRepository;
import com.banksalad.collectmydata.insu.summary.dto.LoanSummary;
import com.banksalad.collectmydata.insu.common.util.TestHelper;
import com.github.tomakehurst.wiremock.WireMockServer;
import javax.transaction.Transactional;
import org.apache.http.entity.ContentType;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.banksalad.collectmydata.insu.common.util.FileUtil.readText;
import static com.banksalad.collectmydata.insu.common.util.TestHelper.BANKSALAD_USER_ID;
import static com.banksalad.collectmydata.insu.common.util.TestHelper.ENTITY_IGNORE_FIELD;
import static com.banksalad.collectmydata.insu.common.util.TestHelper.ORGANIZATION_CODE;
import static com.banksalad.collectmydata.insu.common.util.TestHelper.ORGANIZATION_ID;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class LoanSummaryServiceTest {

  @Autowired
  private LoanSummaryService loanSummaryService;

  @Autowired
  private LoanSummaryRepository loanSummaryRepository;

//  @Autowired
//  private UserSyncStatusRepository userSyncStatusRepository;

  private static WireMockServer wireMockServer;

  @BeforeAll
  static void setup() {
    wireMockServer = new WireMockServer(WireMockSpring.options().dynamicPort());
    wireMockServer.start();
    setupMockServer();
  }

  @AfterAll
  static void tearDown() {
    wireMockServer.shutdown();
  }

  @Test
  @Transactional
  @DisplayName("6.5.8 대출 목록정보 조회 테스트 : 성공 케이스")
  public void getLoanSummariesTest_case1() {
    ExecutionContext context = TestHelper.getExecutionContext(wireMockServer.port());
    List<LoanSummary> insuranceSummaries = loanSummaryService
        .listLoanSummaries(context, ORGANIZATION_CODE);

    List<LoanSummaryEntity> loanSummaryEntities = loanSummaryRepository.findAll();

    assertEquals(1, insuranceSummaries.size());
    assertEquals(1, loanSummaryEntities.size());

    assertThat(insuranceSummaries.get(0)).usingRecursiveComparison()
        .isEqualTo(
            LoanSummary.builder()
                .prodName("보금자리론")
                .accountNum("123456789")
                .consent(true)
                .accountType("3245")
                .accountStatus("01")
                .build()
        );

    assertThat(loanSummaryEntities.get(0)).usingRecursiveComparison()
        .ignoringFields(ENTITY_IGNORE_FIELD)
        .isEqualTo(
            LoanSummaryEntity.builder()
                .banksaladUserId(BANKSALAD_USER_ID)
                .organizationId(ORGANIZATION_ID)
                .prodName("보금자리론")
                .accountNum("123456789")
                .consent(true)
                .accountType("3245")
                .accountStatus("01")
                .build()
        );

//    UserSyncStatusEntity userSyncStatusEntity = userSyncStatusRepository
//        .findByBanksaladUserIdAndOrganizationIdAndApiId(BANKSALAD_USER_ID, ORGANIZATION_ID,
//            Apis.insurance_get_loan_summaries.getId()).get();
//
//    assertThat(userSyncStatusEntity).usingRecursiveComparison()
//        .ignoringFields(ENTITY_IGNORE_FIELD)
//        .isEqualTo(
//            UserSyncStatusEntity.builder()
//                .banksaladUserId(BANKSALAD_USER_ID)
//                .organizationId(ORGANIZATION_ID)
//                .searchTimestamp(1000L)
//                .apiId(Apis.insurance_get_loan_summaries.getId())
//                .build()
//        );
  }

  private static void setupMockServer() {
    // 6.5.1 보험 목록 조회
    wireMockServer.stubFor(get(urlMatching("/loans.*"))
        .withQueryParam("org_code", equalTo(ORGANIZATION_CODE))
        .withQueryParam("search_timestamp", equalTo("0"))
        .willReturn(
            aResponse()
                .withFixedDelay(1000)
                .withStatus(HttpStatus.OK.value())
                .withHeader("Content-Type", ContentType.APPLICATION_JSON.toString())
                .withBody(readText("classpath:mock/response/IS11_001_single_page_00.json"))));
  }
}
