package com.banksalad.collectmydata.bank.common.db.entity;

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
@Table(name = "loan_account_transaction")
public class LoanAccountTransactionEntity extends BaseEntity {

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

  private String seqno;

  @Column(nullable = false)
  private String uniqueTransNo;

  @Column(nullable = false)
  private String transDtime;

  private String transNo;

  @Column(nullable = false)
  private String transType;

  @Column(nullable = false, precision = 18, scale = 3)
  private BigDecimal transAmt;

  @Column(nullable = false, precision = 18, scale = 3)
  private BigDecimal balanceAmt;

  @Column(nullable = false, precision = 18, scale = 3)
  private BigDecimal principalAmt;

  @Column(nullable = false, precision = 18, scale = 3)
  private BigDecimal intAmt;
}
