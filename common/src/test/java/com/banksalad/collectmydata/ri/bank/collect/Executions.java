package com.banksalad.collectmydata.ri.bank.collect;

import com.banksalad.collectmydata.common.collect.execution.Execution;
import com.banksalad.collectmydata.ri.bank.account.dto.AccountsResponse;

public class Executions {

  public static final Execution finance_bank_accounts =
      Execution.create()
          .exchange(Apis.finance_bank_accounts)
          .as(AccountsResponse.class)
          .build();
}
