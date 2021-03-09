package com.banksalad.collectmydata.insu.common.util;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.enums.Industry;
import com.banksalad.collectmydata.common.enums.MydataSector;
import com.banksalad.collectmydata.common.util.DateUtil;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

public class TestHelper {

  public static final MydataSector SECTOR = MydataSector.FINANCE;
  public static final Industry INDUSTRY = Industry.INSU;
  public static final LocalDateTime SYNCED_AT = LocalDateTime.now();
  public static final long BANKSALAD_USER_ID = 1L;
  public static final String ORGANIZATION_ID = "X-loan";
  public static final String ORGANIZATION_CODE = "020";
  public static final String ORGANIZATION_HOST = "localhost";
  public static final String ACCESS_TOKEN = "accessToken";
  public static final Map<String, String> HEADERS = Map.of("Authorization", ACCESS_TOKEN);

  public static ExecutionContext getExecutionContext(int port) {
    return ExecutionContext.builder()
        .organizationHost("http://" + ORGANIZATION_HOST + ":" + port)
        .accessToken(ACCESS_TOKEN)
        .banksaladUserId(BANKSALAD_USER_ID)
        .organizationId(ORGANIZATION_ID)
        .executionRequestId(UUID.randomUUID().toString())
        .syncStartedAt(LocalDateTime.now(DateUtil.UTC_ZONE_ID))
        .build();
  }
}
