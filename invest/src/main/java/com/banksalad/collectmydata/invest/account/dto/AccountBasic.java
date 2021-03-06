package com.banksalad.collectmydata.invest.account.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class AccountBasic {

  private String issueDate;

  @JsonProperty("is_tax_benefits")
  private Boolean taxBenefits;

  private BigDecimal withholdingsAmt;

  private BigDecimal creditLoanAmt;

  private BigDecimal mortgageAmt;

  @Builder.Default
  private String currencyCode = "KRW";
}
