package com.banksalad.collectmydata.irp.account;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;

public interface IrpAccountService {

  void getIrpAccountBasics(ExecutionContext executionContext);

  void listIrpAccountDetails(ExecutionContext executionContext);
}
