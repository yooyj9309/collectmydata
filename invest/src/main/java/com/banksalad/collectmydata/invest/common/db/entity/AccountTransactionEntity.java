package com.banksalad.collectmydata.invest.common.db.entity;

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
@Table(name = "account_transaction")
public class AccountTransactionEntity extends BaseTimeAndUserEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private Integer transactionYearMonth;

  @Column(nullable = false)
  private LocalDateTime syncedAt;

  @Column(nullable = false)
  private Long banksaladUserId;

  @Column(nullable = false)
  private String organizationId;

  @Column(nullable = false, name = "account_num_encrypted")
  private String accountNum;

  @Column(nullable = false)
  private String uniqueTransNo;

  @Column(nullable = false)
  private String prodCode;

  @Column(nullable = false)
  private String transDtime;

  @Column(nullable = false)
  private String prodName;

  @Column(nullable = false)
  private String transType;

  private String transTypeDetail;

  @Column(nullable = false)
  private Long transNum;

  @Column(nullable = false, precision = 17, scale = 4)
  private BigDecimal baseAmt;

  @Column(nullable = false, precision = 18, scale = 3)
  private BigDecimal transAmt;

  @Column(nullable = false, precision = 18, scale = 3)
  private BigDecimal settleAmt;

  @Column(nullable = false, precision = 18, scale = 3)
  private BigDecimal balanceAmt;

  @Column(nullable = false)
  private BigDecimal currencyCode;
}
