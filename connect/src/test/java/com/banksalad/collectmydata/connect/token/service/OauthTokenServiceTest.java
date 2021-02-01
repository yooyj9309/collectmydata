package com.banksalad.collectmydata.connect.token.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import com.banksalad.collectmydata.common.exception.collectMydataException.NotFoundTokenException;
import com.banksalad.collectmydata.connect.common.db.entity.OauthTokenEntity;
import com.banksalad.collectmydata.connect.common.db.repository.OauthTokenRepository;
import com.banksalad.collectmydata.connect.token.dto.OauthToken;
import com.github.banksalad.idl.apis.v1.connectmydata.ConnectmydataProto.GetAccessTokenRequest;
import com.github.banksalad.idl.apis.v1.connectmydata.ConnectmydataProto.RefreshTokenRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@DisplayName("OauthTokenService Test")
class OauthTokenServiceTest {

  @Autowired
  private OauthTokenService oauthTokenService;

  @Autowired
  private OauthTokenRepository oauthTokenRepository;

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
    assertEquals(oauthToken.getAccessToken(), oauthTokenEntity.getAccessToken());
    assertEquals(oauthToken.getScopes(), oauthTokenEntity.getParseScope());
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
    GetAccessTokenRequest request = getAccessTokenRequest(NonExistBanksaladUserId.toString(), oauthTokenEntity.getOrganizationId());

    // then
    assertThrows(NotFoundTokenException.class, () -> oauthTokenService.getAccessToken(request));
  }

  @Test
  @Transactional
  @DisplayName("access token 기한 만료로 토큰 갱신하는 테스트")
  public void getAccessToken_refresh() {
    // given
    OauthTokenEntity oauthTokenEntity = getExpiredOauthTokenEntity();
    oauthTokenRepository.save(oauthTokenEntity);

    // when
    GetAccessTokenRequest accessTokenRequest = getAccessTokenRequest(oauthTokenEntity.getBanksaladUserId().toString(),
        oauthTokenEntity.getOrganizationId());
    RefreshTokenRequest refreshTokenRequest = RefreshTokenRequest.newBuilder()
        .setBanksaladUserId(oauthTokenEntity.getBanksaladUserId().toString())
        .setOrganizationId(oauthTokenEntity.getOrganizationId())
        .build();
    OauthToken oauthToken = oauthTokenService.getAccessToken(accessTokenRequest);

    // then
    assertEquals(oauthToken, oauthTokenService.refreshToken(refreshTokenRequest));
  }

  private OauthTokenEntity getOauthTokenEntity() {
    return OauthTokenEntity.builder()
        .banksaladUserId(1234567890L)
        .organizationId("shinhancard")
        .authorizationCode("authorizationCode123")
        .accessToken("accessToken123")
        .refreshToken("refreshToken123")
        .accessTokenExpiresAt(LocalDateTime.now().plusDays(90))
        .accessTokenExpiresIn(90 * 3600)
        .refreshTokenExpiresAt(LocalDateTime.now().plusDays(365))
        .refreshTokenExpiresIn(365 * 3600)
        .tokenType("Bearer")
        .scope("card.loan card.bill")
        .isExpired(false)
        .build();
  }

  private OauthTokenEntity getExpiredOauthTokenEntity() {
    return OauthTokenEntity.builder()
        .banksaladUserId(1234567890L)
        .organizationId("shinhancard")
        .authorizationCode("authorizationCode123")
        .accessToken("accessToken123")
        .refreshToken("refreshToken123")
        .accessTokenExpiresAt(LocalDateTime.now().minusDays(5))
        .accessTokenExpiresIn(90 * 3600)
        .refreshTokenExpiresAt(LocalDateTime.now().plusDays(365))
        .refreshTokenExpiresIn(365 * 3600)
        .tokenType("Bearer")
        .scope("card.loan card.bill")
        .isExpired(false)
        .build();
  }

  private GetAccessTokenRequest getAccessTokenRequest(String banksaladId, String organizationId) {
    return GetAccessTokenRequest.newBuilder()
        .setBanksaladUserId(banksaladId)
        .setOrganizationId(organizationId)
        .build();
  }
}
