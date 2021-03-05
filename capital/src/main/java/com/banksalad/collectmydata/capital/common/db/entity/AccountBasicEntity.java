package com.banksalad.collectmydata.capital.common.db.entity;

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
@Table(name = "account_basic")
public class AccountBasicEntity extends BaseTimeAndUserEntity {

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

  @Column(nullable = false, name = "holder_name_encrypted")
  private String holderName;

  @Column(nullable = false)
  private LocalDate issueDate;

  @Column(nullable = false)
  private LocalDate expDate;

  @Column(nullable = false)
  private BigDecimal lastOfferedRate;

  @Column(nullable = false)
  private String repayDate;

  @Column(nullable = false)
  private String repayMethod;

  @Column(nullable = false)
  private String repayOrgCode;

  @Column(name = "repay_account_num_encrypted")
  private String repayAccountNum;
}
