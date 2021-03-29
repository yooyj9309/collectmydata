package com.banksalad.collectmydata.finance.api.userbase;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;

public interface UserBaseRequestHelper<UserBaseRequest> {

  UserBaseRequest make(ExecutionContext executionContext, long searchTimestamp);
}
