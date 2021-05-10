package com.banksalad.collectmydata.card.publishment.userbase.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class LoanShortTermPublishment {

  private Short loanShortTermNo;

  private String loanDtime;

  private BigDecimal loanAmt;

  private String payDueDate;

  private BigDecimal intRate;

  private LocalDateTime createdAt;

  private LocalDateTime updatedAt;
}
