package com.banksalad.collectmydata.efin.common.db.entity;

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

  private String subKey;

  @Column(nullable = false)
  private String accountId;

  @Column(nullable = false, name = "is_consent", columnDefinition = "BIT", length = 1)
  private Boolean consent;

  @Column(nullable = false)
  private String accountStatus;

  @Column(nullable = false, name = "is_pay_reg", columnDefinition = "BIT", length = 1)
  private Boolean payReg;

  private Long balanceSearchTimestamp;

  private String balanceResponseCode;

  private Long chargeSearchTimestamp;

  private String chargeResponseCode;

  private LocalDateTime transactionSyncedAt;

  private String transactionResponseCode;

  private LocalDateTime prepaidTransactionSyncedAt;

  private String prepaidTransactionResponseCode;

}
