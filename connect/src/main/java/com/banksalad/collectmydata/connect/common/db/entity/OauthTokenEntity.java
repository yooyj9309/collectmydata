package com.banksalad.collectmydata.connect.common.db.entity;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
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
@ToString
public class OauthTokenEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
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
