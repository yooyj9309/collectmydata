package com.banksalad.collectmydata.insu.insurance;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.finance.api.transaction.TransactionApiService;
import com.banksalad.collectmydata.finance.api.transaction.TransactionRequestHelper;
import com.banksalad.collectmydata.finance.api.transaction.TransactionResponseHelper;
import com.banksalad.collectmydata.insu.collect.Executions;
import com.banksalad.collectmydata.insu.common.db.entity.InsuranceSummaryEntity;
import com.banksalad.collectmydata.insu.common.db.entity.InsuranceTransactionEntity;
import com.banksalad.collectmydata.insu.common.db.repository.InsuranceSummaryRepository;
import com.banksalad.collectmydata.insu.common.db.repository.InsuranceTransactionRepository;
import com.banksalad.collectmydata.insu.common.util.TestHelper;
import com.banksalad.collectmydata.insu.insurance.dto.InsuranceTransaction;
import com.banksalad.collectmydata.insu.insurance.dto.ListInsuranceTransactionsRequest;
import com.banksalad.collectmydata.insu.summary.dto.InsuranceSummary;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.WireMockSpring;
import org.springframework.http.HttpStatus;

import com.github.tomakehurst.wiremock.WireMockServer;
import javax.transaction.Transactional;
import org.apache.http.entity.ContentType;
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
public class InsuranceTransactionServiceTest {

  @Autowired
  private TransactionApiService<InsuranceSummary, ListInsuranceTransactionsRequest, InsuranceTransaction> transactionApiService;

  @Autowired
  private TransactionRequestHelper<InsuranceSummary, ListInsuranceTransactionsRequest> requestHelper;

  @Autowired
  private TransactionResponseHelper<InsuranceSummary, InsuranceTransaction> responseHelper;

  @Autowired
  private InsuranceTransactionRepository insuranceTransactionRepository;

  @Autowired
  private InsuranceSummaryRepository insuranceSummaryRepository;

  private static WireMockServer wireMockServer;

  @BeforeAll
  static void setup() {
    wireMockServer = new WireMockServer(WireMockSpring.options().dynamicPort());
    wireMockServer.start();
    setupMockServer();
  }

  @Test
  @Transactional
  @DisplayName("6.5.6 보험 거래내역 조회 서비스 테스트1. 성공케이스")
  public void listInsuranceTransaction_success() {
    //20200101,20200302
    ExecutionContext context = TestHelper.getExecutionContext(
        wireMockServer.port(),
        LocalDateTime.of(2020, 03, 02, 9, 0, 0)
    );

    saveAndGetInsuranceSummary();
    transactionApiService.listTransactions(context,
        Executions.insurance_get_transactions, requestHelper, responseHelper);

    List<InsuranceTransactionEntity> insuranceTransactionEntities = insuranceTransactionRepository.findAll();

    assertEquals(3, insuranceTransactionEntities.size());

    // TODO : compare with db
//    assertThat(insuranceTransactions.get(0)).usingRecursiveComparison()
//        .isEqualTo(
//            InsuranceTransaction.builder()
//                .insuNum("123456789")
//                .transDate("20200103")
//                .transAppliedMonth("202001")
//                .transNo(1)
//                .paidAmt(new BigDecimal("12345.123"))
//                .currencyCode("AFA")
//                .payMethod("01")
//                .build()
//        );

    assertThat(insuranceTransactionEntities.get(1)).usingRecursiveComparison()
        .ignoringFields(ENTITY_IGNORE_FIELD)
        .isEqualTo(
            InsuranceTransactionEntity.builder()
                .banksaladUserId(BANKSALAD_USER_ID)
                .organizationId(ORGANIZATION_ID)
                .transactionYearMonth(202001)
                .insuNum("123456789")
                .transDate("20200105")
                .transAppliedMonth(202001)
                .transNo(2)
                .paidAmt(new BigDecimal("123456789.45"))
                .currencyCode("ABL")
                .payMethod("02")
                .build()
        );

    InsuranceSummaryEntity insuranceSummaryEntity = insuranceSummaryRepository
        .findByBanksaladUserIdAndOrganizationIdAndInsuNum(BANKSALAD_USER_ID, ORGANIZATION_ID, "123456789").get();

    assertEquals(context.getSyncStartedAt(), insuranceSummaryEntity.getTransactionSyncedAt());
  }

  private void saveAndGetInsuranceSummary() {
    insuranceSummaryRepository.save(
        InsuranceSummaryEntity.builder()
            .syncedAt(LocalDateTime.of(2020, 01, 01, 9, 0, 0))
            .banksaladUserId(BANKSALAD_USER_ID)
            .organizationId(ORGANIZATION_ID)
            .insuNum("123456789")
            .consent(true)
            .insuType("05")
            .insuStatus("02")
            .prodName("묻지도 따지지도않고 암보험")
            .transactionSyncedAt(LocalDateTime.of(2020, 01, 01, 9, 0, 0))
            .build()
    );
  }

  private static void setupMockServer() {
    // 6.5.6 보험 거래내역 조회
    wireMockServer.stubFor(post(urlMatching("/insurances/transactions"))
        .withRequestBody(
            equalToJson(readText("classpath:mock/request/IS06_001_multi_page_00.json")))
        .willReturn(
            aResponse()
                .withStatus(HttpStatus.OK.value())
                .withHeader("Content-Type", ContentType.APPLICATION_JSON.toString())
                .withBody(readText("classpath:mock/response/IS06_001_multi_page_00.json"))));

    wireMockServer.stubFor(post(urlMatching("/insurances/transactions"))
        .withRequestBody(
            equalToJson(readText("classpath:mock/request/IS06_001_multi_page_01.json")))
        .willReturn(
            aResponse()
                .withStatus(HttpStatus.OK.value())
                .withHeader("Content-Type", ContentType.APPLICATION_JSON.toString())
                .withBody(readText("classpath:mock/response/IS06_001_multi_page_01.json"))));
  }
}
