package com.banksalad.collectmydata.bank.testutil;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;

import java.time.LocalDateTime;
import java.util.UUID;

public final class ExecutionContextUtil {

  private ExecutionContextUtil() {
  }

  public static ExecutionContext create(long banksaladUserId, String organizationId, LocalDateTime syncStartedAt,
      int port) {

    return ExecutionContext.builder()
        .banksaladUserId(banksaladUserId)
        .organizationId(organizationId)
        .syncRequestId(UUID.randomUUID().toString())
        .executionRequestId(UUID.randomUUID().toString())
        .accessToken("test")
        .organizationCode("020")
        .organizationHost("http://localhost:" + port)
        .syncStartedAt(syncStartedAt)
        .build();
  }
}
