package com.banksalad.collectmydata.bank.common.collect;

import com.banksalad.collectmydata.bank.common.dto.ListAccountSummariesResponse;
import com.banksalad.collectmydata.bank.depoist.dto.GetDepositAccountBasicResponse;
import com.banksalad.collectmydata.bank.common.dto.ListAccountSummariesResponse;
import com.banksalad.collectmydata.bank.invest.dto.GetInvestAccountBasicResponse;
import com.banksalad.collectmydata.bank.invest.dto.GetInvestAccountDetailResponse;
import com.banksalad.collectmydata.common.collect.execution.Execution;

public class Executions {

  public static final Execution finance_bank_accounts =
      Execution.create()
          .exchange(Apis.finance_bank_accounts)
          .as(ListAccountSummariesResponse.class)
          .build();

  public static final Execution finance_bank_deposit_account_basic =
      Execution.create()
          .exchange(Apis.finance_bank_deposit_account_basic)
          .as(GetDepositAccountBasicResponse.class)
          .build();

  public static final Execution finance_bank_invest_account_basic =
      Execution.create()
          .exchange(Apis.finance_bank_invest_account_basic)
          .as(GetInvestAccountBasicResponse.class)
          .build();

  public static final Execution finance_bank_invest_account_detail =
      Execution.create()
          .exchange(Apis.finance_bank_invest_account_detail)
          .as(GetInvestAccountDetailResponse.class)
          .build();
}
