package com.banksalad.collectmydata.insu.common.db.entity;

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
@Table(name = "insurance_summary")
public class InsuranceSummaryEntity extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private LocalDateTime syncedAt;

  @Column(nullable = false)
  private Long banksaladUserId;

  @Column(nullable = false)
  private String organizationId;

  @Column(nullable = false)
  private String insuNum;

  @Column(nullable = false, name = "is_consent", columnDefinition = "BIT", length = 1)
  private Boolean consent;

  @Column(nullable = false)
  private String insuType;

  @Column(nullable = false)
  private String prodName;

  @Column(nullable = false)
  private String insuStatus;

  @Column(nullable = false)
  private String consentId;

  @Column(nullable = false)
  private String syncRequestId;

  private Long basicSearchTimestamp;

  private String basicResponseCode;

  private Long carSearchTimestamp;

  private String carResponseCode;

  private Long paymentSearchTimestamp;

  private String paymentResponseCode;

  private LocalDateTime transactionSyncedAt;

  private String transactionResponseCode;
}
