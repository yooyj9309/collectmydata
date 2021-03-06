package com.banksalad.collectmydata.mock.invest.service;

import com.banksalad.collectmydata.mock.invest.dto.InvestAccountBasic;
import com.banksalad.collectmydata.mock.invest.dto.InvestAccountBasicSearch;
import com.banksalad.collectmydata.mock.invest.dto.InvestAccountProduct;
import com.banksalad.collectmydata.mock.invest.dto.InvestAccountProductListSearch;
import com.banksalad.collectmydata.mock.invest.dto.InvestAccountSummary;
import com.banksalad.collectmydata.mock.invest.dto.InvestAccountSummarySearch;
import com.banksalad.collectmydata.mock.invest.dto.InvestAccountTransactionPage;
import com.banksalad.collectmydata.mock.invest.dto.InvestAccountTransactionPageSearch;

import java.util.List;

public interface InvestService {

  String getRegistrationDate(InvestAccountSummarySearch investAccountSummarySearch);

  List<InvestAccountSummary> getInvestAccountList(InvestAccountSummarySearch investAccountSummarySearch);

  InvestAccountBasic getInvestAccountBasic(InvestAccountBasicSearch investAccountBasicSearch);

  InvestAccountTransactionPage getInvestAccountTransactionPage(
      InvestAccountTransactionPageSearch investAccountTransactionPageSearch);

  List<InvestAccountProduct> getInvestAccountProductList(InvestAccountProductListSearch investAccountProductListSearch);
}
