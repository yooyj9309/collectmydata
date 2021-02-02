package com.banksalad.collectmydata.bank.collect;

import com.banksalad.collectmydata.bank.account.dto.AccountsResponse;
import com.banksalad.collectmydata.common.collect.execution.Execution;

public class Executions {

  public static final Execution finance_bank_accounts =
      Execution.create()
          .exchange(Apis.finance_bank_accounts)
          .as(AccountsResponse.class)
          .build();
}
