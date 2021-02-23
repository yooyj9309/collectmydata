package com.banksalad.collectmydata.referencebank.account;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.collect.execution.ExecutionRequest;
import com.banksalad.collectmydata.common.collect.execution.ExecutionResponse;
import com.banksalad.collectmydata.common.collect.executor.CollectExecutor;
import com.banksalad.collectmydata.common.exception.CollectException;
import com.banksalad.collectmydata.referencebank.account.dto.Account;
import com.banksalad.collectmydata.referencebank.account.dto.ListAccountsRequest;
import com.banksalad.collectmydata.referencebank.account.dto.ListAccountsResponse;
import com.banksalad.collectmydata.referencebank.collect.Executions;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class AccountServiceImpl implements AccountService {

  private final CollectExecutor collectExecutor;

  public AccountServiceImpl(CollectExecutor collectExecutor) {
    this.collectExecutor = collectExecutor;
  }

  @Override
  public List<Account> listAccounts(ExecutionContext executionContext) throws CollectException {

    /* request header */
    //Map<String, String> header = headerService.makeHeader(banksaladUserId, organizationId);
    Map<String, String> header = Map.of("Authorization", executionContext.getAccessToken());

    List<Account> accounts = new ArrayList<>();
    ExecutionResponse<ListAccountsResponse> executionResponse = null;

    do {
      /* request body & execution */
      ExecutionRequest<ListAccountsRequest> executionRequest =
          ExecutionRequest.<ListAccountsRequest>builder()
              .headers(header)
              .request(
                  ListAccountsRequest.builder()
                      .orgCode("020")
                      .searchTimestamp(0L)
                      .limit(500)
                      .nextPage(executionResponse != null ? executionResponse.getNextPage() : null)
                      .build())
              .build();

      executionResponse = collectExecutor.execute(executionContext, Executions.finance_bank_accounts, executionRequest);

      if (executionResponse.getHttpStatusCode() != HttpStatus.OK.value()) {
        throw new CollectException("getAccounts Statue is not OK");
      }

      accounts.addAll(saveAccounts(executionResponse.getResponse()));

    } while (executionResponse.getNextPage() != null);

    return accounts;
  }

  private List<Account> saveAccounts(ListAccountsResponse listAccountsResponse) {
    return listAccountsResponse.getAccountList();
  }
}
