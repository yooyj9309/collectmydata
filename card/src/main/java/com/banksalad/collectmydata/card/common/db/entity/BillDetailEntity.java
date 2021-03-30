package com.banksalad.collectmydata.card.common.db.entity;

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
@Table(name = "bill_detail")
public class BillDetailEntity extends BaseEntity {

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
  private Integer chargeMonth;

  private String seqno;

  @Column(nullable = false)
  private Integer billDetailNo;

  @Column(nullable = false)
  private String cardId;

  @Column(nullable = false)
  private String paidDtime;

  @Column(nullable = false, precision = 18, scale = 3)
  private BigDecimal paidAmt;

  private String currencyCode;

  @Column(nullable = false)
  private String merchantName;

  @Column(nullable = false, precision = 18, scale = 3)
  private BigDecimal creditFreeAmt;

  private Integer totalInstallCnt;

  private Integer curInstallCnt;

  @Column(nullable = false, precision = 18, scale = 3)
  private BigDecimal balanceAmt;

  @Column(nullable = false)
  private String prodType;
}
