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
@Builder(toBuilder = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "card")
public class CardEntity extends BaseEntity {

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
  private String cardId;

  @Column(nullable = false)
  private String cardType;

  @Column(nullable = false, name = "is_trans_payable", columnDefinition = "BIT", length = 1)
  private Boolean transPayable;

  @Column(nullable = false, name = "is_cash_card", columnDefinition = "BIT", length = 1)
  private Boolean cashCard;

  @Column(nullable = false)
  private String linkedBankCode;

  @Column(nullable = false)
  private String cardBrand;

  @Column(nullable = false, precision = 18, scale = 3)
  private BigDecimal annualFee;

  @Column(nullable = false)
  private String issueDate;

  @Column(nullable = false)
  private String consentId;

  private String syncRequestId;

}
