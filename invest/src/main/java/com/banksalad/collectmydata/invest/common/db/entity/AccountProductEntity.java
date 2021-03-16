package com.banksalad.collectmydata.invest.common.db.entity;

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
@Table(name = "account_product")
public class AccountProductEntity extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private LocalDateTime syncedAt;

  @Column(nullable = false)
  private Long banksaladUserId;

  @Column(nullable = false)
  private String organizationId;

  @Column(nullable = false, name = "account_num_encrypted")
  private String accountNum;

  @Column(nullable = false)
  private Integer prodNo;

  @Column(nullable = false)
  private String prodCode;

  @Column(nullable = false)
  private String prodType;

  @Column(nullable = false)
  private String prodTypeDetail;

  @Column(nullable = false)
  private String prodName;

  @Column(precision = 18, scale = 3)
  private BigDecimal purchaseAmt;

  private Long holdingNum;

  private Long availForSaleNum;

  @Column(precision = 18, scale = 3)
  private BigDecimal evalAmt;

  private String issueDate;

  @Column(precision = 18, scale = 3)
  private BigDecimal paidInAmt;

  @Column(precision = 18, scale = 3)
  private BigDecimal withdrawalAmt;

  private String lastPaidInDate;

  @Column(precision = 18, scale = 3)
  private BigDecimal rcvAmt;

  @Column(nullable = false)
  private String currencyCode;
}
