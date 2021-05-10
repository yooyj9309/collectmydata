package com.banksalad.collectmydata.card.publishment.userbase.dto;

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
public class LoanLongTermPublishment {

  private Short loanLongTermNo;

  private String loanDtime;

  private Integer loanCnt;

  private String loanType;

  private String loanName;

  private BigDecimal loanAmt;

  private BigDecimal intRate;

  private String expDate;

  private BigDecimal balanceAmt;

  private String repayMethod;

  private BigDecimal intAmt;

  private LocalDateTime createdAt;

  private LocalDateTime updatedAt;

}
