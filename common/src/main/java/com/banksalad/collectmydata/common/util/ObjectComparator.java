package com.banksalad.collectmydata.common.util;

import org.apache.commons.lang3.builder.EqualsBuilder;

public class ObjectComparator {

  public static boolean isSame(Object left, Object right, final String... excludeFields) {
    return EqualsBuilder.reflectionEquals(left, right, excludeFields);
  }
}
