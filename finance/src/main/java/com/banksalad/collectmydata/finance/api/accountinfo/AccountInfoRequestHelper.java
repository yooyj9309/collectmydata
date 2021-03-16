package com.banksalad.collectmydata.finance.api.accountinfo;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;

import java.util.List;

public interface AccountInfoRequestHelper<AccountRequest, Summary> {

  List<Summary> listSummaries(ExecutionContext executionContext);

  AccountRequest make(ExecutionContext executionContext, Summary summary);

}
