package com.banksalad.collectmydata.schedule.sync.schedule;

import org.springframework.stereotype.Component;

import com.banksalad.collectmydata.schedule.common.db.entity.ScheduledSync;
import com.banksalad.collectmydata.schedule.common.db.repository.ScheduledSyncRepository;
import com.banksalad.collectmydata.schedule.sync.dto.ScheduledSyncRequest;
import lombok.RequiredArgsConstructor;

import static java.lang.Boolean.FALSE;

@Component
@RequiredArgsConstructor
public class ScheduledSyncScheduler {

  private final ScheduledSyncRepository scheduledSyncRepository;

  public void register(ScheduledSyncRequest scheduledSyncRequest) {
    ScheduledSync scheduledSync = getScheduledSyncFrom(scheduledSyncRequest);
    scheduledSyncRepository.save(scheduledSync);
  }

  public void unregister(ScheduledSyncRequest scheduledSyncRequest) {
//    scheduledSyncRepository.deleteById(Long.valueOf(scheduledSyncRequest.getRequestId()));
  }

  private ScheduledSync getScheduledSyncFrom(ScheduledSyncRequest scheduledSyncRequest) {
    return ScheduledSync.builder()
        .banksaladUserId(scheduledSyncRequest.getBanksaladUserId())
        .sector(scheduledSyncRequest.getSector())
        .industry(scheduledSyncRequest.getIndustry())
        .organizationId(scheduledSyncRequest.getOrganizationId())
        .isDeleted(FALSE)
        .build();
  }
}
