package com.banksalad.collectmydata.card.publishment.userbase.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
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
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class LoanSummaryPublishment {

  @JsonProperty(value = "is_loan_revolving")
  private boolean loanRevolving;

  @JsonProperty(value = "is_loan_short_term")
  private boolean loanShortTerm;

  @JsonProperty(value = "is_loan_long_term")
  private boolean loanLongTerm;

  private LocalDateTime createdAt;

  private LocalDateTime updatedAt;

}
