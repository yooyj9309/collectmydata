package com.banksalad.collectmydata.referencebank.common.db.entity;

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
@Table(name = "deposit_account_basic_history")
public class DepositAccountBasicHistoryEntity extends BaseEntity {

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

  private String seqno;

  private String currencyCode;

  @Column(nullable = false)
  private String savingMethod;

  @Column(nullable = false, name = "holder_name_encrypted")
  private String holderName;

  @Column(nullable = false)
  private String issueDate;

  private String expDate;

  private BigDecimal commitAmt;

  private BigDecimal monthlyPaidInAmt;
}
