package com.banksalad.collectmydata.mock.invest.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
public class InvestAccountBasic {

  private LocalDate issueDate;
  private Boolean isTaxBenefits;
  private BigDecimal withholdingsAmt;
  private BigDecimal creditLoanAmt;
  private BigDecimal mortgageAmt;
  private String currencyCode;
}
