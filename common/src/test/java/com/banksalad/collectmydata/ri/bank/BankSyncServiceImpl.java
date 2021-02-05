package com.banksalad.collectmydata.ri.bank;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.exception.CollectException;
import com.banksalad.collectmydata.common.util.DateUtil;
import com.banksalad.collectmydata.ri.bank.account.AccountService;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
public class BankSyncServiceImpl implements BankSyncService {

  private final AccountService accountService;

  public BankSyncServiceImpl(AccountService accountService) {
    this.accountService = accountService;
  }

  @Override
  public void sync(long banksaladUserId, String organizationId) throws CollectException {

    ExecutionContext executionContext = ExecutionContext.builder()
        .banksaladUserId(banksaladUserId)
        .organizationId(organizationId)
        .accessToken("fixme")
        .organizationHost("http://whatever")
        .executionRequestId(UUID.randomUUID().toString())
        .syncStartedAt(LocalDateTime.now(DateUtil.UTC_ZONE_ID))
        .build();

    accountService.getAccounts(executionContext);
  }
}
