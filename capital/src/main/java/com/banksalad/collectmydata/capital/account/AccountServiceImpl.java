package com.banksalad.collectmydata.capital.account;

import org.springframework.stereotype.Service;

import com.banksalad.collectmydata.capital.common.dto.Organization;
import com.banksalad.collectmydata.capital.common.service.ExternalApiService;
import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

  private final ExternalApiService externalApiService;

  @Override
  public void syncAllAccounts(ExecutionContext executionContext, Organization organization) {
    externalApiService.getAccounts(executionContext, organization);

    // TODO ...
  }
}
