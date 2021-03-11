package com.banksalad.collectmydata.insu.insurance;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.WireMockSpring;
import org.springframework.http.HttpStatus;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.util.DateUtil;
import com.banksalad.collectmydata.insu.common.db.entity.InsuranceBasicEntity;
import com.banksalad.collectmydata.insu.common.db.entity.InsuranceBasicHistoryEntity;
import com.banksalad.collectmydata.insu.common.db.entity.InsuranceContractEntity;
import com.banksalad.collectmydata.insu.common.db.entity.InsuranceContractHistoryEntity;
import com.banksalad.collectmydata.insu.common.db.entity.InsuranceSummaryEntity;
import com.banksalad.collectmydata.insu.common.db.entity.InsuredEntity;
import com.banksalad.collectmydata.insu.common.db.entity.InsuredHistoryEntity;
import com.banksalad.collectmydata.insu.common.db.repository.InsuranceBasicHistoryRepository;
import com.banksalad.collectmydata.insu.common.db.repository.InsuranceBasicRepository;
import com.banksalad.collectmydata.insu.common.db.repository.InsuranceContractHistoryRepository;
import com.banksalad.collectmydata.insu.common.db.repository.InsuranceContractRepository;
import com.banksalad.collectmydata.insu.common.db.repository.InsuranceSummaryRepository;
import com.banksalad.collectmydata.insu.common.db.repository.InsuredHistoryRepository;
import com.banksalad.collectmydata.insu.common.db.repository.InsuredRepository;
import com.banksalad.collectmydata.insu.common.db.repository.UserSyncStatusRepository;
import com.banksalad.collectmydata.insu.common.dto.InsuranceSummary;
import com.banksalad.collectmydata.insu.insurance.dto.InsuranceBasic;
import com.banksalad.collectmydata.insu.insurance.dto.InsuranceContract;
import com.banksalad.collectmydata.insu.insurance.dto.Insured;
import com.banksalad.collectmydata.insu.insurance.service.InsuranceService;
import com.github.tomakehurst.wiremock.WireMockServer;
import org.apache.http.entity.ContentType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static com.banksalad.collectmydata.insu.common.util.FileUtil.readText;
import static com.banksalad.collectmydata.insu.common.util.TestHelper.ACCESS_TOKEN;
import static com.banksalad.collectmydata.insu.common.util.TestHelper.BANKSALAD_USER_ID;
import static com.banksalad.collectmydata.insu.common.util.TestHelper.ENTITY_IGNORE_FIELD;
import static com.banksalad.collectmydata.insu.common.util.TestHelper.ORGANIZATION_CODE;
import static com.banksalad.collectmydata.insu.common.util.TestHelper.ORGANIZATION_HOST;
import static com.banksalad.collectmydata.insu.common.util.TestHelper.ORGANIZATION_ID;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class InsuranceServiceTest {

  @Autowired
  private InsuranceService insuranceService;

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

  @Autowired
  private InsuranceContractRepository insuranceContractRepository;

  @Autowired
  private InsuranceContractHistoryRepository insuranceContractHistoryRepository;

  @Autowired
  private UserSyncStatusRepository userSyncStatusRepository;

  private static WireMockServer wireMockServer;

  @BeforeAll
  static void setup() {
    wireMockServer = new WireMockServer(WireMockSpring.options().dynamicPort());
    wireMockServer.start();
    setupMockServer();
  }

  @AfterEach
  private void after() {
    userSyncStatusRepository.deleteAll();
    insuranceBasicRepository.deleteAll();
    insuranceBasicHistoryRepository.deleteAll();
    insuranceSummaryRepository.deleteAll();
    insuredRepository.deleteAll();
    insuredHistoryRepository.deleteAll();
  }


  @Test
  @DisplayName("6.5.2 보험 기본정보 조회 서비스 테스트1. 성공케이스")
  public void listInsuranceBasics_success() {
    ExecutionContext context = getExecutionContext();
    InsuranceSummaryEntity insuranceSummaryEntity = InsuranceSummaryEntity.builder()
        .syncedAt(context.getSyncStartedAt())
        .banksaladUserId(BANKSALAD_USER_ID)
        .organizationId(ORGANIZATION_ID)
        .insuNum("123456789")
        .consent(true)
        .prodName("name")
        .insuType("01")
        .insuStatus("02")
        .build();
    insuranceSummaryRepository.save(insuranceSummaryEntity);

    List<InsuranceSummary> listInsuranceSummaries = List.of(
        InsuranceSummary.builder()
            .insuNum("123456789")
            .build()
    );

    List<InsuranceBasic> insuranceBasics = insuranceService
        .listInsuranceBasics(context, ORGANIZATION_CODE, listInsuranceSummaries);

    List<InsuranceBasicEntity> insurancebasicEntities = insuranceBasicRepository.findAll();
    List<InsuranceBasicHistoryEntity> insuranceBasicHistoryEntities = insuranceBasicHistoryRepository.findAll();
    List<InsuredEntity> insuredEntities = insuredRepository.findAll();
    List<InsuredHistoryEntity> insuredHistoryEntities = insuredHistoryRepository.findAll();

    assertEquals(1, insurancebasicEntities.size());
    assertEquals(1, insuranceBasicHistoryEntities.size());
    assertEquals(1, insuranceBasics.size());
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
                .issueDate(DateUtil.toLocalDate("20200101"))
                .expDate(DateUtil.toLocalDate("99991231"))
                .faceAmt(new BigDecimal("53253.333"))
                .currencyCode("KRW")
                .variable(true)
                .universal(true)
                .pensionRcvStartDate(DateUtil.toLocalDate("20200101"))
                .pensionRcvCycle("3M")
                .loanable(true)
                .insuredCount(1)
                .build()
        );

    assertThat(insuranceBasics.get(0)).usingRecursiveComparison()
        .isEqualTo(
            InsuranceBasic.builder()
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
                .insuredList(List.of(
                    Insured.builder()
                        .insuredNo("01")
                        .insuredName("뱅샐")
                        .build()
                ))
                .build()
        );

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
                .build()
        );
  }

  @Test
  @DisplayName("6.5.3 보험 특약정 조회 서비스 테스트1. 성공케이스")
  public void listInsuranceContracts_success() {
    ExecutionContext context = getExecutionContext();
    InsuredEntity insuredEntity = InsuredEntity.builder()
        .syncedAt(context.getSyncStartedAt())
        .banksaladUserId(BANKSALAD_USER_ID)
        .organizationId(ORGANIZATION_ID)
        .insuNum("123456789")
        .insuredNo("01")
        .insuredName("뱅샐")
        .build();
    insuredRepository.save(insuredEntity);

    List<InsuranceBasic> insuranceBasics = List.of(
        InsuranceBasic.builder()
            .insuNum("123456789")
            .insuredCount(1)
            .insuredList(
                List.of(
                    Insured.builder()
                        .insuredNo("01")
                        .insuredName("뱅샐")
                        .build()
                )
            )
            .build()
    );

    List<InsuranceContract> insuranceContracts = insuranceService
        .listInsuranceContracts(context, ORGANIZATION_CODE, insuranceBasics);

    List<InsuranceContractEntity> insuranceContractEntities = insuranceContractRepository.findAll();
    List<InsuranceContractHistoryEntity> insuranceContractHistoryEntities = insuranceContractHistoryRepository
        .findAll();

    assertEquals(2, insuranceContracts.size());
    assertEquals(2, insuranceContractEntities.size());
    assertEquals(2, insuranceContractHistoryEntities.size());
    assertThat(insuranceContracts.get(0)).usingRecursiveComparison()
        .isEqualTo(
            InsuranceContract.builder()
                .insuNum("123456789")
                .insuredNo("01")
                .contractName("묻지도따지지도않고")
                .contractStatus("02")
                .contractExpDate("99991231")
                .contractFaceAmt(new BigDecimal("153212463.135"))
                .currencyCode("KRW")
                .required(true)
                .build()
        );

    assertThat(insuranceContractEntities.get(0)).usingRecursiveComparison()
        .ignoringFields(ENTITY_IGNORE_FIELD)
        .isEqualTo(
            InsuranceContractEntity.builder()
                .syncedAt(context.getSyncStartedAt())
                .banksaladUserId(BANKSALAD_USER_ID)
                .organizationId(ORGANIZATION_ID)
                .insuNum("123456789")
                .insuredNo("01")
                .contractNo(0)
                .contractName("묻지도따지지도않고")
                .contractStatus("02")
                .contractExpDate("99991231")
                .contractFaceAmt(new BigDecimal("153212463.135"))
                .currencyCode("KRW")
                .required(true)
                .build()
        );

  }

  private ExecutionContext getExecutionContext() {
    return ExecutionContext.builder()
        .organizationHost("http://" + ORGANIZATION_HOST + ":" + wireMockServer.port())
        .accessToken(ACCESS_TOKEN)
        .banksaladUserId(BANKSALAD_USER_ID)
        .organizationId(ORGANIZATION_ID)
        .executionRequestId(UUID.randomUUID().toString())
        .syncStartedAt(LocalDateTime.now(DateUtil.UTC_ZONE_ID))
        .build();
  }

  private static void setupMockServer() {
    // 6.5.2 보험 기본정보 조회
    wireMockServer.stubFor(post(urlMatching("/insurances/basic.*"))
        .withRequestBody(
            equalToJson(readText("classpath:mock/request/IS02_001_single_page_00.json")))
        .willReturn(
            aResponse()
                .withFixedDelay(1000)
                .withStatus(HttpStatus.OK.value())
                .withHeader("Content-Type", ContentType.APPLICATION_JSON.toString())
                .withBody(readText("classpath:mock/response/IS02_001_single_page_00.json"))));

    // 6.5.3 보험 특약정보 조회
    wireMockServer.stubFor(post(urlMatching("/insurances/contracts"))
        .withRequestBody(
            equalToJson(readText("classpath:mock/request/IS03_001_single_page_00.json")))
        .willReturn(
            aResponse()
                .withFixedDelay(1000)
                .withStatus(HttpStatus.OK.value())
                .withHeader("Content-Type", ContentType.APPLICATION_JSON.toString())
                .withBody(readText("classpath:mock/response/IS03_001_single_page_00.json"))));

  }
}
