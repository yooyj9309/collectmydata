package com.banksalad.collectmydata.connect.common.db.entity;

import javax.persistence.Column;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;

@Entity
@Getter
@Builder

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

  public List<String> getParseScope() {
    return Arrays.asList(scope.split(" "));
  }

  public boolean isAccessTokenExpired() {
    return accessTokenExpiresAt.isBefore(LocalDateTime.now(ZoneId.of("Asia/Seoul")));
  }

  public void disableToken() {
    this.isExpired = true;
  }
}
