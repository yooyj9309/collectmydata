package com.banksalad.collectmydata.finance.api.summary;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.finance.api.summary.dto.SummaryResponse;

import java.util.Iterator;

public interface SummaryResponseHelper<Summary> {

  Iterator<Summary> iterator(SummaryResponse response);

  void saveOrganizationUser(ExecutionContext executionContext, SummaryResponse response);

  void saveSummary(ExecutionContext executionContext, Summary summary);
}
