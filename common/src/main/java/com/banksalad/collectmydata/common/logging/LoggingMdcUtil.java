package com.banksalad.collectmydata.common.logging;

import org.springframework.core.task.TaskDecorator;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;

import java.util.Map;

public class LoggingMdcUtil {

  public static void set(String sector, String industry, long banksaladUserId, String organizationId,
      String syncRequestId) {
    MDC.put(CollectLogbackJsonLayout.JSON_KEY_SECTOR, sector);
    MDC.put(CollectLogbackJsonLayout.JSON_KEY_INDUSTRY, industry);
    MDC.put(CollectLogbackJsonLayout.JSON_KEY_BANKSALAD_USER_ID, String.valueOf(banksaladUserId));
    MDC.put(CollectLogbackJsonLayout.JSON_KEY_ORGANIZATION_ID, organizationId);
    MDC.put(CollectLogbackJsonLayout.JSON_KEY_SYNC_REQUEST_ID, syncRequestId);
  }

  @Deprecated
  public static void set(long banksaladUserId, String organizationId, String syncRequestId) {
    MDC.put(CollectLogbackJsonLayout.JSON_KEY_BANKSALAD_USER_ID, String.valueOf(banksaladUserId));
    MDC.put(CollectLogbackJsonLayout.JSON_KEY_ORGANIZATION_ID, organizationId);
    MDC.put(CollectLogbackJsonLayout.JSON_KEY_SYNC_REQUEST_ID, syncRequestId);
  }

  public static void clear() {
    MDC.clear();
  }

  public static String get(String key, String defaultValue) {
    String value = MDC.get(key);

    if (StringUtils.isEmpty(value)) {
      return defaultValue;
    }

    return value;
  }

  public static TaskDecorator createTaskDecorator() {
    return runnable -> {
      Map<String, String> contextMap = MDC.getCopyOfContextMap();

      return () -> {
        try {
          if (contextMap != null) {
            MDC.setContextMap(contextMap);
          }

          runnable.run();

        } finally {
          MDC.clear();
        }
      };
    };
  }
}
