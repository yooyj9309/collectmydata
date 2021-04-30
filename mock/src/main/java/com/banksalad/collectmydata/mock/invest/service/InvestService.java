package com.banksalad.collectmydata.mock.invest.service;

import com.banksalad.collectmydata.mock.invest.dto.InvestAccountSummary;
import com.banksalad.collectmydata.mock.invest.dto.InvestAccountSummarySearch;

import java.util.List;

public interface InvestService {

  String getRegistrationDate(InvestAccountSummarySearch investAccountSummarySearch);

  List<InvestAccountSummary> getInvestAccountList(
      InvestAccountSummarySearch investAccountSummarySearch);
}
