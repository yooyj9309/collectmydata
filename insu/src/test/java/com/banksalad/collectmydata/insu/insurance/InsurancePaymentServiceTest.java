package com.banksalad.collectmydata.insu.insurance;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.finance.api.accountinfo.AccountInfoRequestHelper;
import com.banksalad.collectmydata.finance.api.accountinfo.AccountInfoResponseHelper;
import com.banksalad.collectmydata.finance.api.accountinfo.AccountInfoService;
import com.banksalad.collectmydata.insu.collect.Executions;
import com.banksalad.collectmydata.insu.common.db.entity.InsurancePaymentEntity;
import com.banksalad.collectmydata.insu.common.db.entity.InsurancePaymentHistoryEntity;
import com.banksalad.collectmydata.insu.common.db.entity.InsuranceSummaryEntity;
import com.banksalad.collectmydata.insu.common.db.repository.InsurancePaymentHistoryRepository;
import com.banksalad.collectmydata.insu.common.db.repository.InsurancePaymentRepository;
import com.banksalad.collectmydata.insu.common.db.repository.InsuranceSummaryRepository;
import com.banksalad.collectmydata.insu.common.util.TestHelper;
import com.banksalad.collectmydata.insu.insurance.dto.GetInsurancePaymentRequest;
import com.banksalad.collectmydata.insu.insurance.dto.InsurancePayment;
import com.banksalad.collectmydata.insu.summary.dto.InsuranceSummary;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.WireMockSpring;
import org.springframework.http.HttpStatus;

import com.github.tomakehurst.wiremock.WireMockServer;
import javax.transaction.Transactional;
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
import static java.lang.Boolean.TRUE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Transactional
@SpringBootTest
class InsurancePaymentServiceTest {

  @Autowired
  private AccountInfoService<InsuranceSummary, GetInsurancePaymentRequest, InsurancePayment> accountInfoService;

  @Autowired
  private AccountInfoRequestHelper<GetInsurancePaymentRequest, InsuranceSummary> requestHelper;

  @Autowired
  private AccountInfoResponseHelper<InsuranceSummary, InsurancePayment> responseHelper;

  @Autowired
  private InsurancePaymentRepository insurancePaymentRepository;

  @Autowired
  private InsurancePaymentHistoryRepository insurancePaymentHistoryRepository;

  @Autowired
  private InsuranceSummaryRepository insuranceSummaryRepository;

  private static WireMockServer wireMockServer;

  @BeforeAll
  static void setup() {
    wireMockServer = new WireMockServer(WireMockSpring.options().dynamicPort());
    wireMockServer.start();
    setUpMockServer();
  }

  @AfterAll
  static void tearDown() {
    wireMockServer.shutdown();
  }

  @Test
  @DisplayName("6.5.5 보험 납입정보 조회 서비스 테스트1. 성공케이스")
  void insurancePayment_success() {
    // Given
    ExecutionContext executionContext = TestHelper.getExecutionContext(wireMockServer.port());
    saveInsuranceSummary(0);

    // When
    accountInfoService
        .listAccountInfos(executionContext, Executions.insurance_get_payment, requestHelper, responseHelper);
    List<InsurancePaymentEntity> insurancePaymentEntities = insurancePaymentRepository.findAll();
    List<InsurancePaymentHistoryEntity> insurancePaymentHistoryRepositories = insurancePaymentHistoryRepository
        .findAll();

    assertEquals(1, insurancePaymentEntities.size());
    assertEquals(1, insurancePaymentHistoryRepositories.size());

    // TODO: compare with db
    //Then
//    assertThat(insurancePayments.get(0)).usingRecursiveComparison()
//        .isEqualTo(
//            InsurancePayment.builder()
//                .insuNum("1234567812345678")
//                .payDue("01")
//                .payCycle("1M")
//                .payCnt(1)
//                .payOrgCode("1234")
//                .payDate("03")
//                .payEndDate("20210320")
//                .payAmt(new BigDecimal("30000.123"))
//                .currencyCode("KRW")
//                .autoPay(true)
//                .build()
//        );

    assertThat(insurancePaymentEntities.get(0)).usingRecursiveComparison()
        .ignoringFields(ENTITY_IGNORE_FIELD)
        .isEqualTo(
            InsurancePaymentEntity.builder()
                .banksaladUserId(BANKSALAD_USER_ID)
                .organizationId(ORGANIZATION_ID)
                .insuNum("1234567812345678")
                .payDue("01")
                .payCycle("1M")
                .payCnt(1)
                .payOrgCode("1234")
                .payDate("03")
                .payEndDate("20210320")
                .payAmt(new BigDecimal("30000.123"))
                .currencyCode("KRW")
                .autoPay(true)
                .build()
        );

    assertThat(insurancePaymentHistoryRepositories.get(0)).usingRecursiveComparison()
        .ignoringFields(ENTITY_IGNORE_FIELD)
        .isEqualTo(
            InsurancePaymentHistoryEntity.builder()
                .banksaladUserId(BANKSALAD_USER_ID)
                .organizationId(ORGANIZATION_ID)
                .insuNum("1234567812345678")
                .payDue("01")
                .payCycle("1M")
                .payCnt(1)
                .payOrgCode("1234")
                .payDate("03")
                .payEndDate("20210320")
                .payAmt(new BigDecimal("30000.123"))
                .currencyCode("KRW")
                .autoPay(true)
                .build()
        );
  }

  public void saveInsuranceSummary(long searchTimestamp) {
    insuranceSummaryRepository.save(
        InsuranceSummaryEntity.builder()
            .syncedAt(LocalDateTime.now())
            .banksaladUserId(BANKSALAD_USER_ID)
            .organizationId(ORGANIZATION_ID)
            .insuNum("1234567812345678")
            .consent(TRUE)
            .insuType("05")
            .prodName("묻지도 따지지도않고 암보험")
            .insuStatus("02")
            .paymentSearchTimestamp(searchTimestamp)
            .build()
    );
  }

  private static void setUpMockServer() {
    wireMockServer.stubFor(post(urlMatching("/insurances/payment.*"))
        .withRequestBody(
            equalToJson(readText("classpath:mock/request/IS05_001_single_page_00.json")))
        .willReturn(
            aResponse()
                .withStatus(HttpStatus.OK.value())
                .withHeader("Content-Type", ContentType.APPLICATION_JSON.toString())
                .withBody(readText("classpath:mock/response/IS05_001_single_page_00.json"))));
  }
}
