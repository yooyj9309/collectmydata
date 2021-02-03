package com.banksalad.collectmydata.connect.common.db.entity;

import com.banksalad.collectmydata.connect.token.dto.ExternalTokenResponse;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Entity
@Getter
@Builder
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "oauth_token")
public class OauthTokenEntity extends BaseTimeAndUserEntity{

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long oauthTokenId;

  @Column(nullable = false)
  private Long banksaladUserId;

  @Column(nullable = false)
  private String organizationId;

  @Column(nullable = false)
  private String authorizationCode;

  @Column(nullable = false)
  private String accessToken;

  @Column(nullable = false)
  private String refreshToken;

  @Column(nullable = false)
  private LocalDateTime accessTokenExpiresAt;

  @Column(nullable = false)
  private Integer accessTokenExpiresIn;

  @Column(nullable = false)
  private LocalDateTime refreshTokenExpiresAt;

  @Column(nullable = false)
  private Integer refreshTokenExpiresIn;

  private String tokenType;

  @Column(nullable = false)
  private String scope;

  @Column(nullable = false, columnDefinition = "BIT", length = 1)
  private Boolean isExpired;

  public void updateFrom(ExternalTokenResponse response) {
    this.accessToken = response.getAccessToken();
    this.refreshToken = response.getRefreshToken();
    this.accessTokenExpiresAt = LocalDateTime.now()
        .plusSeconds(response.getAccessTokenExpiresIn());
    this.accessTokenExpiresIn = response.getAccessTokenExpiresIn();
    this.refreshTokenExpiresAt = LocalDateTime.now()
        .plusSeconds(response.getRefreshTokenExpiresIn());
    this.refreshTokenExpiresIn = response.getRefreshTokenExpiresIn();
    this.tokenType = response.getTokenType();
    this.scope = response.getScope();
    this.isExpired = false;
  }

  public void updateFrom(String authorizationCode, ExternalTokenResponse response) {
    this.authorizationCode = authorizationCode;
    updateFrom(response);
  }

  public List<String> getParseScope() {
    return Arrays.asList(scope.split(" "));
  }

  public boolean isAccessTokenExpired() {
    return accessTokenExpiresAt.isBefore(LocalDateTime.now());
  }

  public void disableToken() {
    this.isExpired = true;
  }

  public boolean isRefreshTokenExpired() {
    return refreshTokenExpiresAt.isBefore(LocalDateTime.now());
  }
}
