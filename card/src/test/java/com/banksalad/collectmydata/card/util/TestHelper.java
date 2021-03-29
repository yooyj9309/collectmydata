package com.banksalad.collectmydata.card.util;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.enums.Industry;
import com.banksalad.collectmydata.common.enums.MydataSector;
import com.banksalad.collectmydata.common.util.DateUtil;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

public class TestHelper {

  public static final MydataSector SECTOR = MydataSector.FINANCE;
  public static final Industry INDUSTRY = Industry.CARD;
  public static final LocalDateTime SYNCED_AT = LocalDateTime.now();
  public static final long BANKSALAD_USER_ID = 1L;
  public static final String ORGANIZATION_ID = "X-loan";
  public static final String ORGANIZATION_CODE = "020";
  public static final String ORGANIZATION_HOST = "localhost";
  public static final String ACCESS_TOKEN = "accessToken";
  public static final Map<String, String> HEADERS = Map.of("Authorization", ACCESS_TOKEN);
  public static final String CURRENCY_CODE = "KRW";
  public static final String[] ENTITY_IGNORE_FIELD = {"id", "syncedAt", "createdAt", "createdBy", "updatedAt",
      "updatedBy"};
  public static final String ACCOUNT_NUM = "1234567812345678";

  public static ExecutionContext getExecutionContext(int port) {
    return ExecutionContext.builder()
        .syncRequestId(UUID.randomUUID().toString())
        .banksaladUserId(BANKSALAD_USER_ID)
        .organizationId(ORGANIZATION_ID)
        .organizationCode(ORGANIZATION_CODE)
        .executionRequestId(UUID.randomUUID().toString())
        .organizationCode(ORGANIZATION_CODE)
        .organizationHost("http://" + ORGANIZATION_HOST + ":" + port)
        .accessToken("test")
        .syncStartedAt(LocalDateTime.now(DateUtil.UTC_ZONE_ID))
        .build();
  }

  public static ExecutionContext getExecutionContext(int port, LocalDateTime now) {
    return ExecutionContext.builder()
        .syncRequestId(UUID.randomUUID().toString())
        .banksaladUserId(BANKSALAD_USER_ID)
        .organizationId(ORGANIZATION_ID)
        .organizationCode(ORGANIZATION_CODE)
        .executionRequestId(UUID.randomUUID().toString())
        .organizationCode(ORGANIZATION_CODE)
        .organizationHost("http://" + ORGANIZATION_HOST + ":" + port)
        .accessToken("test")
        .syncStartedAt(now)
        .build();
  }

}
