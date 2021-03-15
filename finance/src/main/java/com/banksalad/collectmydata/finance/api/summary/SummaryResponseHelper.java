package com.banksalad.collectmydata.finance.api.summary;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.finance.api.summary.dto.SummaryResponse;

import java.util.Iterator;

public interface SummaryResponseHelper<SummaryDto> {

  Iterator<SummaryDto> iterator(SummaryResponse response);

  void saveOragnizationUser(ExecutionContext executionContext, SummaryResponse response);

  void saveSummary(ExecutionContext executionContext, SummaryDto summaryDto);
}
