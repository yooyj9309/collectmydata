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
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "car_insurance_transaction")
public class CarInsuranceTransactionEntity extends BaseEntity {

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

  @Column(nullable = false, name = "car_number_encrypted")
  private String carNumber;

  @Column(nullable = false)
  private Integer transNo;

  @Column(nullable = false, precision = 18, scale = 3)
  private BigDecimal faceAmt;

  @Column(nullable = false, precision = 18, scale = 3)
  private BigDecimal paidAmt;

  @Column(nullable = false)
  private String payMethod;
}
