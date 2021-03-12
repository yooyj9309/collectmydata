package com.banksalad.collectmydata.insu.loan.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class LoanTransaction {

  private String transNo;
  private String transDtime;
  private String currencyCode;
  private BigDecimal loanPaidAmt;
  private BigDecimal intPaidAmt;
  private int intCnt;

  @Builder.Default
  private List<LoanTransactionInterest> intList = new ArrayList<>();
}
