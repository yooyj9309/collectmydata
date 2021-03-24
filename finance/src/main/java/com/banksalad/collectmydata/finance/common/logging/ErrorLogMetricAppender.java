package com.banksalad.collectmydata.finance.common.logging;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.UnsynchronizedAppenderBase;
import com.banksalad.collectmydata.common.logging.CollectLogbackJsonLayout;
import com.banksalad.collectmydata.common.logging.LoggingMdcUtil;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;

public class ErrorLogMetricAppender extends UnsynchronizedAppenderBase<ILoggingEvent> {

  private static final String TAG_SECTOR = "sector";
  private static final String TAG_INDUSTRY = "industry";
  private static final String TAG_ORGANIZATION_ID = "organizationId";

  private static final String LOG_EVENT_NAME = "error.log.events";

  private final MeterRegistry meterRegistry;

  public ErrorLogMetricAppender(MeterRegistry meterRegistry) {
    this.meterRegistry = meterRegistry;
  }

  @Override
  protected void append(ILoggingEvent eventObject) {
    if (!eventObject.getLevel().isGreaterOrEqual(Level.ERROR)) {
      return;
    }

    String sector = LoggingMdcUtil.get(CollectLogbackJsonLayout.JSON_KEY_SECTOR, "UNKNOWN");
    String industry = LoggingMdcUtil.get(CollectLogbackJsonLayout.JSON_KEY_INDUSTRY, "UNKNOWN");
    String organizationId = LoggingMdcUtil.get(CollectLogbackJsonLayout.JSON_KEY_ORGANIZATION_ID, "UNKNOWN");

    Tags tags = Tags.of(TAG_SECTOR, sector)
        .and(TAG_INDUSTRY, industry)
        .and(TAG_ORGANIZATION_ID, organizationId);

    meterRegistry.counter(LOG_EVENT_NAME, tags).increment();
  }
}
