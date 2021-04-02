package com.banksalad.collectmydata.card.card.dto;

import com.banksalad.collectmydata.finance.api.transaction.dto.TransactionResponse;

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
public class ListApprovalOverseasResponse implements TransactionResponse {

  private String rspCode;

  private String rspMsg;

  private String nextPage;

  private int approvedCnt;

  private List<ApprovalOverseas> approvedList;
}
