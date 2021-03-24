package com.banksalad.collectmydata.ginsu.collect;

import com.banksalad.collectmydata.common.collect.execution.Execution;
import com.banksalad.collectmydata.ginsu.summary.dto.ListGinsuSummariesResponse;

public class Executions {

  public static final Execution finance_ginsu_summaries =
      Execution.create()
          .exchange(Apis.finance_ginsu_summaries)
          .as(ListGinsuSummariesResponse.class)
          .build();
}
