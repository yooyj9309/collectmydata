package com.banksalad.collectmydata.capital.oplease.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

@Getter
@Builder
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor(access = PRIVATE)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class OperatingLeaseTransactionResponse {

  private String rspCode;
  private String rspMsg;
  private String nextPage;
  private int transCnt;

  @JsonProperty(value = "trans_list")
  @Builder.Default
  private List<OperatingLeaseTransaction> operatingLeaseTransactions = new ArrayList<>();

  public void updateFrom(OperatingLeaseTransactionResponse pageResponse) {
    this.rspCode = pageResponse.getRspCode();
    this.rspMsg = pageResponse.getRspMsg();
    this.nextPage = pageResponse.getNextPage();
    this.transCnt += pageResponse.getTransCnt();
    this.operatingLeaseTransactions.addAll(pageResponse.getOperatingLeaseTransactions());
  }
}
