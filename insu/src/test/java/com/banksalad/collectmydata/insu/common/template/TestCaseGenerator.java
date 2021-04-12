package com.banksalad.collectmydata.insu.common.template;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;

import java.util.UUID;

import static com.banksalad.collectmydata.insu.common.constant.FinanceConstants.ACCESS_TOKEN;
import static com.banksalad.collectmydata.insu.common.constant.FinanceConstants.BANKSALAD_USER_ID;
import static com.banksalad.collectmydata.insu.common.constant.FinanceConstants.NEW_SYNCED_AT;
import static com.banksalad.collectmydata.insu.common.constant.FinanceConstants.ORGANIZATION_CODE;
import static com.banksalad.collectmydata.insu.common.constant.FinanceConstants.ORGANIZATION_HOST;
import static com.banksalad.collectmydata.insu.common.constant.FinanceConstants.ORGANIZATION_ID;

public class TestCaseGenerator {

  public static ExecutionContext generateExecutionContext() {

    return ExecutionContext.builder()
        .syncRequestId(UUID.randomUUID().toString())
        .executionRequestId(UUID.randomUUID().toString())
        .banksaladUserId(BANKSALAD_USER_ID)
        .organizationId(ORGANIZATION_ID)
        .organizationCode(ORGANIZATION_CODE)
        .organizationHost(ORGANIZATION_HOST)
        .accessToken(ACCESS_TOKEN)
        .syncStartedAt(NEW_SYNCED_AT)
        .build();
  }
}
