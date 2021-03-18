package com.banksalad.collectmydata.irp.account;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.irp.common.dto.IrpAccountTransaction;

import java.util.List;

public interface IrpAccountTransactionService {

  List<IrpAccountTransaction> listTransactions(ExecutionContext executionContext);
}
