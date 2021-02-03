package com.banksalad.collectmydata.connect.token.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import com.banksalad.collectmydata.common.exception.collectMydataException.NotFoundOrganizationException;
import com.banksalad.collectmydata.common.exception.collectMydataException.NotFoundTokenException;
import com.banksalad.collectmydata.connect.common.db.entity.OauthTokenEntity;
import com.banksalad.collectmydata.connect.common.db.entity.OrganizationInfoEntity;
import com.banksalad.collectmydata.connect.common.db.repository.OauthTokenRepository;
import com.banksalad.collectmydata.connect.common.db.repository.OrganizationInfoRepository;
import com.banksalad.collectmydata.connect.token.dto.ExternalTokenResponse;
import com.banksalad.collectmydata.connect.token.dto.OauthToken;
import com.github.banksalad.idl.apis.v1.connectmydata.ConnectmydataProto.GetAccessTokenRequest;
import com.github.banksalad.idl.apis.v1.connectmydata.ConnectmydataProto.IssueTokenRequest;
import com.github.banksalad.idl.apis.v1.connectmydata.ConnectmydataProto.RefreshTokenRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@SpringBootTest
@DisplayName("OauthTokenService Test")
class OauthTokenServiceTest {

  @Autowired
  private OauthTokenService oauthTokenService;

  @Autowired
  private OauthTokenRepository oauthTokenRepository;

  @Autowired
  private OrganizationInfoRepository organizationInfoRepository;

  @MockBean
  private ExternalTokenService externalTokenService;

  @Test
  @Transactional
  @DisplayName("access token 조회를 성공하는 테스트")
  public void getAccessToken_success() {
    // given
    OauthTokenEntity oauthTokenEntity = getOauthTokenEntity();
    oauthTokenRepository.save(oauthTokenEntity);

    // when
    GetAccessTokenRequest request = getAccessTokenRequest(oauthTokenEntity.getBanksaladUserId().toString(),
        oauthTokenEntity.getOrganizationId());
    OauthToken oauthToken = oauthTokenService.getAccessToken(request);

    // then
    assertEquals(oauthTokenEntity.getAccessToken(), oauthToken.getAccessToken());
    assertEquals(oauthTokenEntity.getParseScope(), oauthToken.getScopes());
    assertNull(oauthToken.getRefreshToken());
  }

  @Test
  @Transactional
  @DisplayName("access token 조회를 실패하는 테스트 - 없는 banksaladUserId 조회")
  public void getAccessToken_fail() {
    // given
    OauthTokenEntity oauthTokenEntity = getOauthTokenEntity();
    oauthTokenRepository.save(oauthTokenEntity);

    // when
    Long NonExistBanksaladUserId = 98765L;
    GetAccessTokenRequest request = getAccessTokenRequest(NonExistBanksaladUserId.toString(),
        oauthTokenEntity.getOrganizationId());

    // then
    assertThrows(NotFoundTokenException.class, () -> oauthTokenService.getAccessToken(request));
  }

  @Test
  @Transactional
  @DisplayName("access token 기한 만료로 토큰 갱신하는 테스트")
  public void getAccessToken_refresh() {
    // given
    OauthTokenEntity oauthTokenEntity = getAccessTokenExpiredOauthTokenEntity();
    oauthTokenRepository.save(oauthTokenEntity);
    OrganizationInfoEntity organizationInfoEntity = getOrganizationInfoEntity();
    organizationInfoRepository.save(organizationInfoEntity);

    // when
    GetAccessTokenRequest accessTokenRequest = getAccessTokenRequest(oauthTokenEntity.getBanksaladUserId().toString(),
        oauthTokenEntity.getOrganizationId());
    ExternalTokenResponse externalTokenResponse = getExternalTokenResponse();

    when(externalTokenService.refreshToken(organizationInfoEntity.getOrganizationCode(), oauthTokenEntity.getRefreshToken()))
        .thenReturn(externalTokenResponse);
    OauthToken oauthToken = oauthTokenService.getAccessToken(accessTokenRequest);

    // then
    assertEquals(externalTokenResponse.getAccessToken(), oauthToken.getAccessToken());
    assertEquals(Arrays.asList(externalTokenResponse.getScope().split(" ")), oauthToken.getScopes());
    assertNull(oauthToken.getRefreshToken());
  }

  @Test
  @Transactional
  @DisplayName("access token 발급을 성공하는 테스트")
  public void issueToken_success() {
    // given
    OauthTokenEntity oauthTokenEntity = getOauthTokenEntity();
    oauthTokenRepository.save(oauthTokenEntity);
    OrganizationInfoEntity organizationInfoEntity = getOrganizationInfoEntity();
    organizationInfoRepository.save(organizationInfoEntity);

    IssueTokenRequest request = getIssueTokenRequest(oauthTokenEntity.getBanksaladUserId().toString(),
        oauthTokenEntity.getOrganizationId(),
        oauthTokenEntity.getAuthorizationCode());

    // when
    ExternalTokenResponse externalTokenResponse = getExternalTokenResponse();
    when(externalTokenService.issueToken(organizationInfoEntity.getOrganizationCode(), request.getAuthorizationCode()))
        .thenReturn(externalTokenResponse);
    OauthToken oauthToken = oauthTokenService.issueToken(request);

    // then
    assertEquals(externalTokenResponse.getAccessToken(), oauthToken.getAccessToken());
    assertEquals(externalTokenResponse.getRefreshToken(), oauthToken.getRefreshToken());
    assertEquals(Arrays.asList(externalTokenResponse.getScope().split(" ")), oauthToken.getScopes());
  }

  @Test
  @Transactional
  @DisplayName("access token 발급을 실패하는 테스트 - 없는 organizationId 조회")
  public void issueToken_fail() {
    // given
    OauthTokenEntity oauthTokenEntity = getOauthTokenEntity();
    oauthTokenRepository.save(oauthTokenEntity);
    OrganizationInfoEntity organizationInfoEntity = getOrganizationInfoEntity();
    organizationInfoRepository.save(organizationInfoEntity);

    IssueTokenRequest request = getIssueTokenRequest(oauthTokenEntity.getBanksaladUserId().toString(),
        "non_exist_organizationId",
        oauthTokenEntity.getAuthorizationCode());

    // when
    ExternalTokenResponse externalTokenResponse = getExternalTokenResponse();
    when(externalTokenService.issueToken(organizationInfoEntity.getOrganizationCode(), request.getAuthorizationCode()))
        .thenReturn(externalTokenResponse);

    // then
    assertThrows(NotFoundOrganizationException.class, () -> oauthTokenService.issueToken(request));
  }

  @Test
  @Transactional
  @DisplayName("access token 갱신을 성공하는 테스트")
  public void refreshToken_success() {
    // given
    OauthTokenEntity oauthTokenEntity = getOauthTokenEntity();
    oauthTokenRepository.save(oauthTokenEntity);
    OrganizationInfoEntity organizationInfoEntity = getOrganizationInfoEntity();
    organizationInfoRepository.save(organizationInfoEntity);

    RefreshTokenRequest request = getRefreshTokenRequest(oauthTokenEntity.getBanksaladUserId().toString(),
        oauthTokenEntity.getOrganizationId());

    // when
    ExternalTokenResponse externalTokenResponse = getExternalTokenResponse();
    when(externalTokenService.refreshToken(organizationInfoEntity.getOrganizationCode(), oauthTokenEntity.getRefreshToken()))
        .thenReturn(externalTokenResponse);
    OauthToken oauthToken = oauthTokenService.refreshToken(request);

    // then
    assertEquals(externalTokenResponse.getAccessToken(), oauthToken.getAccessToken());
    assertEquals(Arrays.asList(externalTokenResponse.getScope().split(" ")), oauthToken.getScopes());
    assertNull(oauthToken.getRefreshToken());
  }

  @Test
  @Transactional
  @DisplayName("access token 갱신을 실패하는 테스트 - 만료된 refresh token")
  public void refreshToken_fail() {
    // given
    OauthTokenEntity oauthTokenEntity = getRefreshTokenExpiredOauthTokenEntity();
    oauthTokenRepository.save(oauthTokenEntity);
    OrganizationInfoEntity organizationInfoEntity = getOrganizationInfoEntity();
    organizationInfoRepository.save(organizationInfoEntity);

    RefreshTokenRequest request = getRefreshTokenRequest(oauthTokenEntity.getBanksaladUserId().toString(),
        oauthTokenEntity.getOrganizationId());

    // when
    ExternalTokenResponse externalTokenResponse = getExternalTokenResponse();
    when(externalTokenService.refreshToken(organizationInfoEntity.getOrganizationCode(), oauthTokenEntity.getRefreshToken()))
        .thenReturn(externalTokenResponse);

    // then
    assertThrows(NotFoundTokenException.class, () -> oauthTokenService.refreshToken(request));

  }

  private OauthTokenEntity getOauthTokenEntity() {
    return OauthTokenEntity.builder()
        .banksaladUserId(1234567890L)
        .organizationId("test_shinhancard")
        .authorizationCode("test_authorizationCode")
        .accessToken("test_accessToken")
        .refreshToken("test_refreshToken")
        .accessTokenExpiresAt(LocalDateTime.now().plusDays(90))
        .accessTokenExpiresIn(90 * 3600)
        .refreshTokenExpiresAt(LocalDateTime.now().plusDays(365))
        .refreshTokenExpiresIn(365 * 3600)
        .tokenType("Bearer")
        .scope("card.loan card.bill")
        .isExpired(false)
        .build();
  }

  private OauthTokenEntity getAccessTokenExpiredOauthTokenEntity() {
    return OauthTokenEntity.builder()
        .banksaladUserId(1234567890L)
        .organizationId("test_shinhancard")
        .authorizationCode("test_authorizationCode")
        .accessToken("test_accessToken")
        .refreshToken("test_refreshToken")
        .accessTokenExpiresAt(LocalDateTime.now().minusDays(5))
        .accessTokenExpiresIn(90 * 3600)
        .refreshTokenExpiresAt(LocalDateTime.now().plusDays(365))
        .refreshTokenExpiresIn(365 * 3600)
        .tokenType("Bearer")
        .scope("card.loan card.bill")
        .isExpired(false)
        .build();
  }

  private OauthTokenEntity getRefreshTokenExpiredOauthTokenEntity() {
    return OauthTokenEntity.builder()
        .banksaladUserId(1234567890L)
        .organizationId("test_shinhancard")
        .authorizationCode("test_authorizationCode")
        .accessToken("test_accessToken")
        .refreshToken("test_refreshToken")
        .accessTokenExpiresAt(LocalDateTime.now().minusDays(5))
        .accessTokenExpiresIn(90 * 3600)
        .refreshTokenExpiresAt(LocalDateTime.now().minusDays(5))
        .refreshTokenExpiresIn(365 * 3600)
        .tokenType("Bearer")
        .scope("card.loan card.bill")
        .isExpired(false)
        .build();
  }

  private OrganizationInfoEntity getOrganizationInfoEntity() {
    return OrganizationInfoEntity.builder()
        .sector("test_finance")
        .industry("test_card")
        .organizationId("test_shinhancard")
        .organizationObjectid("test_objectId")
        .organizationCode("test_001")
        .organizationDomain("test_shinhancard.com")
        .build();
  }

  private ExternalTokenResponse getExternalTokenResponse() {
    return ExternalTokenResponse.builder()
        .tokenType("Bearer")
        .accessToken("test_received_accessToken")
        .accessTokenExpiresIn(90 * 3600)
        .refreshToken("test_received_refreshToken")
        .refreshTokenExpiresIn(365 * 3600)
        .scope("received_scope1 received_scope2")
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
}
