package com.banksalad.collectmydata.card.card.dto;

import com.banksalad.collectmydata.finance.api.bill.dto.BillTransactionResponse;
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
public class ListBillDetailResponse implements BillTransactionResponse {

  private String rspCode;

  private String rspMsg;

  private String nextPage;

  private int billDetailCnt;

  @JsonProperty("bill_detail_list")
  private List<BillDetail> billDetails;
}
