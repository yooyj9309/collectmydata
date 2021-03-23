package com.banksalad.collectmydata.insu.insurance.dto;

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
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class InsuranceBasic {

  private String insuNum;

  @JsonProperty("is_renewable")
  private boolean renewable;

  private String issueDate;

  private String expDate;

  private BigDecimal faceAmt;

  private String currencyCode;

  @JsonProperty("is_variable")
  private boolean variable;

  @JsonProperty("is_universal")
  private boolean universal;

  private String pensionRcvStartDate;

  private String pensionRcvCycle;

  @JsonProperty("is_loanable")
  private boolean loanable;

  private int insuredCount;

  @Builder.Default
  private List<Insured> insuredList = new ArrayList<>();
}
