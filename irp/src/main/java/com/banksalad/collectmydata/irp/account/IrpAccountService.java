package com.banksalad.collectmydata.irp.account;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.irp.common.dto.IrpAccountBasicResponse;
import com.banksalad.collectmydata.irp.common.dto.IrpAccountDetail;
import com.banksalad.collectmydata.irp.common.dto.IrpAccountSummary;

import java.util.List;

public interface IrpAccountService {

  List<IrpAccountBasicResponse> getIrpAccountBasics(ExecutionContext executionContext,
      List<IrpAccountSummary> accountSummaries);

  List<IrpAccountDetail> listIrpAccountDetails(ExecutionContext executionContext,
      List<IrpAccountSummary> accountSummaries);

}
