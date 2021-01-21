package com.banksalad.collectmydata.schedule.sync.poll;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.banksalad.collectmydata.schedule.common.db.entity.ScheduledSync;
import com.banksalad.collectmydata.schedule.common.db.repository.ScheduledSyncRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ScheduledSyncPoller {

  private final ScheduledSyncRepository scheduledSyncRepository;
  private final ScheduledSyncTemplate scheduledSyncTemplate;

  @Scheduled
  public void poll() {
    List<ScheduledSync> scheduledSyncs = scheduledSyncRepository.findAll();
    for (ScheduledSync target : scheduledSyncs) {
      scheduledSyncTemplate.sync(target);
    }
  }
}
