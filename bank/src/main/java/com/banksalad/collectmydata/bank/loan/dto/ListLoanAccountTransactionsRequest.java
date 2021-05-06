package com.banksalad.collectmydata.bank.loan.dto;

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
public class ListLoanAccountTransactionsRequest {

  private String orgCode;

  private String accountNum;

  private String seqno;

  private String fromDate;

  private String toDate;

  private String nextPage;

  private String limit; // fixme : int
}
