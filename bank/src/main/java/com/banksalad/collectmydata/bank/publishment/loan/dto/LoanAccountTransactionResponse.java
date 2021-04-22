package com.banksalad.collectmydata.bank.publishment.loan.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class LoanAccountTransactionResponse {

  private String accountNum;

  private String seqno;

  private String transDtime;

  private String transNo;

  private String transType;

  private BigDecimal transAmt;

  private BigDecimal balanceAmt;

  private BigDecimal principalAmt;

  private BigDecimal intAmt;
  
  private List<LoanAccountTransactionInterest> loanAccountTransactionInterests;

  private LocalDateTime createdAt;

  private LocalDateTime updatedAt;
}
