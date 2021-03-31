package com.banksalad.collectmydata.card.loan.dto;

import com.banksalad.collectmydata.finance.api.userbase.dto.UserBaseResponse;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class GetLoanSummaryResponse implements UserBaseResponse {

  private String rspCode;
  private String rspMsg;
  private long searchTimestamp;

  @JsonUnwrapped
  private LoanSummary loanSummary;
}
