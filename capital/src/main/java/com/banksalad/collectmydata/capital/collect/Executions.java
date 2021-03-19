package com.banksalad.collectmydata.capital.collect;

import com.banksalad.collectmydata.capital.account.dto.AccountDetailResponse;
import com.banksalad.collectmydata.capital.account.dto.GetAccountBasicResponse;
import com.banksalad.collectmydata.capital.account.dto.ListAccountTransactionsResponse;
import com.banksalad.collectmydata.capital.oplease.dto.GetOperatingLeaseBasicResponse;
import com.banksalad.collectmydata.capital.oplease.dto.OperatingLeaseTransactionResponse;
import com.banksalad.collectmydata.capital.summary.dto.ListAccountSummariesResponse;
import com.banksalad.collectmydata.common.collect.execution.Execution;

public class Executions {

  // 6.7.1 계좌 목록 조회
  public static final Execution capital_get_accounts =
      Execution.create()
          .exchange(Apis.capital_get_accounts)
          .as(ListAccountSummariesResponse.class)
          .build();

  // 6.7.2 대출상품계좌 기본정보 조회
  public static final Execution capital_get_account_basic =
      Execution.create()
          .exchange(Apis.capital_get_account_basic)
          .as(GetAccountBasicResponse.class)
          .build();

  // 6.7.3 대출상품계좌 추가정보 조회
  public static final Execution capital_get_account_detail =
      Execution.create()
          .exchange(Apis.capital_get_account_detail)
          .as(AccountDetailResponse.class)
          .build();

  // 6.7.4 대출상품계좌 거래내역 조회
  public static final Execution capital_get_account_transactions =
      Execution.create()
          .exchange(Apis.capital_get_account_transactions)
          .as(ListAccountTransactionsResponse.class)
          .build();

  // 6.7.5 운용리스 기본정보 조회
  public static final Execution capital_get_operating_lease_basic =
      Execution.create()
          .exchange(Apis.capital_get_operating_lease_basic)
          .as(GetOperatingLeaseBasicResponse.class)
          .build();

  // 6.7.6 운용리스 거래내역 조회
  public static final Execution capital_get_operating_lease_transactions =
      Execution.create()
          .exchange(Apis.capital_get_operating_lease_transactions)
          .as(OperatingLeaseTransactionResponse.class)
          .build();
}
