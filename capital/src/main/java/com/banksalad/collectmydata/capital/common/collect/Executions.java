package com.banksalad.collectmydata.capital.common.collect;

import com.banksalad.collectmydata.capital.account.dto.AccountResponse;
import com.banksalad.collectmydata.common.collect.execution.Execution;

public class Executions {

  public static final Execution capital_get_accounts =
      Execution.create()
          .exchange(Apis.capital_get_accounts)
          .as(AccountResponse.class)
          .build();

}
