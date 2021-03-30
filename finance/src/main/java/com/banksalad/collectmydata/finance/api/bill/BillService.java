package com.banksalad.collectmydata.finance.api.bill;

import com.banksalad.collectmydata.common.collect.execution.Execution;
import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.finance.common.exception.ResponseNotOkException;

import java.util.List;

public interface BillService<BillRequest, Bill, BillDetail> {

  List<Bill> listBills(
      ExecutionContext executionContext,
      Execution execution,
      BillRequestHelper<BillRequest> requestHelper,
      BillResponseHelper<Bill> responseHelper
  ) throws ResponseNotOkException;

  List<BillDetail> listBillDetails(
      ExecutionContext executionContext,
      Execution execution,
      List<Bill> bills,
      BillDetailRequestHelper<Bill> requestHelper,
      BillDetailResponseHelper<BillDetail> responseHelper
  );
}
