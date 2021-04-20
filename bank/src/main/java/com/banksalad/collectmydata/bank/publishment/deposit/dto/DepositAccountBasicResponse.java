package com.banksalad.collectmydata.bank.publishment.deposit.dto;

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
public class DepositAccountBasicResponse {

  private String accountNum;

  private String seqno;

  @Builder.Default
  private String currencyCode = "KRW";

  private String savingMethod;

  private String holderName;

  private String issueDate;

  private String expDate;

  private BigDecimal commitAmt;

  private BigDecimal monthlyPaidInAmt;

  private LocalDateTime createdAt;

  private LocalDateTime updatedAt;
}
