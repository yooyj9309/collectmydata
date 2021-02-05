package com.banksalad.collectmydata.ri.bank.account;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.collect.execution.ExecutionRequest;
import com.banksalad.collectmydata.common.collect.execution.ExecutionResponse;
import com.banksalad.collectmydata.common.collect.executor.CollectExecutor;
import com.banksalad.collectmydata.common.exception.CollectException;
import com.banksalad.collectmydata.ri.bank.account.dto.Account;
import com.banksalad.collectmydata.ri.bank.account.dto.AccountsRequest;
import com.banksalad.collectmydata.ri.bank.account.dto.AccountsResponse;
import com.banksalad.collectmydata.ri.bank.collect.Executions;

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
  public List<Account> getAccounts(ExecutionContext executionContext) throws CollectException {

    /* request header */
    //Map<String, String> header = headerService.makeHeader(banksaladUserId, organizationId);
    Map<String, String> header = Map.of("Authorization", executionContext.getAccessToken());

    List<Account> accounts = new ArrayList<>();
    ExecutionResponse<AccountsResponse> executionResponse = null;

    do {
      /* request body & execution */
      ExecutionRequest<AccountsRequest> executionRequest =
          ExecutionRequest.<AccountsRequest>builder()
              .headers(header)
              .request(
                  AccountsRequest.builder()
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

  private List<Account> saveAccounts(AccountsResponse accountsResponse) {
    return accountsResponse.getAccountList();
  }
}
