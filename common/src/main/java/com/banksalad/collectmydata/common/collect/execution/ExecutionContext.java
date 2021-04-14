package com.banksalad.collectmydata.common.collect.execution;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Getter
@Setter
public class ExecutionContext {

  // TODO : make immutuable
  private final String consentId;
  private final String syncRequestId;
  //  private final String executionRequestId;
  private String executionRequestId;
  private final long banksaladUserId;
  private final String organizationId;
  private final String organizationCode;
  private String organizationHost;
  private final String accessToken;
  private final LocalDateTime syncStartedAt;

  @Deprecated
  public void generateAndsUpdateExecutionRequestId() {
    this.executionRequestId = UUID.randomUUID().toString();
  }

  public ExecutionContext copyWith(String executionRequestId) {
    return ExecutionContext.builder()
        .syncRequestId(syncRequestId)
        .executionRequestId(executionRequestId)
        .banksaladUserId(banksaladUserId)
        .organizationId(organizationId)
        .organizationCode(organizationCode)
        .organizationHost(organizationHost)
        .accessToken(accessToken)
        .syncStartedAt(syncStartedAt)
        .build();
  }

  public static String generateExecutionRequestId() {
    return UUID.randomUUID().toString();
  }
}
