package com.banksalad.collectmydata.insu.common.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.WireMockSpring;
import org.springframework.http.HttpStatus;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.insu.common.db.entity.InsuranceSummaryEntity;
import com.banksalad.collectmydata.insu.common.db.repository.InsuranceSummaryRepository;
import com.banksalad.collectmydata.insu.summary.dto.InsuranceSummary;
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
import static com.banksalad.collectmydata.insu.common.util.TestHelper.ORGANIZATION_CODE;
import static com.banksalad.collectmydata.insu.common.util.TestHelper.ORGANIZATION_ID;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class InsuranceSummaryServiceTest {

  @Autowired
  private InsuranceSummaryService insuranceSummaryService;

  @Autowired
  private InsuranceSummaryRepository insuranceSummaryRepository;

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
  @DisplayName("6.5.1 보험 목록정보 조회 테스트 : 성공 케이스")
  public void getInsuranceSummariesTest_case1() {
    ExecutionContext context = TestHelper.getExecutionContext(wireMockServer.port());
    List<InsuranceSummary> insuranceSummaries = insuranceSummaryService
        .listInsuranceSummaries(context, ORGANIZATION_CODE);

    assertEquals(1, insuranceSummaries.size());
    assertThat(insuranceSummaries.get(0)).usingRecursiveComparison()
        .isEqualTo(
            InsuranceSummary.builder()
                .insuNum("123456789")
                .consent(true)
                .prodName("묻지도 따지지도않고 암보험")
                .insuType("05")
                .insuStatus("02")
                .build()
        );

    List<InsuranceSummaryEntity> insuranceSummaryEntities = insuranceSummaryRepository.findAll();
    assertEquals(1, insuranceSummaryEntities.size());
    assertThat(insuranceSummaryEntities.get(0)).usingRecursiveComparison()
        .ignoringFields("id", "syncedAt", "createdAt", "createdBy", "updatedAt", "updatedBy")
        .isEqualTo(
            InsuranceSummaryEntity.builder()
                .banksaladUserId(BANKSALAD_USER_ID)
                .organizationId(ORGANIZATION_ID)
                .insuNum("123456789")
                .consent(true)
                .prodName("묻지도 따지지도않고 암보험")
                .insuType("05")
                .insuStatus("02")
                .build()
        );

//    UserSyncStatusEntity userSyncStatusEntity = userSyncStatusRepository
//        .findByBanksaladUserIdAndOrganizationIdAndApiId(BANKSALAD_USER_ID, ORGANIZATION_ID,
//            Apis.insurance_get_summaries.getId()).get();
//
//    assertEquals(userSyncStatusEntity.getSyncedAt(), context.getSyncStartedAt());
  }

  private static void setupMockServer() {
    // 6.5.1 보험 목록 조회
    wireMockServer.stubFor(get(urlMatching("/insurances.*"))
        .withQueryParam("org_code", equalTo(ORGANIZATION_CODE))
        .withQueryParam("search_timestamp", equalTo("0"))
        .willReturn(
            aResponse()
                .withFixedDelay(1000)
                .withStatus(HttpStatus.OK.value())
                .withHeader("Content-Type", ContentType.APPLICATION_JSON.toString())
                .withBody(readText("classpath:mock/response/IS01_001_single_page_00.json"))));
  }
}
