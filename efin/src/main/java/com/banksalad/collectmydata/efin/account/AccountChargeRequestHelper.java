package com.banksalad.collectmydata.efin.account;

import org.springframework.stereotype.Component;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.efin.summary.dto.AccountSummary;
import com.banksalad.collectmydata.finance.api.accountinfo.AccountInfoRequestHelper;
import com.github.banksalad.idl.apis.v1.openbanking.kftc.KftcProto.GetAccountBalanceRequest;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AccountChargeRequestHelper implements AccountInfoRequestHelper<GetAccountBalanceRequest, AccountSummary> {

  @Override
  public List<AccountSummary> listSummaries(ExecutionContext executionContext) {
    //TODO 구현 필요
    return null;
  }

  @Override
  public GetAccountBalanceRequest make(ExecutionContext executionContext, AccountSummary accountSummary) {
    //TODO 구현 필요
    return null;
  }
}
