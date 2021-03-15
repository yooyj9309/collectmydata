package com.banksalad.collectmydata.finance.api.summary;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;

public interface SummaryRequestHelper<SummaryRequest> {

  SummaryRequest make(ExecutionContext executionContext, long searchTimestamp, String nextPage);
}
