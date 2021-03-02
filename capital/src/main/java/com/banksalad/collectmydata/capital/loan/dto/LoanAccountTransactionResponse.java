package com.banksalad.collectmydata.capital.loan.dto;


import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@ToString
public class LoanAccountTransactionResponse {

  private String rspCode;
  private String rspMsg;
  private String nextPage;
  private int transCnt;
  private List<LoanAccountTransaction> transList;
}