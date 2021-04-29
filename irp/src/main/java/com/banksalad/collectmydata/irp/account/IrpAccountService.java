package com.banksalad.collectmydata.irp.account;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;

public interface IrpAccountService {

  void listIrpAccountBasics(ExecutionContext executionContext);

  void listIrpAccountDetails(ExecutionContext executionContext);
}
