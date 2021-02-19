package com.banksalad.collectmydata.capital.account.dto;


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
public class AccountTransactionResponse {

  private String rspCode;
  private String rspMsg;
  private String nextPage;
  private int transCnt;
  private List<AccountTransaction> transList;
}
