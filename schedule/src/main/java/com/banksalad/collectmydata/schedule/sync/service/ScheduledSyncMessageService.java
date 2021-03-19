package com.banksalad.collectmydata.schedule.sync.service;

import com.banksalad.collectmydata.common.message.SyncRequestedMessage;

public interface ScheduledSyncMessageService {

  void produceSyncRequest(SyncRequestedMessage syncRequestedMessage, String topic);
}
