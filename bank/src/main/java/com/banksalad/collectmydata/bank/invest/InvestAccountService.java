package com.banksalad.collectmydata.bank.invest;

import com.banksalad.collectmydata.bank.common.dto.AccountSummary;
import com.banksalad.collectmydata.bank.invest.dto.InvestAccountBasic;
import com.banksalad.collectmydata.bank.invest.dto.InvestAccountDetail;
import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.organization.Organization;

import java.util.List;

public interface InvestAccountService {

  List<InvestAccountBasic> listInvestAccountBasics(ExecutionContext executionContext, Organization organization,
      List<AccountSummary> accountSummaries);

  List<InvestAccountDetail> listInvestAccountDetails(ExecutionContext executionContext, Organization organization,
      List<AccountSummary> accountSummaries);

  //TODO: transaction 추가시 메소드 추가할 예정
}
