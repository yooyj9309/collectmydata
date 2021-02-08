package com.banksalad.collectmydata.capital.common.db.entity;

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

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "operating_lease")
public class OperatingLeaseEntity extends BaseTimeAndUserEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long operatingLeaseId;

  @Column(nullable = false)
  private LocalDateTime syncedAt;

  @Column(nullable = false)
  private Long banksaladUserId;

  @Column(nullable = false)
  private Long organizationId;

  @Column(nullable = false)
  private String accountNumEncrypted;

  private Integer seqno;

  @Column(nullable = false)
  private String holderNameEncrypted;

  @Column(nullable = false)
  private LocalDate issueDate;

  @Column(nullable = false)
  private LocalDate expDate;

  @Column(nullable = false)
  private Integer repayDate;

  @Column(nullable = false)
  private String repayMethod;

  private String repayOrgCode;

  private String repayAccountNumEncrypted;

  @Column(nullable = false)
  private LocalDate nextRepaydate;
}
