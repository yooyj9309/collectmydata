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
@Table(name = "loan_account_basic_history")
public class LoanAccountBasicHistoryEntity extends BaseTimeAndUserEntity {

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

  private LocalDate expDate;

  @Column(nullable = false, precision = 7, scale = 5)
  private BigDecimal lastOfferedRate;

  private String repayDate;

  @Column(nullable = false)
  private String repayMethod;

  private String repayOrgCode;

  //TODO: encyrpted가 필요해보임
  private String repayAccountNum;

}