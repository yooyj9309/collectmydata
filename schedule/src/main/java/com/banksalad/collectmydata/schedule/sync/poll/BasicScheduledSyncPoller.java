package com.banksalad.collectmydata.schedule.sync.poll;

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
public class BasicScheduledSyncPoller implements ScheduledSyncPoller {

  private final ScheduledSyncRepository scheduledSyncRepository;
  private final ScheduledSyncTemplate scheduledSyncKafkaTemplate;
  private static final String EVERY_TUESDAY_AM_ZERO = "0 0 0 * * TUE";

  @Override
  @Scheduled(cron = EVERY_TUESDAY_AM_ZERO)
  public void poll() {
    log.info("BasicScheduledSyncPoller Starts Every Tuesday 00:00:00");

    scheduledSyncRepository.findAllByIsDeletedEquals(FALSE)
        .forEach(target -> {
          target.setSyncType(BASIC);
          scheduledSyncKafkaTemplate.sync(target);
        });
  }
}
