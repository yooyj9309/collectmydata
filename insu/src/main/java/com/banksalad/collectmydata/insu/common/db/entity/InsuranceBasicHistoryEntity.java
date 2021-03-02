package com.banksalad.collectmydata.insu.common.db.entity;

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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "insurance_basic_history")
public class InsuranceBasicHistoryEntity extends BaseTimeAndUserEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private LocalDateTime syncedAt;

  @Column(nullable = false)
  private Long banksaladUserId;

  @Column(nullable = false)
  private String organizationId;

  @Column(nullable = false)
  private String insuNum;

  @Column(nullable = false, columnDefinition = "BIT", length = 1)
  private Boolean isRenewable;

  @Column(nullable = false)
  private LocalDate issueDate;

  @Column(nullable = false)
  private LocalDate expDate;

  @Column(nullable = false, precision = 18, scale = 3)
  private BigDecimal faceAmt;

  private String currencyCode;

  @Column(nullable = false, columnDefinition = "BIT", length = 1)
  private Boolean isVariable;

  @Column(nullable = false, columnDefinition = "BIT", length = 1)
  private Boolean isUniversal;

  private LocalDate pensionRcvStartDate;

  private String pensionRcvCycle;

  @Column(nullable = false, columnDefinition = "BIT", length = 1)
  private Boolean isLoanable;

  @Column(nullable = false)
  private Integer insuredCount;
}
