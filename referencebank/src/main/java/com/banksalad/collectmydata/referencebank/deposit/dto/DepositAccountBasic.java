package com.banksalad.collectmydata.referencebank.deposit.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class DepositAccountBasic {

  private String savingMethod;
  private String holderName;
  private String issueDate;
  private String expDate;
  private String currencyCode;
  private BigDecimal commitAmt;
  private BigDecimal monthlyPaidInAmt;
}
