package com.banksalad.collectmydata.insu.common.db.entity;

import com.banksalad.collectmydata.finance.common.db.entity.BaseEntity;
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
import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "car_insurance_history")
public class CarInsuranceHistoryEntity extends BaseEntity {

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
  private Integer carInsuranceNo;

  @Column(nullable = false, name = "car_number_encrypted")
  private String carNumber;

  @Column(nullable = false)
  private String carInsuType;

  private String carName;

  @Column(nullable = false)
  private String startDate;

  @Column(nullable = false)
  private String endDate;

  @Column(nullable = false)
  private String contractAge;

  @Column(nullable = false)
  private String contractDriver;

  @Column(nullable = false, name = "is_own_dmg_coverage", columnDefinition = "BIT", length = 1)
  private Boolean ownDmgCoverage;

  @Column(nullable = false)
  private String selfPayRate;

  @Column(nullable = false, precision = 18, scale = 3)
  private BigDecimal selfPayAmt;

  private LocalDateTime transactionSyncedAt;

  private String transactionResponseCode;
}
