package com.banksalad.collectmydata.bank.publishment.loan.dto;

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
public class LoanAccountBasicResponse {

  private String accountNum;

  private String seqno;

  private String holderName;

  private String issueDate;

  private String expDate;

  private BigDecimal lastOfferedRate;

  private String repayDate;

  private String repayMethod;

  private String repayOrgCode;

  private String repayAccountNum;

  private LocalDateTime createdAt;

  private LocalDateTime updatedAt;
}
