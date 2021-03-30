package com.banksalad.collectmydata.finance.api.bill;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.finance.api.bill.dto.BillResponse;

import java.util.List;

public interface BillResponseHelper<Bill> {

  List<Bill> getBillsFromResponse(BillResponse billResponse);

  void saveBills(ExecutionContext executionContext, List<Bill> bills);
}
