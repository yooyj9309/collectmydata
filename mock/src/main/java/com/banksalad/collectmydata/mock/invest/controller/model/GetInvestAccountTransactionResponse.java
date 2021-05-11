package com.banksalad.collectmydata.mock.invest.controller.model;

import com.banksalad.collectmydata.mock.invest.dto.InvestAccountTransaction;
import com.banksalad.collectmydata.mock.invest.dto.InvestAccountTransactionPage;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class GetInvestAccountTransactionResponse {

  private String nextPage;
  private Integer transCnt;
  private List<InvestAccountTransaction> investAccountTransactionList;
}
