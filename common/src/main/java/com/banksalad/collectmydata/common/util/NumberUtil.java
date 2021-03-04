package com.banksalad.collectmydata.common.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class NumberUtil {

  private static final int X100 = 100;
  private static final int X1000 = 1000;
  private static final int AMOUNT_SCALE = 3;

  public static long multiply1000(BigDecimal source) {
    if (source == null) {
      throw new IllegalArgumentException();
    }

    BigDecimal result = source.multiply(BigDecimal.valueOf(X1000));
    if (result.compareTo(BigDecimal.valueOf(Long.MAX_VALUE)) > 0) {
      throw new IllegalArgumentException();
    }

    return result.longValueExact();
  }

  public static long multiply100(BigDecimal source) {
    if (source == null) {
      throw new IllegalArgumentException();
    }

    BigDecimal result = source.multiply(BigDecimal.valueOf(X100));
    if (result.compareTo(BigDecimal.valueOf(Long.MAX_VALUE)) > 0) {
      throw new IllegalArgumentException();
    }

    return result.longValueExact();
  }

  public static BigDecimal setScale(BigDecimal source) {
    if (source == null) {
      return null;
    }
    return source.setScale(AMOUNT_SCALE, RoundingMode.UNNECESSARY);
  }

  public static BigDecimal bigDecimalOf(double source){
    return setScale(BigDecimal.valueOf(source));
  }

  public static BigDecimal bigDecimalOf(long source){
    return setScale(BigDecimal.valueOf(source));
  }
}
