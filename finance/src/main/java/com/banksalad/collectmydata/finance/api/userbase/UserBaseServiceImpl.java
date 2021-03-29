package com.banksalad.collectmydata.finance.api.userbase;

import com.banksalad.collectmydata.common.collect.execution.Execution;
import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.collect.execution.ExecutionRequest;
import com.banksalad.collectmydata.common.collect.execution.ExecutionResponse;
import com.banksalad.collectmydata.common.collect.executor.CollectExecutor;
import com.banksalad.collectmydata.common.exception.CollectRuntimeException;
import com.banksalad.collectmydata.finance.api.userbase.dto.UserBaseResponse;
import com.banksalad.collectmydata.finance.common.exception.ResponseNotOkException;
import com.banksalad.collectmydata.finance.common.service.UserSyncStatusService;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserBaseServiceImpl<UserBaseRequest, UserBaseInfo> implements UserBaseService<UserBaseRequest, UserBaseInfo> {

  private static final String AUTHORIZATION = "Authorization";

  private final CollectExecutor collectExecutor;
  private final UserSyncStatusService userSyncStatusService;

  @Override
  public void getUserBaseInfo(
      ExecutionContext executionContext,
      Execution execution,
      UserBaseRequestHelper<UserBaseRequest> requestHelper,
      UserBaseResponseHelper<UserBaseInfo> responseHelper
  ) throws ResponseNotOkException {

    /* copy ExecutionContext for new executionRequestId */
    ExecutionContext executionContextLocal = executionContext.copyWith(ExecutionContext.generateExecutionRequestId());

    if (executionContextLocal.getSyncRequestId() == null) {
      throw new CollectRuntimeException("syncRequestId is not setted");
    }

    long searchTimeStamp = userSyncStatusService.getSearchTimestamp(
        executionContext.getBanksaladUserId(), executionContext.getOrganizationId(), execution.getApi());

    ExecutionResponse<UserBaseResponse> executionResponse = collectExecutor.execute(
        executionContextLocal,
        execution,
        ExecutionRequest.builder()
            .headers(Map.of(AUTHORIZATION, executionContext.getAccessToken()))
            .request(requestHelper.make(executionContext, searchTimeStamp))
            .build());

    /* validate response  */
    checkResponseAndThrow(executionResponse);
    UserBaseResponse userBaseResponse = executionResponse.getResponse();
    UserBaseInfo userBaseInfo = responseHelper.getUserBaseInfoFromResponse(userBaseResponse);

    /* save response */
    responseHelper.saveUserBaseInfo(executionContextLocal, userBaseInfo);

    /* update user_sync_status, search_timestamp */
    userSyncStatusService.updateUserSyncStatus(
        executionContext.getBanksaladUserId(),
        executionContext.getOrganizationId(),
        execution.getApi().getId(),
        executionContext.getSyncStartedAt(),
        userBaseResponse.getSearchTimestamp()
    );
  }

  private void checkResponseAndThrow(ExecutionResponse<UserBaseResponse> executionResponse) throws ResponseNotOkException {
    UserBaseResponse userBaseResponse = executionResponse.getResponse();

    if (executionResponse.getHttpStatusCode() != HttpStatus.OK.value()) {

      throw new ResponseNotOkException(executionResponse.getHttpStatusCode(), userBaseResponse.getRspCode(),
          userBaseResponse.getRspMsg());
    }
  }
}