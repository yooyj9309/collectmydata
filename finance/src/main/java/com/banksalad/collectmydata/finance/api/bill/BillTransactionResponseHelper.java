package com.banksalad.collectmydata.finance.api.bill;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.finance.api.bill.dto.BillTransactionResponse;

import java.util.List;

public interface BillTransactionResponseHelper<Bill, BillTransaction> {

  List<BillTransaction> getBillTransactionsFromResponse(BillTransactionResponse response);

  void saveBillTransactions(ExecutionContext executionContext, Bill bill, List<BillTransaction> billDetails);
}
