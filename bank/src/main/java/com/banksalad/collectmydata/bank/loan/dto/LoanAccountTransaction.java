package com.banksalad.collectmydata.bank.loan.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class LoanAccountTransaction {

  private String transDtime;

  private String transNo;

  private String transType;

  private BigDecimal transAmt;

  private BigDecimal balanceAmt;

  private BigDecimal principalAmt;

  private BigDecimal intAmt;

  private int intCnt;

  @JsonProperty(value = "int_list")
  private List<LoanAccountTransactionInterest> loanAccountTransactionInterests;

}
