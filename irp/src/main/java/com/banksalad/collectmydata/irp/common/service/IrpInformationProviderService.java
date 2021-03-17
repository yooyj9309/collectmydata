package com.banksalad.collectmydata.irp.common.service;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.organization.Organization;
import com.banksalad.collectmydata.irp.common.dto.IrpAccountBasicResponse;
import com.banksalad.collectmydata.irp.common.dto.IrpAccountDetailsResponse;
import com.banksalad.collectmydata.irp.common.dto.IrpAccountSummariesResponse;
import com.banksalad.collectmydata.irp.common.dto.IrpAccountSummary;

public interface IrpInformationProviderService {

  IrpAccountSummariesResponse getIrpAccountSummaries(ExecutionContext executionContext, String orgCode,
      long searchTimeStamp);

  IrpAccountBasicResponse getAccountBasic(ExecutionContext executionContext, Organization organization,
      IrpAccountSummary irpAccountSummary);

  IrpAccountDetailsResponse getAccountDetails(ExecutionContext executionContext, Organization organization,
      IrpAccountSummary irpAccountSummary);
}
