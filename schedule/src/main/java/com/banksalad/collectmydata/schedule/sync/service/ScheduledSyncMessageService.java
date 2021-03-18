package com.banksalad.collectmydata.schedule.sync.service;

import com.banksalad.collectmydata.schedule.common.db.entity.ScheduledSyncEntity;
import com.banksalad.collectmydata.schedule.common.enums.SyncType;

public interface ScheduledSyncMessageService {

  void produce(ScheduledSyncEntity scheduledSyncEntity, SyncType syncType);
}
