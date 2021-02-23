package com.banksalad.collectmydata.capital.account;

import com.banksalad.collectmydata.capital.account.dto.Account;
import com.banksalad.collectmydata.capital.account.dto.AccountInfo;
import com.banksalad.collectmydata.capital.account.dto.AccountTransaction;
import com.banksalad.collectmydata.capital.common.dto.Organization;
import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;

import java.util.List;

public interface AccountService {

  List<Account> listAccounts(ExecutionContext executionContext, Organization organization);

  List<AccountInfo> listAccountInfo(ExecutionContext executionContext, Organization organization,
      List<Account> accounts);

  List<AccountInfo> listAccountBasicInfo(ExecutionContext executionContext, Organization organization,
      List<Account> accounts);

  List<AccountInfo> listAccountDetailInfo(ExecutionContext executionContext, Organization organization,
      List<Account> accounts);

  List<AccountTransaction> listAccountTransaction(ExecutionContext executionContext, Organization organization,
      List<Account> accounts);

  void updateSearchTimestampForAccount(long banksaladUserId, String organizationId, Account account);
}
