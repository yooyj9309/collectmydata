package com.banksalad.collectmydata.efin.account;

import org.springframework.stereotype.Component;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.efin.account.dto.AccountCharge;
import com.banksalad.collectmydata.efin.summary.dto.AccountSummary;
import com.banksalad.collectmydata.finance.api.accountinfo.AccountInfoResponseHelper;
import com.banksalad.collectmydata.finance.api.accountinfo.dto.AccountResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AccountChargeResponseHelper implements AccountInfoResponseHelper<AccountSummary, AccountCharge> {

  @Override
  public AccountCharge getAccountFromResponse(AccountResponse accountResponse) {
    //TODO 구현 필요
    return null;
  }

  @Override
  public void saveAccountAndHistory(ExecutionContext executionContext, AccountSummary accountSummary,
      AccountCharge accountCharge) {
    //TODO 구현 필요

  }

  @Override
  public void saveSearchTimestamp(ExecutionContext executionContext, AccountSummary accountSummary,
      long searchTimestamp) {
    //TODO 구현 필요

  }

  @Override
  public void saveResponseCode(ExecutionContext executionContext, AccountSummary accountSummary, String responseCode) {
    //TODO 구현 필요
  }
}
