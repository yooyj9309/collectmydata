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

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "organization_client")
public class OrganizationClientEntity extends BaseTimeAndUserEntity{
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long organizationClientId;

  @Column(nullable = false)
  private String organizationId;

  @Column(nullable = false)
  private String clientId;

  @Column(nullable = false)
  private String clientSecret;
}
