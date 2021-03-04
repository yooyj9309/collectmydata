package com.banksalad.collectmydata.bank.depoist;

import com.banksalad.collectmydata.bank.common.dto.AccountSummary;
import com.banksalad.collectmydata.bank.depoist.dto.DepositAccountBasic;
import com.banksalad.collectmydata.bank.depoist.dto.DepositAccountDetail;
import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;

import java.util.List;

public interface DepositAccountService {

  List<DepositAccountBasic> listDepositAccountBasics(ExecutionContext executionContext,
      List<AccountSummary> accountSummaries);

  List<DepositAccountDetail> listDepositAccountDetails(ExecutionContext executionContext,
      List<AccountSummary> accountSummaries);
}
