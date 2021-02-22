package com.banksalad.collectmydata.common.collect.execution;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Getter
public class ExecutionContext {

  private final String syncRequestId;
  private String executionRequestId;
  private final long banksaladUserId;
  private final String organizationId;
  private final String accessToken;
  private final String organizationHost;
  private final LocalDateTime syncStartedAt;

  public void generateAndsUpdateExecutionRequestId() {
    this.executionRequestId = UUID.randomUUID().toString();
  }
}
