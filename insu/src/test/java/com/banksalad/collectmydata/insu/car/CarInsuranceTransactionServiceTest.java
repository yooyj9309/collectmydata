package com.banksalad.collectmydata.insu.car;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.contract.wiremock.WireMockSpring;
import org.springframework.http.HttpStatus;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.finance.api.transaction.TransactionApiService;
import com.banksalad.collectmydata.finance.api.transaction.TransactionRequestHelper;
import com.banksalad.collectmydata.finance.api.transaction.TransactionResponseHelper;
import com.banksalad.collectmydata.insu.car.dto.CarInsurance;
import com.banksalad.collectmydata.insu.car.dto.CarInsuranceTransaction;
import com.banksalad.collectmydata.insu.car.dto.ListCarInsuranceTransactionsRequest;
import com.banksalad.collectmydata.insu.car.service.CarInsuranceService;
import com.banksalad.collectmydata.insu.collect.Executions;
import com.banksalad.collectmydata.insu.common.service.InsuranceSummaryService;
import com.banksalad.collectmydata.insu.summary.dto.InsuranceSummary;
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
import static com.banksalad.collectmydata.insu.common.util.TestHelper.*;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@DisplayName("자동차보험 거래내역 서비스 테스트")
class CarInsuranceTransactionServiceTest {

  @Autowired
  private TransactionApiService<CarInsurance, ListCarInsuranceTransactionsRequest, CarInsuranceTransaction> carInsuranceTransactionApiService;

  @Autowired
  private TransactionRequestHelper<CarInsurance, ListCarInsuranceTransactionsRequest> carInsuranceTransactionRequestHelper;

  @Autowired
  private TransactionResponseHelper<CarInsurance, CarInsuranceTransaction> carInsuranceTransactionResponseHelper;

  @MockBean
  private CarInsuranceService carInsuranceService;

  @MockBean
  private InsuranceSummaryService insuranceSummaryService;

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
  @DisplayName("6.5.7 자동차보험 거래내역 조회")
  void carInsuranceTransaction_transactionService_listTransactions_test() {
    // given
    LocalDateTime timeOnlyForTest = LocalDateTime.of(2020, 3, 2, 1, 0, 0);
    ExecutionContext executionContext = getExecutionContext(wireMockServer.port(), timeOnlyForTest);

    final String INSURANCE_NUM = "123456789";
    when(insuranceSummaryService.listSummariesConsented(BANKSALAD_USER_ID, ORGANIZATION_ID))
        .thenReturn(List.of(
            InsuranceSummary.builder()
                .insuNum(INSURANCE_NUM)
                .consent(true)
                .prodName("묻지도 따지지도않고 암보험")
                .insuType("05")
                .insuStatus("02")
                .build()
        ));
    when(carInsuranceService.listCarInsurances(BANKSALAD_USER_ID, ORGANIZATION_ID, INSURANCE_NUM))
        .thenReturn(List.of(
            CarInsurance.builder()
                .insuNum(INSURANCE_NUM)
                .carNumber("60무1234")
                .transactionSyncedAt(LocalDateTime.of(2020, 1, 1, 1, 0, 0))
                .build()
        ));

    // when
    List<CarInsuranceTransaction> carInsuranceTransactions = carInsuranceTransactionApiService
        .listTransactions(executionContext, Executions.insurance_get_car_transactions,
            carInsuranceTransactionRequestHelper, carInsuranceTransactionResponseHelper);

    // then
    assertThat(carInsuranceTransactions).usingRecursiveComparison().isEqualTo(
        List.of(
            CarInsuranceTransaction.builder()
                .faceAmt(BigDecimal.valueOf(900000))
                .transNo(2)
                .paidAmt(BigDecimal.valueOf(450000))
                .payMethod("02")
                .build(),
            CarInsuranceTransaction.builder()
                .faceAmt(BigDecimal.valueOf(100000))
                .transNo(3)
                .paidAmt(BigDecimal.valueOf(70000))
                .payMethod("04")
                .build()
        )
    );
  }

  private static void setupMockServer() {
    // 6.5.7 자동차보험 거래내역 조회 : single page 조회
    wireMockServer.stubFor(post(urlMatching("/insurances/car/transactions"))
        .withRequestBody(
            equalToJson(readText("classpath:mock/request/IS07_002_single_page_00.json")))
        .willReturn(
            aResponse()
                .withStatus(HttpStatus.OK.value())
                .withHeader("Content-Type", ContentType.APPLICATION_JSON.toString())
                .withBody(readText("classpath:mock/response/IS07_002_single_page_00.json"))));
  }
}