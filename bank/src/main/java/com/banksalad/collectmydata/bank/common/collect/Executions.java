package com.banksalad.collectmydata.bank.common.collect;

import com.banksalad.collectmydata.bank.common.dto.ListAccountSummariesResponse;
import com.banksalad.collectmydata.common.collect.execution.Execution;

public class Executions {

  public static final Execution finance_bank_accounts =
      Execution.create()
          .exchange(Apis.finance_bank_accounts)
          .as(ListAccountSummariesResponse.class)
          .build();
}
