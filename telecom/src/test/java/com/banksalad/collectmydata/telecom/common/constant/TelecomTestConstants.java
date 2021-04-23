package com.banksalad.collectmydata.telecom.common.constant;

import java.time.LocalDateTime;

import static com.banksalad.collectmydata.common.util.DateUtil.utcLocalDateTimeToKstYearMonthString;
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.NEW_SYNCED_AT;

public final class TelecomTestConstants {

  public static final LocalDateTime LAST_LAST_MONTH_SYNCED_AT = NEW_SYNCED_AT.minusMonths(2);
  public static final LocalDateTime LAST_MONTH_SYNCED_AT = NEW_SYNCED_AT.minusMonths(1);
  public static final Integer LAST_LAST_CHARGE_MONTH = Integer
      .valueOf(utcLocalDateTimeToKstYearMonthString(LAST_LAST_MONTH_SYNCED_AT));
  public static final Integer LAST_CHARGE_MONTH = Integer
      .valueOf(utcLocalDateTimeToKstYearMonthString(LAST_MONTH_SYNCED_AT));
  public static final String CHARGE_MONTH = "202105";
  public static final String MGMT_ID1 = "mgmt_id1";
  public static final String MGMT_ID2 = "mgmt_id2";
}
