package com.banksalad.collectmydata.capital.common.db.entity;

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

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "operating_lease_history")
public class OperatingLeaseHistoryEntity extends BaseEntity {

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
  private String repayDate;

  @Column(nullable = false)
  private String repayMethod;

  private String repayOrgCode;

  @Column(name = "repay_account_num_encrypted")
  private String repayAccountNum;

  @Column(nullable = false)
  private LocalDate nextRepayDate;
}
