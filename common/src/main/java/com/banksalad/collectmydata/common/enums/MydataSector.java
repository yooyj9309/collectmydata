package com.banksalad.collectmydata.common.enums;

public enum MydataSector {
  FINANCE,
  PUBLIC,
  HEALTHCARE,
  UNKNOWN;

  public static MydataSector getSector(String eco) {
    for (MydataSector mydataSector : MydataSector.values()) {
      if (eco.equalsIgnoreCase(mydataSector.name())) {
        return mydataSector;
      }
    }
    return null;
  }
}
