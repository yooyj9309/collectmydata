package com.banksalad.collectmydata.card.common.db.entity;

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
@Table(name = "approval_domestic")
public class ApprovalDomesticEntity extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private Integer approvalYearMonth;

  @Column(nullable = false)
  private LocalDateTime syncedAt;

  @Column(nullable = false)
  private Long banksaladUserId;

  @Column(nullable = false)
  private String organizationId;

  @Column(nullable = false)
  private String cardId;

  @Column(nullable = false)
  private String approvedNum;

  @Column(nullable = false)
  private String status;

  @Column(nullable = false)
  private String payType;

  @Column(nullable = false)
  private String approvedDtime;

  private String cancelDtime;

  @Column(nullable = false)
  private String merchantName;

  @Column(nullable = false, precision = 18, scale = 3)
  private BigDecimal approvedAmt;

  private Integer totalInstallCnt;

  @Column(nullable = false)
  private String consentId;

  private String syncRequestId;
}
