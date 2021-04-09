package com.banksalad.collectmydata.finance.test.template;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;

import java.util.UUID;

import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.ACCESS_TOKEN;
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.BANKSALAD_USER_ID;
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.NEW_SYNCED_AT;
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.ORGANIZATION_CODE;
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.ORGANIZATION_HOST;
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.ORGANIZATION_ID;

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
