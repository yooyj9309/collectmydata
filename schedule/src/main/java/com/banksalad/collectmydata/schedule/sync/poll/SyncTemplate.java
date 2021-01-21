package com.banksalad.collectmydata.schedule.sync.poll;

import com.banksalad.collectmydata.schedule.common.db.entity.SyncEntity;

public interface SyncTemplate {

  void sync(SyncEntity sync);
}
