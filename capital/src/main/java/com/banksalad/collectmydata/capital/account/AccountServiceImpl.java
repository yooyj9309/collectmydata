package com.banksalad.collectmydata.capital.account;

import org.springframework.stereotype.Service;

import com.banksalad.collectmydata.capital.account.dto.Account;
import com.banksalad.collectmydata.capital.common.dto.Organization;
import com.banksalad.collectmydata.capital.common.service.ExternalApiService;
import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

  private final ExternalApiService externalApiService;

  public static final String AUTHORIZATION = "Authorization";

  @Override
  public void syncAccounts(ExecutionContext executionContext, Organization organization) {

    Long banksaladUserId = executionContext.getBanksaladUserId();
    String organizationId = executionContext.getOrganizationId();

    List<Account> accounts = externalApiService.getAccounts(executionContext, organization);

  }

}
