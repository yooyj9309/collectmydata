package com.banksalad.collectmydata.invest.collect;

import com.banksalad.collectmydata.common.collect.execution.Execution;
import com.banksalad.collectmydata.invest.common.dto.ListAccountSummariesResponse;

public class Executions {

  public static final Execution finance_invest_accounts =
      Execution.create()
          .exchange(Apis.finance_invest_accounts)
          .as(ListAccountSummariesResponse.class)
          .build();
}
