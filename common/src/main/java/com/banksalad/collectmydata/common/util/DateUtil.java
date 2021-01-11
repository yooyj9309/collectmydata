package com.banksalad.collectmydata.common.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Period;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DateUtil {

  public static final ZoneId KST_ZONE_ID = ZoneId.of("Asia/Seoul");
  public static final ZoneId UTC_ZONE_ID = ZoneId.of("UTC");

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

  public static String kstCurrentDate() {
    return dateFormatter.format(LocalDate.now(KST_ZONE_ID));
  }

  public static String localDateToDateString(LocalDate source) {
    if (source == null) {
      return null;
    }

    return dateFormatter.format(source);
  }

  public static LocalDate stringToLocalDate(String source) {
    validateDate(source);

    return LocalDate.parse(source, dateFormatter);
  }

  public static LocalTime stringToLocalTime(String source) {
    validateDate(source);

    return LocalTime.parse(source, timeFormatter);
  }

  public static LocalDateTime toLocalDateTime(String date, String time) {
    return LocalDateTime.parse(date + time, datetimeFormatter);
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
}
