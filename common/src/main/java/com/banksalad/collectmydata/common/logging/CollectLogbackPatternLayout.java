package com.banksalad.collectmydata.common.logging;

import ch.qos.logback.classic.PatternLayout;

public class CollectLogbackPatternLayout extends PatternLayout {

  static {
    defaultConverterMap.put("request", GrpcRequestIdConverter.class.getName());
  }
}
