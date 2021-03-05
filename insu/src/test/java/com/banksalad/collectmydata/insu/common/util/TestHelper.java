package com.banksalad.collectmydata.insu.common.util;

import com.banksalad.collectmydata.common.enums.Industry;
import com.banksalad.collectmydata.common.enums.MydataSector;

import java.time.LocalDateTime;
import java.util.Map;

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

}
