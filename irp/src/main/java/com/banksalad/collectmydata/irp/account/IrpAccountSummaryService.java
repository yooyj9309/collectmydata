package com.banksalad.collectmydata.irp.account;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.irp.common.dto.IrpAccountSummary;

import java.util.List;

public interface IrpAccountSummaryService {

  List<IrpAccountSummary> listAccountSummaries(ExecutionContext executionContext);

  void updateBasicSearchTimestamp(long banksaladUserId, String organizationId, IrpAccountSummary irpAccountSummary,
      long basicSearchTimestamp);

  void updateDetailSearchTimestamp(long banksaladUserId, String organizationId, IrpAccountSummary irpAccountSummary,
      long detailSearchTimestamp);
}
