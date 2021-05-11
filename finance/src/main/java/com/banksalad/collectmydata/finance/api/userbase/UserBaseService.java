package com.banksalad.collectmydata.finance.api.userbase;

import com.banksalad.collectmydata.common.collect.execution.Execution;
import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.finance.common.exception.ResponseNotOkException;

import java.util.List;

public interface UserBaseService<UserBaseRequest, UserBaseInfo> {

  void getUserBaseInfo(
      ExecutionContext executionContext,
      Execution execution,
      UserBaseRequestHelper<UserBaseRequest> requestHelper,
      UserBaseResponseHelper<UserBaseInfo> responseHelper
  );

  void getUserBaseInfo(
      ExecutionContext executionContext,
      Execution execution,
      UserBaseRequestHelper<UserBaseRequest> requestHelper,
      UserBaseResponseHelper<UserBaseInfo> responseHelper,
      UserbasePublishmentHelper publishmentHelper
  );
}
