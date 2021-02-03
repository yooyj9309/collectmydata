package com.banksalad.collectmydata.schedule.sync.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.banksalad.collectmydata.schedule.common.db.repository.ScheduledSyncRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static com.banksalad.collectmydata.schedule.common.enums.SyncType.BASIC;
import static java.lang.Boolean.FALSE;

@Slf4j
@Component
@RequiredArgsConstructor
public class BasicScheduledSyncScheduler implements ScheduledSyncScheduler {

  private final ScheduledSyncRepository scheduledSyncRepository;
  private final ScheduledSyncTemplate scheduledSyncKafkaTemplate;
  private static final String EVERY_TUESDAY_AM_ZERO = "0 0 0 * * TUE";

  @Override
  @Scheduled(cron = EVERY_TUESDAY_AM_ZERO)
  public void schedule() {
    log.info("Basic Scheduler Starts, Every Tuesday 00:00:00");

    scheduledSyncRepository
        .findAllByIsDeletedEquals(FALSE)
        .forEach(target -> scheduledSyncKafkaTemplate.sync(target, BASIC));
  }
}
