package com.banksalad.collectmydata.common.util;

import org.apache.commons.lang3.builder.EqualsBuilder;

import java.util.HashSet;
import java.util.List;

public class ObjectComparator {

  public static boolean isSame(Object left, Object right, final String... excludeFields) {
    return EqualsBuilder.reflectionEquals(left, right, excludeFields);
  }

  public static <T> boolean isSameList(List<T> left, List<T> right, final String... excludeFields) {
    if (left == null || right == null) {
      return false;
    }

    if (left.size() != right.size()) {
      return false;
    }

    int listSize = left.size();
    for (int idx = 0; idx < listSize; idx++) {
      if (!ObjectComparator.isSame(left.get(idx), right.get(idx), excludeFields)) {
        return false;
      }
    }
    
    return true;
  }

  public static <T> boolean isSameListIgnoreOrder(List<T> left, List<T> right) {
    return new HashSet<>(left).equals(new HashSet<>(right));
  }
}
