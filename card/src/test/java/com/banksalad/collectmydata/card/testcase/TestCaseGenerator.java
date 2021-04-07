package com.banksalad.collectmydata.card.testcase;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;

import java.util.UUID;

import static com.banksalad.collectmydata.card.testcase.FinanceConstants.ACCESS_TOKEN;
import static com.banksalad.collectmydata.card.testcase.FinanceConstants.BANKSALAD_USER_ID;
import static com.banksalad.collectmydata.card.testcase.FinanceConstants.NEW_SYNCED_AT;
import static com.banksalad.collectmydata.card.testcase.FinanceConstants.ORGANIZATION_CODE;
import static com.banksalad.collectmydata.card.testcase.FinanceConstants.ORGANIZATION_HOST;
import static com.banksalad.collectmydata.card.testcase.FinanceConstants.ORGANIZATION_ID;

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
