package com.banksalad.collectmydata.connect.common.db.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@ToString
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class OauthTokenEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long oauthTokenId;

  private Long banksaladUserId;

  private String organizationId;

  private String organizationCode;

  private String tokenType;

  private String accessToken;

  private String refreshToken;

  private LocalDateTime accessTokenExpiresAt;

  private LocalDateTime refreshTokenExpiresAt;

  private String scope;

  private LocalDateTime issuedAt;

  private LocalDateTime refreshedAt;

  private LocalDate createdAt;

  private LocalDate updatedAt;
}
