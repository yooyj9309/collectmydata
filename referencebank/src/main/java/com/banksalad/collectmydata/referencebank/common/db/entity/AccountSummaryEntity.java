package com.banksalad.collectmydata.referencebank.common.db.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "account_summary")
public class AccountSummaryEntity extends BaseEntity {

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

  @Column(nullable = false, columnDefinition = "TINYINT", length = 1)
  private Boolean isConsent;

  private String seqno;

  @Column(nullable = false, columnDefinition = "TINYINT", length = 1)
  private Boolean isForeignDeposit;

  @Column(nullable = false)
  private String prodName;

  @Column(nullable = false)
  private String accountType;

  @Column(nullable = false)
  private String accountStatus;

  private Long basicSearchTimestamp;

  private Long detailSearchTimestamp;

  private LocalDateTime transactionSyncedAt;

  // TODO : remove @Transient after table modifying
  @Transient
  private String basicResponseCode;

  @Transient
  private String detailResponseCode;

  @Transient
  private String transactionResponseCode;
}
