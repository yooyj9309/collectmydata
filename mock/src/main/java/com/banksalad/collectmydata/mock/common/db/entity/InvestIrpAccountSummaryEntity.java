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

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder(toBuilder = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "invest_irp_account_summary")
public class InvestIrpAccountSummaryEntity extends BaseEntity {

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

  private String consentId;

  private String syncRequestId;

  @Column(nullable = false, columnDefinition = "BIT", length = 1)
  private Boolean isConsent;

  @Column(nullable = false)
  private String prodName;

  @Column(nullable = false)
  private String accountStatus;

  private Long basicSearchTimestamp;

  private Long detailSearchTimestamp;

  private LocalDateTime transactionSyncedAt;

  private String basicResponseCode;

  private String detailResponseCode;

  private String transactionResponseCode;
}
