package com.banksalad.collectmydata.capital.common.collect;

import com.banksalad.collectmydata.capital.account.dto.AccountDetailResponse;
import com.banksalad.collectmydata.capital.account.dto.AccountResponse;
import com.banksalad.collectmydata.capital.account.dto.TransactionResponse;
import com.banksalad.collectmydata.common.collect.execution.Execution;

public class Executions {

  // 6.7.1 계좌 목록 조회
  public static final Execution capital_get_accounts =
      Execution.create()
          .exchange(Apis.capital_get_accounts)
          .as(AccountResponse.class)
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
                  .as(TransactionResponse.class)
                  .build();
}
