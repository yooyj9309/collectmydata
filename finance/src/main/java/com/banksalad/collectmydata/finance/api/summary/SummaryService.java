package com.banksalad.collectmydata.finance.api.summary;

import com.banksalad.collectmydata.common.collect.execution.Execution;
import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.finance.common.exception.ResponseNotOkException;

public interface SummaryService<SummaryRequest, Summary> {

  @Deprecated
  void listAccountSummaries(
      ExecutionContext executionContext,
      Execution execution,
      SummaryRequestHelper<SummaryRequest> requestHelper,
      SummaryResponseHelper<Summary> responseHelper
  ) throws ResponseNotOkException;

  void listAccountSummaries(
      ExecutionContext executionContext,
      Execution execution,
      SummaryRequestHelper<SummaryRequest> requestHelper,
      SummaryResponseHelper<Summary> responseHelper,
      SummaryPublishmentHelper publishmentHelper
  ) throws ResponseNotOkException;
}
