package com.banksalad.collectmydata.mock.common.db.entity;

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
@Table(name = "invest_irp_account_detail")
public class InvestIrpAccountDetailEntity extends BaseEntity {

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

  @Column(nullable = false)
  private Short irpDetailNo;

  @Column(nullable = false)
  private String consentId;

  private String syncRequestId;

  @Column(nullable = false)
  private String irpName;

  @Column(nullable = false)
  private String irpType;

  @Column(nullable = false, precision = 18, scale = 3)
  private BigDecimal evalAmt;

  @Column(nullable = false, precision = 18, scale = 3)
  private BigDecimal invPrincipal;

  private Integer fundNum;

  @Column(nullable = false)
  private String openDate;

  private String expDate;

  @Column(precision = 5, scale = 3)
  private BigDecimal intRate;
}
