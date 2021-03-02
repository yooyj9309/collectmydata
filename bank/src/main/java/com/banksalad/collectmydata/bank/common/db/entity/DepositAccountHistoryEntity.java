package com.banksalad.collectmydata.bank.common.db.entity;

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
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "deposit_account_history")
public class DepositAccountHistoryEntity extends BaseTimeAndUserEntity {

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

  private Integer seqno;

  private String currencyCode;

  @Column(nullable = false)
  private String savingMethod;

  @Column(nullable = false, name = "holder_name_encrypted")
  private String holderName;

  @Column(nullable = false)
  private LocalDate issueDate;

  private LocalDate expDate;

  private BigDecimal commitAmt;

  private BigDecimal monthlyPaidInAmt;

  private BigDecimal terminationAmt;

  private BigDecimal lastOfferedRate;

  @Column(nullable = false)
  private BigDecimal balanceAmt;

  @Column(nullable = false)
  private BigDecimal withdrawableAmt;

  @Column(nullable = false)
  private BigDecimal offeredRate;

  private Integer lastPaidInCnt;
}