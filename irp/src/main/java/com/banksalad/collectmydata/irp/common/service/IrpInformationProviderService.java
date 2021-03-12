package com.banksalad.collectmydata.irp.common.service;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.irp.common.dto.IrpAccountSummariesResponse;

public interface IrpInformationProviderService {

  IrpAccountSummariesResponse getIrpAccountSummaries(ExecutionContext executionContext, String orgCode, long searchTimeStamp);
}
