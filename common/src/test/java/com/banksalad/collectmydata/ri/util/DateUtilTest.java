package com.banksalad.collectmydata.ri.util;

import com.banksalad.collectmydata.common.util.DateUtil;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DateUtilTest {

  @Test
  @DisplayName("Dateutil LocalDateTime to EpochMilliSecond 테스트")
  public void convertLocalDateTimeToEpochMilliSecondTest() {
    LocalDateTime localDateTime = LocalDateTime.of(2020, 10, 10, 18, 0, 0);

    //1602352800000 == 2020년 10월 11일 일요일 오전 3:00:00 GMT+09:00
    // EpochMilliSecond 의 경우 +9 시간이 적용되어야함
    assertEquals(1602352800000L, DateUtil.utcLocalDateTimeToEpochMilliSecond(localDateTime));

    //1602320400000 == 2020년 10월 10일 토요일 오후 6:00:00 GMT+09:00
    assertEquals(1602320400000L, DateUtil.kstLocalDateTimeToEpochMilliSecond(localDateTime));
  }

  @Test
  @DisplayName("LocalDateTime to String of Date")
  public void convertLocalDateTimetoStringOfDate() {
    LocalDateTime localDateTime = LocalDateTime.of(2020, 10, 10, 18, 0, 0);
    assertEquals("20201010", DateUtil.localDateTimeToDateString(localDateTime));
  }
}
