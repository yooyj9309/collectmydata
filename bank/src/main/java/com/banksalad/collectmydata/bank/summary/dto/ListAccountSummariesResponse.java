package com.banksalad.collectmydata.bank.summary.dto;

import com.banksalad.collectmydata.finance.api.summary.dto.SummaryResponse;
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
public class ListAccountSummariesResponse implements SummaryResponse {

  private String rspCode;

  private String rspMsg;

  private long searchTimestamp;

  private String regDate;

  private String nextPage;

  private int accountCnt;

  private List<AccountSummary> accountList;
}
