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

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "account_summary")
public class AccountSummaryEntity extends BaseTimeAndUserEntity {

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

  @Column(nullable = false, name = "is_consent", columnDefinition = "TINYINT")
  private Boolean consent;

  private String seqno;

  @Column(name = "is_foreign_deposit", columnDefinition = "TINYINT")
  private Boolean foreignDeposit;

  @Column(nullable = false)
  private String prodName;

  @Column(nullable = false)
  private String accountType;

  @Column(nullable = false)
  private String accountStatus;

  private Long basicSearchTimestamp;

  private String basicSearchResponseCode;

  private Long detailSearchTimestamp;

  private String detailSearchResponseCode;

  private LocalDateTime transactionSyncedAt;
}
