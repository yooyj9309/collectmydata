package com.banksalad.collectmydata.bank.invest;

import org.springframework.stereotype.Service;

import com.banksalad.collectmydata.bank.common.dto.AccountSummary;
import com.banksalad.collectmydata.bank.invest.dto.InvestAccountBasic;
import com.banksalad.collectmydata.bank.invest.dto.InvestAccountDetail;
import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.organization.Organization;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InvestAccountServiceImpl implements InvestAccountService {

  @Override
  public List<InvestAccountBasic> listInvestAccountBasics(ExecutionContext executionContext, Organization organization,
      List<AccountSummary> accountSummaries) {
    return null;
  }

  @Override
  public List<InvestAccountDetail> listInvestAccountDetails(ExecutionContext executionContext,
      Organization organization, List<AccountSummary> accountSummaries) {
    return null;
  }
}
