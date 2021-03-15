package com.banksalad.collectmydata.telecom.collect;

import com.banksalad.collectmydata.common.collect.execution.Execution;
import com.banksalad.collectmydata.telecom.common.dto.ListTelecomSummaryResponse;
import com.banksalad.collectmydata.telecom.telecom.dto.ListTelecomBillResponse;
import com.banksalad.collectmydata.telecom.telecom.dto.ListTelecomPaidTransactionResponse;
import com.banksalad.collectmydata.telecom.telecom.dto.ListTelecomTransactionResponse;

public class Executions {

  // 6.9.1 통신 계약 목록 조회
  public static final Execution finance_telecom_summaries =
      Execution.create()
          .exchange(Apis.finance_telecom_summaries)
          .as(ListTelecomSummaryResponse.class)
          .build();

  // 6.9.2 청구 정보 조회
  public static Execution finance_telecom_bills =
      Execution.create()
          .exchange(Apis.finance_telecom_bills)
          .as(ListTelecomBillResponse.class)
          .build();

  // 6.9.3 통신 거래내역 조회
  public static Execution finance_telecom_transactions =
      Execution.create()
          .exchange(Apis.finance_telecom_transactions)
          .as(ListTelecomTransactionResponse.class)
          .build();

  // 6.9.4 결제내역 조회
  public static Execution finance_telecom_paid_transactions =
      Execution.create()
          .exchange(Apis.finance_telecom_paid_transactions)
          .as(ListTelecomPaidTransactionResponse.class)
          .build();
}
