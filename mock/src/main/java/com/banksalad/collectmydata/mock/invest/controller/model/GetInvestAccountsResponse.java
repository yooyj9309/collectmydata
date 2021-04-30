package com.banksalad.collectmydata.mock.invest.controller.model;

import com.banksalad.collectmydata.mock.invest.dto.InvestAccountSummary;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class GetInvestAccountsResponse {

  private String regDate;
  private int accountCnt;
  private List<InvestAccountSummary> investAccountSummaryList;
}
