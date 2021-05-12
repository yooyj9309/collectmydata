package com.banksalad.collectmydata.insu.publishment.loan.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class LoanBasicPublishmentResponse {

  private String accountNum;

  private String loanStartDate;

  private String loanExpDate;

  private String repayMethod;

  private String insuNum;

  private LocalDateTime createdAt;

  private LocalDateTime updatedAt;
}
