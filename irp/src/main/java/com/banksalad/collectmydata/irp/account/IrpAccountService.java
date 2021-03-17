package com.banksalad.collectmydata.irp.account;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.irp.common.dto.IrpAccountBasic;
import com.banksalad.collectmydata.irp.common.dto.IrpAccountDetail;
import com.banksalad.collectmydata.irp.common.dto.IrpAccountSummary;

import java.util.List;

public interface IrpAccountService {

  List<IrpAccountBasic> getIrpAccountBasics(ExecutionContext executionContext);

  List<IrpAccountDetail> listIrpAccountDetails(ExecutionContext executionContext,
      List<IrpAccountSummary> irpAccountSummaries);
}
