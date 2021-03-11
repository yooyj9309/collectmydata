package com.banksalad.collectmydata.insu.insurance;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.WireMockSpring;
import org.springframework.http.HttpStatus;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.organization.Organization;
import com.banksalad.collectmydata.common.util.DateUtil;
import com.banksalad.collectmydata.insu.common.db.entity.InsurancePaymentEntity;
import com.banksalad.collectmydata.insu.common.db.entity.InsuranceSummaryEntity;
import com.banksalad.collectmydata.insu.common.db.repository.InsurancePaymentRepository;
import com.banksalad.collectmydata.insu.common.db.repository.InsuranceSummaryRepository;
import com.banksalad.collectmydata.insu.common.dto.InsuranceSummary;
import com.banksalad.collectmydata.insu.insurance.service.InsurancePaymentService;
import com.github.tomakehurst.wiremock.WireMockServer;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import org.apache.http.entity.ContentType;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import static com.banksalad.collectmydata.common.enums.Industry.*;
import static com.banksalad.collectmydata.common.enums.MydataSector.*;
import static com.banksalad.collectmydata.insu.common.util.FileUtil.readText;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static java.lang.Boolean.TRUE;
import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Transactional
@SpringBootTest
class InsurancePaymentServiceTest {

  @Autowired
  private InsurancePaymentService insurancePaymentService;

  @Autowired
  private InsurancePaymentRepository insurancePaymentRepository;

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
  @DisplayName("6.5.5 insurance_payment table 에 row 가 있음 && Data Provider API Response 와 다름")
  void givenExistingInsuPaymentDifferedWithApiResponse_whenListInsuPayments_ThenUpdateInsuPayment() {
    // Given
    ExecutionContext executionContext = getExecutionContext();
    Organization organization = getOrganization();
    InsuranceSummary insuranceSummary = getInsuranceSummary();

    InsuranceSummaryEntity insuranceSummaryEntity = InsuranceSummaryEntity.builder()
        .syncedAt(LocalDateTime.now())
        .banksaladUserId(executionContext.getBanksaladUserId())
        .organizationId(organization.getOrganizationId())
        .insuNum(insuranceSummary.getInsuNum())
        .consent(TRUE)
        .insuType("05")
        .prodName("묻지도 따지지도않고 암보험")
        .insuStatus("02")
        .basicSearchTimestamp(1000L)
        .carSearchTimestamp(2000L)
        .paymentSearchTimestamp(3000L)
        .build();
    insuranceSummaryRepository.save(insuranceSummaryEntity);

    InsurancePaymentEntity insurancePaymentEntity = InsurancePaymentEntity.builder()
        .syncedAt(LocalDateTime.now())
        .banksaladUserId(executionContext.getBanksaladUserId())
        .organizationId(organization.getOrganizationId())
        .insuNum(insuranceSummary.getInsuNum())
        .payDue("01")
        .payCycle("1M")
        .payCnt(1)
        .payOrgCode("1234")
        .payDate("03")
        .payEndDate("20210320")
        .payAmt(BigDecimal.valueOf(30000.999))
        .currencyCode("KRW")
        .isAutoPay(true)
        .build();
    insurancePaymentRepository.save(insurancePaymentEntity);

    // When
    insurancePaymentService.listInsurancePayments(executionContext, organization, singletonList(insuranceSummary));

    // Then
    InsurancePaymentEntity actualInsurancePaymentEntity = insurancePaymentRepository
        .findByBanksaladUserIdAndOrganizationIdAndInsuNum(executionContext.getBanksaladUserId(),
            organization.getOrganizationId(), insuranceSummary.getInsuNum())
        .orElseThrow(EntityNotFoundException::new);
    assertEquals(BigDecimal.valueOf(30000.123), actualInsurancePaymentEntity.getPayAmt());
  }

  @Test
  @DisplayName("6.7.2 account_payment table 에 row 가 없음")
  void givenNotExistingInsuPayment_whenListInsuPayments_ThenSaveInsuPaymentAndUpdateSearchTimestamp() {
    // Given
    ExecutionContext executionContext = getExecutionContext();
    Organization organization = getOrganization();
    InsuranceSummary insuranceSummary = getInsuranceSummary();

    InsuranceSummaryEntity insuranceSummaryEntity = InsuranceSummaryEntity.builder()
        .syncedAt(LocalDateTime.now())
        .banksaladUserId(executionContext.getBanksaladUserId())
        .organizationId(organization.getOrganizationId())
        .insuNum(insuranceSummary.getInsuNum())
        .consent(TRUE)
        .insuType("05")
        .prodName("묻지도 따지지도않고 암보험")
        .insuStatus("02")
        .basicSearchTimestamp(1000L)
        .carSearchTimestamp(2000L)
        .paymentSearchTimestamp(3000L)
        .build();
    insuranceSummaryRepository.save(insuranceSummaryEntity);

    // When
    insurancePaymentService.listInsurancePayments(executionContext, organization, singletonList(insuranceSummary));

    // Then
    InsurancePaymentEntity actualInsurancePaymentEntity = insurancePaymentRepository
        .findByBanksaladUserIdAndOrganizationIdAndInsuNum(executionContext.getBanksaladUserId(),
            organization.getOrganizationId(), insuranceSummary.getInsuNum())
        .orElseThrow(EntityNotFoundException::new);
    assertNotNull(actualInsurancePaymentEntity);

    InsuranceSummaryEntity actualInsuranceSummaryEntity = insuranceSummaryRepository
        .findByBanksaladUserIdAndOrganizationIdAndInsuNum(executionContext.getBanksaladUserId(),
            organization.getOrganizationId(), insuranceSummary.getInsuNum())
        .orElseThrow(EntityExistsException::new);
    assertEquals(1000, actualInsuranceSummaryEntity.getPaymentSearchTimestamp());
  }

  private InsuranceSummary getInsuranceSummary() {
    return InsuranceSummary.builder()
        .insuNum("123456789")
        .consent(true)
        .prodName("묻지도 따지지도않고 암보험")
        .insuType("05")
        .insuStatus("02")
        .build();
  }

  private ExecutionContext getExecutionContext() {
    return ExecutionContext.builder()
        .organizationHost("http://localhost:" + wireMockServer.port())
        .accessToken("abc.def.ghi")
        .banksaladUserId(1L)
        .organizationId("X-loan")
        .executionRequestId(UUID.randomUUID().toString())
        .syncStartedAt(LocalDateTime.now(DateUtil.UTC_ZONE_ID))
        .build();
  }

  private Organization getOrganization() {
    return Organization.builder()
        .sector(FINANCE)
        .industry(CAPITAL)
        .organizationId("X-loan")
        .organizationCode("020")
        .build();
  }

  private static void setUpMockServer() {
    wireMockServer.stubFor(post(urlMatching("/insurances/payment.*"))
        .withRequestBody(
            equalToJson(readText("classpath:mock/request/IS05_001.json")))
        .willReturn(
            aResponse()
                .withFixedDelay(1000)
                .withStatus(HttpStatus.OK.value())
                .withHeader("Content-Type", ContentType.APPLICATION_JSON.toString())
                .withBody(readText("classpath:mock/response/IS05_001.json"))));
  }
}