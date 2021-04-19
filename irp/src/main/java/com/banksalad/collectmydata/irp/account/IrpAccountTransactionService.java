package com.banksalad.collectmydata.irp.account;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;

public interface IrpAccountTransactionService {

  void listTransactions(ExecutionContext executionContext);
}
