package com.banksalad.collectmydata.finance.test.constant;

import com.banksalad.collectmydata.common.util.DateUtil;

import java.time.LocalDateTime;

public class FinanceTestConstants {

  public static final String[] IGNORING_ENTITY_FIELDS = {"id", "createdAt", "createdBy", "updatedAt",
      "updatedBy"};

  public static final String CONSENT_ID = "consent_id1";
  public static final String SYNC_REQUEST_ID = "sync_request_id1";
  public static final String ORGANIZATION_ID = "organization_id1";
  public static final String ORGANIZATION_CODE = "organization_code1";
  public static final String ORGANIZATION_HOST = "localhost";
  public static final String ACCESS_TOKEN = "xxx.yyy.zzz";
  public static final LocalDateTime OLD_SYNCED_AT = DateUtil.toLocalDateTime("20210401", "101000");
  public static final LocalDateTime NEW_SYNCED_AT = LocalDateTime.now(DateUtil.UTC_ZONE_ID);
  public static final long BANKSALAD_USER_ID = 1L;

  public static final long ZERO_USS_ST = 0L;
  public static final long OLD_USS_ST = 100L;
  public static final long NEW_USS_ST = 200L;
  public static final long OLD_ST1 = 1000L;
  public static final long NEW_ST1 = 1100L;
  public static final long OLD_ST2 = 2000L;
  public static final long NEW_ST2 = 2100L;

  // TODO: enum
  public static final int STATUS_INTERNAL_SERVER_ERROR = 500;
  public static final String RSP_CODE_SYSTEM_FAILURE = "50001";
  public static final int STATUS_NOT_FOUND = 404;
  public static final String RSP_CODE_NO_ACCOUNT = "40402";
  public static final int STATUS_TOO_MANY_REQUEST = 429;
  public static final String RSP_CODE_OVER_QUOTA = "42901";
  public static final int STATUS_FORBIDDEN = 403;
  public static final String RSP_CODE_INVALID_ACCOUNT = "40305";
  public static final int STATUS_OK = 200;
  public static final String RSP_CODE_SUCCESS = "00000";
}
