package com.banksalad.collectmydata.referencebank.collect;

import com.banksalad.collectmydata.common.collect.execution.Execution;
import com.banksalad.collectmydata.referencebank.account.dto.ListAccountsResponse;

public class Executions {

  public static final Execution finance_bank_accounts =
      Execution.create()
          .exchange(Apis.finance_bank_accounts)
          .as(ListAccountsResponse.class)
          .build();
}
