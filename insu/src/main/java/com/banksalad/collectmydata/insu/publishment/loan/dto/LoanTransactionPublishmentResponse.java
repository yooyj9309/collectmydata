package com.banksalad.collectmydata.insu.publishment.loan.dto;

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
public class LoanTransactionPublishmentResponse {

  private String accountNum;

  private String transNo;

  private String transDtime;

  private String currencyCode;

  private BigDecimal loanPaidAmt;

  private BigDecimal intPaidAmt;

  private final List<LoanTransactionInterestPublishmentResponse> interestPublishmentResponses = new ArrayList<>();

  private LocalDateTime createdAt;

  private LocalDateTime updatedAt;
}
