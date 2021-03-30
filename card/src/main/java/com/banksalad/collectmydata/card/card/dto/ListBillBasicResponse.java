package com.banksalad.collectmydata.card.card.dto;

import com.banksalad.collectmydata.finance.api.bill.dto.BillResponse;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class ListBillBasicResponse implements BillResponse {

  private String rspCode;

  private String rspMsg;

  private String nextPage;

  private int billCnt;

  @JsonProperty("bill_list")
  private List<BillBasic> billBasics;
}
