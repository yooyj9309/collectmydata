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
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "loan_long_term_history")
public class LoanLongTermHistoryEntity extends BaseEntity {

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
  private Short loanLongTermNo;

  @Column(nullable = false)
  private String loanDtime;

  private Integer loanCnt;

  @Column(nullable = false)
  private String loanType;

  @Column(nullable = false)
  private String loanName;

  @Column(nullable = false, precision = 18, scale = 3)
  private BigDecimal loanAmt;

  @Column(nullable = false, precision = 5, scale = 3)
  private BigDecimal intRate;

  @Column(nullable = false)
  private String expDate;

  @Column(nullable = false, precision = 18, scale = 3)
  private BigDecimal balanceAmt;

  @Column(nullable = false)
  private String repayMethod;

  @Column(nullable = false, precision = 18, scale = 3)
  private BigDecimal intAmt;

  @Column(nullable = false)
  private String consentId;

  private String syncRequestId;
}
