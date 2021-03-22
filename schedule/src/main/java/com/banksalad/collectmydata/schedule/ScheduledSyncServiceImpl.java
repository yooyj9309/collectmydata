package com.banksalad.collectmydata.schedule;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.banksalad.collectmydata.common.enums.SyncRequestType;
import com.banksalad.collectmydata.common.message.SyncRequestedMessage;
import com.banksalad.collectmydata.schedule.common.db.entity.ScheduledSyncEntity;
import com.banksalad.collectmydata.schedule.common.db.repository.ScheduledSyncRepository;
import com.banksalad.collectmydata.schedule.sync.service.ScheduledSyncMessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.UUID;

import static com.banksalad.collectmydata.common.enums.SyncRequestType.SCHEDULED_ADDITIONAL;
import static com.banksalad.collectmydata.common.enums.SyncRequestType.SCHEDULED_BASIC;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScheduledSyncServiceImpl implements ScheduledSyncService {

  private final ScheduledSyncRepository scheduledSyncRepository;
  private final ScheduledSyncMessageService scheduledSyncMessageService;

  // FIXME
  private static final String EVERY_SUNDAY_AM_ZERO = "0 0 0 * * SUN";
  private static final String EVERY_AM_ZERO_EXCEPT_FOR_SUNDAY = "0 0 0 * * MON,TUE,WED,THU,FRI,SAT";
  private static final String TOPIC_SUFFIX = "SyncRequested";
  private static final Integer MAX_READ_SIZE = 100;

  @Override
  @Transactional(readOnly = true)
  @Scheduled(cron = EVERY_SUNDAY_AM_ZERO)
  public void syncBasic() {
    log.info("Basic Scheduler Starts, Every Sunday 00:00:00");
    produceSyncRequestBy(SCHEDULED_BASIC);
  }

  @Override
  @Transactional(readOnly = true)
  @Scheduled(cron = EVERY_AM_ZERO_EXCEPT_FOR_SUNDAY)
  public void syncAdditional() {
    log.info("Additional Scheduler Starts, Every 00:00:00 Except for Sunday");
    produceSyncRequestBy(SCHEDULED_ADDITIONAL);
  }

  private void produceSyncRequestBy(SyncRequestType syncRequestType) {
    Pageable pageRequest = PageRequest.of(0, MAX_READ_SIZE);

    while (true) {
      List<ScheduledSyncEntity> scheduledSyncEntities = scheduledSyncRepository.findAll(pageRequest).getContent();
      if (scheduledSyncEntities.size() == 0) {
        return;
      }

      scheduledSyncEntities.forEach(scheduledSyncEntity -> scheduledSyncMessageService.produceSyncRequest(
          getSyncRequestedMessageFrom(scheduledSyncEntity, syncRequestType),
          scheduledSyncEntity.getIndustry() + TOPIC_SUFFIX
          )
      );

      pageRequest = pageRequest.next();
    }
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
