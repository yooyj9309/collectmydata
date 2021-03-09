package com.banksalad.collectmydata.bank.deposit;

import com.banksalad.collectmydata.bank.common.dto.AccountSummary;
import com.banksalad.collectmydata.bank.deposit.dto.DepositAccountBasic;
import com.banksalad.collectmydata.bank.deposit.dto.DepositAccountDetail;
import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;

import java.util.List;

public interface DepositAccountService {

  List<DepositAccountBasic> listDepositAccountBasics(ExecutionContext executionContext,
      List<AccountSummary> accountSummaries);

  List<DepositAccountDetail> listDepositAccountDetails(ExecutionContext executionContext,
      List<AccountSummary> accountSummaries);
}
