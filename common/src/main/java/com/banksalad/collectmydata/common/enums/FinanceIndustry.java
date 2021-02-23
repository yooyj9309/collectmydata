package com.banksalad.collectmydata.common.enums;

public enum FinanceIndustry {
  BANK("bank"),
  CARD("card"),
  INVEST("invest"),
  INSU("insu"),
  EFIN("efin"),
  CAPITAL("capital");

  private String value;

  FinanceIndustry(String value) {
    this.value = value;
  }

  public String getValue() {
    return this.value;
  }
}
