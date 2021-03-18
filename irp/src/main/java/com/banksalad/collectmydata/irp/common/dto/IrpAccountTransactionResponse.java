package com.banksalad.collectmydata.irp.common.dto;

import com.banksalad.collectmydata.finance.api.transaction.dto.TransactionResponse;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class IrpAccountTransactionResponse implements TransactionResponse {

  private String rspCode;
  private String rspMsg;
  private String nextPage;

  private int transCnt;

  @JsonProperty(value = "trans_list")
  @Builder.Default
  private List<IrpAccountTransaction> irpAccountTransactions = new ArrayList<>();
}
