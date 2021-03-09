package com.banksalad.collectmydata.insu.insurance.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
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
public class GetInsuranceBasicResponse {

  private String rspCode;

  private String rspMsg;

  private long searchTimestamp;

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

  private List<Insured> insuredList;
}
