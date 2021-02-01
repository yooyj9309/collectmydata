package com.banksalad.collectmydata.connect.common.db.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * TODO
 * organization(기관 정보) 관련 DB table 정의가 완료되면 클래스 및 일부 필드명 수정 예정
 */
@Entity
@Getter
@Builder
public class OrganizationInfoEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long organizationInfoId;

  private String sector;

  private String industry;

  private String organizationId;

  private String organizationObjectid;

  private String organizationCode;

  private String organizationName;

  private String organizationStatus;

  private String organizationDomain;

  private LocalDateTime createdAt;

  private LocalDateTime updatedAt;
}
