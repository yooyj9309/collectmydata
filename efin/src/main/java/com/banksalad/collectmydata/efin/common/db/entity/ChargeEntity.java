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

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "charge")
public class ChargeEntity extends BaseEntity {

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
  private String chargeOrgCode;

  @Column(nullable = false, name = "charge_account_num_encrypted")
  private String chargeAccountNum;

  @Column(nullable = false)
  private String chargeOption;

  @Column(columnDefinition = "TINYINT", length = 4)
  private Integer chargeDay;

  @Column(precision = 18, scale = 3)
  private BigDecimal chargeBaseAmt;

  @Column(precision = 18, scale = 3)
  private BigDecimal chargeAmt;

}
