package com.banksalad.collectmydata.capital.common.service;

import org.springframework.stereotype.Service;

import com.banksalad.collectmydata.capital.common.dto.Account;
import com.banksalad.collectmydata.capital.common.dto.Organization;
import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

  private final ExternalApiService externalApiService;

  /**
   * 6.7.1 계좌 목록 조회
   */
  @Override
  public List<Account> listAccounts(ExecutionContext executionContext, Organization organization) {
    externalApiService.getAccounts(executionContext, organization);

    // TODO ...
    List<Account> accounts = null;
    return accounts;
  }
}
