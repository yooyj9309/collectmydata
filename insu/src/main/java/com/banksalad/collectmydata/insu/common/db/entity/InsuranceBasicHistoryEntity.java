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
@Table(name = "insurance_basic_history")
public class InsuranceBasicHistoryEntity extends BaseEntity {

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

  @Column(nullable = false, name = "is_renewable", columnDefinition = "BIT", length = 1)
  private Boolean renewable;

  @Column(nullable = false)
  private String issueDate;

  @Column(nullable = false)
  private String expDate;

  @Column(nullable = false, precision = 18, scale = 3)
  private BigDecimal faceAmt;

  private String currencyCode;

  @Column(nullable = false, name = "is_variable", columnDefinition = "BIT", length = 1)
  private Boolean variable;

  @Column(nullable = false, name = "is_universal", columnDefinition = "BIT", length = 1)
  private Boolean universal;

  private String pensionRcvStartDate;

  private String pensionRcvCycle;

  @Column(nullable = false, name = "is_loanable", columnDefinition = "BIT", length = 1)
  private Boolean loanable;

  @Column(nullable = false, columnDefinition = "tinyint")
  private Integer insuredCount;

  @Column(nullable = false)
  private String consentId;

  @Column(nullable = false)
  private String syncRequestId;
}
