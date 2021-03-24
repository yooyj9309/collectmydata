package com.banksalad.collectmydata.efin.common.db.entity;

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
@Table(name = "balance_history")
public class BalanceHistoryEntity extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private LocalDateTime syncedAt;

  @Column(nullable = false)
  private Long banksaladUserId;

  @Column(nullable = false)
  private String organizationId;

  private String subKey;

  @Column(nullable = false)
  private String fobName;

  @Column(nullable = false)
  private String fobNo;

  @Column(nullable = false, precision = 18, scale = 3)
  private BigDecimal totalBalanceAmt;

  @Column(nullable = false, precision = 18, scale = 3)
  private BigDecimal chargeBalanceAmt;

  @Column(nullable = false, precision = 18, scale = 3)
  private BigDecimal reserveBalanceAmt;

  @Column(nullable = false, precision = 18, scale = 3)
  private BigDecimal reserveDueAmt;

  @Column(nullable = false, precision = 18, scale = 3)
  private BigDecimal expDueAmt;

  @Column(nullable = false, precision = 18, scale = 3)
  private BigDecimal limitAmt;
}
