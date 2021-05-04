package com.banksalad.collectmydata.common.util;

public class LogFormatUtil {

  public static String makeLogFormat(String location, String title) {
    return new StringBuilder(location).append(" ").append(title).append(", ").append("{}").toString();
  }
}
