package com.banksalad.collectmydata.ginsu.insurance;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import org.junit.jupiter.api.DisplayName;

@SpringBootTest
@DisplayName("보증보험 기본 정보 테스트")
@Transactional
class InsuranceBasicInfoServiceTest {

//  private static final WireMockServer wiremock = new WireMockServer(WireMockSpring.options().dynamicPort());
//
//  private static final Long BANKSALAD_USER_ID = 1L;
//  private static final String ORGANIZATION_ID = "organizationId";
//  private static final String ORGANIZATION_HOST = "http://localhost";
//  private static final String INSURANCE_NUM = "100246541123";
//
//  @Autowired
//  private AccountInfoService<InsuranceSummary, GetInsuranceBasicRequest, InsuranceBasic> insuranceBasicInfoService;
//
//  @Autowired
//  private AccountInfoRequestHelper<GetInsuranceBasicRequest, InsuranceSummary> insuranceBasicInfoRequestHelper;
//
//  @Autowired
//  private AccountInfoResponseHelper<InsuranceSummary, InsuranceBasic> insuranceBasicInfoResponseHelper;
//
//  @Autowired
//  private InsuranceSummaryRepository insuranceSummaryRepository;
//
//  @Autowired
//  private InsuranceBasicRepository insuranceBasicRepository;
//
//  @Autowired
//  private InsuredRepository insuredRepository;
//
//  @BeforeAll
//  public static void setUp() {
//    wiremock.start();
//  }
//
//  @AfterEach
//  public void after() {
//    wiremock.resetAll();
//  }
//
//  @AfterAll
//  public static void clean() {
//    wiremock.shutdown();
//  }
//
//  @Test
//  @DisplayName("보증보험 기본정보 조회")
//  void step_01_insuranceBasic_single_page_success() {
//    setupServerGinsuBasic();
//
//    InsuranceSummaryEntity insuranceSummaryEntity = InsuranceSummaryEntity.builder()
//        .id(1L)
//        .syncedAt(LocalDateTime.now(DateUtil.UTC_ZONE_ID))
//        .banksaladUserId(BANKSALAD_USER_ID)
//        .organizationId(ORGANIZATION_ID)
//        .consent(true)
//        .insuNum(INSURANCE_NUM)
//        .insuStatus("01")
//        .prodName("보증 보험")
//        .insuType("20")
//        .build();
//
//    insuranceSummaryRepository.save(insuranceSummaryEntity);
//
//    ExecutionContext executionContext = ExecutionContext.builder()
//        .banksaladUserId(BANKSALAD_USER_ID)
//        .organizationId(ORGANIZATION_ID)
//        .syncRequestId(UUID.randomUUID().toString())
//        .executionRequestId(UUID.randomUUID().toString())
//        .accessToken("test")
//        .organizationCode("020")
//        .organizationHost(ORGANIZATION_HOST + ":" + wiremock.port())
//        .syncStartedAt(LocalDateTime.now(DateUtil.UTC_ZONE_ID))
//        .build();
//
//    insuranceBasicInfoService
//        .listAccountInfos(executionContext, Executions.finance_ginsu_insurance_basic, insuranceBasicInfoRequestHelper,
//            insuranceBasicInfoResponseHelper);
//
//    Optional<InsuranceBasicEntity> insuranceBasicEntity = insuranceBasicRepository
//        .findByBanksaladUserIdAndOrganizationIdAndInsuNum(BANKSALAD_USER_ID, ORGANIZATION_ID, INSURANCE_NUM);
//
//    assertTrue(insuranceBasicEntity.isPresent());
//
//    List<InsuredEntity> insuredEntities = insuredRepository
//        .findByBanksaladUserIdAndOrganizationIdAndInsuNum(BANKSALAD_USER_ID, ORGANIZATION_ID, INSURANCE_NUM);
//
//    assertEquals(2, insuredEntities.size());
//  }
//
//  private void setupServerGinsuBasic() {
//    wiremock.stubFor(post(urlMatching("/insurances/basic"))
//        .withRequestBody(equalToJson(readText("classpath:mock/request/GI02_001_single_page_00.json")))
//        .willReturn(
//            aResponse()
//                .withStatus(HttpStatus.OK.value())
//                .withHeader("Content-Type", ContentType.APPLICATION_JSON.toString())
//                .withBody(readText("classpath:mock/response/GI02_001_single_page_00.json"))));
//  }
}
