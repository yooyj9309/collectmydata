package com.banksalad.collectmydata.common.logging;

import ch.qos.logback.classic.pattern.ClassOfCallerConverter;
import ch.qos.logback.classic.pattern.ExtendedThrowableProxyConverter;
import ch.qos.logback.classic.pattern.LineOfCallerConverter;
import ch.qos.logback.classic.pattern.MethodOfCallerConverter;
import ch.qos.logback.classic.pattern.ThrowableProxyConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.contrib.json.classic.JsonLayout;

import java.util.Map;

public class CollectLogbackJsonLayout extends JsonLayout {

  public static final String JSON_KEY_SECTOR = "sector";
  public static final String JSON_KEY_INDUSTRY = "industry";
  public static final String JSON_KEY_BANKSALAD_USER_ID = "banksaladUserId";
  public static final String JSON_KEY_ORGANIZATION_ID = "organizationId";
  public static final String JSON_KEY_SYNC_REQUEST_ID = "syncRequestId";

  private static final String JSON_KEY_LINE = "line";
  private static final String JSON_KEY_CLASS = "class";
  private static final String JSON_KEY_METHOD = "method";
  private static final String JSON_KEY_EXCEPTION = "exception";
  private static final String JSON_KEY_X_THROWABLE = "xThrowable";

  private static final LineOfCallerConverter lineOfCallerConverter = new LineOfCallerConverter();
  private static final ClassOfCallerConverter classOfCallerConverter = new ClassOfCallerConverter();
  private static final MethodOfCallerConverter methodOfCallerConverter = new MethodOfCallerConverter();
  private static final ThrowableProxyConverter throwableProxyConverter = new ThrowableProxyConverter();
  private static final ExtendedThrowableProxyConverter extendedThrowableProxyConverter = new ExtendedThrowableProxyConverter();

  @Override
  protected void addCustomDataToJsonMap(Map<String, Object> map, ILoggingEvent event) {
    map.put(JSON_KEY_LINE, lineOfCallerConverter.convert(event));
    map.put(JSON_KEY_CLASS, classOfCallerConverter.convert(event));
    map.put(JSON_KEY_METHOD, methodOfCallerConverter.convert(event));
    map.put(JSON_KEY_EXCEPTION, throwableProxyConverter.convert(event));
    map.put(JSON_KEY_X_THROWABLE, extendedThrowableProxyConverter.convert(event));
  }
}
