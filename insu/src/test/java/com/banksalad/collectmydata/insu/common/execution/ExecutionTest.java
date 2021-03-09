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
import com.banksalad.collectmydata.insu.collect.Executions;
import com.banksalad.collectmydata.insu.common.dto.InsuranceSummary;
import com.banksalad.collectmydata.insu.common.dto.ListInsuranceSummariesRequest;
import com.banksalad.collectmydata.insu.common.dto.ListInsuranceSummariesResponse;
import com.banksalad.collectmydata.insu.insurance.dto.GetInsuranceContractRequest;
import com.banksalad.collectmydata.insu.insurance.dto.GetInsuranceContractResponse;
import com.banksalad.collectmydata.insu.insurance.dto.InsuranceContract;
import com.banksalad.collectmydata.insu.insurance.dto.GetInsuranceBasicRequest;
import com.banksalad.collectmydata.insu.insurance.dto.GetInsuranceBasicResponse;
import com.banksalad.collectmydata.insu.insurance.dto.Insured;
import com.github.tomakehurst.wiremock.WireMockServer;
import org.apache.http.entity.ContentType;
import org.junit.jupiter.api.AfterAll;
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
import static com.banksalad.collectmydata.insu.common.util.TestHelper.HEADERS;
import static com.banksalad.collectmydata.insu.common.util.TestHelper.ORGANIZATION_CODE;
import static com.banksalad.collectmydata.insu.common.util.TestHelper.ORGANIZATION_HOST;
import static com.banksalad.collectmydata.insu.common.util.TestHelper.ORGANIZATION_ID;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static org.assertj.core.api.Assertions.assertThat;

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
                .isRenewable(false)
                .issueDate("20200101")
                .expDate("99991231")
                .faceAmt(BigDecimal.valueOf(53253.333))
                .currencyCode("KRW")
                .isVariable(true)
                .isUniversal(true)
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
    wireMockServer.stubFor(post(urlMatching("/insurances/contracts.*"))
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
