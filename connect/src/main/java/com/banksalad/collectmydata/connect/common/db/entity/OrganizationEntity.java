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
@Table(name = "organization")
public class OrganizationEntity extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private LocalDateTime syncedAt;

  @Column(nullable = false)
  private String sector;

  @Column(nullable = false)
  private String industry;

  private String organizationId;

  @Column(nullable = false)
  private String opType;

  @Column(nullable = false)
  private String orgCode;

  @Column(nullable = false)
  private String orgType;

  private String orgName;

  private String orgRegno;

  private String corpRegno;

  private String address;

  private String domain;

  private String relayOrgCode;

  private String authType;

}
