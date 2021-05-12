package com.banksalad.collectmydata.insu.publishment.insurance.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class InsuranceBasicPublishmentResponse {

  private String insuNum;

  @JsonProperty("is_renewable")
  private boolean renewable;

  private String issueDate;

  private String expDate;

  private BigDecimal faceAmt;

  private String currencyCode;

  @JsonProperty("is_variable")
  private Boolean variable;

  @JsonProperty("is_universal")
  private Boolean universal;

  private String pensionRcvStartDate;

  private String pensionRcvCycle;

  @JsonProperty("is_loanable")
  private Boolean loanable;

  @Builder.Default
  private final List<InsuredPublishmentResponse> insuredPublishmentResponse = new ArrayList<>();

  private LocalDateTime createdAt;

  private LocalDateTime updatedAt;
}
