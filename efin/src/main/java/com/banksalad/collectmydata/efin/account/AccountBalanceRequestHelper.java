package com.banksalad.collectmydata.efin.account;

import org.springframework.stereotype.Component;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.efin.account.dto.ListAccountBalancesRequest;
import com.banksalad.collectmydata.efin.summary.dto.AccountSummary;
import com.banksalad.collectmydata.finance.api.accountinfo.AccountInfoRequestHelper;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AccountBalanceRequestHelper implements
    AccountInfoRequestHelper<AccountSummary, ListAccountBalancesRequest> {

  @Override
  public List<ListAccountBalancesRequest> listSummaries(ExecutionContext executionContext) {
    //TODO 구현 필요
    return null;
  }

  @Override
  public AccountSummary make(ExecutionContext executionContext, ListAccountBalancesRequest listAccountBalancesRequest) {
    //TODO 구현 필요
    return null;
  }
}
