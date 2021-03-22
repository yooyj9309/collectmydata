package com.banksalad.collectmydata.telecom.telecom.dto;

import com.banksalad.collectmydata.finance.api.transaction.dto.TransactionResponse;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class ListTelecomPaidTransactionsResponse implements TransactionResponse {

  private String rspCode;

  private String rspMsg;

  private String nextPage;

  private int transCnt;

  private List<TelecomPaidTransaction> transList;
}
