package com.banksalad.collectmydata.connect.token.service;

import org.springframework.boot.test.context.SpringBootTest;

import org.junit.jupiter.api.DisplayName;

@SpringBootTest
@DisplayName("ExternalTokenServiceImpl Test")
class ExternalTokenServiceImplTest {

//  @Autowired
//  private BanksaladClientSecretRepository banksaladClientSecretRepository;
//
//  @Autowired
//  private ConnectOrganizationRepository connectOrganizationRepository;
//
//  @Autowired
//  private ExternalTokenService externalTokenService;
//
//  private static WireMockServer wiremock = new WireMockServer(WireMockSpring.options().dynamicPort());
//
//  @BeforeAll
//  public static void setupClass() {
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
//  @DisplayName("5.1.2 접근토큰 발급 요청을 성공하는 테스트")
//  void issueToken_success() {
//    // given
//    setupServerIssueToken();
//
//    ConnectOrganizationEntity connectOrganizationEntity = createConnectOrganizationEntity("test_organizationId");
//    connectOrganizationRepository.save(connectOrganizationEntity);
//    BanksaladClientSecretEntity banksaladClientSecretEntity = createOrganizationClientEntity(connectOrganizationEntity);
//    banksaladClientSecretRepository.save(banksaladClientSecretEntity);
//    ExternalTokenResponse response = createExternalTokenResponse();
//    Organization organization = createOrganization(connectOrganizationEntity);
//
//    // when
//    ExternalTokenResponse externalTokenResponse = externalTokenService
//        .issueToken(organization, "test_authorizationCode");
//
//    // then
//    assertThat(externalTokenResponse).usingRecursiveComparison().isEqualTo(response);
//  }
//
//  @Test
//  @Transactional
//  @DisplayName("5.1.3 접근토큰 갱신을 성공하는 테스트")
//  void refreshToken_success() {
//    // given
//    setupServerRefreshToken();
//
//    ConnectOrganizationEntity connectOrganizationEntity = createConnectOrganizationEntity("test_organizationId");
//    connectOrganizationRepository.save(connectOrganizationEntity);
//    BanksaladClientSecretEntity banksaladClientSecretEntity = createOrganizationClientEntity(connectOrganizationEntity);
//    banksaladClientSecretRepository.save(banksaladClientSecretEntity);
//    ExternalTokenResponse response = createExternalTokenResponse();
//    Organization organization = createOrganization(connectOrganizationEntity);
//
//    // when
//    ExternalTokenResponse externalTokenResponse = externalTokenService
//        .refreshToken(organization, "test_refreshToken");
//
//    // then
//    assertThat(externalTokenResponse).usingRecursiveComparison().isEqualTo(response);
//  }
//
//  @Test
//  @Transactional
//  @DisplayName("5.1.4 접근토큰 폐기를 성공하는 테스트")
//  void revokeToken_success() {
//    // given
//    setupServerRevokeToken();
//
//    ConnectOrganizationEntity connectOrganizationEntity = createConnectOrganizationEntity("test_organizationId");
//    connectOrganizationRepository.save(connectOrganizationEntity);
//    BanksaladClientSecretEntity banksaladClientSecretEntity = createOrganizationClientEntity(connectOrganizationEntity);
//    banksaladClientSecretRepository.save(banksaladClientSecretEntity);
//    Organization organization = createOrganization(connectOrganizationEntity);
//
//    // when, then
//    externalTokenService.revokeToken(organization, "test_accessToken");
//  }
//
//  private void setupServerIssueToken() {
//    // 5.1.2 접근토큰 발급 요청
//    wiremock.stubFor(post(urlMatching("/oauth/2.0/token"))
//        .willReturn(
//            aResponse()
//                .withStatus(HttpStatus.OK.value())
//                .withHeader("Content-Type", ContentType.APPLICATION_JSON.toString())
//                .withBody(readText("classpath:mock.api5/AU02_001.json"))));
//  }
//
//  private void setupServerRefreshToken() {
//    // 5.1.3 접근토큰 갱신
//    wiremock.stubFor(get(urlMatching("/oauth/2.0/token"))
//        .willReturn(
//            aResponse()
//                .withStatus(HttpStatus.OK.value())
//                .withHeader("Content-Type", ContentType.APPLICATION_JSON.toString())
//                .withBody(readText("classpath:mock.api5/AU02_001.json"))));
//  }
//
//  private void setupServerRevokeToken() {
//    // 5.1.4 접근토큰 폐기
//    wiremock.stubFor(get(urlMatching("/oauth/2.0/revoke"))
//        .willReturn(
//            aResponse()
//                .withStatus(HttpStatus.OK.value())
//                .withHeader("Content-Type", ContentType.APPLICATION_JSON.toString())
//                .withBody(readText("classpath:mock.api5/AU03_001.json"))));
//  }
//
//  private ConnectOrganizationEntity createConnectOrganizationEntity(String organizationId) {
//    return ConnectOrganizationEntity.builder()
//        .sector("test_finance")
//        .industry("test_card")
//        .organizationId(organizationId)
//        .organizationObjectid("test_objectId")
//        .organizationStatus("test_organizationStatus")
//        .build();
//  }
//
//  private ExternalTokenResponse createExternalTokenResponse() {
//    return ExternalTokenResponse.builder()
//        .tokenType("Bearer")
//        .accessToken("accessToken")
//        .expiresIn(324000)
//        .refreshToken("refreshToken")
//        .refreshTokenExpiresIn(1314000)
//        .scope("industry.scope1 industry.scope2")
//        .build();
//  }
//
//  private Organization createOrganization(ConnectOrganizationEntity connectOrganizationEntity) {
//    return Organization.builder()
//        .sector(connectOrganizationEntity.getSector())
//        .industry(connectOrganizationEntity.getIndustry())
//        .organizationId(connectOrganizationEntity.getOrganizationId())
//        .build();
//  }
//
//  private BanksaladClientSecretEntity createOrganizationClientEntity(
//      ConnectOrganizationEntity connectOrganizationEntity) {
//    return BanksaladClientSecretEntity.builder()
//        .clientId("clientId")
//        .clientSecret("clientSecret")
//        .build();
//  }
}
