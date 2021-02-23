package com.banksalad.collectmydata.common.enums;

public enum Sector {

  FINANCE("finance"),
  PUBLIC("public"),
  HEALTHCARE("healthcare"),
  UNKNOWN("unknown"),
  ;

  private final String value;

  Sector(String value) {
    this.value = value;
  }

  public String getValue() {
    return this.value;
  }

  public static Sector of(String value) {
    if (FINANCE.getValue().equals(value)) {
      return FINANCE;

    } else if (PUBLIC.getValue().equals(value)) {
      return PUBLIC;

    } else if (HEALTHCARE.getValue().equals(value)) {
      return HEALTHCARE;

    } else {
      return UNKNOWN;
    }
  }
}


