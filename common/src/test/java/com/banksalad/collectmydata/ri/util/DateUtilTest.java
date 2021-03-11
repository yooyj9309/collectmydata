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
  public void convertLocalDateTimeToStringOfDate() {
    LocalDateTime localDateTime = LocalDateTime.of(2020, 10, 10, 18, 0, 0);
    assertEquals("20201010180000", DateUtil.toDateString(localDateTime));
  }

  @Test
  @DisplayName("LocalDateTime to LocalDate")
  public void convertLocalDateTimeToLocalDate() {
    LocalDateTime localDateTime = LocalDateTime.of(2020, 10, 10, 18, 0, 0);
    assertEquals(DateUtil.toLocalDate("20201010"), DateUtil.toLocalDate(localDateTime));
  }

  @Test
  @DisplayName("UTC LocalDateTime to String(yyyyMMdd) 테스트")
  public void convertUtcLocalDateTimeToString() {

    LocalDateTime utcTime1 = LocalDateTime.of(2020, 10, 10, 17, 0, 0);
    assertEquals("20201011", DateUtil.utcLocalDateTimeToKstDateString(utcTime1));

    LocalDateTime utcTime2 = LocalDateTime.of(2020, 10, 10, 16, 0, 0);
    assertEquals("20201011", DateUtil.utcLocalDateTimeToKstDateString(utcTime2));

    LocalDateTime utcTime3 = LocalDateTime.of(2020, 10, 10, 15, 0, 0);
    assertEquals("20201011", DateUtil.utcLocalDateTimeToKstDateString(utcTime3));

    LocalDateTime utcTime4 = LocalDateTime.of(2020, 10, 10, 14, 0, 0);
    assertEquals("20201010", DateUtil.utcLocalDateTimeToKstDateString(utcTime4));
  }
}
