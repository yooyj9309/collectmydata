package com.banksalad.collectmydata.schedule.sync.scheduler;

import com.banksalad.collectmydata.schedule.common.db.entity.ScheduledSync;
import com.banksalad.collectmydata.schedule.common.enums.SyncType;

public interface ScheduledSyncTemplate {

  void sync(ScheduledSync scheduledSync, SyncType syncType);
}
