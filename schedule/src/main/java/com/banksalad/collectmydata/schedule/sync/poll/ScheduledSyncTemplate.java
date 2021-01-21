package com.banksalad.collectmydata.schedule.sync.poll;

import com.banksalad.collectmydata.schedule.common.db.entity.ScheduledSync;

public interface ScheduledSyncTemplate {

  void sync(ScheduledSync scheduledSync);
}
