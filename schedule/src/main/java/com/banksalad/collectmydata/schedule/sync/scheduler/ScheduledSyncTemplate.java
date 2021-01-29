package com.banksalad.collectmydata.schedule.sync.scheduler;

import com.banksalad.collectmydata.schedule.common.db.entity.ScheduledSync;

public interface ScheduledSyncTemplate {

  void sync(ScheduledSync scheduledSync);
}
