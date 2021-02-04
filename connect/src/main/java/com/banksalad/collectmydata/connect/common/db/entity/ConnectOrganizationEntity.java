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
@Table(name = "connect_organization")
public class ConnectOrganizationEntity extends BaseTimeAndUserEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long connectOrganizationId;

  @Column(nullable = false)
  private String sector;

  @Column(nullable = false)
  private String industry;

  @Column(nullable = false)
  private String organizationId;

  @Column(nullable = false)
  private String organizationObjectid;

  @Column(name = "org_code", nullable = false)
  private String organizationCode;

  @Column(nullable = false)
  private String orgType;

  private String orgName;

  @Column(nullable = false)
  private String organizationStatus;

  private String orgRegno;

  private String corpRegno;

  private String address;

  private String domain;

  @Column(nullable = false, columnDefinition = "BIT", length = 1)
  private Boolean isRelayOrganization;

  private String relayOrgCode;
}
