package com.banksalad.collectmydata.connect.common.db.entity;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
public class OauthTokenHistoryEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long oauthTokenHistoryId;

  private Long oauthTokenId;

  private Long banksaladUserId;

  private String organizationId;

  private String authorizationCode;

  private String accessToken;

  private String refreshToken;

  private LocalDateTime accessTokenExpiresAt;

  private Integer accessTokenExpiresIn;

  private LocalDateTime refreshTokenExpiresAt;

  private Integer refreshTokenExpiresIn;

  private String tokenType;

  private String scope;

  private Boolean isExpired;

  @CreatedDate
  private LocalDateTime createdAt;

  @LastModifiedDate
  private LocalDateTime updatedAt;
}
