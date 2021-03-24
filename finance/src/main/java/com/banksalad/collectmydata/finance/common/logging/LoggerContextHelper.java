package com.banksalad.collectmydata.finance.common.logging;

import org.springframework.stereotype.Component;

import ch.qos.logback.classic.LoggerContext;
import io.micrometer.core.instrument.MeterRegistry;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import org.slf4j.LoggerFactory;

import static org.slf4j.Logger.ROOT_LOGGER_NAME;

@Component
public class LoggerContextHelper {

  private final ErrorLogMetricAppender errorLogMetricAppender;

  public LoggerContextHelper(MeterRegistry meterRegistry) {
    errorLogMetricAppender = new ErrorLogMetricAppender(meterRegistry);
  }

  @PostConstruct
  private void init() {
    LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();

    errorLogMetricAppender.setContext(loggerContext);
    errorLogMetricAppender.setName("errorLogMetricAppender");
    errorLogMetricAppender.start();

    loggerContext.getLogger(ROOT_LOGGER_NAME).addAppender(errorLogMetricAppender);
  }

  @PreDestroy
  private void destroy() {
    errorLogMetricAppender.stop();
  }
}
