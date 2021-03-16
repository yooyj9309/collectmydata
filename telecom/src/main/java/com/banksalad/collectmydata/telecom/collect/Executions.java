package com.banksalad.collectmydata.telecom.collect;

import com.banksalad.collectmydata.common.collect.execution.Execution;
import com.banksalad.collectmydata.telecom.common.dto.ListTelecomSummariesResponse;
import com.banksalad.collectmydata.telecom.telecom.dto.ListTelecomBillsResponse;
import com.banksalad.collectmydata.telecom.telecom.dto.ListTelecomPaidTransactionsResponse;
import com.banksalad.collectmydata.telecom.telecom.dto.ListTelecomTransactionsResponse;

public class Executions {

  // 6.9.1 통신 계약 목록 조회
  public static final Execution finance_telecom_summaries =
      Execution.create()
          .exchange(Apis.finance_telecom_summaries)
          .as(ListTelecomSummariesResponse.class)
          .build();

  // 6.9.2 청구 정보 조회
  public static Execution finance_telecom_bills =
      Execution.create()
          .exchange(Apis.finance_telecom_bills)
          .as(ListTelecomBillsResponse.class)
          .build();

  // 6.9.3 통신 거래내역 조회
  public static Execution finance_telecom_transactions =
      Execution.create()
          .exchange(Apis.finance_telecom_transactions)
          .as(ListTelecomTransactionsResponse.class)
          .build();

  // 6.9.4 결제내역 조회
  public static Execution finance_telecom_paid_transactions =
      Execution.create()
          .exchange(Apis.finance_telecom_paid_transactions)
          .as(ListTelecomPaidTransactionsResponse.class)
          .build();
}
