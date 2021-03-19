package com.banksalad.collectmydata.insu.common.execution;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.WireMockSpring;
import org.springframework.http.HttpStatus;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.collect.execution.ExecutionRequest;
import com.banksalad.collectmydata.common.collect.execution.ExecutionResponse;
import com.banksalad.collectmydata.common.collect.executor.CollectExecutor;
import com.banksalad.collectmydata.common.util.DateUtil;
import com.banksalad.collectmydata.common.util.ExecutionUtil;
import com.banksalad.collectmydata.insu.car.dto.CarInsurance;
import com.banksalad.collectmydata.insu.car.dto.CarInsuranceTransaction;
import com.banksalad.collectmydata.insu.car.dto.GetCarInsuranceRequest;
import com.banksalad.collectmydata.insu.car.dto.GetCarInsuranceResponse;
import com.banksalad.collectmydata.insu.car.dto.ListCarInsuranceTransactionsRequest;
import com.banksalad.collectmydata.insu.car.dto.ListCarInsuranceTransactionsResponse;
import com.banksalad.collectmydata.insu.collect.Executions;
import com.banksalad.collectmydata.insu.insurance.dto.GetInsuranceBasicRequest;
import com.banksalad.collectmydata.insu.insurance.dto.GetInsuranceBasicResponse;
import com.banksalad.collectmydata.insu.insurance.dto.GetInsuranceContractRequest;
import com.banksalad.collectmydata.insu.insurance.dto.GetInsuranceContractResponse;
import com.banksalad.collectmydata.insu.insurance.dto.InsuranceContract;
import com.banksalad.collectmydata.insu.insurance.dto.InsuranceTransaction;
import com.banksalad.collectmydata.insu.insurance.dto.Insured;
import com.banksalad.collectmydata.insu.insurance.dto.ListInsuranceTransactionsRequest;
import com.banksalad.collectmydata.insu.insurance.dto.ListInsuranceTransactionsResponse;
import com.banksalad.collectmydata.insu.loan.dto.GetLoanBasicRequest;
import com.banksalad.collectmydata.insu.loan.dto.GetLoanBasicResponse;
import com.banksalad.collectmydata.insu.loan.dto.GetLoanDetailRequest;
import com.banksalad.collectmydata.insu.loan.dto.GetLoanDetailResponse;
import com.banksalad.collectmydata.insu.loan.dto.ListLoanTransactionRequest;
import com.banksalad.collectmydata.insu.loan.dto.ListLoanTransactionResponse;
import com.banksalad.collectmydata.insu.loan.dto.LoanBasic;
import com.banksalad.collectmydata.insu.loan.dto.LoanTransaction;
import com.banksalad.collectmydata.insu.loan.dto.LoanTransactionInterest;
import com.banksalad.collectmydata.insu.summary.dto.InsuranceSummary;
import com.banksalad.collectmydata.insu.summary.dto.ListInsuranceSummariesRequest;
import com.banksalad.collectmydata.insu.summary.dto.ListInsuranceSummariesResponse;
import com.banksalad.collectmydata.insu.summary.dto.ListLoanSummariesRequest;
import com.banksalad.collectmydata.insu.summary.dto.ListLoanSummariesResponse;
import com.banksalad.collectmydata.insu.summary.dto.LoanSummary;
import com.github.tomakehurst.wiremock.WireMockServer;
import org.apache.http.entity.ContentType;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static com.banksalad.collectmydata.insu.common.util.FileUtil.readText;
import static com.banksalad.collectmydata.insu.common.util.TestHelper.ACCESS_TOKEN;
import static com.banksalad.collectmydata.insu.common.util.TestHelper.ACCOUNT_NUM;
import static com.banksalad.collectmydata.insu.common.util.TestHelper.BANKSALAD_USER_ID;
import static com.banksalad.collectmydata.insu.common.util.TestHelper.HEADERS;
import static com.banksalad.collectmydata.insu.common.util.TestHelper.ORGANIZATION_CODE;
import static com.banksalad.collectmydata.insu.common.util.TestHelper.ORGANIZATION_HOST;
import static com.banksalad.collectmydata.insu.common.util.TestHelper.ORGANIZATION_ID;
import static com.banksalad.collectmydata.insu.common.util.TestHelper.buildGetLoanDetailResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class ExecutionTest {

  private static WireMockServer wireMockServer;

  @Autowired
  private CollectExecutor collectExecutor;

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
  @DisplayName("6.5.1 보험 기본정보 조회")
  public void getInsuranceSummariesApiTest() {
    ExecutionContext executionContext = getExecutionContext();

    ListInsuranceSummariesRequest request = ListInsuranceSummariesRequest.builder()
        .orgCode(ORGANIZATION_CODE)
        .searchTimestamp(0L)
        .build();

    ExecutionRequest<ListInsuranceSummariesRequest> executionRequest = ExecutionUtil
        .assembleExecutionRequest(HEADERS, request);

    ExecutionResponse<ListInsuranceSummariesResponse> executionResponse = collectExecutor
        .execute(executionContext, Executions.insurance_get_summaries, executionRequest);

    assertThat(executionResponse.getResponse()).usingRecursiveComparison()
        .isEqualTo(
            ListInsuranceSummariesResponse.builder()
                .rspCode("00000")
                .rspMsg("success")
                .searchTimestamp(1000L)
                .insuCnt(1)
                .insuList(List.of(
                    InsuranceSummary.builder()
                        .insuNum("123456789")
                        .consent(true)
                        .prodName("묻지도 따지지도않고 암보험")
                        .insuType("05")
                        .insuStatus("02")
                        .build()
                )).build()
        );
  }

  @Test
  @DisplayName("6.5.2 보험 기본정보 조회")
  public void getInsuranceBasicApiTest() {
    ExecutionContext executionContext = getExecutionContext();

    GetInsuranceBasicRequest request = GetInsuranceBasicRequest.builder()
        .orgCode(ORGANIZATION_CODE)
        .insuNum("123456789")
        .searchTimestamp(0L)
        .build();

    ExecutionRequest<GetInsuranceBasicRequest> executionRequest = ExecutionUtil
        .assembleExecutionRequest(HEADERS, request);

    ExecutionResponse<GetInsuranceBasicResponse> executionResponse = collectExecutor
        .execute(executionContext, Executions.insurance_get_basic, executionRequest);

    assertThat(executionResponse.getResponse()).usingRecursiveComparison()
        .isEqualTo(
            GetInsuranceBasicResponse.builder()
                .rspCode("00000")
                .rspMsg("success")
                .searchTimestamp(1000L)
                .renewable(false)
                .issueDate("20200101")
                .expDate("99991231")
                .faceAmt(BigDecimal.valueOf(53253.333))
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
                    )
                )
                .build()
        );
  }

  @Test
  @DisplayName("6.5.3 보험 특약정보 조회")
  public void getInsuranceContractApiTest() {
    ExecutionContext executionContext = getExecutionContext();

    GetInsuranceContractRequest request = GetInsuranceContractRequest.builder()
        .orgCode(ORGANIZATION_CODE)
        .insuNum("123456789")
        .insuredNo("01")
        .searchTimestamp(0L)
        .build();

    ExecutionRequest<GetInsuranceContractRequest> executionRequest = ExecutionUtil
        .assembleExecutionRequest(HEADERS, request);

    ExecutionResponse<GetInsuranceContractResponse> executionResponse = collectExecutor
        .execute(executionContext, Executions.insurance_get_contract, executionRequest);

    assertThat(executionResponse.getResponse()).usingRecursiveComparison()
        .isEqualTo(
            GetInsuranceContractResponse.builder()
                .rspCode("00000")
                .rspMsg("success")
                .searchTimestamp(1000L)
                .contractCnt(2)
                .contractList(
                    List.of(
                        InsuranceContract.builder()
                            .contractName("묻지도따지지도않고")
                            .contractStatus("02")
                            .contractExpDate("99991231")
                            .contractFaceAmt(new BigDecimal("153212463.135"))
                            .currencyCode("KRW")
                            .required(true)
                            .build(),
                        InsuranceContract.builder()
                            .contractName("무조건보장")
                            .contractStatus("02")
                            .contractExpDate("99991201")
                            .contractFaceAmt(new BigDecimal("35363.135"))
                            .currencyCode("KRW")
                            .required(true)
                            .build()
                    )
                )
                .build()
        );
  }

  @Test
  @DisplayName("6.5.4 자동차보험 정보 조회")
  void listCarInsurancesApiTest() {
    ExecutionContext executionContext = getExecutionContext();

    GetCarInsuranceRequest request = GetCarInsuranceRequest.builder()
        .orgCode(ORGANIZATION_CODE)
        .insuNum("123456789")
        .searchTimestamp(0L)
        .build();

    ExecutionRequest<GetCarInsuranceRequest> executionRequest = ExecutionUtil
        .assembleExecutionRequest(HEADERS, request);

    ExecutionResponse<GetCarInsuranceResponse> executionResponse = collectExecutor
        .execute(executionContext, Executions.insurance_get_car, executionRequest);

    GetCarInsuranceResponse response = executionResponse.getResponse();

    assertThat(response).usingRecursiveComparison().isEqualTo(
        GetCarInsuranceResponse.builder()
            .rspCode("00000")
            .rspMsg("success")
            .searchTimestamp(1000)
            .carInsuCnt(2)
            .carInsurances(
                List.of(
                    CarInsurance.builder()
                        .carNumber("60무1234")
                        .carInsuType("02")
                        .carName("그랜져 IG")
                        .startDate("20200101")
                        .endDate("20210101")
                        .contractAge("21세")
                        .contractDriver("가족한정")
                        .ownDmgCoverage(true)
                        .selfPayRate("01")
                        .selfPayAmt(200000)
                        .build(),
                    CarInsurance.builder()
                        .carNumber("60무1234")
                        .carInsuType("04")
                        .carName("그랜져 IG")
                        .startDate("20200601")
                        .endDate("20210601")
                        .contractAge("21세")
                        .contractDriver("본인")
                        .ownDmgCoverage(false)
                        .selfPayRate("02")
                        .selfPayAmt(30000)
                        .build()
                ))
            .build()
    );
  }

  @Test
  @DisplayName("6.5.6 보험 거래내역 조회")
  void getInsuranceTransactionApiTest() {
    ExecutionContext executionContext = getExecutionContext();

    ListInsuranceTransactionsRequest request = ListInsuranceTransactionsRequest.builder()
        .orgCode(ORGANIZATION_CODE)
        .insuNum("123456789")
        .fromDate("20200101")
        .toDate("20200302")
        .limit(500)
        .build();

    ExecutionRequest<ListInsuranceTransactionsRequest> executionRequest = ExecutionUtil
        .assembleExecutionRequest(HEADERS, request);

    ExecutionResponse<ListInsuranceTransactionsResponse> executionResponse = collectExecutor
        .execute(executionContext, Executions.insurance_get_transactions, executionRequest);

    ListInsuranceTransactionsResponse response = executionResponse.getResponse();
    assertEquals(2, response.getTransCnt());
    assertEquals("01", response.getNextPage());
    assertThat(response.getTransList().get(0)).usingRecursiveComparison()
        .isEqualTo(
            InsuranceTransaction.builder()
                .transDate("20200103")
                .transAppliedMonth("202001")
                .transNo(1)
                .paidAmt(new BigDecimal("12345.123"))
                .currencyCode("AFA")
                .payMethod("01")
                .build()
        );
  }

  @Test
  @DisplayName("6.5.7 자동차보험 거래내역 조회")
  void listCarInsuranceTransactionsApiTest() {
    ExecutionContext executionContext = getExecutionContext();

    ListCarInsuranceTransactionsRequest request = ListCarInsuranceTransactionsRequest.builder()
        .orgCode(ORGANIZATION_CODE)
        .insuNum("123456789")
        .carNumber("60무1234")
        .fromDate("20200101")
        .toDate("20200302")
        .limit(500)
        .build();

    ExecutionRequest<GetCarInsuranceRequest> executionRequest = ExecutionUtil
        .assembleExecutionRequest(HEADERS, request);

    ExecutionResponse<ListCarInsuranceTransactionsResponse> executionResponse = collectExecutor
        .execute(executionContext, Executions.insurance_get_car_transactions, executionRequest);

    ListCarInsuranceTransactionsResponse response = executionResponse.getResponse();

    assertThat(response).usingRecursiveComparison().isEqualTo(
        ListCarInsuranceTransactionsResponse.builder()
            .rspCode("00000")
            .rspMsg("success")
            .nextPage("2")
            .transCnt(2)
            .carInsuranceTransactions(
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
                ))
            .build()
    );
  }

  @Test
  @DisplayName("6.5.8 보험 기본정보 조회")
  public void listLoanSummaryApiTest() {
    ExecutionContext executionContext = getExecutionContext();

    ListLoanSummariesRequest request = ListLoanSummariesRequest.builder()
        .orgCode(ORGANIZATION_CODE)
        .searchTimestamp(0L)
        .build();

    ExecutionRequest<ListLoanSummariesRequest> executionRequest = ExecutionUtil
        .assembleExecutionRequest(HEADERS, request);

    ExecutionResponse<ListLoanSummariesResponse> executionResponse = collectExecutor
        .execute(executionContext, Executions.insurance_get_loan_summaries, executionRequest);

    assertThat(executionResponse.getResponse()).usingRecursiveComparison()
        .isEqualTo(
            ListLoanSummariesResponse.builder()
                .rspCode("00000")
                .rspMsg("success")
                .searchTimestamp(1000L)
                .loanCnt(1)
                .loanList(
                    List.of(
                        LoanSummary.builder()
                            .prodName("보금자리론")
                            .accountNum("123456789")
                            .consent(true)
                            .accountType("3245")
                            .accountStatus("01")
                            .build()
                    )
                )
                .build()
        );
  }

  @Test
  @DisplayName("6.5.9 대출 기본 조회")
  public void getLoanBasicApiTest() {
    ExecutionContext executionContext = getExecutionContext();

    GetLoanBasicRequest request = GetLoanBasicRequest.builder()
        .orgCode(ORGANIZATION_CODE)
        .accountNum(ACCOUNT_NUM)
        .searchTimestamp(0L)
        .build();

    ExecutionRequest<GetLoanBasicRequest> executionRequest = ExecutionUtil.assembleExecutionRequest(HEADERS, request);

    ExecutionResponse<GetLoanBasicResponse> executionResponse = collectExecutor
        .execute(executionContext, Executions.insurance_get_loan_basic, executionRequest);

    assertThat(executionResponse.getResponse()).usingRecursiveComparison()
        .isEqualTo(
            GetLoanBasicResponse.builder()
                .rspCode("00000")
                .rspMsg("success")
                .searchTimestamp(1000L)
                .loanBasic(LoanBasic.builder()
                    .loanStartDate("20210305")
                    .loanExpDate("20300506")
                    .repayMethod("03")
                    .insuNum("123456789")
                    .build()
                )
                .build()
        );
  }

  @Test
  @DisplayName("6.5.10 대출상품 추가정보 조회")
  public void getLoanDetailApiTest() {
    // Given
    ExecutionContext executionContext = getExecutionContext();
    GetLoanDetailRequest request = GetLoanDetailRequest.builder()
        .orgCode(ORGANIZATION_CODE)
        .accountNum(ACCOUNT_NUM)
        .searchTimestamp(0L)
        .build();
    ExecutionRequest<GetLoanDetailResponse> executionRequest = ExecutionUtil.assembleExecutionRequest(HEADERS, request);
    GetLoanDetailResponse expectedGetLoanDetailResponse = buildGetLoanDetailResponse();

    // When
    ExecutionResponse<GetLoanDetailResponse> actualExecutionResponse = collectExecutor
        .execute(executionContext, Executions.insurance_get_loan_detail, executionRequest);

    // Then
    assertThat(actualExecutionResponse.getResponse()).usingRecursiveComparison()
        .isEqualTo(expectedGetLoanDetailResponse);
  }

  @Test
  @DisplayName("6.5.10 대출상품 거래내역 조회")
  public void getLoanTransactionsApiTest() {
    // Given
    ExecutionContext executionContext = getExecutionContext();
    ListLoanTransactionRequest request = ListLoanTransactionRequest.builder()
        .orgCode(ORGANIZATION_CODE)
        .accountNum("1234567812345678")
        .fromDate("20210121")
        .toDate("20210122")
        .limit(2)
        .build();

    ExecutionRequest<ListLoanTransactionResponse> executionRequest = ExecutionUtil
        .assembleExecutionRequest(HEADERS, request);

    LoanTransaction loanTransaction01 = LoanTransaction.builder()
        .transNo("trans#2")
        .transDtime("20210121103000")
        .currencyCode("KRW")
        .loanPaidAmt(BigDecimal.valueOf(1000.312))
        .intPaidAmt(BigDecimal.valueOf(18000.712))
        .intCnt(2)
        .intList(Arrays.asList(
            LoanTransactionInterest.builder()
                .intStartDate("20201201")
                .intEndDate("20201231")
                .intRate(BigDecimal.valueOf(4.112))
                .intType("02")
                .build(),
            LoanTransactionInterest.builder()
                .intStartDate("20201201")
                .intEndDate("20201231")
                .intRate(BigDecimal.valueOf(3.012))
                .intType("01")
                .build()
        ))
        .build();

    LoanTransaction loanTransaction02 = LoanTransaction.builder()
        .transNo("trans#1")
        .transDtime("20210121093000")
        .currencyCode("KRW")
        .loanPaidAmt(BigDecimal.valueOf(1000.312))
        .intPaidAmt(BigDecimal.valueOf(18000.712))
        .intCnt(1)
        .intList(Collections.singletonList(
            LoanTransactionInterest.builder()
                .intStartDate("20201201")
                .intEndDate("20201231")
                .intRate(BigDecimal.valueOf(3.012))
                .intType("99")
                .build()
        ))
        .build();

    ListLoanTransactionResponse expectedLoanTransactionResponse = ListLoanTransactionResponse.builder()
        .rspCode("00000")
        .rspMsg("rsp_msg")
        .nextPage(null)
        .transCnt(2)
        .transList(Arrays.asList(loanTransaction01, loanTransaction02))
        .build();

    // When
    ExecutionResponse<ListLoanTransactionResponse> actualExecutionResponse = collectExecutor
        .execute(executionContext, Executions.insurance_get_loan_transactions, executionRequest);

    // Then
    assertThat(actualExecutionResponse.getResponse()).usingRecursiveComparison()
        .isEqualTo(expectedLoanTransactionResponse);
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

    // 6.5.2 보험 기본정보 조회
    wireMockServer.stubFor(post(urlMatching("/insurances/basic"))
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

    // 6.5.4 자동차보험 정보 조회
    wireMockServer.stubFor(post(urlMatching("/insurances/car"))
        .withRequestBody(
            equalToJson(readText("classpath:mock/request/IS04_001_single_page_00.json")))
        .willReturn(
            aResponse()
                .withFixedDelay(1000)
                .withStatus(HttpStatus.OK.value())
                .withHeader("Content-Type", ContentType.APPLICATION_JSON.toString())
                .withBody(readText("classpath:mock/response/IS04_001_single_page_00.json"))));

    // 6.5.6 보험 거래내역 조회
    wireMockServer.stubFor(post(urlMatching("/insurances/transactions"))
        .withRequestBody(
            equalToJson(readText("classpath:mock/request/IS06_001_multi_page_00.json")))
        .willReturn(
            aResponse()
                .withFixedDelay(1000)
                .withStatus(HttpStatus.OK.value())
                .withHeader("Content-Type", ContentType.APPLICATION_JSON.toString())
                .withBody(readText("classpath:mock/response/IS06_001_multi_page_00.json"))));

    // 6.5.7 자동차보험 거래내역 조회
    wireMockServer.stubFor(post(urlMatching("/insurances/car/transactions"))
        .withRequestBody(
            equalToJson(readText("classpath:mock/request/IS07_001_multi_page_00.json")))
        .willReturn(
            aResponse()
                .withFixedDelay(1000)
                .withStatus(HttpStatus.OK.value())
                .withHeader("Content-Type", ContentType.APPLICATION_JSON.toString())
                .withBody(readText("classpath:mock/response/IS07_001_multi_page_00.json"))));

    // 6.5.8 대출상품 목록 조회
    wireMockServer.stubFor(get(urlMatching("/loans.*"))
        .withRequestBody(
            equalToJson(readText("classpath:mock/request/IS11_001_single_page_00.json")))
        .willReturn(
            aResponse()
                .withFixedDelay(1000)
                .withStatus(HttpStatus.OK.value())
                .withHeader("Content-Type", ContentType.APPLICATION_JSON.toString())
                .withBody(readText("classpath:mock/response/IS11_001_single_page_00.json"))));

    // 6.5.9 대출상품 기본정보 조회
    wireMockServer.stubFor(post(urlMatching("/loans/basic"))
        .withRequestBody(
            equalToJson(readText("classpath:mock/request/IS12_001_single_page_00.json")))
        .willReturn(
            aResponse()
                .withFixedDelay(1000)
                .withStatus(HttpStatus.OK.value())
                .withHeader("Content-Type", ContentType.APPLICATION_JSON.toString())
                .withBody(readText("classpath:mock/response/IS12_001_single_page_00.json"))));

    // 6.5.10 대출상품 추가정보 조회
    wireMockServer.stubFor(post(urlMatching("/loans/detail"))
        .withRequestBody(
            equalToJson(readText("classpath:mock/request/IS13_001_single_page_00.json")))
        .willReturn(
            aResponse()
                .withFixedDelay(1000)
                .withStatus(HttpStatus.OK.value())
                .withHeader("Content-Type", ContentType.APPLICATION_JSON.toString())
                .withBody(readText("classpath:mock/response/IS13_001_single_page_00.json"))));

    // 6.5.11 대출상품 거래내역 조회
    wireMockServer.stubFor(post(urlMatching("/loans/transactions"))
        .withRequestBody(
            equalToJson(readText("classpath:mock/request/IS14_001.json")))
        .willReturn(
            aResponse()
                .withFixedDelay(1000)
                .withStatus(HttpStatus.OK.value())
                .withHeader("Content-Type", ContentType.APPLICATION_JSON.toString())
                .withBody(readText("classpath:mock/response/IS14_001.json"))));
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
}
