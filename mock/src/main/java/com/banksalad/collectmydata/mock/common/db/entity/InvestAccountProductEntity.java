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
@Table(name = "invest_account_product")
public class InvestAccountProductEntity extends BaseEntity {

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

  @Column(nullable = false)
  private Integer prodNo;

  @Column(nullable = false)
  private String prodCode;

  @Column(nullable = false)
  private String prodType;

  @Column(nullable = false)
  private String prodTypeDetail;

  @Column(nullable = false)
  private String prodName;

  private BigDecimal purchaseAmt;

  private Long holdingNum;

  private Long availForSaleNum;

  private BigDecimal evalAmt;

  private String issueDate;

  private BigDecimal paidInAmt;

  private BigDecimal withdrawalAmt;

  private String lastPaidInDate;

  private BigDecimal rcvAmt;

  private String currencyCode;

  @Column(nullable = false)
  private String consentId;

  private String syncRequestId;
}
