package com.banksalad.collectmydata.common.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Period;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Deprecated
// TODO : Do refactoring before use
public class DateUtil {

  public static final ZoneId KST_ZONE_ID = ZoneId.of("Asia/Seoul");
  public static final ZoneId UTC_ZONE_ID = ZoneId.of("UTC");

  public static final String ASIA_SEOUL_ZONEOFFSET = "+09:00";

  private static final DateTimeFormatter yearMonthFormatter = DateTimeFormatter.ofPattern("yyyyMM");
  private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
  private static final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HHmmss");
  private static final DateTimeFormatter datetimeFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

  public static long toDateEpochMilli(String source) {
    validateDate(source);

    return LocalDate.parse(source.replaceAll("[- ]+", ""), dateFormatter)
        .atStartOfDay(KST_ZONE_ID).toInstant().toEpochMilli();
  }

  public static long toDatetimeEpochMilli(String source) {
    validateDate(source);

    return LocalDateTime.parse(source.replaceAll("[-: ]+", ""), datetimeFormatter)
        .atZone(KST_ZONE_ID).toInstant().toEpochMilli();
  }

  public static long toDatetimeEpochMilli(String dateSource, String timeSource) {
    validateDate(dateSource);

    if (timeSource == null || timeSource.isBlank()) {
      timeSource = "000000";
    }

    return toDatetimeEpochMilli(dateSource + timeSource);
  }

  public static long kstLocalDateTimeToEpochMilliSecond(LocalDateTime localDateTime) {
    return localDateTime.toInstant(ZoneOffset.of(ASIA_SEOUL_ZONEOFFSET)).toEpochMilli();
  }

  public static long utcLocalDateTimeToEpochMilliSecond(LocalDateTime localDateTime) {
    return localDateTime.toInstant(ZoneOffset.UTC).toEpochMilli();
  }

  public static String kstCurrentDate() {
    return dateFormatter.format(LocalDate.now(KST_ZONE_ID));
  }

  public static String kstCurrentDatetime() {
    return datetimeFormatter.format(LocalDateTime.now(KST_ZONE_ID));
  }

  public static String toDateString(LocalDate localDate) {
    if (localDate == null) {
      return null;
    }

    return dateFormatter.format(localDate);
  }

  public static String toDateString(LocalDateTime localDateTime) {
    if (localDateTime == null) {
      return null;
    }

    return datetimeFormatter.format(localDateTime);
  }

  public static LocalDate toLocalDate(String dateString) {
    validateDate(dateString);

    return LocalDate.parse(dateString, dateFormatter);
  }

  public static LocalTime toLocalTime(String timeString) {
    validateDate(timeString);

    return LocalTime.parse(timeString, timeFormatter);
  }

  public static LocalDateTime toLocalDateTime(String dateString, String timeString) {
    return LocalDateTime.parse(dateString + timeString, datetimeFormatter);
  }

  public static LocalDateTime utcLocalDateTimeToKstLocalDateTime(LocalDateTime localDateTime) {
    return LocalDateTime.ofInstant(localDateTime.atZone(UTC_ZONE_ID).toInstant(), KST_ZONE_ID);
  }

  public static String utcLocalDateTimeToKstDateString(LocalDateTime localDateTime) {
    return localDateTime.atZone(UTC_ZONE_ID).withZoneSameInstant(DateUtil.KST_ZONE_ID).format(dateFormatter);
  }

  public static String utcLocalDateTimeToKstYearMonthString(LocalDateTime localDateTime) {
    return localDateTime.atZone(UTC_ZONE_ID).withZoneSameInstant(DateUtil.KST_ZONE_ID).format(yearMonthFormatter);
  }
  
  public static LocalDate toLocalDate(LocalDateTime localDateTime) {
    return localDateTime.toLocalDate();
  }

  public static List<DateRange> splitDate(LocalDate startDate, LocalDate endDate, int intervalMonths) {
    List<DateRange> dateRanges = new ArrayList<>();
    LocalDate from = endDate;

    while (from.isAfter(startDate)) {
      LocalDate to = from;
      from = from.minusMonths(intervalMonths).withDayOfMonth(1);

      dateRanges.add(new DateRange(
          from.isBefore(startDate) ? startDate : from,
          to.isEqual(endDate) ? endDate : to.minusDays(1)));
    }

    return dateRanges;
  }

  public static List<String> getTransactionYearMonths(LocalDateTime startDatetime) {
    LocalDate startDate = LocalDate.ofInstant(startDatetime.atZone(UTC_ZONE_ID).toInstant(), KST_ZONE_ID);
    return startDate.withDayOfMonth(1)
        .datesUntil(LocalDate.now(KST_ZONE_ID).withDayOfMonth(2), Period.ofMonths(1))
        .map(localDate -> localDate.format(DateTimeFormatter.ofPattern("yyyyMM")))
        .collect(Collectors.toList());
  }

  private static void validateDate(String source) {
    if (source == null || source.isBlank()) {
      throw new IllegalArgumentException("Source is null or empty");
    }
  }

  public static LocalDateTime utcEpochMilliSecondTokstLocalDateTime(long utcEpochmilliSecond) {
    return LocalDateTime.ofEpochSecond(utcEpochmilliSecond, 0, ZoneOffset.UTC);
  }
}
