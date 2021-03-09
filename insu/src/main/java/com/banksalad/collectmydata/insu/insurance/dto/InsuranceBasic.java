package com.banksalad.collectmydata.insu.insurance.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class InsuranceBasic {

  private boolean renewable;
  private String issueDate;
  private String expDate;
  private BigDecimal faceAmt;
  private String currencyCode;
  private boolean variable;
  private boolean universal;
  private String pensionRcvStartDate;
  private String pensionRcvCycle;
  private boolean loanable;
  private int insuredCount;
  private List<Insured> insuredList;
}
