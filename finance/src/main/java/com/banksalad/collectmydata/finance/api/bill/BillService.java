package com.banksalad.collectmydata.finance.api.bill;

import com.banksalad.collectmydata.common.collect.execution.Execution;
import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.finance.common.exception.ResponseNotOkException;

import java.util.List;

public interface BillService<BillRequest, Bill, BillTransactionRequest, BillTransaction> {

  List<Bill> listBills(
      ExecutionContext executionContext,
      Execution execution,
      BillRequestHelper<BillRequest> requestHelper,
      BillResponseHelper<Bill> responseHelper
  );

  List<Bill> listBills(
      ExecutionContext executionContext,
      Execution execution,
      BillRequestHelper<BillRequest> requestHelper,
      BillResponseHelper<Bill> responseHelper,
      BillPublishmentHelper billPublishmentHelper
  );

  void listBillDetails(
      ExecutionContext executionContext,
      Execution execution,
      List<Bill> bills,
      BillTransactionRequestHelper<BillTransactionRequest, Bill> requestHelper,
      BillTransactionResponseHelper<Bill, BillTransaction> responseHelper
  );

  void listBillDetails(
      ExecutionContext executionContext,
      Execution execution,
      List<Bill> bills,
      BillTransactionRequestHelper<BillTransactionRequest, Bill> requestHelper,
      BillTransactionResponseHelper<Bill, BillTransaction> responseHelper,
      BillPublishmentHelper billPublishmentHelper
  );
}
