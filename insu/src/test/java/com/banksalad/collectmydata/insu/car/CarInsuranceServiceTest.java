package com.banksalad.collectmydata.insu.car;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.finance.api.accountinfo.AccountInfoRequestHelper;
import com.banksalad.collectmydata.finance.api.accountinfo.AccountInfoResponseHelper;
import com.banksalad.collectmydata.finance.api.accountinfo.AccountInfoService;
import com.banksalad.collectmydata.insu.car.dto.CarInsurance;
import com.banksalad.collectmydata.insu.car.dto.GetCarInsuranceRequest;
import com.banksalad.collectmydata.insu.collect.Executions;
import com.banksalad.collectmydata.insu.common.service.InsuranceSummaryService;
import com.banksalad.collectmydata.insu.summary.dto.InsuranceSummary;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.contract.wiremock.WireMockSpring;
import org.springframework.http.HttpStatus;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.apache.http.entity.ContentType;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.banksalad.collectmydata.insu.common.util.FileUtil.readText;
import static com.banksalad.collectmydata.insu.common.util.TestHelper.BANKSALAD_USER_ID;
import static com.banksalad.collectmydata.insu.common.util.TestHelper.ORGANIZATION_ID;
import static com.banksalad.collectmydata.insu.common.util.TestHelper.getExecutionContext;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@DisplayName("자동차보험 서비스 테스트")
class CarInsuranceServiceTest {

  @Autowired
  private AccountInfoService<InsuranceSummary, GetCarInsuranceRequest, List<CarInsurance>> carInsuranceApiService;

  @Autowired
  private AccountInfoRequestHelper<GetCarInsuranceRequest, InsuranceSummary> carInsuranceRequestHelper;

  @Autowired
  private AccountInfoResponseHelper<InsuranceSummary, List<CarInsurance>> carInsuranceResponseHelper;

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
  @DisplayName("6.5.4 자동차보험 정보 조회")
  void carInsurance_accountInfoService_listAccountInfos_test() {
    // given
    ExecutionContext executionContext = getExecutionContext(wireMockServer.port());

    when(insuranceSummaryService.listSummariesConsented(BANKSALAD_USER_ID, ORGANIZATION_ID))
        .thenReturn(List.of(
            InsuranceSummary.builder()
                .insuNum("123456789")
                .consent(true)
                .prodName("묻지도 따지지도않고 암보험")
                .insuType("05")
                .insuStatus("02")
                .build()
        ));

    // when
    carInsuranceApiService
        .listAccountInfos(executionContext, Executions.insurance_get_car, carInsuranceRequestHelper,
            carInsuranceResponseHelper);

    // TODO : compare with db
    // then
//    assertThat(carInsurances.get(0)).usingRecursiveComparison().isEqualTo(
//        List.of(
//            CarInsurance.builder()
//                .carNumber("60무1234")
//                .carInsuType("02")
//                .carName("그랜져 IG")
//                .startDate("20200101")
//                .endDate("20210101")
//                .contractAge("21세")
//                .contractDriver("가족한정")
//                .ownDmgCoverage(true)
//                .selfPayRate("01")
//                .selfPayAmt(200000)
//                .build(),
//            CarInsurance.builder()
//                .carNumber("60무1234")
//                .carInsuType("04")
//                .carName("그랜져 IG")
//                .startDate("20200601")
//                .endDate("20210601")
//                .contractAge("21세")
//                .contractDriver("본인")
//                .ownDmgCoverage(false)
//                .selfPayRate("02")
//                .selfPayAmt(30000)
//                .build()
//        ));
  }

  private static void setupMockServer() {
    // 6.5.4 자동차보험 정보 조회
    wireMockServer.stubFor(post(urlMatching("/insurances/car"))
        .withRequestBody(
            equalToJson(readText("classpath:mock/request/IS04_001_single_page_00.json")))
        .willReturn(
            aResponse()
                .withStatus(HttpStatus.OK.value())
                .withHeader("Content-Type", ContentType.APPLICATION_JSON.toString())
                .withBody(readText("classpath:mock/response/IS04_001_single_page_00.json"))));
  }
}
