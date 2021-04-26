package com.banksalad.collectmydata.irp.api;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;

import java.util.List;

public interface AccountInfoRequestPaginationHelper<AccountRequest, Summary> {

  List<Summary> listSummaries(ExecutionContext executionContext);

  AccountRequest make(ExecutionContext executionContext, Summary summary, String nextPage);

}