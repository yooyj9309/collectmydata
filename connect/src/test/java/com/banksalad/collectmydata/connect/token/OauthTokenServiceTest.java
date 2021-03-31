package com.banksalad.collectmydata.connect.token;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;

import com.banksalad.collectmydata.common.collect.execution.Execution;
import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.collect.execution.ExecutionRequest;
import com.banksalad.collectmydata.common.collect.execution.ExecutionResponse;
import com.banksalad.collectmydata.common.collect.executor.CollectExecutor;
import com.banksalad.collectmydata.common.exception.CollectRuntimeException;
import com.banksalad.collectmydata.common.exception.GrpcException;
import com.banksalad.collectmydata.connect.common.db.entity.BanksaladClientSecretEntity;
import com.banksalad.collectmydata.connect.common.db.repository.BanksaladClientSecretRepository;
import com.banksalad.collectmydata.connect.common.exception.ConnectException;
import com.banksalad.collectmydata.connect.common.db.entity.ConnectOrganizationEntity;
import com.banksalad.collectmydata.connect.common.db.entity.OauthTokenEntity;
import com.banksalad.collectmydata.connect.common.db.repository.ConnectOrganizationRepository;
import com.banksalad.collectmydata.connect.common.db.repository.OauthTokenRepository;
import com.banksalad.collectmydata.connect.common.enums.ConnectErrorType;
import com.banksalad.collectmydata.connect.organization.dto.Organization;
import com.banksalad.collectmydata.connect.token.dto.GetOauthTokenResponse;
import com.banksalad.collectmydata.connect.token.dto.OauthToken;
import com.banksalad.collectmydata.connect.token.service.OauthTokenService;
import com.github.banksalad.idl.apis.v1.connectmydata.ConnectmydataProto.GetAccessTokenRequest;
import com.github.banksalad.idl.apis.v1.connectmydata.ConnectmydataProto.IssueTokenRequest;
import com.github.banksalad.idl.apis.v1.connectmydata.ConnectmydataProto.RefreshTokenRequest;
import com.github.banksalad.idl.apis.v1.connectmydata.ConnectmydataProto.RevokeAllTokensRequest;
import com.github.banksalad.idl.apis.v1.connectmydata.ConnectmydataProto.RevokeTokenRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.banksalad.collectmydata.connect.common.ConnectConstant.*;
import static java.lang.Boolean.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@SpringBootTest
@DisplayName("OauthTokenService Test")
class OauthTokenServiceTest {

  @Autowired
  private OauthTokenService oauthTokenService;

  @Autowired
  private OauthTokenRepository oauthTokenRepository;

  @Autowired
  private ConnectOrganizationRepository connectOrganizationRepository;

  @Autowired
  private BanksaladClientSecretRepository banksaladClientSecretRepository;

  @MockBean
  private CollectExecutor collectExecutor;

  private OauthTokenEntity oauthTokenEntity;
  private ConnectOrganizationEntity connectOrganizationEntity;
  private BanksaladClientSecretEntity banksaladClientSecretEntity;

  private final int TOTAL_ORGANIZATION_SIZE = 5;

  @BeforeEach
  void setUp() {
    oauthTokenEntity = getOauthTokenEntity();
    connectOrganizationEntity = getConnectOrganizationEntity();
    banksaladClientSecretEntity = getBanksaladClientSecretEntity();
    oauthTokenRepository.save(oauthTokenEntity);
    connectOrganizationRepository.save(connectOrganizationEntity);
    banksaladClientSecretRepository.save(banksaladClientSecretEntity);
  }

  @AfterEach
  void tearDown() {
    oauthTokenRepository.deleteAll();
    connectOrganizationRepository.deleteAll();
    connectOrganizationRepository.deleteAll();
  }

  @Test
  @Transactional
  @DisplayName("access token 조회를 성공하는 테스트")
  void getAccessToken_success() {
    // given
    GetAccessTokenRequest request = getAccessTokenRequest(oauthTokenEntity.getBanksaladUserId().toString(),
        oauthTokenEntity.getOrganizationId());

    // when
    OauthToken oauthToken = oauthTokenService.getAccessToken(request);

    // then
    assertEquals(oauthTokenEntity.getAccessToken(), oauthToken.getAccessToken());
    assertEquals(Arrays.asList(oauthTokenEntity.getScope().split(" ")), oauthToken.getScopes());
    assertNull(oauthToken.getRefreshToken());
  }

  @Test
  @Transactional
  @DisplayName("access token 조회를 실패하는 테스트 - 없는 banksaladUserId 조회")
  void getAccessToken_fail() {
    // given
    Long NonExistBanksaladUserId = 98765L;
    GetAccessTokenRequest request = getAccessTokenRequest(NonExistBanksaladUserId.toString(),
        oauthTokenEntity.getOrganizationId());

    // when, then
    Exception responseException = assertThrows(Exception.class, () -> oauthTokenService.getAccessToken(request));
    assertThat(responseException).isInstanceOf(ConnectException.class);
    assertEquals(ConnectErrorType.NOT_FOUND_TOKEN.getMessage(), responseException.getMessage());
  }

  @Test
  @Transactional
  @DisplayName("access token 기한 만료로 토큰 갱신하는 테스트")
  void getAccessToken_refresh() {
    // given
    OauthTokenEntity oauthTokenEntity = oauthTokenRepository
        .findByBanksaladUserIdAndOrganizationId(BANKSALAD_USER_ID, ORGANIZATION_ID)
        .orElseThrow(() -> new CollectRuntimeException("NOT FOUND OAUTH TOKEN ENTITY"));

    oauthTokenEntity.setAccessTokenExpiresAt(LocalDateTime.now().minusDays(5));
    oauthTokenRepository.save(oauthTokenEntity);

    GetAccessTokenRequest accessTokenRequest = getAccessTokenRequest(oauthTokenEntity.getBanksaladUserId().toString(),
        oauthTokenEntity.getOrganizationId());

    GetOauthTokenResponse getOauthTokenResponse = getExternalTokenResponse();
    when(collectExecutor.execute(any(ExecutionContext.class), any(Execution.class), any(ExecutionRequest.class)))
        .thenReturn(
            ExecutionResponse.builder()
                .httpStatusCode(HttpStatus.OK.value())
                .response(getOauthTokenResponse)
                .build());

    // when
    OauthToken oauthToken = oauthTokenService.getAccessToken(accessTokenRequest);

    // then
    assertEquals(getOauthTokenResponse.getAccessToken(), oauthToken.getAccessToken());
    assertEquals(Arrays.asList(getOauthTokenResponse.getScope().split(" ")), oauthToken.getScopes());
    assertNull(oauthToken.getRefreshToken());
  }

  @Test
  @Transactional
  @DisplayName("access token 발급을 성공하는 테스트")
  void issueToken_success() {
    // given
    IssueTokenRequest request = getIssueTokenRequest(oauthTokenEntity.getBanksaladUserId().toString(),
        oauthTokenEntity.getOrganizationId(),
        oauthTokenEntity.getAuthorizationCode());

    GetOauthTokenResponse getOauthTokenResponse = getExternalTokenResponse();
    when(collectExecutor.execute(any(ExecutionContext.class), any(Execution.class), any(ExecutionRequest.class)))
        .thenReturn(
            ExecutionResponse.builder()
                .httpStatusCode(HttpStatus.OK.value())
                .response(getOauthTokenResponse)
                .build());

    // when
    OauthToken oauthToken = oauthTokenService.issueToken(request);

    // then
    assertEquals(getOauthTokenResponse.getAccessToken(), oauthToken.getAccessToken());
    assertEquals(getOauthTokenResponse.getRefreshToken(), oauthToken.getRefreshToken());
    assertEquals(Arrays.asList(getOauthTokenResponse.getScope().split(" ")), oauthToken.getScopes());
  }

  @Test
  @Transactional
  @DisplayName("access token 발급을 실패하는 테스트 - 없는 organizationId 조회")
  void issueToken_fail() {
    // given
    IssueTokenRequest request = getIssueTokenRequest(oauthTokenEntity.getBanksaladUserId().toString(),
        "non_exist_organizationId",
        oauthTokenEntity.getAuthorizationCode());

    GetOauthTokenResponse getOauthTokenResponse = getExternalTokenResponse();
    when(collectExecutor.execute(any(ExecutionContext.class), any(Execution.class), any(ExecutionRequest.class)))
        .thenReturn(
            ExecutionResponse.builder()
                .httpStatusCode(HttpStatus.OK.value())
                .response(getOauthTokenResponse)
                .build());

    // when, then
    Exception responseException = assertThrows(Exception.class, () -> oauthTokenService.issueToken(request));
    assertThat(responseException).isInstanceOf(ConnectException.class);
    assertEquals(ConnectErrorType.NOT_FOUND_ORGANIZATION.getMessage(), responseException.getMessage());
  }

  @Test
  @Transactional
  @DisplayName("access token 갱신을 성공하는 테스트")
  void refreshToken_success() {
    // given
    RefreshTokenRequest request = getRefreshTokenRequest(oauthTokenEntity.getBanksaladUserId().toString(),
        oauthTokenEntity.getOrganizationId());

    GetOauthTokenResponse getOauthTokenResponse = getExternalTokenResponse();
    when(collectExecutor.execute(any(ExecutionContext.class), any(Execution.class), any(ExecutionRequest.class)))
        .thenReturn(
            ExecutionResponse.builder()
                .httpStatusCode(HttpStatus.OK.value())
                .response(getOauthTokenResponse)
                .build());

    // when
    OauthToken oauthToken = oauthTokenService.refreshToken(request);

    // then
    assertEquals(getOauthTokenResponse.getAccessToken(), oauthToken.getAccessToken());
    assertEquals(Arrays.asList(getOauthTokenResponse.getScope().split(" ")), oauthToken.getScopes());
    assertNull(oauthToken.getRefreshToken());
  }

  @Test
  @Transactional
  @DisplayName("access token 갱신을 실패하는 테스트 - 만료된 refresh token")
  void refreshToken_fail() {
    // given
    OauthTokenEntity oauthTokenEntity = oauthTokenRepository
        .findByBanksaladUserIdAndOrganizationId(BANKSALAD_USER_ID, ORGANIZATION_ID)
        .orElseThrow(() -> new CollectRuntimeException("NOT FOUND OAUTH TOKEN ENTITY"));

    oauthTokenEntity.setAccessTokenExpiresAt(LocalDateTime.now().minusDays(5));
    oauthTokenEntity.setRefreshTokenExpiresAt(LocalDateTime.now().minusDays(5));
    oauthTokenRepository.save(oauthTokenEntity);

    RefreshTokenRequest request = getRefreshTokenRequest(oauthTokenEntity.getBanksaladUserId().toString(),
        oauthTokenEntity.getOrganizationId());

    GetOauthTokenResponse getOauthTokenResponse = getExternalTokenResponse();
    when(collectExecutor.execute(any(ExecutionContext.class), any(Execution.class), any(ExecutionRequest.class)))
        .thenReturn(
            ExecutionResponse.builder()
                .httpStatusCode(HttpStatus.OK.value())
                .response(getOauthTokenResponse)
                .build());

    // when, then
    Exception responseException = assertThrows(Exception.class, () -> oauthTokenService.refreshToken(request));
    assertThat(responseException).isInstanceOf(ConnectException.class);
    assertEquals(ConnectErrorType.EXPIRED_TOKEN.getMessage(), responseException.getMessage());
  }

  @Test
  @Transactional
  @DisplayName("access token 폐기를 성공하는 테스트")
  void revokeToken_success() {
    // given
    RevokeTokenRequest request = getRevokeTokenRequest(oauthTokenEntity.getBanksaladUserId().toString(),
        oauthTokenEntity.getOrganizationId());

    when(collectExecutor.execute(any(ExecutionContext.class), any(Execution.class), any(ExecutionRequest.class)))
        .thenReturn(
            ExecutionResponse.builder()
                .httpStatusCode(HttpStatus.OK.value())
                .build());

    // when
    oauthTokenService.revokeToken(request);

    // then
    assertThrows(GrpcException.class, () -> oauthTokenRepository
        .findByBanksaladUserIdAndOrganizationId(oauthTokenEntity.getBanksaladUserId(),
            request.getOrganizationId())
        .orElseThrow(GrpcException::new));
  }

  @Test
  @Transactional
  @DisplayName("access token 폐기를 실패하는 테스트 - 없는 organizationId 삭제")
  void revokeToken_fail() {
    // given
    RevokeTokenRequest request = getRevokeTokenRequest(oauthTokenEntity.getBanksaladUserId().toString(),
        "non-exist-organizationId");

    when(collectExecutor.execute(any(ExecutionContext.class), any(Execution.class), any(ExecutionRequest.class)))
        .thenReturn(
            ExecutionResponse.builder()
                .httpStatusCode(HttpStatus.OK.value())
                .build());

    // when, then
    Exception responseException = assertThrows(Exception.class, () -> oauthTokenService.revokeToken(request));
    assertThat(responseException).isInstanceOf(ConnectException.class);
    assertEquals(ConnectErrorType.NOT_FOUND_TOKEN.getMessage(), responseException.getMessage());
  }

  @Test
  @Transactional
  @DisplayName("뱅크샐러드 DB에서 토큰 제거 후 기관에 폐기 요청하였으나 예외가 발생한 경우 - 뱅크샐러드 DB에는 토큰 폐기 상태를 유지")
  void revokeToken_transaction_success_external_request_fail() {
    // given
    RevokeTokenRequest request = getRevokeTokenRequest(oauthTokenEntity.getBanksaladUserId().toString(),
        oauthTokenEntity.getOrganizationId());

    when(collectExecutor.execute(any(ExecutionContext.class), any(Execution.class), any(ExecutionRequest.class)))
        .thenThrow(new GrpcException());

    // when, then
    assertThat(oauthTokenRepository
        .findByBanksaladUserIdAndOrganizationId(oauthTokenEntity.getBanksaladUserId(),
            connectOrganizationEntity.getOrganizationId()))
        .isNotEmpty();
    assertThrows(GrpcException.class, () -> oauthTokenService.revokeToken(request));
    assertThat(oauthTokenRepository.findByBanksaladUserIdAndOrganizationId(
        oauthTokenEntity.getBanksaladUserId(), connectOrganizationEntity.getOrganizationId()))
        .isEmpty();
  }

  @Test
  @Transactional
  @DisplayName("모든 access token 폐기를 성공하는 테스트 - 다른 유저의 토큰이 삭제되지 않은 것 포함하여 검증")
  void revokeAllTokens_success() {
    // given
    OauthTokenEntity oauthTokenEntityWithOtherBanksaladUserId = getOauthTokenEntityWithOtherBanksaladUserId();
    oauthTokenRepository.save(oauthTokenEntityWithOtherBanksaladUserId);

    RevokeAllTokensRequest request = getRevokeAllTokensRequest(oauthTokenEntity.getBanksaladUserId().toString());

    when(collectExecutor.execute(any(ExecutionContext.class), any(Execution.class), any(ExecutionRequest.class)))
        .thenReturn(
            ExecutionResponse.builder()
                .httpStatusCode(HttpStatus.OK.value())
                .build());

    List<OauthTokenEntity> BeforeOauthTokenEntitiesWithOtherBanksaladUserId = oauthTokenRepository
        .findAllByBanksaladUserId(oauthTokenEntityWithOtherBanksaladUserId.getBanksaladUserId());

    // when
    oauthTokenService.revokeAllTokens(request);
    List<OauthTokenEntity> AfterOauthTokenEntitiesWithOtherBanksaladUserId = oauthTokenRepository
        .findAllByBanksaladUserId(oauthTokenEntityWithOtherBanksaladUserId.getBanksaladUserId());

    // then
    assertThat(oauthTokenRepository.findAllByBanksaladUserId(oauthTokenEntity.getBanksaladUserId()).isEmpty())
        .isTrue();
    assertEquals(BeforeOauthTokenEntitiesWithOtherBanksaladUserId.size(),
        AfterOauthTokenEntitiesWithOtherBanksaladUserId.size());
  }

  @Test
  @Transactional
  @DisplayName("존재하지않는 banksaladUserId의 토큰 삭제 요청 시 - 예외가 발생하지 않고 아무 처리없이 정상응답")
  void revokeAllTokens_success_non_existing_user_request() {
    // given
    OauthTokenEntity oauthTokenEntityWithDifferentBanksaladUserId = getOauthTokenEntityWithOtherBanksaladUserId();
    RevokeAllTokensRequest request = getRevokeAllTokensRequest(
        oauthTokenEntityWithDifferentBanksaladUserId.getBanksaladUserId().toString());

    when(collectExecutor.execute(any(ExecutionContext.class), any(Execution.class), any(ExecutionRequest.class)))
        .thenThrow(CollectRuntimeException.class);

    // when
    List<OauthTokenEntity> beforeOauthTokenEntities = oauthTokenRepository.findAll();
    oauthTokenService.revokeAllTokens(request);
    List<OauthTokenEntity> afterOauthTokenEntities = oauthTokenRepository.findAll();

    // then
    assertThat(afterOauthTokenEntities).usingRecursiveComparison().isEqualTo(beforeOauthTokenEntities);
  }

  @Test
  @Disabled
  @Transactional
  @DisplayName("유저의 모든 토큰 제거 과정 중간에 특정 기관에서 예외가 발생한 경우 - 해당 예외 발생 전까지의 과정은 정상 트랜잭션 진행 (진행된 뱅크샐러드 DB에는 토큰 폐기 상태를 유지)")
  void revokeAllTokens_transaction_success_external_request_fail() {
    // given
    final int ERROR_INDEX = TOTAL_ORGANIZATION_SIZE - 1; // must be less than TOTAL_ORGANIZATION_COUNT

    initRepository();
    List<Organization> organizations = getOrganizations();
    List<OauthTokenEntity> oauthTokenEntities = oauthTokenRepository.findAll();
    List<ConnectOrganizationEntity> connectOrganizationEntities = connectOrganizationRepository.findAll();

//    doThrow(new GrpcException())
//        .when(collectExecutor)
//        .revokeToken(organizations.get(ERROR_INDEX), oauthTokenEntities.get(ERROR_INDEX).getAccessToken());

    Long banksaladUserId = oauthTokenEntities.get(0).getBanksaladUserId();
    RevokeAllTokensRequest request = getRevokeAllTokensRequest(banksaladUserId.toString());

    // when, then
    for (int i = 0; i < TOTAL_ORGANIZATION_SIZE; i++) {
      String errorOrganizationId = connectOrganizationEntities.get(i).getOrganizationId();
      assertThat(oauthTokenRepository
          .findByBanksaladUserIdAndOrganizationId(banksaladUserId, errorOrganizationId))
          .isNotEmpty();
    }
    assertThrows(GrpcException.class, () -> oauthTokenService.revokeAllTokens(request));

    for (int i = 0; i <= ERROR_INDEX; i++) {
      String errorOrganizationId = connectOrganizationEntities.get(i).getOrganizationId();
      assertThat(oauthTokenRepository
          .findByBanksaladUserIdAndOrganizationId(banksaladUserId, errorOrganizationId))
          .isEmpty();
    }
    for (int i = ERROR_INDEX + 1; i < TOTAL_ORGANIZATION_SIZE; i++) {
      String errorOrganizationId = connectOrganizationEntities.get(i).getOrganizationId();
      assertThat(oauthTokenRepository
          .findByBanksaladUserIdAndOrganizationId(banksaladUserId, errorOrganizationId))
          .isNotEmpty();
    }
  }

  private void initRepository() {
    List<OauthTokenEntity> oauthTokenEntities = new ArrayList<>();
    List<ConnectOrganizationEntity> connectOrganizationEntities = new ArrayList<>();

    for (int i = 0; i < TOTAL_ORGANIZATION_SIZE; i++) {
      OauthTokenEntity oauthTokenEntity = OauthTokenEntity.builder()
          .syncedAt(LocalDateTime.now().minusDays(1))
          .banksaladUserId(BANKSALAD_USER_ID)
          .organizationId(ORGANIZATION_ID + i)
          .consentId(CONSENT_ID)
          .authorizationCode(AUTHORIZATION_CODE)
          .accessToken(ACCESS_TOKEN)
          .refreshToken(REFRESH_TOKEN)
          .accessTokenExpiresAt(ACCESS_TOKEN_EXPIRES_AT)
          .accessTokenExpiresIn(ACCESS_TOKEN_EXPIRES_IN)
          .refreshTokenExpiresAt(REFRESH_TOKEN_EXPIRES_AT)
          .refreshTokenExpiresIn(REFRESH_TOKEN_EXPIRES_IN)
          .tokenType(TOKEN_TYPE)
          .scope(SCOPE)
          .issuedAt(LocalDateTime.now().minusDays(1))
          .refreshedAt(LocalDateTime.now().minusDays(1))
          .build();
      ConnectOrganizationEntity connectOrganizationEntity = ConnectOrganizationEntity.builder()
          .sector(SECTOR)
          .industry(INDUSTRY)
          .organizationId(ORGANIZATION_ID + i)
          .organizationObjectid(ORGANIZATION_OBJECT_ID)
          .orgCode(ORGANIZATION_CODE)
          .organizationStatus(ORGANIZATION_STATUS)
          .deleted(false)
          .build();

      oauthTokenEntities.add(oauthTokenEntity);
      connectOrganizationEntities.add(connectOrganizationEntity);
    }
    oauthTokenRepository.saveAll(oauthTokenEntities);
    connectOrganizationRepository.saveAll(connectOrganizationEntities);
  }

  private OauthTokenEntity getOauthTokenEntity() {
    return OauthTokenEntity.builder()
        .syncedAt(LocalDateTime.now().minusDays(1))
        .banksaladUserId(BANKSALAD_USER_ID)
        .organizationId(ORGANIZATION_ID)
        .consentId(CONSENT_ID)
        .authorizationCode(AUTHORIZATION_CODE)
        .accessToken(ACCESS_TOKEN)
        .refreshToken(REFRESH_TOKEN)
        .accessTokenExpiresAt(ACCESS_TOKEN_EXPIRES_AT)
        .accessTokenExpiresIn(ACCESS_TOKEN_EXPIRES_IN)
        .refreshTokenExpiresAt(REFRESH_TOKEN_EXPIRES_AT)
        .refreshTokenExpiresIn(REFRESH_TOKEN_EXPIRES_IN)
        .tokenType(TOKEN_TYPE)
        .scope(SCOPE)
        .issuedAt(LocalDateTime.now().minusDays(1))
        .refreshedAt(LocalDateTime.now().minusDays(1))
        .build();
  }

  private OauthTokenEntity getOauthTokenEntityWithOtherBanksaladUserId() {
    final long NEW_BANKSALAD_USER_ID = 2L;
    return OauthTokenEntity.builder()
        .syncedAt(LocalDateTime.now().minusDays(1))
        .banksaladUserId(NEW_BANKSALAD_USER_ID)
        .organizationId(ORGANIZATION_ID)
        .consentId(CONSENT_ID)
        .authorizationCode(AUTHORIZATION_CODE)
        .accessToken(ACCESS_TOKEN)
        .refreshToken(REFRESH_TOKEN)
        .accessTokenExpiresAt(ACCESS_TOKEN_EXPIRES_AT)
        .accessTokenExpiresIn(ACCESS_TOKEN_EXPIRES_IN)
        .refreshTokenExpiresAt(REFRESH_TOKEN_EXPIRES_AT)
        .refreshTokenExpiresIn(REFRESH_TOKEN_EXPIRES_IN)
        .tokenType(TOKEN_TYPE)
        .scope(SCOPE)
        .issuedAt(LocalDateTime.now().minusDays(1))
        .refreshedAt(LocalDateTime.now().minusDays(1))
        .build();
  }

  private ConnectOrganizationEntity getConnectOrganizationEntity() {
    return ConnectOrganizationEntity.builder()
        .sector(SECTOR)
        .industry(INDUSTRY)
        .organizationId(ORGANIZATION_ID)
        .organizationObjectid(ORGANIZATION_OBJECT_ID)
        .orgCode(ORGANIZATION_CODE)
        .organizationStatus(ORGANIZATION_STATUS)
        .deleted(FALSE)
        .build();
  }

  private List<Organization> getOrganizations() {
    List<Organization> organizations = new ArrayList<>();
    for (int i = 0; i < TOTAL_ORGANIZATION_SIZE; i++) {
      Organization organization = Organization.builder()
          .sector(SECTOR)
          .industry(INDUSTRY)
          .organizationId(ORGANIZATION_ID + i)
          .organizationCode(ORGANIZATION_CODE)
          .domain(DOMAIN)
          .build();
      organizations.add(organization);
    }
    return organizations;
  }

  private BanksaladClientSecretEntity getBanksaladClientSecretEntity() {
    return BanksaladClientSecretEntity.builder()
        .secretType(SECTOR)
        .clientId(CLIENT_ID)
        .clientSecret(CLIENT_SECRET)
        .build();
  }

  private GetOauthTokenResponse getExternalTokenResponse() {
    return GetOauthTokenResponse.builder()
        .tokenType(TOKEN_TYPE)
        .accessToken(ACCESS_TOKEN)
        .expiresIn(ACCESS_TOKEN_EXPIRES_IN)
        .refreshToken(REFRESH_TOKEN)
        .refreshTokenExpiresIn(REFRESH_TOKEN_EXPIRES_IN)
        .scope(SCOPE)
        .build();
  }

  private GetAccessTokenRequest getAccessTokenRequest(String banksaladId, String organizationId) {
    return GetAccessTokenRequest.newBuilder()
        .setBanksaladUserId(banksaladId)
        .setOrganizationId(organizationId)
        .build();
  }

  private IssueTokenRequest getIssueTokenRequest(String banksaladId, String organizationId,
      String authorizationCode) {
    return IssueTokenRequest.newBuilder()
        .setBanksaladUserId(banksaladId)
        .setOrganizationId(organizationId)
        .setAuthorizationCode(authorizationCode)
        .build();
  }

  private RefreshTokenRequest getRefreshTokenRequest(String banksaladId, String organizationId) {
    return RefreshTokenRequest.newBuilder()
        .setBanksaladUserId(banksaladId)
        .setOrganizationId(organizationId)
        .build();
  }

  private RevokeTokenRequest getRevokeTokenRequest(String banksaladId, String organizationId) {
    return RevokeTokenRequest.newBuilder()
        .setBanksaladUserId(banksaladId)
        .setOrganizationId(organizationId)
        .build();
  }

  private RevokeAllTokensRequest getRevokeAllTokensRequest(String banksaladId) {
    return RevokeAllTokensRequest.newBuilder()
        .setBanksaladUserId(banksaladId)
        .build();
  }
}
