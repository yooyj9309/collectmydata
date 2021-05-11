package com.banksalad.collectmydata.card.publishment.bill.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class BillDetailPublishment {

  private Integer chargeMonth;

  private String seqno;

  private String cardId;

  private String paidDtime;

  private BigDecimal paidAmt;

  private String currencyCode;

  private String merchantName;

  private BigDecimal creditFeeAmt;

  private Integer totalInstallCnt;

  private Integer curInstallCnt;

  private BigDecimal balanceAmt;

  private String prodType;

  private LocalDateTime createdAt;

  private LocalDateTime updatedAt;

}
