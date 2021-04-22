//package com.banksalad.collectmydata.insu.insurance;
//
//import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
//import com.banksalad.collectmydata.common.util.DateUtil;
//import com.banksalad.collectmydata.finance.api.accountinfo.AccountInfoRequestHelper;
//import com.banksalad.collectmydata.finance.api.accountinfo.AccountInfoResponseHelper;
//import com.banksalad.collectmydata.finance.api.accountinfo.AccountInfoService;
//import com.banksalad.collectmydata.finance.common.exception.ResponseNotOkException;
//import com.banksalad.collectmydata.insu.collect.Executions;
//import com.banksalad.collectmydata.insu.common.db.entity.InsuranceContractEntity;
//import com.banksalad.collectmydata.insu.common.db.entity.InsuranceContractHistoryEntity;
//import com.banksalad.collectmydata.insu.common.db.entity.InsuranceSummaryEntity;
//import com.banksalad.collectmydata.insu.common.db.entity.InsuredEntity;
//import com.banksalad.collectmydata.insu.common.db.repository.InsuranceContractHistoryRepository;
//import com.banksalad.collectmydata.insu.common.db.repository.InsuranceContractRepository;
//import com.banksalad.collectmydata.insu.common.db.repository.InsuranceSummaryRepository;
//import com.banksalad.collectmydata.insu.common.db.repository.InsuredRepository;
//import com.banksalad.collectmydata.insu.insurance.dto.InsuranceContract;
//import com.banksalad.collectmydata.insu.insurance.dto.Insured;
//import com.banksalad.collectmydata.insu.insurance.dto.ListInsuranceContractsRequest;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.cloud.contract.wiremock.WireMockSpring;
//import org.springframework.http.HttpStatus;
//
//import com.github.tomakehurst.wiremock.WireMockServer;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.http.entity.ContentType;
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.BeforeAll;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//
//import java.math.BigDecimal;
//import java.time.LocalDateTime;
//import java.util.List;
//import java.util.UUID;
//
//import static com.banksalad.collectmydata.insu.common.util.FileUtil.readText;
//import static com.banksalad.collectmydata.insu.common.util.TestHelper.ACCESS_TOKEN;
//import static com.banksalad.collectmydata.insu.common.util.TestHelper.BANKSALAD_USER_ID;
//import static com.banksalad.collectmydata.insu.common.util.TestHelper.ENTITY_IGNORE_FIELD;
//import static com.banksalad.collectmydata.insu.common.util.TestHelper.ORGANIZATION_CODE;
//import static com.banksalad.collectmydata.insu.common.util.TestHelper.ORGANIZATION_HOST;
//import static com.banksalad.collectmydata.insu.common.util.TestHelper.ORGANIZATION_ID;
//import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
//import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
//import static com.github.tomakehurst.wiremock.client.WireMock.post;
//import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.junit.jupiter.api.Assertions.assertEquals;
//
//@Slf4j
//@SpringBootTest
//public class InsuranceContractServiceTest {
//
//  @Autowired
//  private AccountInfoService<Insured, ListInsuranceContractsRequest, List<InsuranceContract>> subInfoService;
//
//  @Autowired
//  private AccountInfoRequestHelper<ListInsuranceContractsRequest, Insured> requestHelper;
//
//  @Autowired
//  private AccountInfoResponseHelper<Insured, List<InsuranceContract>> responseHelper;
//
//  @Autowired
//  private InsuranceSummaryRepository insuranceSummaryRepository;
//
//  @Autowired
//  private InsuredRepository insuredRepository;
//
//  @Autowired
//  private InsuranceContractRepository insuranceContractRepository;
//
//  @Autowired
//  private InsuranceContractHistoryRepository insuranceContractHistoryRepository;
//
//  private static WireMockServer wireMockServer;
//
//  @BeforeAll
//  static void setup() {
//    wireMockServer = new WireMockServer(WireMockSpring.options().dynamicPort());
//    wireMockServer.start();
//    setupMockServer();
//  }
//
//  @AfterEach
//  private void after() {
//    insuranceSummaryRepository.deleteAll();
//    insuredRepository.deleteAll();
//    insuranceContractRepository.deleteAll();
//    insuranceContractHistoryRepository.deleteAll();
//  }
//
//  @Test
//  @DisplayName("6.5.3 보험 특약정 조회 서비스 테스트1. 성공케이스")
//  public void listInsuranceContracts_success() throws ResponseNotOkException {
//    ExecutionContext context = getExecutionContext();
//    saveInsuranceSummary(0);
//    InsuredEntity insuredEntity = InsuredEntity.builder()
//        .syncedAt(context.getSyncStartedAt())
//        .banksaladUserId(BANKSALAD_USER_ID)
//        .organizationId(ORGANIZATION_ID)
//        .insuNum("123456789")
//        .insuredNo("01")
//        .insuredName("뱅샐")
//        .build();
//    insuredRepository.save(insuredEntity);
//
//    subInfoService
//        .listAccountInfos(context, Executions.insurance_get_contract, requestHelper, responseHelper);
//
//    List<InsuranceContractEntity> insuranceContractEntities = insuranceContractRepository.findAll();
//    List<InsuranceContractHistoryEntity> insuranceContractHistoryEntities = insuranceContractHistoryRepository
//        .findAll();
//    assertEquals(2, insuranceContractEntities.size());
//    assertEquals(2, insuranceContractHistoryEntities.size());
//
//    // TODO : compare with db
////    assertThat(insuranceContracts.get(0)).usingRecursiveComparison()
////        .isEqualTo(
////            InsuranceContract.builder()
////                .insuNum("123456789")
////                .insuredNo("01")
////                .contractName("묻지도따지지도않고")
////                .contractStatus("02")
////                .contractExpDate("99991231")
////                .contractFaceAmt(new BigDecimal("153212463.135"))
////                .currencyCode("KRW")
////                .required(true)
////                .build()
////        );
//
//    assertThat(insuranceContractEntities.get(0)).usingRecursiveComparison()
//        .ignoringFields(ENTITY_IGNORE_FIELD)
//        .isEqualTo(
//            InsuranceContractEntity.builder()
//                .syncedAt(context.getSyncStartedAt())
//                .banksaladUserId(BANKSALAD_USER_ID)
//                .organizationId(ORGANIZATION_ID)
//                .insuNum("123456789")
//                .insuredNo("01")
//                .contractNo(0)
//                .contractName("묻지도따지지도않고")
//                .contractStatus("02")
//                .contractExpDate("99991231")
//                .contractFaceAmt(new BigDecimal("153212463.135"))
//                .currencyCode("KRW")
//                .required(true)
//                .build()
//        );
//  }
//
//  private ExecutionContext getExecutionContext() {
//    return ExecutionContext.builder()
//        .organizationHost("http://" + ORGANIZATION_HOST + ":" + wireMockServer.port())
//        .accessToken(ACCESS_TOKEN)
//        .banksaladUserId(BANKSALAD_USER_ID)
//        .organizationId(ORGANIZATION_ID)
//        .organizationCode(ORGANIZATION_CODE)
//        .executionRequestId(UUID.randomUUID().toString())
//        .syncStartedAt(LocalDateTime.now(DateUtil.UTC_ZONE_ID))
//        .build();
//  }
//
//  public void saveInsuranceSummary(long searchTimestamp) {
//    insuranceSummaryRepository.save(
//        InsuranceSummaryEntity.builder()
//            .syncedAt(LocalDateTime.now())
//            .banksaladUserId(BANKSALAD_USER_ID)
//            .organizationId(ORGANIZATION_ID)
//            .insuNum("123456789")
//            .consent(true)
//            .insuType("05")
//            .prodName("묻지도 따지지도않고 암보험")
//            .insuStatus("02")
//            .paymentSearchTimestamp(searchTimestamp)
//            .build()
//    );
//  }
//
//  private static void setupMockServer() {
//    // 6.5.3 보험 특약정보 조회
//    wireMockServer.stubFor(post(urlMatching("/insurances/contracts"))
//        .withRequestBody(
//            equalToJson(readText("classpath:mock/request/IS03_001_single_page_00.json")))
//        .willReturn(
//            aResponse()
//                .withStatus(HttpStatus.OK.value())
//                .withHeader("Content-Type", ContentType.APPLICATION_JSON.toString())
//                .withBody(readText("classpath:mock/response/IS03_001_single_page_00.json"))));
//
//  }
//}
