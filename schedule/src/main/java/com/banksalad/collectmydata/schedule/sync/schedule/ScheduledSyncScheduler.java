package com.banksalad.collectmydata.schedule.sync.schedule;

import org.springframework.stereotype.Component;

import com.banksalad.collectmydata.schedule.common.db.entity.ScheduledSync;
import com.banksalad.collectmydata.schedule.common.db.repository.ScheduledSyncRepository;
import com.banksalad.collectmydata.schedule.sync.dto.ScheduledSyncRequest;
import javax.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

@Component
@RequiredArgsConstructor
public class ScheduledSyncScheduler {

  private final ScheduledSyncRepository scheduledSyncRepository;

  public void register(ScheduledSyncRequest scheduledSyncRequest) {
    ScheduledSync scheduledSync = ScheduledSync.of(scheduledSyncRequest);
    scheduledSyncRepository.save(scheduledSync);
  }

  public void unregister(ScheduledSyncRequest scheduledSyncRequest) {
    ScheduledSync scheduledSync =
        scheduledSyncRepository
            .findByBanksaladUserIdAndSectorAndIndustryAndOrganizationIdAndIsDeleted(
                scheduledSyncRequest.getBanksaladUserId(), scheduledSyncRequest.getSector(),
                scheduledSyncRequest.getIndustry(), scheduledSyncRequest.getOrganizationId(),
                FALSE
            )
            .orElseThrow(EntityNotFoundException::new);

    scheduledSync.disable();
    scheduledSyncRepository.save(scheduledSync);
  }
}
