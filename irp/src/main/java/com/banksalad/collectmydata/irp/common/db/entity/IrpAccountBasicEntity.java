package com.banksalad.collectmydata.irp.common.db.entity;

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
@Table(name = "irp_account_basic")
public class IrpAccountBasicEntity extends BaseEntity {

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

  @Column(nullable = false, precision = 18, scale = 3)
  private BigDecimal accumAmt;

  @Column(nullable = false, precision = 18, scale = 3)
  private BigDecimal evalAmt;

  @Column(nullable = false, precision = 18, scale = 3)
  private BigDecimal employerAmt;

  @Column(nullable = false, precision = 18, scale = 3)
  private BigDecimal employeeAmt;

  @Column(nullable = false)
  private String issueDate;

  @Column(nullable = false)
  private String firstDepositDate;
}
