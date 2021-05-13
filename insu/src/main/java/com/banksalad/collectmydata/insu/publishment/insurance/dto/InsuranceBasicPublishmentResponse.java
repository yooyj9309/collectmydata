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

  private boolean renewable;

  private String issueDate;

  private String expDate;

  private BigDecimal faceAmt;

  private String currencyCode;

  private Boolean variable;

  private Boolean universal;

  private String pensionRcvStartDate;

  private String pensionRcvCycle;

  private Boolean loanable;

  private final List<InsuredPublishmentResponse> insuredPublishmentResponse = new ArrayList<>();

  private LocalDateTime createdAt;

  private LocalDateTime updatedAt;
}
