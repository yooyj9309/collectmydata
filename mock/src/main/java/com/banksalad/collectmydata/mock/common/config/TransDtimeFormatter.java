package com.banksalad.collectmydata.mock.common.config;

import java.time.format.DateTimeFormatter;

public class TransDtimeFormatter {

  private static final String format = "yyyyMMddHHmmss";

  public static DateTimeFormatter get() {
    return DateTimeFormatter.ofPattern(format);
  }
}
