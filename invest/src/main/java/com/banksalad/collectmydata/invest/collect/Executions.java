package com.banksalad.collectmydata.invest.collect;

import com.banksalad.collectmydata.common.collect.execution.Execution;
import com.banksalad.collectmydata.invest.account.dto.GetAccountBasicResponse;
import com.banksalad.collectmydata.invest.summary.dto.ListAccountSummariesResponse;


public class Executions {

  public static final Execution finance_invest_accounts =
      Execution.create()
          .exchange(Apis.finance_invest_accounts)
          .as(ListAccountSummariesResponse.class)
          .build();

  public static final Execution finance_invest_account_basic =
      Execution.create()
          .exchange(Apis.finance_invest_account_basic)
          .as(GetAccountBasicResponse.class)
          .build();

}
