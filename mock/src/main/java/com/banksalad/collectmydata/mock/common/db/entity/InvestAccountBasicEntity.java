package com.banksalad.collectmydata.mock.common.db.entity;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

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
@Table(name = "invest_account_basic")
public class InvestAccountBasicEntity extends BaseEntity {

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
  private String issueDate;

  @Column(nullable = false, columnDefinition = "BIT", length = 1)
  private Boolean isTaxBenefits;

  @Column(nullable = false, precision = 18, scale = 3)
  private BigDecimal withholdings_amt;

  @Column(nullable = false, precision = 18, scale = 3)
  private BigDecimal credit_loan_amt;

  @Column(nullable = false, precision = 18, scale = 3)
  private BigDecimal mortgage_amt;

  private String currency_code;

  @Column(nullable = false)
  private String consentId;

  private String syncRequestId;
}
