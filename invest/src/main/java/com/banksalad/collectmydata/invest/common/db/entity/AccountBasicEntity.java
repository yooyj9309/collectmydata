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
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder(toBuilder = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "account_basic")
public class AccountBasicEntity extends BaseEntity {

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
  private String issueDate;

  @Column(nullable = false, name = "is_tax_benefits", columnDefinition = "BIT", length = 1)
  private Boolean taxBenefits;

  @Column(nullable = false, precision = 18, scale = 3)
  private BigDecimal withholdingsAmt;

  @Column(nullable = false, precision = 18, scale = 3)
  private BigDecimal creditLoanAmt;

  @Column(nullable = false, precision = 18, scale = 3)
  private BigDecimal mortgageAmt;

  @Column(nullable = false)
  private String currencyCode;

  @Column(nullable = false)
  private String consentId;

  private String syncRequestId;
}
