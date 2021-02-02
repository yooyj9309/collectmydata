package com.banksalad.collectmydata.common.collect.execution;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder
@Getter
public class ExecutionContext {

  private final long banksaladUserId;
  private final String organizationId;
  private final String executionRequestId;
  private final String accessToken;
  private final String organizationHost;
  private final LocalDateTime syncStartedAt;
}
