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
@Table(name = "insurance_contract")
public class InsuranceContractEntity extends BaseTimeAndUserEntity {

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

  @Column(nullable = false)
  private String insuredNo;

  @Column(nullable = false)
  private Integer contractNo;

  @Column(nullable = false)
  private String contractStatus;

  @Column(nullable = false)
  private String contractName;

  @Column(nullable = false)
  private LocalDate contractExpDate;

  @Column(nullable = false, precision = 18, scale = 3)
  private BigDecimal contractFaceAmt;

  @Column(nullable = false)
  private String currencyCode;

  @Column(nullable = false, columnDefinition = "BIT", length = 1)
  private Boolean isRequired;
}
