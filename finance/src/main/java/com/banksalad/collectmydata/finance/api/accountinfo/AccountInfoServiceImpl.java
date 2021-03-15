package com.banksalad.collectmydata.finance.api.accountinfo;

import com.banksalad.collectmydata.common.collect.execution.Execution;
import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.collect.execution.ExecutionRequest;
import com.banksalad.collectmydata.common.collect.execution.ExecutionResponse;
import com.banksalad.collectmydata.common.collect.executor.CollectExecutor;
import com.banksalad.collectmydata.finance.api.accountinfo.dto.AccountResponse;
import com.banksalad.collectmydata.finance.common.service.UserSyncStatusService;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountInfoServiceImpl<Summary, AccountRequest, Account> implements
    AccountInfoService<Summary, AccountRequest, Account> {

  private static final String AUTHORIZATION = "Authorization";

  private final CollectExecutor collectExecutor;
  private final UserSyncStatusService userSyncStatusService;

  @Override
  public List<Account> listAccountInfos(
      ExecutionContext executionContext,
      Execution execution,
      AccountInfoRequestHelper<AccountRequest, Summary> requestHelper,
      AccountInfoResponseHelper<Summary, Account> responseHelper
  ) {

    List<Summary> summaries = requestHelper.listSummaries(executionContext);

    ExecutionResponse<AccountResponse> executionResponse;
    List<Account> accounts = new ArrayList<>();

    for (Summary summary : summaries) {
      /* copy ExecutionContext for new executionRequestId */
      ExecutionContext executionContextLocal = executionContext.copyWith(ExecutionContext.generateExecutionRequestId());

      executionResponse = collectExecutor.execute(
          executionContextLocal,
          execution,
          ExecutionRequest.builder()
              .headers(Map.of(AUTHORIZATION, executionContext.getAccessToken()))
              .request(requestHelper.make(executionContext, summary))
              .build());

      /* validate response  */
      if (executionResponse.getHttpStatusCode() != HttpStatus.OK.value()) {
        /* update response code to summary table if not ok */
        responseHelper.saveResponseCode(executionContext, summary,
            Optional.ofNullable(executionResponse.getResponse())
                .map(AccountResponse::getRspCode)
                .orElse(String.valueOf(executionResponse.getHttpStatusCode())));

        continue;
      }

      /* update account */
      AccountResponse accountResponse = executionResponse.getResponse();
      Account account = responseHelper.getAccountFromResponse(accountResponse);

      responseHelper.saveAccountAndHistory(executionContext, summary, account);
      accounts.add(account);

      /* update search_timestamp */
      responseHelper.saveSearchTimestamp(executionContext, summary, accountResponse.getSearchTimestamp());
    }

    /* update user_sync_status */
    userSyncStatusService.updateUserSyncStatus(
        executionContext.getBanksaladUserId(),
        executionContext.getOrganizationId(),
        execution.getApi().getId(),
        executionContext.getSyncStartedAt(),
        0
    );

    return accounts;
  }
}
