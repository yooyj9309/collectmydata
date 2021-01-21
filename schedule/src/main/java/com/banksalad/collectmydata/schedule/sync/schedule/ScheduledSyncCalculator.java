package com.banksalad.collectmydata.schedule.sync.schedule;

import org.springframework.stereotype.Component;

import com.banksalad.collectmydata.schedule.common.db.entity.ScheduledSync;
import com.banksalad.collectmydata.schedule.sync.dto.ScheduledSyncRequest;

@Component
public class ScheduledSyncCalculator {

  public ScheduledSync calculate(ScheduledSyncRequest scheduledSyncRequest) {
    return new ScheduledSync();
  }
}
