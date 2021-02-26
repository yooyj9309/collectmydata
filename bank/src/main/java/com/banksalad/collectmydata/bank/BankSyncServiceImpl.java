package com.banksalad.collectmydata.bank;

import org.springframework.stereotype.Service;

import com.banksalad.collectmydata.bank.common.dto.Account;
import com.banksalad.collectmydata.bank.common.service.AccountService;
import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.logging.CollectLogbackJsonLayout;
import com.banksalad.collectmydata.common.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class BankSyncServiceImpl implements BankSyncService {

  private final AccountService accountService;

  public BankSyncServiceImpl(AccountService accountService) {
    this.accountService = accountService;
  }

  @Override
  public void sync(long banksaladUserId, String organizationId) {

    ExecutionContext executionContext = ExecutionContext.builder()
        .banksaladUserId(banksaladUserId)
        .organizationId(organizationId)
        .accessToken("fixme")
        .organizationHost("http://whatever")
        .executionRequestId(UUID.randomUUID().toString())
        .syncStartedAt(LocalDateTime.now(DateUtil.KST_ZONE_ID))
        .build();

    MDC.put(CollectLogbackJsonLayout.JSON_KEY_BANKSALAD_USER_ID, String.valueOf(banksaladUserId));
    MDC.put(CollectLogbackJsonLayout.JSON_KEY_ORGANIZATION_ID, organizationId);

    log.info("collectmydata-bank sync start");

    List<Account> accounts = accountService.listAccounts(executionContext);
  }
}
