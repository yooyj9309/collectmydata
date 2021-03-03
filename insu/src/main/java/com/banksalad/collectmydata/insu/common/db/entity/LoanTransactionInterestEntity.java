package com.banksalad.collectmydata.insu.common.db.entity;

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
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "loan_transaction_interest")
public class LoanTransactionInterestEntity extends BaseTimeAndUserEntity {

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
  private LocalDateTime transDtime;

  @Column(nullable = false)
  private String transNo;

  @Column(nullable = false)
  private Integer intNo;

  @Column(nullable = false)
  private LocalDate intStartDate;

  @Column(nullable = false)
  private LocalDate intEndDate;

  @Column(nullable = false, precision = 5, scale = 3)
  private BigDecimal intRate;

  @Column(nullable = false)
  private String intType;
}