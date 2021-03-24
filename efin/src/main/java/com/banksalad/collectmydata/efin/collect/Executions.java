package com.banksalad.collectmydata.efin.collect;

import com.banksalad.collectmydata.common.collect.execution.Execution;
import com.banksalad.collectmydata.efin.account.dto.GetAccountChargeResponse;
import com.banksalad.collectmydata.efin.account.dto.ListAccountBalancesResponse;
import com.banksalad.collectmydata.efin.account.dto.ListAccountPrepaidTransactionsResponse;
import com.banksalad.collectmydata.efin.account.dto.ListAccountTransactionsResponse;
import com.banksalad.collectmydata.efin.summary.dto.ListAccountSummariesResponse;

public class Executions {

  // 6.6.1 전자지급수단 목록 조회
  public static final Execution finance_efin_summaries =
      Execution.create()
          .exchange(Apis.finance_efin_summaries)
          .as(ListAccountSummariesResponse.class)
          .build();

  // 6.6.2 전자지급수단 잔액정보 조회
  public static Execution finance_efin_balances =
      Execution.create()
          .exchange(Apis.finance_efin_balances)
          .as(ListAccountBalancesResponse.class)
          .build();

  // 6.6.3 전자지급수단 자동충전정보 조회
  public static Execution finance_efin_charge =
      Execution.create()
          .exchange(Apis.finance_efin_charge)
          .as(GetAccountChargeResponse.class)
          .build();

  // 6.6.4 선불 거래내역 조회
  public static Execution finance_efin_prepaid_transactions =
      Execution.create()
          .exchange(Apis.finance_efin_prepaid_transactions)
          .as(ListAccountPrepaidTransactionsResponse.class)
          .build();

  // 6.6.5 결제내역 조회
  public static Execution finance_efin_transactions =
      Execution.create()
          .exchange(Apis.finance_efin_transactions)
          .as(ListAccountTransactionsResponse.class)
          .build();


}
