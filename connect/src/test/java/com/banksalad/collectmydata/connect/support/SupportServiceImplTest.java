package com.banksalad.collectmydata.connect.support;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@DisplayName("SupportServiceImpl Test")
public class SupportServiceImplTest {

//  @Autowired
//  private SupportServiceImpl supportServiceImpl;
//
//  @Autowired
//  private BanksaladClientSecretRepository banksaladClientSecretRepository;
//
//  @Autowired
//  private OrganizationOauthTokenRepository organizationOauthTokenRepository;
//
//  @Autowired
//  private CollectExecutor collectExecutor;
//
//  private static WireMockServer wiremock = new WireMockServer(WireMockSpring.options().port(9091));
//  private static final String ORGANIZATION_HOST = "http://localhost:9091";
//
//  @BeforeEach
//  public void setupClass() {
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
//  @Transactional
//  @DisplayName("엑세스 토큰 발급 테스트")
//  void getAccessTokenTest() {
//    setupServer();
//    ExecutionContext executionContext = ExecutionContext.builder()
//        .accessToken("test")
//        .organizationHost("http://localhost:9091")
//        .build();
//
//    banksaladClientSecretRepository.save(
//        BanksaladClientSecretEntity.builder()
//            .secretType("") // fixme
//            .clientId("clientId")
//            .clientSecret("clientSecret")
//            .build()
//
//    );
//
//    String token = supportServiceImpl.getAccessToken(executionContext);
//    assertEquals("accessToken", token);
//
//    assertThat(organizationOauthTokenRepository.findByOrganizationId("banksalad").orElse(null))
//        .usingRecursiveComparison()
//        .ignoringFields("accessTokenExpiresAt", "organizationOauthTokenId")
//        .isEqualTo(OrganizationOauthTokenEntity.builder()
//            .organizationId("banksalad")
//            .accessToken("accessToken")
//            .accessTokenExpiresIn(123456789)
//            .tokenType("Bearer")
//            .scope("manage")
//            .build()
//        );
//  }
//
//  @Test
//  @DisplayName("기관정보 조회 execution test")
//  public void getOrganizationInfoTest() {
//    setupServer();
//
//    /* execution context */
//    ExecutionContext executionContext = ExecutionContext.builder()
//        .accessToken("test")
//        .organizationHost(ORGANIZATION_HOST)
//        .build();
//
//    ExecutionRequest<FinanceOrganizationRequest> executionRequest =
//        ExecutionRequest.<FinanceOrganizationRequest>builder()
//            .headers(new HashMap<>())
//            .request(FinanceOrganizationRequest.builder().searchTimestamp(0L).build())
//            .build();
//
//    // 7.1.2 기관 정보 조회 및 적재
//    ExecutionResponse<FinanceOrganizationResponse> executionResponse = collectExecutor
//        .execute(executionContext, Executions.support_get_organization_info, executionRequest);
//    FinanceOrganizationResponse financeOrganizationResponse = executionResponse.getResponse();
//
//    assertEquals(2, financeOrganizationResponse.getOrgList().size());
//    AssertionsForClassTypes.assertThat(financeOrganizationResponse).usingRecursiveComparison().isEqualTo(
//        financeOrganizationResponse.builder()
//            .rspCode("000")
//            .rspMsg("rsp_msg")
//            .searchTimestamp(1000L)
//            .orgCnt(2)
//            .orgList(List.of(
//                FinanceOrganizationInfo.builder()
//                    .opType("I")
//                    .orgCode("020")
//                    .orgType("01")
//                    .orgName("기관1")
//                    .orgRegno("1234567890")
//                    .corpRegno("1234567890")
//                    .address("address1")
//                    .domain("domain1")
//                    .relayOrgCode("relay_org_code1")
//                    .build(),
//                FinanceOrganizationInfo.builder()
//                    .opType("I")
//                    .orgCode("030")
//                    .orgType("02")
//                    .orgName("기관2")
//                    .orgRegno("1234567890")
//                    .corpRegno("1234567890")
//                    .address("address2")
//                    .domain("domain2")
//                    .relayOrgCode("relay_org_code2")
//                    .build()
//            ))
//            .build()
//    );
//  }
//
//  private void setupServer() {
//    // 7.1.1 접근토큰 발급
//    wiremock.stubFor(post(urlMatching("/oauth/2.0/token"))
//        .willReturn(
//            aResponse()
//                .withStatus(HttpStatus.OK.value())
//                .withHeader("Content-Type", ContentType.APPLICATION_JSON.toString())
//                .withBody(readText("classpath:mock/response/SU01_001.json"))));
//
//    // 7.1.2 기관정보 조회
//    wiremock.stubFor(get(urlMatching("/mgmts/orgs.*"))
//        .withQueryParam("search_timestamp", equalTo("0"))
//        .willReturn(
//            aResponse()
//                .withStatus(HttpStatus.OK.value())
//                .withHeader("Content-Type", ContentType.APPLICATION_JSON.toString())
//                .withBody(readText("classpath:mock/response/SU02_001.json"))));
//  }
}
