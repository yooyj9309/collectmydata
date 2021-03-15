package com.banksalad.collectmydata.referencebank.collect;

import com.banksalad.collectmydata.common.collect.execution.Execution;
import com.banksalad.collectmydata.referencebank.deposit.dto.GetDepositAccountBasicResponse;
import com.banksalad.collectmydata.referencebank.deposit.dto.GetDepositAccountDetailResponse;
import com.banksalad.collectmydata.referencebank.deposit.dto.ListDepositAccountTransactionsResponse;
import com.banksalad.collectmydata.referencebank.summaries.dto.ListAccountSummariesResponse;

public class Executions {

  public static final Execution finance_bank_summaries =
      Execution.create()
          .exchange(Apis.finance_bank_summaries)
          .as(ListAccountSummariesResponse.class)
          .build();

  public static final Execution finance_bank_deposit_account_basic =
      Execution.create()
          .exchange(Apis.finance_bank_deposit_account_basic)
          .as(GetDepositAccountBasicResponse.class)
          .build();

  public static final Execution finance_bank_deposit_account_detail =
      Execution.create()
          .exchange(Apis.finance_bank_deposit_account_detail)
          .as(GetDepositAccountDetailResponse.class)
          .build();

  public static final Execution finance_bank_deposit_account_transaction =
      Execution.create()
          .exchange(Apis.finance_bank_deposit_account_transaction)
          .as(ListDepositAccountTransactionsResponse.class)
          .build();
  
}
