package com.banksalad.collectmydata.schedule.sync.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.banksalad.collectmydata.schedule.common.db.repository.ScheduledSyncRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static com.banksalad.collectmydata.schedule.common.enums.SyncType.*;
import static java.lang.Boolean.FALSE;

@Slf4j
@Component
@RequiredArgsConstructor
public class AdditionalScheduledSyncScheduler implements ScheduledSyncScheduler {

  private final ScheduledSyncRepository scheduledSyncRepository;
  private final ScheduledSyncTemplate scheduledSyncKafkaTemplate;
  private static final String EVERY_AM_ZERO = "0 0 0 * * *";

  @Override
  @Scheduled(cron = EVERY_AM_ZERO)
  public void schedule() {
    log.info("Additional Scheduler Starts, Every 00:00:00");

    scheduledSyncRepository
        .findAllByIsDeletedEquals(FALSE)
        .forEach(target -> scheduledSyncKafkaTemplate.sync(target, ADDITIONAL));
  }
}
