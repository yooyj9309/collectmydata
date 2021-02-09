package com.banksalad.collectmydata.capital.account.dto;


import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@ToString
public class TransactionResponse {
  private String rspCode;
  private String rspMsg;
  private String nextPage;
  private int transCnt;
  private List<Transaction> transList;
}
