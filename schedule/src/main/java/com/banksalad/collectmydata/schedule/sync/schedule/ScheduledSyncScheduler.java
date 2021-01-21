package com.banksalad.collectmydata.schedule.sync.schedule;

import org.springframework.stereotype.Component;

import com.banksalad.collectmydata.schedule.common.db.entity.ScheduledSync;
import com.banksalad.collectmydata.schedule.common.db.repository.ScheduledSyncRepository;
import com.banksalad.collectmydata.schedule.sync.dto.ScheduledSyncRequest;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ScheduledSyncScheduler {

  private final ScheduledSyncCalculator scheduledSyncCalculator;
  private final ScheduledSyncRepository scheduledSyncRepository;

  public void register(ScheduledSyncRequest scheduledSyncRequest) {
    ScheduledSync sync = scheduledSyncCalculator.calculate(scheduledSyncRequest);
    scheduledSyncRepository.save(sync);
  }

  public void unregister(ScheduledSyncRequest scheduledSyncRequest) {
//    scheduledSyncRepository.deleteById(Long.valueOf(scheduledSyncRequest.getRequestId()));
  }
}
