package com.banksalad.collectmydata.insu.insurance;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.finance.api.accountinfo.AccountInfoRequestHelper;
import com.banksalad.collectmydata.finance.api.accountinfo.AccountInfoResponseHelper;
import com.banksalad.collectmydata.finance.api.accountinfo.AccountInfoService;
import com.banksalad.collectmydata.insu.collect.Executions;
import com.banksalad.collectmydata.insu.common.db.entity.InsuranceBasicEntity;
import com.banksalad.collectmydata.insu.common.db.entity.InsuranceBasicHistoryEntity;
import com.banksalad.collectmydata.insu.common.db.entity.InsuranceSummaryEntity;
import com.banksalad.collectmydata.insu.common.db.entity.InsuredEntity;
import com.banksalad.collectmydata.insu.common.db.entity.InsuredHistoryEntity;
import com.banksalad.collectmydata.insu.common.db.repository.InsuranceBasicHistoryRepository;
import com.banksalad.collectmydata.insu.common.db.repository.InsuranceBasicRepository;
import com.banksalad.collectmydata.insu.common.db.repository.InsuranceSummaryRepository;
import com.banksalad.collectmydata.insu.common.db.repository.InsuredHistoryRepository;
import com.banksalad.collectmydata.insu.common.db.repository.InsuredRepository;
import com.banksalad.collectmydata.insu.common.util.TestHelper;
import com.banksalad.collectmydata.insu.insurance.dto.GetInsuranceBasicRequest;
import com.banksalad.collectmydata.insu.insurance.dto.InsuranceBasic;
import com.banksalad.collectmydata.insu.summary.dto.InsuranceSummary;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.WireMockSpring;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.apache.http.entity.ContentType;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static com.banksalad.collectmydata.insu.common.util.FileUtil.readText;
import static com.banksalad.collectmydata.insu.common.util.TestHelper.BANKSALAD_USER_ID;
import static com.banksalad.collectmydata.insu.common.util.TestHelper.ENTITY_IGNORE_FIELD;
import static com.banksalad.collectmydata.insu.common.util.TestHelper.ORGANIZATION_ID;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class InsuranceBasicServiceTest {

  @Autowired
  private AccountInfoService<InsuranceSummary, GetInsuranceBasicRequest, InsuranceBasic> accountInfoService;

  @Autowired
  private AccountInfoRequestHelper<GetInsuranceBasicRequest, InsuranceSummary> requestHelper;

  @Autowired
  private AccountInfoResponseHelper<InsuranceSummary, InsuranceBasic> responseHelper;

  @Autowired
  private InsuranceSummaryRepository insuranceSummaryRepository;

  @Autowired
  private InsuranceBasicRepository insuranceBasicRepository;

  @Autowired
  private InsuranceBasicHistoryRepository insuranceBasicHistoryRepository;

  @Autowired
  private InsuredRepository insuredRepository;

  @Autowired
  private InsuredHistoryRepository insuredHistoryRepository;

  private static WireMockServer wireMockServer;

  @BeforeAll
  static void setup() {
    wireMockServer = new WireMockServer(WireMockSpring.options().dynamicPort());
    wireMockServer.start();
    setUpMockServer();
  }

  @Test
  @Transactional
  @DisplayName("6.5.2 보험 기본정보 조회 서비스 테스트1. 성공케이스")
  public void listInsuranceBasics_success() {
    ExecutionContext context = TestHelper.getExecutionContext(wireMockServer.port());
    saveInsuranceSummary(0);

    accountInfoService
        .listAccountInfos(context, Executions.insurance_get_basic, requestHelper, responseHelper);

    List<InsuranceBasicEntity> insurancebasicEntities = insuranceBasicRepository.findAll();
    List<InsuranceBasicHistoryEntity> insuranceBasicHistoryEntities = insuranceBasicHistoryRepository.findAll();
    List<InsuredEntity> insuredEntities = insuredRepository.findAll();
    List<InsuredHistoryEntity> insuredHistoryEntities = insuredHistoryRepository.findAll();

    assertEquals(1, insurancebasicEntities.size());
    assertEquals(1, insuranceBasicHistoryEntities.size());
    assertEquals(1, insuredEntities.size());
    assertEquals(1, insuredHistoryEntities.size());
    assertThat(insurancebasicEntities.get(0)).usingRecursiveComparison()
        .ignoringFields(ENTITY_IGNORE_FIELD)
        .isEqualTo(
            InsuranceBasicEntity.builder()
                .syncedAt(context.getSyncStartedAt())
                .banksaladUserId(BANKSALAD_USER_ID)
                .organizationId(ORGANIZATION_ID)
                .insuNum("123456789")
                .renewable(false)
                .issueDate("20200101")
                .expDate("99991231")
                .faceAmt(new BigDecimal("53253.333"))
                .currencyCode("KRW")
                .variable(true)
                .universal(true)
                .pensionRcvStartDate("20200101")
                .pensionRcvCycle("3M")
                .loanable(true)
                .insuredCount(1)
                .build()
        );

    // TODO : compare with db
//    assertThat(insuranceBasics.get(0)).usingRecursiveComparison()
//        .isEqualTo(
//            InsuranceBasic.builder()
//                .renewable(false)
//                .issueDate("20200101")
//                .expDate("99991231")
//                .faceAmt(new BigDecimal("53253.333"))
//                .currencyCode("KRW")
//                .variable(true)
//                .universal(true)
//                .pensionRcvStartDate("20200101")
//                .pensionRcvCycle("3M")
//                .loanable(true)
//                .insuredCount(1)
//                .insuredList(List.of(
//                    Insured.builder()
//                        .insuredNo("01")
//                        .insuredName("뱅샐")
//                        .build()
//                ))
//                .build()
//        );

    assertThat(insuredEntities.get(0)).usingRecursiveComparison()
        .ignoringFields(ENTITY_IGNORE_FIELD)
        .isEqualTo(
            InsuredEntity.builder()
                .syncedAt(context.getSyncStartedAt())
                .banksaladUserId(BANKSALAD_USER_ID)
                .organizationId(ORGANIZATION_ID)
                .insuNum("123456789")
                .insuredName("뱅샐")
                .insuredNo("01")
                .contractSearchTimestamp(0L)
                .build()
        );

  }

  public void saveInsuranceSummary(long searchTimestamp) {
    insuranceSummaryRepository.save(
        InsuranceSummaryEntity.builder()
            .syncedAt(LocalDateTime.now())
            .banksaladUserId(BANKSALAD_USER_ID)
            .organizationId(ORGANIZATION_ID)
            .insuNum("123456789")
            .consent(true)
            .insuType("05")
            .prodName("묻지도 따지지도않고 암보험")
            .insuStatus("02")
            .paymentSearchTimestamp(searchTimestamp)
            .build()
    );
  }

  private static void setUpMockServer() {
    // 6.5.2 보험 기본정보 조회
    wireMockServer.stubFor(post(urlMatching("/insurances/basic.*"))
        .withRequestBody(
            equalToJson(readText("classpath:mock/request/IS02_001_single_page_00.json")))
        .willReturn(
            aResponse()
                .withFixedDelay(0)
                .withStatus(HttpStatus.OK.value())
                .withHeader("Content-Type", ContentType.APPLICATION_JSON.toString())
                .withBody(readText("classpath:mock/response/IS02_001_single_page_00.json"))));

  }

  @AfterAll
  static void tearDown() {
    wireMockServer.shutdown();
  }

}
