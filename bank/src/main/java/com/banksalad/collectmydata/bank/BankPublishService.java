package com.banksalad.collectmydata.bank;

import java.time.LocalDateTime;

public interface BankPublishService {

  void publish(long banksaladUserId, String organizationId, LocalDateTime previousSyncedAt);
}
