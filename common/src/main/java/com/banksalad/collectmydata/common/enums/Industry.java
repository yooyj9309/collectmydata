package com.banksalad.collectmydata.common.enums;

import com.banksalad.collectmydata.common.exception.CollectRuntimeException;

public enum Industry {
  UNKNOWN,
  CARD,
  BANK,
  INSU,
  CAPITAL,
  EFIN,
  INVERT;

  public static Industry getIndustry(String key) {
    for (Industry industry : Industry.values()) {
      if (key.equalsIgnoreCase(industry.name())) {
        return industry;
      }
    }
    throw new CollectRuntimeException("fixme"); //TODO Exceoption처리방식이 결정된 후, Exceoption로직 변경
  }
}
