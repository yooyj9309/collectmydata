package com.banksalad.collectmydata.schedule;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.banksalad.collectmydata.schedule.common.db.repository.ScheduledSyncRepository;
import com.banksalad.collectmydata.schedule.sync.service.ScheduledSyncMessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static com.banksalad.collectmydata.schedule.common.enums.SyncType.ADDITIONAL;
import static com.banksalad.collectmydata.schedule.common.enums.SyncType.BASIC;
import static java.lang.Boolean.FALSE;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScheduledSyncServiceImpl implements ScheduledSyncService {

  private final ScheduledSyncRepository scheduledSyncRepository;
  private final ScheduledSyncMessageService scheduledSyncMessageService;

  private static final String EVERY_TUESDAY_AM_ZERO = "0 0 0 * * TUE";
  private static final String EVERY_AM_ZERO = "0 0 0 * * *";

  @Override
  @Scheduled(cron = EVERY_TUESDAY_AM_ZERO)
  public void syncBasic() {
    log.info("Basic Scheduler Starts, Every Tuesday 00:00:00");

    scheduledSyncRepository
        .findAllByIsDeletedEquals(FALSE)
        .forEach(target -> scheduledSyncMessageService.produce(target, BASIC));
  }

  @Override
  @Scheduled(cron = EVERY_AM_ZERO)
  public void syncAdditional() {
    log.info("Additional Scheduler Starts, Every 00:00:00");

    scheduledSyncRepository
        .findAllByIsDeletedEquals(FALSE)
        .forEach(target -> scheduledSyncMessageService.produce(target, ADDITIONAL));
  }
}
