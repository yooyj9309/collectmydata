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

  private Integer seqno;

  @NotNull
  private String uniqueTransNo;

  @NotNull
  private LocalDateTime transDtime;

  private String transNo;

  @NotNull
  private String transType;

  @NotNull
  private BigDecimal transAmt;

  @NotNull
  private BigDecimal balanceAmt;

  @NotNull
  private BigDecimal principalAmt;

  @NotNull
  private BigDecimal intAmt;
}
