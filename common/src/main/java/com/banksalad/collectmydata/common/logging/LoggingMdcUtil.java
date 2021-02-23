package com.banksalad.collectmydata.common.logging;

import org.springframework.core.task.TaskDecorator;

import org.slf4j.MDC;

import java.util.Map;

public class LoggingMdcUtil {

  public static void set(long banksaladUserId, String organizationId, String syncRequestId) {
    MDC.put(CollectLogbackJsonLayout.JSON_KEY_BANKSALAD_USER_ID, String.valueOf(banksaladUserId));
    MDC.put(CollectLogbackJsonLayout.JSON_KEY_ORGANIZATION_ID, organizationId);
    MDC.put(CollectLogbackJsonLayout.JSON_KEY_SYNC_REQUEST_ID, syncRequestId);
  }

  public static void clear() {
    MDC.clear();
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
