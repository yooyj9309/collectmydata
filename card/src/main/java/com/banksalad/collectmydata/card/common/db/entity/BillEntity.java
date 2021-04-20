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
@Table(name = "bill")
public class BillEntity extends BaseEntity {

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
  private Integer chargeMonth;

  @Column(nullable = false)
  private String cardType;

  private String seqno;

  @Column(nullable = false, precision = 18, scale = 3)
  private BigDecimal chargeAmt;

  // TODO jayden-lee 결제일 데이터 타입이 N이기 때문에 Byte. 향후 aN으로 변경 되면, String 으로 타입 변경 예정
  @Column(nullable = false)
  private Byte chargeDay;

  @Column(nullable = false)
  private String paidOutDate;
}
