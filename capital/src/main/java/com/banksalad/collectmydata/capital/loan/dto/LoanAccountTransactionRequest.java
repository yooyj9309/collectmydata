package com.banksalad.collectmydata.capital.loan.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@ToString
public class LoanAccountTransactionRequest {

  private String orgCode;
  private String accountNum;
  private Integer seqno;
  private String fromDtime;
  private String toDtime;
  private String nextPage;
  private int limit;
}
