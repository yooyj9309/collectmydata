package com.banksalad.collectmydata.finance.api.userbase;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.finance.api.userbase.dto.UserBaseResponse;

public interface UserBaseResponseHelper<UserBaseInfo> {

  UserBaseInfo getUserBaseInfoFromResponse(UserBaseResponse userBaseResponse);

  void saveUserBaseInfo(ExecutionContext executionContext, UserBaseInfo userBaseInfo);

//  void saveResponseCode(ExecutionContext executionContext, String responseCode);
  
}
