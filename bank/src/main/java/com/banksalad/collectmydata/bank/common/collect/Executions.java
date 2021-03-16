package com.banksalad.collectmydata.bank.common.collect;

import com.banksalad.collectmydata.bank.summary.dto.ListAccountSummariesResponse;
import com.banksalad.collectmydata.bank.deposit.dto.ListDepositAccountTransactionsResponse;
import com.banksalad.collectmydata.bank.deposit.dto.GetDepositAccountBasicResponse;
import com.banksalad.collectmydata.bank.deposit.dto.GetDepositAccountDetailResponse;
import com.banksalad.collectmydata.bank.deposit.dto.ListDepositAccountTransactionsResponse;
import com.banksalad.collectmydata.bank.invest.dto.GetInvestAccountBasicResponse;
import com.banksalad.collectmydata.bank.invest.dto.GetInvestAccountDetailResponse;
import com.banksalad.collectmydata.bank.invest.dto.ListInvestAccountTransactionsResponse;
import com.banksalad.collectmydata.bank.loan.dto.GetLoanAccountBasicResponse;
import com.banksalad.collectmydata.bank.loan.dto.GetLoanAccountDetailResponse;
import com.banksalad.collectmydata.bank.summary.dto.ListAccountSummariesResponse;
import com.banksalad.collectmydata.common.collect.execution.Execution;

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

  public static final Execution finance_bank_invest_account_transaction =
      Execution.create()
          .exchange(Apis.finance_bank_invest_account_transaction)
          .as(ListInvestAccountTransactionsResponse.class)
          .build();

  public static final Execution finance_bank_loan_account_basic =
      Execution.create()
          .exchange(Apis.finance_bank_loan_account_basic)
          .as(GetLoanAccountBasicResponse.class)
          .build();

  public static final Execution finance_bank_loan_account_detail =
      Execution.create()
          .exchange(Apis.finance_bank_loan_account_detail)
          .as(GetLoanAccountDetailResponse.class)
          .build();
}
