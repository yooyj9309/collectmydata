package com.banksalad.collectmydata.capital.common.db.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
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
@Table(name = "account_transaction_interest")
public class AccountTransactionInterestEntity extends BaseTimeAndUserEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotNull
  private Integer transactionYearMonth;

  @NotNull
  private LocalDateTime syncedAt;

  @NotNull
  private Long banksaladUserId;

  @NotNull
  private String organizationId;

  @NotNull
  @Column(nullable = false, name = "account_num_encrypted")
  private String accountNum;

  private String seqno;

  @NotNull
  private String uniqueTransNo;

  @NotNull
  private Integer intNo;

  @NotNull
  private LocalDate intStartDate;

  @NotNull
  private LocalDate intEndDate;

  @NotNull
  @Column(precision = 5, scale = 3)
  private BigDecimal intRate;

  @NotNull
  private String intType;
}
