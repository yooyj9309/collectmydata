package com.banksalad.collectmydata.connect.token.service;

import org.springframework.boot.test.context.SpringBootTest;

import org.junit.jupiter.api.DisplayName;

@SpringBootTest
@DisplayName("OauthTokenServiceImplTest Test")
class OauthTokenServiceImplTest {

//  @Autowired
//  private OauthTokenService oauthTokenService;
//
//  @Autowired
//  private OauthTokenRepository oauthTokenRepository;
//
//  @Autowired
//  private ConnectOrganizationRepository connectOrganizationRepository;
//
//  @MockBean
//  private ExternalTokenService externalTokenService;
//
//  @Test
//  @Transactional
//  @DisplayName("access token 조회를 성공하는 테스트")
//  void getAccessToken_success() {
//    // given
//    OauthTokenEntity oauthTokenEntity = createOauthTokenEntity();
//    oauthTokenRepository.save(oauthTokenEntity);
//
//    GetAccessTokenRequest request = buildAccessTokenRequest(oauthTokenEntity.getBanksaladUserId().toString(),
//        oauthTokenEntity.getOrganizationId());
//
//    // when
//    OauthToken oauthToken = oauthTokenService.getAccessToken(request);
//
//    // then
//    assertEquals(oauthTokenEntity.getAccessToken(), oauthToken.getAccessToken());
//    assertEquals(Arrays.asList(oauthTokenEntity.getScope().split(" ")), oauthToken.getScopes());
//    assertNull(oauthToken.getRefreshToken());
//  }
//
//  @Test
//  @Transactional
//  @DisplayName("access token 조회를 실패하는 테스트 - 없는 banksaladUserId 조회")
//  void getAccessToken_fail() {
//    // given
//    OauthTokenEntity oauthTokenEntity = createOauthTokenEntity();
//    oauthTokenRepository.save(oauthTokenEntity);
//
//    Long NonExistBanksaladUserId = 98765L;
//    GetAccessTokenRequest request = buildAccessTokenRequest(NonExistBanksaladUserId.toString(),
//        oauthTokenEntity.getOrganizationId());
//
//    // when, then
//    Exception responseException = assertThrows(Exception.class, () -> oauthTokenService.getAccessToken(request));
//    assertThat(responseException).isInstanceOf(ConnectException.class);
//    assertEquals(ConnectErrorType.NOT_FOUND_TOKEN.getMessage(), responseException.getMessage());
//  }
//
//  @Test
//  @Transactional
//  @DisplayName("access token 기한 만료로 토큰 갱신하는 테스트")
//  void getAccessToken_refresh() {
//    // given
//    OauthTokenEntity oauthTokenEntity = createAccessTokenExpiredOauthTokenEntity();
//    oauthTokenRepository.save(oauthTokenEntity);
//    ConnectOrganizationEntity connectOrganizationEntity = createConnectOrganizationEntity(
//        oauthTokenEntity.getOrganizationId());
//    connectOrganizationRepository.save(connectOrganizationEntity);
//    Organization organization = createOrganization(connectOrganizationEntity);
//
//    GetAccessTokenRequest accessTokenRequest = buildAccessTokenRequest(oauthTokenEntity.getBanksaladUserId().toString(),
//        oauthTokenEntity.getOrganizationId());
//
//    ExternalTokenResponse externalTokenResponse = createExternalTokenResponse();
//    when(externalTokenService
//        .refreshToken(organization, oauthTokenEntity.getRefreshToken()))
//        .thenReturn(externalTokenResponse);
//
//    // when
//    OauthToken oauthToken = oauthTokenService.getAccessToken(accessTokenRequest);
//
//    // then
//    assertEquals(externalTokenResponse.getAccessToken(), oauthToken.getAccessToken());
//    assertEquals(Arrays.asList(externalTokenResponse.getScope().split(" ")), oauthToken.getScopes());
//    assertNull(oauthToken.getRefreshToken());
//  }
//
//  @Test
//  @Transactional
//  @DisplayName("access token 발급을 성공하는 테스트")
//  void issueToken_success() {
//    // given
//    OauthTokenEntity oauthTokenEntity = createOauthTokenEntity();
//    oauthTokenRepository.save(oauthTokenEntity);
//    ConnectOrganizationEntity connectOrganizationEntity = createConnectOrganizationEntity(
//        oauthTokenEntity.getOrganizationId());
//    connectOrganizationRepository.save(connectOrganizationEntity);
//    Organization organization = createOrganization(connectOrganizationEntity);
//
//    IssueTokenRequest request = buildIssueTokenRequest(oauthTokenEntity.getBanksaladUserId().toString(),
//        oauthTokenEntity.getOrganizationId(),
//        oauthTokenEntity.getAuthorizationCode());
//
//    ExternalTokenResponse externalTokenResponse = createExternalTokenResponse();
//    when(externalTokenService
//        .issueToken(organization, request.getAuthorizationCode()))
//        .thenReturn(externalTokenResponse);
//
//    // when
//    OauthToken oauthToken = oauthTokenService.issueToken(request);
//
//    // then
//    assertEquals(externalTokenResponse.getAccessToken(), oauthToken.getAccessToken());
//    assertEquals(externalTokenResponse.getRefreshToken(), oauthToken.getRefreshToken());
//    assertEquals(Arrays.asList(externalTokenResponse.getScope().split(" ")), oauthToken.getScopes());
//  }
//
//  @Test
//  @Transactional
//  @DisplayName("access token 발급을 실패하는 테스트 - 없는 organizationId 조회")
//  void issueToken_fail() {
//    // given
//    OauthTokenEntity oauthTokenEntity = createOauthTokenEntity();
//    oauthTokenRepository.save(oauthTokenEntity);
//    ConnectOrganizationEntity connectOrganizationEntity = createConnectOrganizationEntity(
//        oauthTokenEntity.getOrganizationId());
//    connectOrganizationRepository.save(connectOrganizationEntity);
//    Organization organization = createOrganization(connectOrganizationEntity);
//
//    IssueTokenRequest request = buildIssueTokenRequest(oauthTokenEntity.getBanksaladUserId().toString(),
//        "non_exist_organizationId",
//        oauthTokenEntity.getAuthorizationCode());
//
//    ExternalTokenResponse externalTokenResponse = createExternalTokenResponse();
//    when(externalTokenService
//        .issueToken(organization, request.getAuthorizationCode()))
//        .thenReturn(externalTokenResponse);
//
//    // when, then
//    Exception responseException = assertThrows(Exception.class, () -> oauthTokenService.issueToken(request));
//    assertThat(responseException).isInstanceOf(ConnectException.class);
//    assertEquals(ConnectErrorType.NOT_FOUND_ORGANIZATION.getMessage(), responseException.getMessage());
//  }
//
//  @Test
//  @Transactional
//  @DisplayName("access token 갱신을 성공하는 테스트")
//  void refreshToken_success() {
//    // given
//    OauthTokenEntity oauthTokenEntity = createOauthTokenEntity();
//    oauthTokenRepository.save(oauthTokenEntity);
//    ConnectOrganizationEntity connectOrganizationEntity = createConnectOrganizationEntity(
//        oauthTokenEntity.getOrganizationId());
//    connectOrganizationRepository.save(connectOrganizationEntity);
//    Organization organization = createOrganization(connectOrganizationEntity);
//
//    RefreshTokenRequest request = buildRefreshTokenRequest(oauthTokenEntity.getBanksaladUserId().toString(),
//        oauthTokenEntity.getOrganizationId());
//
//    ExternalTokenResponse externalTokenResponse = createExternalTokenResponse();
//    when(externalTokenService
//        .refreshToken(organization, oauthTokenEntity.getRefreshToken()))
//        .thenReturn(externalTokenResponse);
//
//    // when
//    OauthToken oauthToken = oauthTokenService.refreshToken(request);
//
//    // then
//    assertEquals(externalTokenResponse.getAccessToken(), oauthToken.getAccessToken());
//    assertEquals(Arrays.asList(externalTokenResponse.getScope().split(" ")), oauthToken.getScopes());
//    assertNull(oauthToken.getRefreshToken());
//  }
//
//  @Test
//  @Transactional
//  @DisplayName("access token 갱신을 실패하는 테스트 - 만료된 refresh token")
//  void refreshToken_fail() {
//    // given
//    OauthTokenEntity oauthTokenEntity = createRefreshTokenExpiredOauthTokenEntity();
//    oauthTokenRepository.save(oauthTokenEntity);
//    ConnectOrganizationEntity connectOrganizationEntity = createConnectOrganizationEntity(
//        oauthTokenEntity.getOrganizationId());
//    connectOrganizationRepository.save(connectOrganizationEntity);
//    Organization organization = createOrganization(connectOrganizationEntity);
//
//    RefreshTokenRequest request = buildRefreshTokenRequest(oauthTokenEntity.getBanksaladUserId().toString(),
//        oauthTokenEntity.getOrganizationId());
//
//    ExternalTokenResponse externalTokenResponse = createExternalTokenResponse();
//    when(externalTokenService
//        .refreshToken(organization, oauthTokenEntity.getRefreshToken()))
//        .thenReturn(externalTokenResponse);
//
//    // when, then
//    Exception responseException = assertThrows(Exception.class, () -> oauthTokenService.refreshToken(request));
//    assertThat(responseException).isInstanceOf(ConnectException.class);
//    assertEquals(ConnectErrorType.EXPIRED_TOKEN.getMessage(), responseException.getMessage());
//  }
//
//  @Test
//  @Transactional
//  @DisplayName("access token 폐기를 성공하는 테스트")
//  void revokeToken_success() {
//    // given
//    OauthTokenEntity oauthTokenEntity = createOauthTokenEntity();
//    oauthTokenRepository.save(oauthTokenEntity);
//    ConnectOrganizationEntity connectOrganizationEntity = createConnectOrganizationEntity(
//        oauthTokenEntity.getOrganizationId());
//    connectOrganizationRepository.save(connectOrganizationEntity);
//
//    RevokeTokenRequest request = buildRevokeTokenRequest(oauthTokenEntity.getBanksaladUserId().toString(),
//        oauthTokenEntity.getOrganizationId());
//
//    // when
//    oauthTokenService.revokeToken(request);
//
//    // then
//    assertThrows(GrpcException.class, () -> oauthTokenRepository
//        .findByBanksaladUserIdAndOrganizationId(oauthTokenEntity.getBanksaladUserId(),
//            request.getOrganizationId())
//        .orElseThrow(GrpcException::new));
//  }
//
//  @Test
//  @Transactional
//  @DisplayName("access token 폐기를 실패하는 테스트 - 없는 organizationId 삭제")
//  void revokeToken_fail() {
//    // given
//    OauthTokenEntity oauthTokenEntity = createOauthTokenEntity();
//    oauthTokenRepository.save(oauthTokenEntity);
//    ConnectOrganizationEntity connectOrganizationEntity = createConnectOrganizationEntity(
//        oauthTokenEntity.getOrganizationId());
//    connectOrganizationRepository.save(connectOrganizationEntity);
//
//    RevokeTokenRequest request = buildRevokeTokenRequest(oauthTokenEntity.getBanksaladUserId().toString(),
//        "non-exist-organizationId");
//
//    // when, then
//    Exception responseException = assertThrows(Exception.class, () -> oauthTokenService.revokeToken(request));
//    assertThat(responseException).isInstanceOf(ConnectException.class);
//    assertEquals(ConnectErrorType.NOT_FOUND_TOKEN.getMessage(), responseException.getMessage());
//  }
//
//  @Test
//  @Transactional
//  @DisplayName("뱅크샐러드 DB에서 토큰 제거 후 기관에 폐기 요청하였으나 예외가 발생한 경우 - 뱅크샐러드 DB에는 토큰 폐기 상태를 유지")
//  void revokeToken_transation_success_external_request_fail() {
//    // given
//    OauthTokenEntity oauthTokenEntity = createOauthTokenEntity();
//    oauthTokenRepository.save(oauthTokenEntity);
//    ConnectOrganizationEntity connectOrganizationEntity = createConnectOrganizationEntity(
//        oauthTokenEntity.getOrganizationId());
//    connectOrganizationRepository.save(connectOrganizationEntity);
//    Organization organization = createOrganization(connectOrganizationEntity);
//
//    RevokeTokenRequest request = buildRevokeTokenRequest(oauthTokenEntity.getBanksaladUserId().toString(),
//        oauthTokenEntity.getOrganizationId());
//
//    doThrow(new GrpcException())
//        .when(externalTokenService)
//        .revokeToken(organization, oauthTokenEntity.getAccessToken());
//
//    // when, then
//    assertThat(oauthTokenRepository
//        .findByBanksaladUserIdAndOrganizationId(oauthTokenEntity.getBanksaladUserId(),
//            connectOrganizationEntity.getOrganizationId()))
//        .isNotEmpty();
//    assertThrows(GrpcException.class, () -> oauthTokenService.revokeToken(request));
//    // TODO 쿼리 수정부분 확인필요
//    assertThat(oauthTokenRepository
//        .findByBanksaladUserIdAndOrganizationId(oauthTokenEntity.getBanksaladUserId(),
//            connectOrganizationEntity.getOrganizationId()))
//        .isEmpty();
//  }
//
//  @Test
//  @Transactional
//  @DisplayName("모든 access token 폐기를 성공하는 테스트 - 다른 유저의 토큰이 삭제되지 않은 것 포함하여 검증")
//  void revokeAllTokens_success() {
//    // given
//    OauthTokenEntity oauthTokenEntity = createOauthTokenEntity();
//    OauthTokenEntity oauthTokenEntityWithOtherBanksaladUserId = createOauthTokenEntityWithOtherBanksaladUserId();
//    oauthTokenRepository.save(oauthTokenEntity);
//    oauthTokenRepository.save(oauthTokenEntityWithOtherBanksaladUserId);
//
//    ConnectOrganizationEntity connectOrganizationEntity = createConnectOrganizationEntity(
//        oauthTokenEntity.getOrganizationId());
//    connectOrganizationRepository.save(connectOrganizationEntity);
//
//    RevokeAllTokensRequest request = buildRevokeAllTokensRequest(oauthTokenEntity.getBanksaladUserId().toString());
//
//    // TODO 쿼리 수정부분 확인필요
//    List<OauthTokenEntity> BeforeOauthTokenEntitiesWithOtherBanksaladUserId = oauthTokenRepository
//        .findAllByBanksaladUserId(oauthTokenEntityWithOtherBanksaladUserId.getBanksaladUserId());
//
//    // when
//    oauthTokenService.revokeAllTokens(request);
//
//    // then
//    // TODO 쿼리 수정부분 확인필요
//    List<OauthTokenEntity> AfterOauthTokenEntitiesWithOtherBanksaladUserId = oauthTokenRepository
//        .findAllByBanksaladUserId(oauthTokenEntityWithOtherBanksaladUserId.getBanksaladUserId());
//
//    assertThrows(GrpcException.class, () -> oauthTokenRepository
//        .findAllByBanksaladUserId(oauthTokenEntity.getBanksaladUserId()));
//
//    assertEquals(BeforeOauthTokenEntitiesWithOtherBanksaladUserId.size(),
//        AfterOauthTokenEntitiesWithOtherBanksaladUserId.size());
//  }
//
//  @Test
//  @Transactional
//  @DisplayName("모든 access token 폐기를 실패하는 테스트 - 없는 banksaladUserId의 토큰 삭제")
//  void revokeAllTokens_fail() {
//    // given
//    OauthTokenEntity oauthTokenEntity = createOauthTokenEntity();
//    oauthTokenRepository.save(oauthTokenEntity);
//    ConnectOrganizationEntity connectOrganizationEntity = createConnectOrganizationEntity(
//        oauthTokenEntity.getOrganizationId());
//    connectOrganizationRepository.save(connectOrganizationEntity);
//
//    OauthTokenEntity oauthTokenEntityWithDifferentBanksaladUserId = createOauthTokenEntityWithOtherBanksaladUserId();
//    RevokeAllTokensRequest request = buildRevokeAllTokensRequest(
//        oauthTokenEntityWithDifferentBanksaladUserId.getBanksaladUserId().toString());
//
//    // when, then
//    Exception responseException = assertThrows(Exception.class, () -> oauthTokenService.revokeAllTokens(request));
//    assertThat(responseException).isInstanceOf(ConnectException.class);
//    assertEquals(ConnectErrorType.NOT_FOUND_TOKEN.getMessage(), responseException.getMessage());
//  }
//
//  @Test
//  @Transactional
//  @DisplayName("유저의 모든 토큰 제거 과정 중간에 특정 기관에서 예외가 발생한 경우 - 해당 예외 발생 전까지의 과정은 정상 트랜잭션 진행 (진행된 뱅크샐러드 DB에는 토큰 폐기 상태를 유지)")
//  void revokeAllTokens_transation_success_external_request_fail() {
//    // given
//    final int TOTAL_ORGANIZATION_COUNT = 5;
//    final int ERROR_INDEX = 2; // must be less than TOTAL_ORGANIZATION_COUNT
//
//    List<OauthTokenEntity> oauthTokenEntities = createOauthTokenEntities(TOTAL_ORGANIZATION_COUNT);
//    List<ConnectOrganizationEntity> connectOrganizationEntities = createConnectOrganizationEntities(
//        oauthTokenEntities);
//    List<Organization> organizations = new ArrayList<>();
//    for (int i = 0; i < TOTAL_ORGANIZATION_COUNT; i++) {
//      oauthTokenRepository.save(oauthTokenEntities.get(i));
//      connectOrganizationRepository.save(connectOrganizationEntities.get(i));
//      organizations.add(createOrganization(connectOrganizationEntities.get(i)));
//    }
//
//    doThrow(new GrpcException())
//        .when(externalTokenService)
//        .revokeToken(
//            organizations.get(ERROR_INDEX),
//            oauthTokenEntities.get(ERROR_INDEX).getAccessToken()
//        );
//
//    Long banksaladUserId = oauthTokenEntities.get(0).getBanksaladUserId();
//    RevokeAllTokensRequest request = buildRevokeAllTokensRequest(banksaladUserId.toString());
//
//    // when, then
//    for (int i = 0; i < TOTAL_ORGANIZATION_COUNT; i++) {
//      String errorOrganizationId = connectOrganizationEntities.get(i).getOrganizationId();
//      assertThat(oauthTokenRepository
//          .findByBanksaladUserIdAndOrganizationId(banksaladUserId, errorOrganizationId))
//          .isNotEmpty();
//    }
//    assertThrows(GrpcException.class, () -> oauthTokenService.revokeAllTokens(request));
//
//    for (int i = 0; i <= ERROR_INDEX; i++) {
//      String errorOrganizationId = connectOrganizationEntities.get(i).getOrganizationId();
//      assertThat(oauthTokenRepository
//          .findByBanksaladUserIdAndOrganizationId(banksaladUserId, errorOrganizationId))
//          .isEmpty();
//    }
//    for (int i = ERROR_INDEX + 1; i < TOTAL_ORGANIZATION_COUNT; i++) {
//      String errorOrganizationId = connectOrganizationEntities.get(i).getOrganizationId();
//      assertThat(oauthTokenRepository
//          .findByBanksaladUserIdAndOrganizationId(banksaladUserId, errorOrganizationId))
//          .isNotEmpty();
//    }
//  }
//
//  private OauthTokenEntity createOauthTokenEntity() {
//    return OauthTokenEntity.builder()
//        .banksaladUserId(1234567890L)
//        .organizationId("test_shinhancard")
//        .authorizationCode("test_authorizationCode")
//        .accessToken("test_accessToken")
//        .refreshToken("test_refreshToken")
//        .accessTokenExpiresAt(LocalDateTime.now().plusDays(90))
//        .accessTokenExpiresIn(90 * 3600)
//        .refreshTokenExpiresAt(LocalDateTime.now().plusDays(365))
//        .refreshTokenExpiresIn(365 * 3600)
//        .tokenType("Bearer")
//        .scope("card.loan card.bill")
//        .build();
//  }
//
//  private OauthTokenEntity createOauthTokenEntityWithOtherBanksaladUserId() {
//    return OauthTokenEntity.builder()
//        .banksaladUserId(543210L)
//        .organizationId("test_shinhancard")
//        .authorizationCode("test_authorizationCode")
//        .accessToken("test_accessToken")
//        .refreshToken("test_refreshToken")
//        .accessTokenExpiresAt(LocalDateTime.now().plusDays(90))
//        .accessTokenExpiresIn(90 * 3600)
//        .refreshTokenExpiresAt(LocalDateTime.now().plusDays(365))
//        .refreshTokenExpiresIn(365 * 3600)
//        .tokenType("Bearer")
//        .scope("card.loan card.bill")
//        .build();
//  }
//
//  private OauthTokenEntity createAccessTokenExpiredOauthTokenEntity() {
//    return OauthTokenEntity.builder()
//        .banksaladUserId(1234567890L)
//        .organizationId("test_shinhancard")
//        .authorizationCode("test_authorizationCode")
//        .accessToken("test_accessToken")
//        .refreshToken("test_refreshToken")
//        .accessTokenExpiresAt(LocalDateTime.now().minusDays(5))
//        .accessTokenExpiresIn(90 * 3600)
//        .refreshTokenExpiresAt(LocalDateTime.now().plusDays(365))
//        .refreshTokenExpiresIn(365 * 3600)
//        .tokenType("Bearer")
//        .scope("card.loan card.bill")
//        .build();
//  }
//
//  private OauthTokenEntity createRefreshTokenExpiredOauthTokenEntity() {
//    return OauthTokenEntity.builder()
//        .banksaladUserId(1234567890L)
//        .organizationId("test_shinhancard")
//        .authorizationCode("test_authorizationCode")
//        .accessToken("test_accessToken")
//        .refreshToken("test_refreshToken")
//        .accessTokenExpiresAt(LocalDateTime.now().minusDays(5))
//        .accessTokenExpiresIn(90 * 3600)
//        .refreshTokenExpiresAt(LocalDateTime.now().minusDays(5))
//        .refreshTokenExpiresIn(365 * 3600)
//        .tokenType("Bearer")
//        .scope("card.loan card.bill")
//        .build();
//  }
//
//  private List<OauthTokenEntity> createOauthTokenEntities(int size) {
//    List<OauthTokenEntity> oauthTokenEntities = new ArrayList<>();
//    for (int i = 0; i < size; i++) {
//      OauthTokenEntity oauthTokenEntity = OauthTokenEntity.builder()
//          .banksaladUserId(1234567890L)
//          .organizationId("test_organizationId" + i)
//          .authorizationCode("test_authorizationCode")
//          .accessToken("test_accessToken")
//          .refreshToken("test_refreshToken")
//          .accessTokenExpiresAt(LocalDateTime.now().plusDays(90))
//          .accessTokenExpiresIn(90 * 3600)
//          .refreshTokenExpiresAt(LocalDateTime.now().plusDays(365))
//          .refreshTokenExpiresIn(365 * 3600)
//          .tokenType("Bearer")
//          .scope("card.loan card.bill")
//          .build();
//      oauthTokenEntities.add(oauthTokenEntity);
//    }
//    return oauthTokenEntities;
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
//  private List<ConnectOrganizationEntity> createConnectOrganizationEntities
//      (List<OauthTokenEntity> oauthTokenEntities) {
//    List<ConnectOrganizationEntity> connectOrganizationEntities = new ArrayList<>();
//    for (int i = 0; i < oauthTokenEntities.size(); i++) {
//      ConnectOrganizationEntity connectOrganizationEntity = ConnectOrganizationEntity.builder()
//          .sector("test_finance")
//          .industry("test_card")
//          .organizationId(oauthTokenEntities.get(i).getOrganizationId())
//          .organizationObjectid("test_objectId")
//          .organizationStatus("test_organizationStatus")
//          .build();
//      connectOrganizationEntities.add(connectOrganizationEntity);
//    }
//    return connectOrganizationEntities;
//  }
//
//  private ExternalTokenResponse createExternalTokenResponse() {
//    return ExternalTokenResponse.builder()
//        .tokenType("Bearer")
//        .accessToken("test_received_accessToken")
//        .expiresIn(90 * 3600)
//        .refreshToken("test_received_refreshToken")
//        .refreshTokenExpiresIn(365 * 3600)
//        .scope("received_scope1 received_scope2")
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
//  private GetAccessTokenRequest buildAccessTokenRequest(String banksaladId, String organizationId) {
//    return GetAccessTokenRequest.newBuilder()
//        .setBanksaladUserId(banksaladId)
//        .setOrganizationId(organizationId)
//        .build();
//  }
//
//  private IssueTokenRequest buildIssueTokenRequest(String banksaladId, String organizationId,
//      String authorizationCode) {
//    return IssueTokenRequest.newBuilder()
//        .setBanksaladUserId(banksaladId)
//        .setOrganizationId(organizationId)
//        .setAuthorizationCode(authorizationCode)
//        .build();
//  }
//
//  private RefreshTokenRequest buildRefreshTokenRequest(String banksaladId, String organizationId) {
//    return RefreshTokenRequest.newBuilder()
//        .setBanksaladUserId(banksaladId)
//        .setOrganizationId(organizationId)
//        .build();
//  }
//
//  private RevokeTokenRequest buildRevokeTokenRequest(String banksaladId, String organizationId) {
//    return RevokeTokenRequest.newBuilder()
//        .setBanksaladUserId(banksaladId)
//        .setOrganizationId(organizationId)
//        .build();
//  }
//
//  private RevokeAllTokensRequest buildRevokeAllTokensRequest(String banksaladId) {
//    return RevokeAllTokensRequest.newBuilder()
//        .setBanksaladUserId(banksaladId)
//        .build();
//  }
}
