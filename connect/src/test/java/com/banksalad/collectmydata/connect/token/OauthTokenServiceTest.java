package com.banksalad.collectmydata.connect.token;

import com.banksalad.collectmydata.common.collect.execution.Execution;
import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.collect.execution.ExecutionRequest;
import com.banksalad.collectmydata.common.collect.execution.ExecutionResponse;
import com.banksalad.collectmydata.common.collect.executor.CollectExecutor;
import com.banksalad.collectmydata.common.exception.CollectRuntimeException;
import com.banksalad.collectmydata.common.exception.GrpcException;
import com.banksalad.collectmydata.connect.collect.Executions;
import com.banksalad.collectmydata.connect.common.db.entity.BanksaladClientSecretEntity;
import com.banksalad.collectmydata.connect.common.db.entity.ConnectOrganizationEntity;
import com.banksalad.collectmydata.connect.common.db.entity.ConsentEntity;
import com.banksalad.collectmydata.connect.common.db.entity.OauthTokenEntity;
import com.banksalad.collectmydata.connect.common.db.repository.BanksaladClientSecretRepository;
import com.banksalad.collectmydata.connect.common.db.repository.ConnectOrganizationRepository;
import com.banksalad.collectmydata.connect.common.db.repository.ConsentRepository;
import com.banksalad.collectmydata.connect.common.db.repository.OauthTokenRepository;
import com.banksalad.collectmydata.connect.common.dto.Consent;
import com.banksalad.collectmydata.connect.common.dto.GetConsentResponse;
import com.banksalad.collectmydata.connect.common.enums.ConnectErrorType;
import com.banksalad.collectmydata.connect.common.exception.ConnectException;
import com.banksalad.collectmydata.connect.organization.dto.Organization;
import com.banksalad.collectmydata.connect.token.dto.GetOauthTokenResponse;
import com.banksalad.collectmydata.connect.token.dto.OauthToken;
import com.banksalad.collectmydata.connect.token.service.OauthTokenService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;

import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydataconnectProto.GetAccessTokenRequest;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydataconnectProto.IssueTokenRequest;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydataconnectProto.RefreshTokenRequest;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydataconnectProto.RevokeAllTokensRequest;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydataconnectProto.RevokeTokenRequest;
import com.github.banksalad.idl.apis.v1.collectschedule.CollectScheduleProto.UnregisterScheduledSyncResponse;
import com.github.banksalad.idl.apis.v1.collectschedule.CollectscheduleGrpc.CollectscheduleBlockingStub;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static com.banksalad.collectmydata.connect.common.ConnectConstant.ACCESS_TOKEN;
import static com.banksalad.collectmydata.connect.common.ConnectConstant.ACCESS_TOKEN_EXPIRES_AT;
import static com.banksalad.collectmydata.connect.common.ConnectConstant.ACCESS_TOKEN_EXPIRES_IN;
import static com.banksalad.collectmydata.connect.common.ConnectConstant.AUTHORIZATION_CODE;
import static com.banksalad.collectmydata.connect.common.ConnectConstant.BANKSALAD_USER_ID;
import static com.banksalad.collectmydata.connect.common.ConnectConstant.CLIENT_ID;
import static com.banksalad.collectmydata.connect.common.ConnectConstant.CLIENT_SECRET;
import static com.banksalad.collectmydata.connect.common.ConnectConstant.CONSENT_ID;
import static com.banksalad.collectmydata.connect.common.ConnectConstant.DOMAIN;
import static com.banksalad.collectmydata.connect.common.ConnectConstant.INDUSTRY;
import static com.banksalad.collectmydata.connect.common.ConnectConstant.ORGANIZATION_CODE;
import static com.banksalad.collectmydata.connect.common.ConnectConstant.ORGANIZATION_GUID;
import static com.banksalad.collectmydata.connect.common.ConnectConstant.ORGANIZATION_ID;
import static com.banksalad.collectmydata.connect.common.ConnectConstant.ORGANIZATION_STATUS;
import static com.banksalad.collectmydata.connect.common.ConnectConstant.REFRESH_TOKEN;
import static com.banksalad.collectmydata.connect.common.ConnectConstant.REFRESH_TOKEN_EXPIRES_AT;
import static com.banksalad.collectmydata.connect.common.ConnectConstant.REFRESH_TOKEN_EXPIRES_IN;
import static com.banksalad.collectmydata.connect.common.ConnectConstant.SCOPE;
import static com.banksalad.collectmydata.connect.common.ConnectConstant.SECTOR;
import static com.banksalad.collectmydata.connect.common.ConnectConstant.TOKEN_TYPE;
import static java.lang.Boolean.FALSE;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@Transactional
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

  @Autowired
  private ConsentRepository consentRepository;

  @MockBean
  private CollectExecutor collectExecutor;

  @MockBean
  private CollectscheduleBlockingStub collectscheduleBlockingStub;

  private OauthTokenEntity oauthTokenEntity;
  private ConsentEntity consentEntity;
  private ConnectOrganizationEntity connectOrganizationEntity;
  private BanksaladClientSecretEntity banksaladClientSecretEntity;

  private final int TOTAL_ORGANIZATION_SIZE = 5;

  @BeforeEach
  void setUp() {
    oauthTokenEntity = getOauthTokenEntity();
    consentEntity = getConsentEntity();
    connectOrganizationEntity = getConnectOrganizationEntity();
    banksaladClientSecretEntity = getBanksaladClientSecretEntity();
    oauthTokenRepository.save(oauthTokenEntity);
    connectOrganizationRepository.save(connectOrganizationEntity);
    banksaladClientSecretRepository.save(banksaladClientSecretEntity);
  }

  @Test
  @DisplayName("access token ????????? ???????????? ?????????")
  void getAccessToken_success() {
    // given
    GetAccessTokenRequest request = getAccessTokenRequest(oauthTokenEntity.getBanksaladUserId().toString(),
        oauthTokenEntity.getOrganizationId());
    long banksaladUserId = Long.parseLong(request.getBanksaladUserId());

    // when
    OauthToken oauthToken = oauthTokenService.getAccessToken(banksaladUserId, request.getOrganizationId());

    // then
    assertEquals(oauthTokenEntity.getAccessToken(), oauthToken.getAccessToken());
    assertEquals(Arrays.asList(oauthTokenEntity.getScope().split(" ")), oauthToken.getScopes());
    assertNull(oauthToken.getRefreshToken());
  }

  @Test
  @DisplayName("access token ????????? ???????????? ????????? - ?????? banksaladUserId ??????")
  void getAccessToken_fail() {
    // given
    Long NonExistBanksaladUserId = 98765L;
    GetAccessTokenRequest request = getAccessTokenRequest(NonExistBanksaladUserId.toString(),
        oauthTokenEntity.getOrganizationId());
    long banksaladUserId = Long.parseLong(request.getBanksaladUserId());

    // when, then
    Exception responseException = assertThrows(Exception.class,
        () -> oauthTokenService.getAccessToken(banksaladUserId, request.getOrganizationId()));
    assertThat(responseException).isInstanceOf(ConnectException.class);
    assertEquals(ConnectErrorType.NOT_FOUND_TOKEN.getMessage(), responseException.getMessage());
  }

  @Test
  @DisplayName("access token ?????? ????????? ?????? ???????????? ?????????")
  void getAccessToken_refresh() {
    // given
    OauthTokenEntity oauthTokenEntity = oauthTokenRepository
        .findByBanksaladUserIdAndOrganizationId(BANKSALAD_USER_ID, ORGANIZATION_ID)
        .orElseThrow(() -> new CollectRuntimeException("NOT FOUND OAUTH TOKEN ENTITY"));

    oauthTokenEntity.setAccessTokenExpiresAt(LocalDateTime.now().minusDays(5));
    oauthTokenRepository.save(oauthTokenEntity);

    GetAccessTokenRequest request = getAccessTokenRequest(oauthTokenEntity.getBanksaladUserId().toString(),
        oauthTokenEntity.getOrganizationId());
    long banksaladUserId = Long.parseLong(request.getBanksaladUserId());

    GetOauthTokenResponse getOauthTokenResponse = getExternalTokenResponse();
    when(collectExecutor.execute(any(ExecutionContext.class), any(Execution.class), any(ExecutionRequest.class)))
        .thenReturn(
            ExecutionResponse.builder()
                .httpStatusCode(HttpStatus.OK.value())
                .response(getOauthTokenResponse)
                .build());

    // when
    OauthToken oauthToken = oauthTokenService.getAccessToken(banksaladUserId, request.getOrganizationId());

    // then
    assertEquals(getOauthTokenResponse.getAccessToken(), oauthToken.getAccessToken());
    assertEquals(Arrays.asList(getOauthTokenResponse.getScope().split(" ")), oauthToken.getScopes());
    assertNull(oauthToken.getRefreshToken());
  }

  @Test
  @DisplayName("access token ????????? ???????????? ?????????")
  void issueToken_success() {
    // given
    IssueTokenRequest request = getIssueTokenRequest(oauthTokenEntity.getBanksaladUserId().toString(),
        oauthTokenEntity.getOrganizationId(),
        oauthTokenEntity.getAuthorizationCode());
    long banksaladUserId = Long.parseLong(request.getBanksaladUserId());

    GetOauthTokenResponse getOauthTokenResponse = getExternalTokenResponse();

    GetConsentResponse getConsentResponse = GetConsentResponse.builder()
        .rspCode("00000")
        .rspMsg("success")
        .consent(getConsent())
        .build();

    when(collectExecutor
        .execute(any(ExecutionContext.class), eq(Executions.common_consent), any(ExecutionRequest.class)))
        .thenReturn(
            ExecutionResponse.builder()
                .httpStatusCode(HttpStatus.OK.value())
                .response(getConsentResponse)
                .build());

    when(collectExecutor
        .execute(any(ExecutionContext.class), eq(Executions.oauth_issue_token), any(ExecutionRequest.class)))
        .thenReturn(
            ExecutionResponse.builder()
                .httpStatusCode(HttpStatus.OK.value())
                .response(getOauthTokenResponse)
                .build());

    // when
    OauthToken oauthToken = oauthTokenService
        .issueToken(banksaladUserId, request.getOrganizationId(), request.getAuthorizationCode());

    // then
    assertEquals(getOauthTokenResponse.getAccessToken(), oauthToken.getAccessToken());
    assertEquals(getOauthTokenResponse.getRefreshToken(), oauthToken.getRefreshToken());
    assertEquals(Arrays.asList(getOauthTokenResponse.getScope().split(" ")), oauthToken.getScopes());
  }

  @Test
  @DisplayName("access token ????????? ???????????? ????????? - ?????? organizationId ??????")
  void issueToken_fail() {
    // given
    IssueTokenRequest request = getIssueTokenRequest(oauthTokenEntity.getBanksaladUserId().toString(),
        "non_exist_organizationId", oauthTokenEntity.getAuthorizationCode());
    long banksaladUserId = Long.parseLong(request.getBanksaladUserId());

    GetOauthTokenResponse getOauthTokenResponse = getExternalTokenResponse();
    when(collectExecutor.execute(any(ExecutionContext.class), any(Execution.class), any(ExecutionRequest.class)))
        .thenReturn(
            ExecutionResponse.builder()
                .httpStatusCode(HttpStatus.OK.value())
                .response(getOauthTokenResponse)
                .build());

    // when, then
    Exception responseException = assertThrows(Exception.class, () -> oauthTokenService
        .issueToken(banksaladUserId, request.getOrganizationId(), request.getAuthorizationCode()));
    assertThat(responseException).isInstanceOf(ConnectException.class);
    assertEquals(ConnectErrorType.NOT_FOUND_ORGANIZATION.getMessage(), responseException.getMessage());
  }

  @Test
  @DisplayName("access token ????????? ???????????? ?????????")
  void refreshToken_success() {
    // given
    RefreshTokenRequest request = getRefreshTokenRequest(oauthTokenEntity.getBanksaladUserId().toString(),
        oauthTokenEntity.getOrganizationId());
    long banksaladUserId = Long.parseLong(request.getBanksaladUserId());

    GetOauthTokenResponse getOauthTokenResponse = getExternalTokenResponse();
    when(collectExecutor.execute(any(ExecutionContext.class), any(Execution.class), any(ExecutionRequest.class)))
        .thenReturn(
            ExecutionResponse.builder()
                .httpStatusCode(HttpStatus.OK.value())
                .response(getOauthTokenResponse)
                .build());

    // when
    OauthToken oauthToken = oauthTokenService.refreshToken(banksaladUserId, request.getOrganizationId());

    // then
    assertEquals(getOauthTokenResponse.getAccessToken(), oauthToken.getAccessToken());
    assertEquals(Arrays.asList(getOauthTokenResponse.getScope().split(" ")), oauthToken.getScopes());
    assertNull(oauthToken.getRefreshToken());
  }

  @Test
  @DisplayName("access token ????????? ???????????? ????????? - ????????? refresh token")
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
    long banksaladUserId = Long.parseLong(request.getBanksaladUserId());

    GetOauthTokenResponse getOauthTokenResponse = getExternalTokenResponse();
    when(collectExecutor.execute(any(ExecutionContext.class), any(Execution.class), any(ExecutionRequest.class)))
        .thenReturn(
            ExecutionResponse.builder()
                .httpStatusCode(HttpStatus.OK.value())
                .response(getOauthTokenResponse)
                .build());

    // when, then
    Exception responseException = assertThrows(Exception.class,
        () -> oauthTokenService.refreshToken(banksaladUserId, request.getOrganizationId()));
    assertThat(responseException).isInstanceOf(ConnectException.class);
    assertEquals(ConnectErrorType.EXPIRED_TOKEN.getMessage(), responseException.getMessage());
  }

  @Test
  @DisplayName("access token ????????? ???????????? ?????????")
  void revokeToken_success() {
    // given
    RevokeTokenRequest request = getRevokeTokenRequest(oauthTokenEntity.getBanksaladUserId().toString(),
        oauthTokenEntity.getOrganizationId());
    long banksaladUserId = Long.parseLong(request.getBanksaladUserId());

    when(collectExecutor.execute(any(ExecutionContext.class), any(Execution.class), any(ExecutionRequest.class)))
        .thenReturn(
            ExecutionResponse.builder()
                .httpStatusCode(HttpStatus.OK.value())
                .build());

    when(collectscheduleBlockingStub.unregisterScheduledSync(any()))
        .thenReturn(UnregisterScheduledSyncResponse.getDefaultInstance());

    // when
    oauthTokenService.revokeToken(banksaladUserId, request.getOrganizationId());

    // then
    assertThrows(GrpcException.class, () -> oauthTokenRepository
        .findByBanksaladUserIdAndOrganizationId(oauthTokenEntity.getBanksaladUserId(),
            request.getOrganizationId())
        .orElseThrow(GrpcException::new));
  }

  @Test
  @DisplayName("access token ????????? ???????????? ????????? - ?????? organizationId ??????")
  void revokeToken_fail() {
    // given
    RevokeTokenRequest request = getRevokeTokenRequest(oauthTokenEntity.getBanksaladUserId().toString(),
        "non-exist-organizationId");
    long banksaladUserId = Long.parseLong(request.getBanksaladUserId());

    when(collectExecutor.execute(any(ExecutionContext.class), any(Execution.class), any(ExecutionRequest.class)))
        .thenReturn(
            ExecutionResponse.builder()
                .httpStatusCode(HttpStatus.OK.value())
                .build());

    // when, then
    Exception responseException = assertThrows(Exception.class,
        () -> oauthTokenService.revokeToken(banksaladUserId, request.getOrganizationId()));
    assertThat(responseException).isInstanceOf(ConnectException.class);
    assertEquals(ConnectErrorType.NOT_FOUND_TOKEN.getMessage(), responseException.getMessage());
  }

  @Test
  @DisplayName("??????????????? DB?????? ?????? ?????? ??? ????????? ?????? ?????????????????? ????????? ????????? ?????? - ??????????????? DB?????? ?????? ?????? ????????? ??????")
  void revokeToken_transaction_success_external_request_fail() {
    // given
    RevokeTokenRequest request = getRevokeTokenRequest(oauthTokenEntity.getBanksaladUserId().toString(),
        oauthTokenEntity.getOrganizationId());
    long banksaladUserId = Long.parseLong(request.getBanksaladUserId());

    when(collectExecutor.execute(any(ExecutionContext.class), any(Execution.class), any(ExecutionRequest.class)))
        .thenThrow(new GrpcException());

    // when, then
    assertThat(oauthTokenRepository
        .findByBanksaladUserIdAndOrganizationId(oauthTokenEntity.getBanksaladUserId(),
            connectOrganizationEntity.getOrganizationId()))
        .isNotEmpty();
    assertThrows(GrpcException.class,
        () -> oauthTokenService.revokeToken(banksaladUserId, request.getOrganizationId()));
    assertThat(oauthTokenRepository.findByBanksaladUserIdAndOrganizationId(
        oauthTokenEntity.getBanksaladUserId(), connectOrganizationEntity.getOrganizationId()))
        .isEmpty();
  }

  @Test
  @DisplayName("?????? access token ????????? ???????????? ????????? - ?????? ????????? ????????? ???????????? ?????? ??? ???????????? ??????")
  void revokeAllTokens_success() {
    // given
    OauthTokenEntity oauthTokenEntityWithOtherBanksaladUserId = getOauthTokenEntityWithOtherBanksaladUserId();
    oauthTokenRepository.save(oauthTokenEntityWithOtherBanksaladUserId);

    RevokeAllTokensRequest request = getRevokeAllTokensRequest(oauthTokenEntity.getBanksaladUserId().toString());
    long banksaladUserId = Long.parseLong(request.getBanksaladUserId());

    when(collectExecutor.execute(any(ExecutionContext.class), any(Execution.class), any(ExecutionRequest.class)))
        .thenReturn(
            ExecutionResponse.builder()
                .httpStatusCode(HttpStatus.OK.value())
                .build());

    List<OauthTokenEntity> BeforeOauthTokenEntitiesWithOtherBanksaladUserId = oauthTokenRepository
        .findAllByBanksaladUserId(oauthTokenEntityWithOtherBanksaladUserId.getBanksaladUserId());

    // when
    oauthTokenService.revokeAllTokens(banksaladUserId);
    List<OauthTokenEntity> AfterOauthTokenEntitiesWithOtherBanksaladUserId = oauthTokenRepository
        .findAllByBanksaladUserId(oauthTokenEntityWithOtherBanksaladUserId.getBanksaladUserId());

    // then
    assertThat(oauthTokenRepository.findAllByBanksaladUserId(oauthTokenEntity.getBanksaladUserId()).isEmpty())
        .isTrue();
    assertEquals(BeforeOauthTokenEntitiesWithOtherBanksaladUserId.size(),
        AfterOauthTokenEntitiesWithOtherBanksaladUserId.size());
  }

  @Test
  @DisplayName("?????????????????? banksaladUserId??? ?????? ?????? ?????? ??? - ????????? ???????????? ?????? ?????? ???????????? ????????????")
  void revokeAllTokens_success_non_existing_user_request() {
    // given
    OauthTokenEntity oauthTokenEntityWithDifferentBanksaladUserId = getOauthTokenEntityWithOtherBanksaladUserId();
    RevokeAllTokensRequest request = getRevokeAllTokensRequest(
        oauthTokenEntityWithDifferentBanksaladUserId.getBanksaladUserId().toString());
    long banksaladUserId = Long.parseLong(request.getBanksaladUserId());

    when(collectExecutor.execute(any(ExecutionContext.class), any(Execution.class), any(ExecutionRequest.class)))
        .thenThrow(CollectRuntimeException.class);

    // when
    List<OauthTokenEntity> beforeOauthTokenEntities = oauthTokenRepository.findAll();
    oauthTokenService.revokeAllTokens(banksaladUserId);
    List<OauthTokenEntity> afterOauthTokenEntities = oauthTokenRepository.findAll();

    // then
    assertThat(afterOauthTokenEntities).usingRecursiveComparison().isEqualTo(beforeOauthTokenEntities);
  }

//  @Test
//  @Disabled
//  @DisplayName("????????? ?????? ?????? ?????? ?????? ????????? ?????? ???????????? ????????? ????????? ?????? - ?????? ?????? ?????? ???????????? ????????? ?????? ???????????? ?????? (????????? ??????????????? DB?????? ?????? ?????? ????????? ??????)")
//  void revokeAllTokens_transaction_success_external_request_fail() {
//    // given
//    final int ERROR_INDEX = TOTAL_ORGANIZATION_SIZE - 1; // must be less than TOTAL_ORGANIZATION_COUNT
//
//    initRepository();
//    List<Organization> organizations = getOrganizations();
//    List<OauthTokenEntity> oauthTokenEntities = oauthTokenRepository.findAll();
//    List<ConnectOrganizationEntity> connectOrganizationEntities = connectOrganizationRepository.findAll();
//
////    doThrow(new GrpcException())
////        .when(collectExecutor)
////        .revokeToken(organizations.get(ERROR_INDEX), oauthTokenEntities.get(ERROR_INDEX).getAccessToken());
//
//    Long banksaladUserId = oauthTokenEntities.get(0).getBanksaladUserId();
//    RevokeAllTokensRequest request = getRevokeAllTokensRequest(banksaladUserId.toString());
//
//    // when, then
//    for (int i = 0; i < TOTAL_ORGANIZATION_SIZE; i++) {
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
//    for (int i = ERROR_INDEX + 1; i < TOTAL_ORGANIZATION_SIZE; i++) {
//      String errorOrganizationId = connectOrganizationEntities.get(i).getOrganizationId();
//      assertThat(oauthTokenRepository
//          .findByBanksaladUserIdAndOrganizationId(banksaladUserId, errorOrganizationId))
//          .isNotEmpty();
//    }
//  }

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
          .organizationGuid(ORGANIZATION_GUID)
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

  private ConsentEntity getConsentEntity() {
    return consentRepository.save(ConsentEntity.builder()
        .syncedAt(LocalDateTime.now())
        .banksaladUserId(BANKSALAD_USER_ID)
        .organizationId(ORGANIZATION_ID)
        .consentId(UUID.randomUUID().toString())
        .scheduled(true)
        .cycle("07")
        .endDate("20210423")
        .purpose("purpose")
        .period(90)
        .build());
  }

  private Consent getConsent() {
    return Consent.builder()
        .scheduled(true)
        .cycle("07")
        .endDate("20210423")
        .purpose("purpose")
        .period(90)
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
        .organizationGuid(ORGANIZATION_GUID)
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
