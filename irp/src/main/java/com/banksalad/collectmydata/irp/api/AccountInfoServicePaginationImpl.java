package com.banksalad.collectmydata.irp.api;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.banksalad.collectmydata.common.collect.execution.Execution;
import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.collect.execution.ExecutionRequest;
import com.banksalad.collectmydata.common.collect.execution.ExecutionResponse;
import com.banksalad.collectmydata.common.collect.executor.CollectExecutor;
import com.banksalad.collectmydata.finance.api.accountinfo.AccountInfoPublishmentHelper;
import com.banksalad.collectmydata.finance.api.accountinfo.dto.AccountResponse;
import com.banksalad.collectmydata.finance.common.service.FinanceMessageService;
import com.banksalad.collectmydata.finance.common.service.HeaderService;
import com.banksalad.collectmydata.finance.common.service.UserSyncStatusService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountInfoServicePaginationImpl<Summary, AccountRequest, Account> implements
    AccountInfoServicePagination<Summary, AccountRequest, Account> {

  private final CollectExecutor collectExecutor;
  private final UserSyncStatusService userSyncStatusService;
  private final FinanceMessageService financeMessageService;
  private final HeaderService headerService;

  @Override
  public void listAccountInfos(
      ExecutionContext executionContext,
      Execution execution,
      AccountInfoRequestPaginationHelper<AccountRequest, Summary> irpAccountDetailInfoRequestHelper,
      AccountInfoResponsePaginationHelper<AccountRequest, Summary, Account> irpAccountDetailInfoResponseHelper) {
    listAccountInfos(executionContext, execution, irpAccountDetailInfoRequestHelper, irpAccountDetailInfoResponseHelper,
        null);
  }

  @Override
  public void listAccountInfos(
      ExecutionContext executionContext,
      Execution execution,
      AccountInfoRequestPaginationHelper<AccountRequest, Summary> requestHelper,
      AccountInfoResponsePaginationHelper<AccountRequest, Summary, Account> responseHelper,
      AccountInfoPublishmentHelper publishmentHelper) {

    List<Summary> summaries = requestHelper.listSummaries(executionContext);

    ExecutionResponse<AccountResponse> executionResponse;

    for (Summary summary : summaries) {
      /* copy ExecutionContext for new executionRequestId */
      ExecutionContext executionContextLocal = executionContext.copyWith(ExecutionContext.generateExecutionRequestId());

      String nextPage = null;
//      boolean isAllPaginationResponseOk = true;

      do {

        AccountRequest accountRequest = requestHelper.make(executionContext, summary, nextPage);
        executionResponse = collectExecutor.execute(
            executionContextLocal,
            execution,
            ExecutionRequest.builder()
                .headers(headerService.makeHeader(executionContext))
                .request(accountRequest)
                .build());

        /* validate response  */
        if (executionResponse.getHttpStatusCode() != HttpStatus.OK.value()) {
          /* update response code to summary table if not ok */
          responseHelper.saveResponseCode(executionContext, summary,
              Optional.ofNullable(executionResponse.getResponse())
                  .map(AccountResponse::getRspCode)
                  .orElse(String.valueOf(executionResponse.getHttpStatusCode())));
//          isAllPaginationResponseOk = false;
          break;
        }

        /* update account */
        AccountResponse accountResponse = executionResponse.getResponse();
        Account account = responseHelper.getAccountFromResponse(accountResponse);

        /*
         *  1. Timestamp구현(UP-TO-DATE)이면서 이전 데이터와 변경이 없는 경우
         *    - responseSearchTimestamp == 0 or 미회신 && requestSearchTimestamp != 0
         *    - detail_search_timestamp를 responseSearchTimestamp값인 0으로 update하지 않는다.
         *    - 이 경우 synced_at(?)
         *  2. Timestamp구현(UP-TO-DATE)이면서 이전 데이터와 변경이 있는 경우
         *    - responseSearchTimestamp != 0
         *  3. Timestamp미구현 => 이 경우 데이터를 무조건 회신하는 경우로서 데이터 확인(변경여부를 확인)
         *    - responseSearchTimestamp == 0 or 미회신 && requestSearchTimestamp == 0
         *  2, 3의 경우 응답데이터를 전송하는 경우로서 변경 데이터 확인
         */
        long responseSearchTimestamp = accountResponse.getSearchTimestamp();
        long requestSearchTimestamp = responseHelper.getSearchTimestamp(accountRequest);

        // Timestamp구현(UP-TO-DATE)이면서 이전 데이터와 변경이 없는 경우
        if (responseSearchTimestamp == 0 && requestSearchTimestamp != 0) {
          break;
        } else {  // Timestamp구현(UP-TO-DATE)이면서 이전 데이터와 변경이 존재 && Timestamp미구현인 경우

          responseHelper.saveAccountAndHistory(executionContext, summary, account, accountRequest);

          /* update search_timestamp */
          responseHelper.saveSearchTimestamp(executionContext, summary, accountResponse.getSearchTimestamp());
        }

        nextPage = executionResponse.getNextPage();
      } while (executionResponse.getNextPage() != null && executionResponse.getNextPage().length() > 0);
    }

    /* publish */
    // TODO(question): to do or not
    if (publishmentHelper != null) {
      financeMessageService.producePublishmentRequested(publishmentHelper.getMessageTopic(),
          publishmentHelper.makePublishmentRequestedMessage(executionContext));
    }

    /* update user_sync_status */
    userSyncStatusService.updateUserSyncStatus(
        executionContext.getBanksaladUserId(),
        executionContext.getOrganizationId(),
        execution.getApi().getId(),
        executionContext.getSyncStartedAt(),
        0
    );
  }
}
