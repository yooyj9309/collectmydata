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
@Table(name = "organization_oauth_token")
public class OrganizationOauthTokenEntity extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String secretType;

  @Column(nullable = false)
  private String accessToken;

  @Column(nullable = false)
  private LocalDateTime accessTokenExpiresAt;

  @Column(nullable = false)
  private Integer accessTokenExpiresIn;

  private String tokenType;

  @Column(nullable = false, name = "scopeEncrypted")
  private String scope;
}
