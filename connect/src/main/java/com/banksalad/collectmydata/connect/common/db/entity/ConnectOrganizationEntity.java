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
import lombok.Setter;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "connect_organization")
public class ConnectOrganizationEntity extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String sector;

  @Column(nullable = false)
  private String industry;

  @Column(nullable = false)
  private String organizationId;

  @Column(nullable = false)
  private String organizationObjectid;

  private String orgCode;

  @Column(nullable = false)
  private String organizationStatus;

  private String domain;

  @Column(name = "is_deleted", nullable = false, columnDefinition = "BIT", length = 1)
  private Boolean deleted;
}
