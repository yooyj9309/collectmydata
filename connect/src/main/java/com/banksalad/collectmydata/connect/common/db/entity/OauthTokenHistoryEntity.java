package com.banksalad.collectmydata.connect.common.db.entity;

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

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "oauth_token_history")
public class OauthTokenHistoryEntity extends BaseTimeAndUserEntity{

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long oauthTokenHistoryId;

  @Column(nullable = false)
  private String oauthTokenId;

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
}
