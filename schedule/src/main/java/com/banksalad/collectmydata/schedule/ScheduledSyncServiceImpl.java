package com.banksalad.collectmydata.schedule;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.banksalad.collectmydata.common.enums.SyncRequestType;
import com.banksalad.collectmydata.common.message.SyncRequestedMessage;
import com.banksalad.collectmydata.schedule.common.db.entity.ScheduledSyncEntity;
import com.banksalad.collectmydata.schedule.common.db.repository.ScheduledSyncRepository;
import com.banksalad.collectmydata.schedule.sync.service.ScheduledSyncMessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

import static com.banksalad.collectmydata.common.enums.SyncRequestType.SCHEDULED_ADDITIONAL;
import static com.banksalad.collectmydata.common.enums.SyncRequestType.SCHEDULED_BASIC;
import static java.lang.Boolean.FALSE;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScheduledSyncServiceImpl implements ScheduledSyncService {

  private final ScheduledSyncRepository scheduledSyncRepository;
  private final ScheduledSyncMessageService scheduledSyncMessageService;

  // FIXME : schedule time
  private static final String EVERY_SUNDAY_AM_ZERO = "0 0 0 * * SUN";
  private static final String EVERY_AM_ZERO_EXCEPT_FOR_SUNDAY = "0 0 0 * * MON,TUE,WED,THU,FRI,SAT";
  private static final String TOPIC_SUFFIX = "SyncRequested";

  @Override
  @Scheduled(cron = EVERY_SUNDAY_AM_ZERO)
  public void syncBasic() {
    log.info("Basic Scheduler Starts, Every Sunday 00:00:00");

    scheduledSyncRepository
        .findAll() // TODO : Optimization
        .forEach(scheduledSyncEntity -> scheduledSyncMessageService.produceSyncRequest(
            getSyncRequestedMessageFrom(scheduledSyncEntity, SCHEDULED_BASIC),
            scheduledSyncEntity.getIndustry() + TOPIC_SUFFIX
            )
        );
  }

  @Override
  @Scheduled(cron = EVERY_AM_ZERO_EXCEPT_FOR_SUNDAY)
  public void syncAdditional() {
    log.info("Additional Scheduler Starts, Every 00:00:00 Except for Sunday");

    scheduledSyncRepository
        .findAll() // TODO : Optimization
        .forEach(scheduledSyncEntity -> scheduledSyncMessageService.produceSyncRequest(
            getSyncRequestedMessageFrom(scheduledSyncEntity, SCHEDULED_ADDITIONAL),
            scheduledSyncEntity.getIndustry() + TOPIC_SUFFIX
            )
        );
  }

  private SyncRequestedMessage getSyncRequestedMessageFrom(ScheduledSyncEntity scheduledSyncEntity,
      SyncRequestType syncRequestType) {

    return SyncRequestedMessage.builder()
        .banksaladUserId(scheduledSyncEntity.getBanksaladUserId())
        .organizationId(scheduledSyncEntity.getOrganizationId())
        .syncRequestId(UUID.randomUUID().toString())
        .syncRequestType(syncRequestType)
        .build();
  }
}
