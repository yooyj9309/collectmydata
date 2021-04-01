package com.banksalad.collectmydata.finance.api.bill;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;

public interface BillTransactionRequestHelper<BillDetailRequest, Bill> {

  int getChargeMonth(Bill bill);

  String getSeqno(Bill bill);

  BillDetailRequest make(ExecutionContext executionContext, Bill bill, String nextPage);
}
